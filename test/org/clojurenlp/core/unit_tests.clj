(ns org.clojurenlp.core.unit-tests
  (:require [org.clojurenlp.core :as core]
            [clojure.test :refer [deftest is]]))

(deftest tokenization-test
  (is
   (= '({:token "This", :start-offset 0, :end-offset 4}
        {:token "is", :start-offset 5, :end-offset 7}
        {:token "a", :start-offset 8, :end-offset 9}
        {:token "simple", :start-offset 10, :end-offset 16}
        {:token "sentence", :start-offset 17, :end-offset 25}
        {:token ".", :start-offset 25, :end-offset 26})
      (core/tokenize "This is a simple sentence."))))

(deftest pos-test
  (is
   (= "JJ"
      (->> "Short and sweet."
           core/tokenize
           core/pos-tag
           first
           .tag)))
  (is (= '("JJ" "CC" "JJ" ".")
         (->> "Short and sweet."
              core/tokenize
              core/pos-tag
              (map #(.tag %))))))
