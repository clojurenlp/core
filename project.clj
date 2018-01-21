(defproject org.clojurenlp/core "3.7.0"
  :description "Clojure wrapper for the Stanford CoreNLP tools."
  :url "https://github.com/clojurenlp/core"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0" 
                  :classifier "models"]
                 [org.clojure/data.json "0.2.6"]
                 [aysylu/loom "1.0.0"]]
  :plugins [[lein-auto "0.1.3"]])
