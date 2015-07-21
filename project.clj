(defproject corenlp "3.3.2"
  :description "Clojure wrapper for the Stanford CoreNLP tools."
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2"
                  :classifier "models"]
                 [org.clojure/data.json "0.2.6"]   
                 [aysylu/loom "0.5.4"]])