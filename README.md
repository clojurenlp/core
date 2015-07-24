# corenlp

Clojure wrapper for Stanford CoreNLP tools.  Currently very incomplete, only
wraps the tokenizer, parser and dependencies. Pull requests are welcome on the dev branch.

[![Circle CI](https://circleci.com/gh/damienstanton/stanford-corenlp.svg?style=shield)](https://circleci.com/gh/damienstanton/stanford-corenlp)


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

Copyright (C) 2011-2015 Contributors (Clojure code only)

Distributed under the Eclipse Public License, the same as Clojure.

## Contributors

- Cory Giles 
- Hans Engel
- Damien Stanton
