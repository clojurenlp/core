(ns stanford-corenlp.dependencies
  (:require
   [stanford-corenlp.parser :as parser])
  (:import
   (edu.stanford.nlp.trees LabeledScoredTreeNode PennTreebankLanguagePack))
  (:use
   [loom graph attr]
   clojure.set))

(defrecord DependencyParse [words tags deps])

(defn roots [dp]
  (difference
   (set (range (count (:words dp))))
   (set (map second (:deps dp)))))

(defn add-roots [dp]
  "Add explicit ROOT relations to the dependency parse. This will turn it from a polytree to a tree."
  ;;Note to self: in the new version of the parser, but not the
  ;;CoreNLP, this is already done. So when incorporating CoreNLP
  ;;updates be sure this isn't redundant.
  (assoc dp :deps
         (concat (:deps dp)
          (for [r (roots dp)]
            [-1 r :root]))))

(defn dependency-graph [dp]
  (let [[words tags deps] (map dp [:words :tags :deps])
        g (apply digraph (map (partial take 2) deps))]
    (reduce (fn [g [i t]] (add-attr g i :tag t))
            (reduce (fn [g [i w]] (add-attr g i :word w))
                    (reduce (fn [g [gov dep type]] (add-attr g gov dep :dep type)) g deps)
                    (map-indexed vector words))
            (map-indexed vector tags))))


(def dependency-parse nil)
(defmulti dependency-parse
  #(if (seq? %)
     :seq
     (class %)))

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

(defmethod dependency-parse String [s]
  (dependency-parse (parser/parse s)))

(defmethod dependency-parse :seq [s]
  (dependency-parse
   (parser/parse
    (map #(edu.stanford.nlp.ling.Word. %) s))))