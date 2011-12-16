(ns stanford-corenlp.parser
  (:import
   (java.io StringReader)
   (edu.stanford.nlp.ling Word)
   (edu.stanford.nlp.trees LabeledScoredTreeReaderFactory)
   (edu.stanford.nlp.parser.lexparser LexicalizedParser)))


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

(defonce parser
  (LexicalizedParser.
   (java.io.ObjectInputStream.
    (java.util.zip.GZIPInputStream.
     (.getInputStream
      (.openConnection
       (clojure.java.io/resource "englishPCFG.ser.gz")))))))

(defmulti parse sequential?)

(defmethod parse true [coll]
  (.apply parser (map #(Word. %) coll)))

(defmethod parse false [s]
  (.apply parser s))

