(ns corenlp-test
  (:require [corenlp :as corenlp]
            [clojure.test :refer :all]))


(deftest tokenize-test
  (testing "tokenize"
    (is (= (map corenlp/word ["Petunia" "is" "my" "cat" "."])
           (corenlp/tokenize "Petunia is my cat."))))
  (testing "tokenize-core-label"
    (let [tokens (corenlp/tokenize-core-label "Petunia is my cat.")]
      (is (every? #(instance? edu.stanford.nlp.ling.CoreLabel %) tokens))
      (is (= ["Petunia" "is" "my" "cat" "."]
             (map str tokens))))))


(deftest split-sentences-test
  (testing "split-sentences"
    (let [sent-tokens (corenlp/split-sentences
                       "Petunia is my cat. She is a DSH.")]
      (is (= [["Petunia" "is" "my" "cat" "."] ["She" "is" "a" "DSH" "."]]
             sent-tokens)))))


(deftest pos-tagging-test
  (testing "pos-tag"
    (let [tagged-words (corenlp/pos-tag "Petunia is my cat.")]
      (is (= [["Petunia" "NNP"] ["is" "VBZ"] ["my" "PRP$"] ["cat" "NN"] ["." "."]]
             tagged-words)))))


(deftest parse-test
  (testing "parse"
    (is (= "(ROOT (S (NP (NNP Petunia)) (VP (VBZ is) (NP (PRP$ my) (NN cat))) (. .)))"
           (str (corenlp/parse (corenlp/tokenize "Petunia is my cat.")))))))
