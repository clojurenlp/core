(ns org.clojurenlp.core.unit-tests
  (:require [org.clojurenlp.core :as corenlp]
            [clojure.test :refer :all]))

(deftest tag
  (is (= "JJ" (->> "Short and sweet." corenlp/tokenize corenlp/pos-tag first .tag))))