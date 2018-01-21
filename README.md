# org.clojurenlp.core

[![Clojars Project](https://img.shields.io/clojars/v/org.clojurenlp/core.svg)](https://clojars.org/org.clojurenlp/core)
[![Build Status](https://travis-ci.org/clojurenlp/core.svg?branch=master)](https://travis-ci.org/clojurenlp/core)

Natural language processing in Clojure based on the Stanford-CoreNLP parser.

**NOTE** a work in progress, currently in the POC phase.

## Usage

### Tokenization

    (use 'org.clojurenlp.core)
    (def text "This is a simple sentence.")
    (tokenize text)

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
[loom](https://github.com/jkk/loom) graph, which you can then traverse with
standard graph algorithms like shortest path, etc. You can also view it:

	(def graph (dependency-graph "I like cheese."))
	(use 'loom.io)
	(view graph)

This requires GraphViz to be installed.

## License

© 2018 The ClojureNLP Organization and Contributors

Distributed under the Apache 2.0 License. See LICENSE for details.

## The ClojureNLP Organization
- Damien Stanton
- Leon Talbot
- Marek Owsikowski

## Contributors
- Cory Giles
- Hans Engel
- Damien Stanton
- Andrew McLoud
- Leon Talbot
- Marek Owsikowski



[![donation](https://img.shields.io/badge/Donate_-to_this_project-green.svg)](https://paypal.me/damienstanton)
