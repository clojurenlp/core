(ns corenlp-test
  (:require [corenlp :as corenlp]
            [clojure.test :refer :all]))


(deftest tokenize-test
  (testing "tokenize"
    (is (= (map corenlp/word ["Petunia" "is" "my" "cat" "."])
           (corenlp/tokenize "Petunia is my cat.")))))


(deftest split-sentences-test
  (testing "split-sentences"
    (let [sent-tokens (corenlp/split-sentences
                       "Petunia is my cat. She is a DSH.")]
      (is (= [["Petunia" "is" "my" "cat" "."] ["She" "is" "a" "DSH" "."]]
             sent-tokens)))))


(deftest pos-tagging-test
  (testing "pos-tag"
    (let [tagged-words (corenlp/pos-tag
                        (corenlp/tokenize "Petunia is my cat."))]
      (is (= (map #(edu.stanford.nlp.ling.TaggedWord. %1 %2)
                  ["Petunia" "is" "my" "cat" "."]
                  ["NNP" "VBZ" "PRP$" "NN" "."])
             tagged-words)))))


(deftest parse-test
  (testing "parse"
    (is (= "(ROOT (S (NP (NNP Petunia)) (VP (VBZ is) (NP (PRP$ my) (NN cat))) (. .)))"
           (str (corenlp/parse (corenlp/tokenize "Petunia is my cat.")))))))
