(ns stanford-corenlp.parser
  (:import
   (java.io StringReader)
   (edu.stanford.nlp.trees LabeledScoredTreeReaderFactory)
   (edu.stanford.nlp.parser.lexparser LexicalizedParser)))


(let [trf (LabeledScoredTreeReaderFactory.)]
 (defn read-parse-tree [s]
   (.readTree
    (.newTreeReader trf
                    (StringReader. s)))))

(defonce parse
  (let [parser (LexicalizedParser.
                (java.io.ObjectInputStream.
                 (java.util.zip.GZIPInputStream.
                  (.getInputStream
                   (.openConnection
                    (clojure.java.io/resource "englishPCFG.ser.gz"))))))]
    #(.apply parser %)))

