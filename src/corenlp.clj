(ns corenlp
  (:import
    (java.io StringReader)
    (edu.stanford.nlp.process 
      DocumentPreprocessor PTBTokenizer)
    (edu.stanford.nlp.ling Word)
    (edu.stanford.nlp.tagger.maxent MaxentTagger)
    (edu.stanford.nlp.trees 
      LabeledScoredTreeNode PennTreebankLanguagePack  
      LabeledScoredTreeReaderFactory)
    (edu.stanford.nlp.parser.lexparser 
      LexicalizedParser))
  
  (:use
    (loom graph attr)
    clojure.set)
  (:gen-class :main true))
 
;;;;;;;;;;;;;;;;
;; Preprocessing
;;;;;;;;;;;;;;;;

(defn tokenize [s]
  "Tokenize an input string into a sequence of Word objects."
  (.tokenize
    (PTBTokenizer/newPTBTokenizer
      (StringReader. s)))) 

(defn split-sentences [text]
  "Split a string into a sequence of sentences, each of which is a sequence of Word objects. (Thus, this method both splits sentences and tokenizes simultaneously.)"
  (let [rdr (StringReader. text)]
    (map #(vec (map str %))
      (iterator-seq
        (.iterator
          (DocumentPreprocessor. rdr))))))
 
(defmulti word 
  "Attempt to convert a given object into a Word, which is used by many downstream algorithms."
  type)

(defmethod word String [s]
  (Word. s))

(defmethod word Word [w] w)

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Part-of-speech tagging
;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^{:private true} 
  load-pos-tagger
  (memoize (fn [] (MaxentTagger. MaxentTagger/DEFAULT_JAR_PATH))))

(defmulti pos-tag 
  "Tag a sequence of words with their parts of speech, returning a sequence of TaggedWord objects."
  type)

(defmethod pos-tag java.util.ArrayList [sentence]
  (.tagSentence (load-pos-tagger) sentence))

(defmethod pos-tag :default [coll]
  (.tagSentence (load-pos-tagger) 
                (java.util.ArrayList. (map word coll))))

;;;;;;;;;;
;; Parsing
;;;;;;;;;;

(let [trf (LabeledScoredTreeReaderFactory.)]

 (defn read-parse-tree [s]
   "Read a parse tree in PTB format from a string (produced by this or another parser)"
   (.readTree
    (.newTreeReader trf
                    (StringReader. s))))
 (defn read-scored-parse-tree [s]
   "Read a parse tree in PTB format with scores from a string."
   (read-parse-tree
    (->>
     (filter #(not (and
                    (.startsWith % "[")
                    (.endsWith % "]")))
             (.split s " "))
     (interpose " ")
     (apply str)))))

(def ^{:private true} load-parser
  (memoize
    (fn []
      (LexicalizedParser/loadModel))))

(defn parse [coll]
  "Use the LexicalizedParser to produce a constituent parse of sequence of strings or CoreNLP Word objects."
  (.apply (load-parser)
          (java.util.ArrayList. 
            (map word coll)))) 

;; Typed Dependencies

(defrecord DependencyParse [words tags edges])

(defn roots [dp]
  (difference
   (set (range (count (:words dp))))
   (set (map second (:edges dp)))))

(defn add-roots [dp]
  "Add explicit ROOT relations to the dependency parse. This will turn it from a polytree to a tree."
  ;;Note to self: in the new version of the parser, but not the
  ;;CoreNLP, this is already done. So when incorporating CoreNLP
  ;;updates be sure this isn't redundant.
  (assoc dp :edges
         (concat (:edges dp)
          (for [r (roots dp)]
            [-1 r :root]))))

(defn dependency-graph [dp]
  "Produce a loom graph from a DependencyParse record."
  (let [[words tags edges] (map #(% dp) [:words :tags :edges])
        g (apply digraph (map (partial take 2) edges))]
    (reduce (fn [g [i t]] (add-attr g i :tag t))
            (reduce (fn [g [i w]] (add-attr g i :word w))
                    (reduce (fn [g [gov dep type]]
                              (add-attr g gov dep :type type)) g edges)
                    (map-indexed vector words))
            (map-indexed vector tags))))

(def dependency-parse nil)

(defmulti dependency-parse 
  "Produce a DependencyParse from a sentence, which is a directed graph structure whose nodes are words and edges are typed dependencies (Marneffe et al, 2005) between them." 
  class)

(let [tlp (PennTreebankLanguagePack.)
      gsf (.grammaticalStructureFactory tlp)]

 (defmethod dependency-parse LabeledScoredTreeNode [n]
   (try
     (let [ty (.taggedYield n)]
       (DependencyParse.
        (vec (map #(.word %) ty))
        (vec (map #(.tag %) ty))
        (map (fn [d] 
               [(dec (.. d gov index))
                (dec (.. d dep index))
                (keyword
                 (.. d reln toString))])
             (.typedDependencies
              (.newGrammaticalStructure gsf n)))))
     (catch java.lang.RuntimeException _))))

(defmethod dependency-parse :default [s]
  (dependency-parse (parse s)))
