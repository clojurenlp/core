(defproject es.corygil/corenlp "3.3.1-r2-SNAPSHOT"
  :description "Clojure wrapper for the Stanford CoreNLP tools."
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.3.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.3.1"
                  :classifier "models"]
                 [org.clojure/data.json "0.2.6"]   
                 [aysylu/loom "0.5.4"]])