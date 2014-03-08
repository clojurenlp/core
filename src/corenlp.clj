(ns corenlp
  (:import
    (java.io StringReader)
    (edu.stanford.nlp.process 
      DocumentPreprocessor PTBTokenizer)
    (edu.stanford.nlp.ling Word)
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
  (.tokenize
    (PTBTokenizer/newPTBTokenizer
      (StringReader. s)))) 

(defn split-sentences [text]
  (let [rdr (StringReader. text)]
    (map #(vec (map str %))
      (iterator-seq
        (.iterator
          (DocumentPreprocessor. rdr))))))
 
(defmulti word type)

(defmethod word String [s]
  (Word. s))

(defmethod word Word [w] w)

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Part-of-speech tagging
;;;;;;;;;;;;;;;;;;;;;;;;;

(def load-tagger
  (memoize (fn [] (MaxentTagger. MaxentTagger/DEFAULT_JAR_PATH))))

(defmulti tag-sentence type)

(defmethod tag-sentence java.util.ArrayList [sentence]
  (.tagSentence (load-tagger) sentence))

(defmethod tag-sentence :default [coll]
  (.tagSentence (load-tagger) (java.util.ArrayList. (map word coll))))

;;;;;;;;;;
;; Parsing
;;;;;;;;;;

(let [trf (LabeledScoredTreeReaderFactory.)]
 (defn read-parse-tree [s]
   (.readTree
    (.newTreeReader trf
                    (StringReader. s))))
 (defn read-scored-parse-tree [s]
   (read-parse-tree
    (->>
     (filter #(not (and
                    (.startsWith % "[")
                    (.endsWith % "]")))
             (.split s " "))
     (interpose " ")
     (apply str)))))

(def load-parser
  (memoize
    (fn []
      (LexicalizedParser/loadModel))))

(defn parse [coll]
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
  (let [[words tags edges] (map #(% dp) [:words :tags :edges])
        g (apply digraph (map (partial take 2) edges))]
    (reduce (fn [g [i t]] (add-attr g i :tag t))
            (reduce (fn [g [i w]] (add-attr g i :word w))
                    (reduce (fn [g [gov dep type]]
                              (add-attr g gov dep :type type)) g edges)
                    (map-indexed vector words))
            (map-indexed vector tags))))

(def dependency-parse nil)

(defmulti dependency-parse class)

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
