# corenlp

Current build: [![CircleCI](https://circleci.com/gh/damienstanton/stanford-corenlp/tree/master.svg?style=svg&circle-token=673be35e60d99ec7be891d6da03b9865a48cb906)](https://circleci.com/gh/damienstanton/stanford-corenlp/tree/master)

Natural language processing in Clojure/ClojureScript based on the Stanford-CoreNLP parser.

**Warning:** Under heavy rewrite. Please refrain from trying to use this until it is complete!

## Usage

### Tokenization

    (use 'corenlp)
    (def text "This is a simple sentence.")
    (tokenize text)

### Part-of-Speech Tagging

    (use 'corenlp)
    (pos-tag (tokenize "Colorless green ideas sleep furiously."))
    ;; => [#<TaggedWord Colorless/JJ> #<TaggedWord green/JJ> ...]

Returns a list of `TaggedWord` objects. Call `.tag()` on a `TaggedWord` instance
to get its tag. For more information, see the [relevant Javadoc](http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ling/TaggedWord.html)
### Named Entity Recognition

To tag named entities utilizing standard Stanford NER model:

    (use 'corenlp)
    (def text "The United States of America will be tagged as a location")
    (tag-ner text)

Training your own model [How to Train Your Own Model](https://nlp.stanford.edu/software/crf-faq.html#a)

To tag named entities utilizing custom trained model: 
    
    (use 'corenlp)
    (def text "The United States of America will be tagged as a location")
    (tag-ner text path-to-serialized-model)
    
Utilizing either NER tagging strategy, a map containing the original text, sentences, tokens, and ner tags will be returned.
    
### Parsing

To parse a sentence:

	(use 'corenlp)
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

Copyright (C) 2011-2016 Contributors (Clojure code only)

Distributed under the Eclipse Public License, the same as Clojure.

## Contributors

- Cory Giles
- Hans Engel
- Damien Stanton
