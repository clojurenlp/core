(ns corenlp
  (:import
    (java.io StringReader)
    (java.util ArrayList Collection)
    (edu.stanford.nlp.process
      DocumentPreprocessor PTBTokenizer)
    (edu.stanford.nlp.ling TaggedWord Word)
    (edu.stanford.nlp.tagger.maxent MaxentTagger)
    (edu.stanford.nlp.trees
      LabeledScoredTreeNode
      LabeledScoredTreeReaderFactory
      PennTreebankLanguagePack
      TypedDependency)
    (edu.stanford.nlp.parser.common
      ParserGrammar)
    (edu.stanford.nlp.parser.lexparser
      LexicalizedParser))
  (:use
    (loom graph attr)
    clojure.set)
  (:require
    [clojure.data.json :as json])
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

(defmethod word String [^String s]
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

(defmethod pos-tag ArrayList [sentence]
  (.tagSentence ^MaxentTagger (load-pos-tagger) ^ArrayList sentence))

(defmethod pos-tag :default [coll]
  (.tagSentence ^MaxentTagger (load-pos-tagger) 
                (ArrayList. ^Collection (map word coll))))

;;;;;;;;;;
;; Parsing
;;;;;;;;;;

(let [trf (LabeledScoredTreeReaderFactory.)]

 (defn read-parse-tree [s]
   "Read a parse tree in PTB format from a string (produced by this or another parser)"
   (.readTree
    (.newTreeReader trf
                    (StringReader. s))))

 (defn read-scored-parse-tree [^String s]
   "Read a parse tree in PTB format with scores from a string."
   (read-parse-tree
    (->>
     (filter (fn [^String w]
               (not (and
                     (.startsWith w "[")
                     (.endsWith w "]"))))
             (.split s " "))
     (interpose " ")
     (apply str)))))

(def ^{:private true} load-parser
  (memoize
    (fn []
      (LexicalizedParser/loadModel))))


(defmulti parse class)

(defmethod parse java.lang.String [s]
  (parse (tokenize s)))

(defmethod parse :default [coll]
  [coll]
  "Use the LexicalizedParser to produce a constituent parse of sequence of strings or CoreNLP Word objects."
  (.apply ^ParserGrammar (load-parser)
          (ArrayList.
           ^Collection (map word coll))))

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

(defmulti dependency-parse 
  "Produce a DependencyParse from a sentence, which is a directed graph structure whose nodes are words and edges are typed dependencies (Marneffe et al, 2005) between them." 
  class)

(let [tlp (PennTreebankLanguagePack.)
      gsf (.grammaticalStructureFactory tlp)]

 (defmethod dependency-parse LabeledScoredTreeNode [^LabeledScoredTreeNode n]
   (try
     (let [ty (.taggedYield n)]
       (DependencyParse.
        (vec (map #(.word ^TaggedWord %) ty))
        (vec (map #(.tag ^TaggedWord %) ty))
        (map (fn [^TypedDependency d] 
               [(dec (.. d gov index))
                (dec (.. d dep index))
                (keyword
                 (.. d reln toString))])
             (.typedDependencies
              (.newGrammaticalStructure gsf n)))))
     (catch java.lang.RuntimeException _))))

(defmethod dependency-parse :default [s]
  (dependency-parse (parse s)))

(defmulti dependency-graph class)

(defmethod dependency-graph DependencyParse [dp]
  "Produce a loom graph from a DependencyParse record."
  (let [[words tags edges] (map #(% dp) [:words :tags :edges])
        g (apply digraph (map (partial take 2) edges))]
    (reduce (fn [g [i t]] (add-attr g i :tag t))
            (reduce (fn [g [i w]] (add-attr g i :word w))
                    (reduce (fn [g [gov dep type]]
                              (add-attr g gov dep :type type)) g edges)
                    (map-indexed vector words))
            (map-indexed vector tags))))

(defmethod dependency-graph :default [x]
  (dependency-graph (dependency-parse x)))
 
;; CLI (for dependency parsing)

(defn between [n low high]
  (and (>= n low) (<= n high)))

;; TODO: use real CLI argument parsing
                
(defn -main [& args]
  (let [min-length 5
        max-length 
        (if (> (count args) 1)
          (Integer/parseInt (second args))
          50)]
    (doseq [line (line-seq (java.io.BufferedReader. *in*))
            :let [parses
                  (or (try
                        (map dependency-parse
                             (filter #(between (count %) 
                                               min-length
                                               max-length)
                                     (split-sentences line))))
                      [])]]
      (if parses
        (println
          (json/write-str parses))))))
