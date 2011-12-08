(ns stanford-corenlp.dependencies
  (:require
   [stanford-corenlp.parser :as parser])
  (:import
   (edu.stanford.nlp.trees LabeledScoredTreeNode PennTreebankLanguagePack))
  (:use
   [loom graph attr]))

(defn make-dependency-graph [words tags deps]
  (let [g (apply digraph (map (partial take 2) deps))]
    (reduce (fn [g [i t]] (add-attr g i :tag t))
            (reduce (fn [g [i w]] (add-attr g i :word w))
                    (reduce (fn [g [gov dep type]] (add-attr g gov dep :dep type)) g deps)
                    (map-indexed vector words))
            (map-indexed vector tags))))

(def dependency-graph nil)
(defmulti dependency-graph
  #(if (seq? %)
     :seq
     (class %)))

(let [tlp (PennTreebankLanguagePack.)
      gsf (.grammaticalStructureFactory tlp)]
 (defmethod dependency-graph LabeledScoredTreeNode [n]
   (try
     (let [ty (.taggedYield n)]
       (make-dependency-graph
        (map #(.word %) ty)
        (map #(.tag %) ty)
        (map (fn [d] 
               [(dec (.. d gov index)) (dec (.. d dep index)) (.. d reln toString)])
             (.typedDependencies
              (.newGrammaticalStructure gsf n)))))
     (catch java.lang.RuntimeException _))))

(defmethod dependency-graph String [s]
  (dependency-graph (parser/parse s)))

(defmethod dependency-graph :seq [s]
  (dependency-graph
   (parser/parse
    (map #(edu.stanford.nlp.ling.Word. %) s))))