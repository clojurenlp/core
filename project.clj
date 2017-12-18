(defproject corenlp "3.6.2"
  :description "Clojure wrapper for the Stanford CoreNLP tools."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0" 
                  :classifier "models"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0" 
                  :classifier "models-french"]
                 [org.clojure/data.json "0.2.6"]
                 [aysylu/loom "1.0.0"]]
  :plugins [[lein-auto "0.1.3"]])
