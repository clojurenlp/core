# corenlp

**Update July 2014 - This project is abandoned. I no longer use Clojure or do much NLP, so I have little incentive to maintain this code. If someone is interested in forking or taking maintainership of this project, please contact me and I will be happy to answer any questions you have, time permitting.**

Clojure wrapper for Stanford CoreNLP tools.  Currently very incomplete, only
wraps the tokenizer, parser and dependencies.

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
to get its tag.

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

Copyright (C) 2011-2014 Contributors (Clojure code only)

Distributed under the Eclipse Public License, the same as Clojure.

## Contributors

- Cory Giles 
- Hans Engel
