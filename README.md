# org.clojurenlp.core

[![Clojars Project](https://img.shields.io/clojars/v/org.clojurenlp/core.svg)](https://clojars.org/org.clojurenlp/core)
[![Build Status](https://travis-ci.org/clojurenlp/core.svg?branch=master)](https://travis-ci.org/clojurenlp/core)
[![Gitter Lobby](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/clojurenlp/Lobby)

Natural language processing in Clojure based on the Stanford-CoreNLP parser.

# 👋 MAINTAINERS WANTED!

We need help getting this project moving. Please feel free to email to leontalbot@gmail.com to join the org, or drop a line in the chat room.


This is a work in progress, currently in the POC phase.

## Usage

### Tokenization

    (use 'org.clojurenlp.core)
    (tokenize "This is a simple sentence.")
    ;; => '({:token "This", :start-offset 0, :end-offset 4}
            {:token "is", :start-offset 5, :end-offset 7}
            {:token "a", :start-offset 8, :end-offset 9}
            {:token "simple", :start-offset 10, :end-offset 16}
            {:token "sentence", :start-offset 17, :end-offset 25}
            {:token ".", :start-offset 25, :end-offset 26}) 
        
        
### Part-of-Speech Tagging

To get a list of `TaggedWord` objects:

    (use 'org.clojurenlp.core)
    ;;  use any of these:
    (-> "Short and sweet." tokenize pos-tag)
    (-> "Short and sweet." split-sentences first pos-tag)
    (-> ["Short" "and" "sweet" "."] pos-tag)
    (-> "Short and sweet." pos-tag)
    
    ;; => [#<TaggedWord Short/JJ> #<TaggedWord and/CC> ...]

To return a tag string from TaggedWord object:
    
    (->> "Short and sweet." tokenize pos-tag first .tag)
    ;; => JJ
    (->> "Short and sweet." tokenize pos-tag (map #(.tag %)))
    ;; => ("JJ" "CC" "JJ" ".")

For more information, see the [relevant Javadoc](http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ling/TaggedWord.html)

### Named Entity Recognition

To tag named entities utilizing standard Stanford NER model:

    (use 'org.clojurenlp.core)
    (def pipeline (initialize-pipeline))
    (def text "The United States of America will be tagged as a location")
    (tag-ner pipeline text)

Training your own model [How to Train Your Own Model](https://nlp.stanford.edu/software/crf-faq.html#a)

To tag named entities utilizing custom trained model: 
    
    (use 'org.clojurenlp.core)
    (def pipeline (initialize-pipeline "path-to-serialized-model"))
    (def text "The United States of America will be tagged as a location")
    (tag-ner pipeline text)
    
Utilizing either NER tagging strategy, a map containing the original text, sentences, tokens, and ner tags will be returned.
    
### Parsing

To parse a sentence:

	(use 'org.clojurenlp.core)
	(parse (tokenize text))

You will get back a LabeledScoredTreeNode which you can plug in to
other Stanford CoreNLP functions or can convert to a standard Treebank
string with:

	(str (parse (tokenize text)))

### Stanford Dependencies

	(dependency-graph "I like cheese.")

will parse the sentence and return the dependency graph as a
[Loom](https://github.com/aysylu/loom) graph, which you can then traverse with
standard graph algorithms like shortest path, etc. You can also view it:

	(def graph (dependency-graph "I like cheese."))
	(use 'loom.io)
	(view graph)

This requires GraphViz to be installed.

## License

© 2018 The ClojureNLP Organization and Contributors

Distributed under the Apache 2.0 License. See LICENSE for details.

## The ClojureNLP Organization
- Leon Talbot @leontalbot
- Andrew McLoud @andrewmcloud

## Contributors
- Cory Giles
- Hans Engel
- Damien Stanton
- Andrew McLoud
- Leon Talbot
- Marek Owsikowski



[![donation](https://img.shields.io/badge/Donate_-to_this_project-green.svg)](https://paypal.me/clojurenlp)
