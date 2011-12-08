# stanford-parser

Clojure wrapper for Stanford CoreNLP tools.  Currently very incomplete, only wraps the parser and dependencies.

## Usage

### Parsing

To parse a sentence:

	(use stanford-corenlp.parser)
	(parse "I like cheese.")

Or if already tokenized:

	(parse ["I" "like" "cheese" "."])

You will get back a LabeledScoredTreeNode which you can plug in to
other Stanford CoreNLP functions or can convert to a standard Treebank
string with:

	(str (parse "I like cheese."))

### Stanford Dependencies

	(use 'stanford-corenlp.dependencies)
	(dependency-graph "I like cheese.")

will parse the sentence and return the dependency graph as a
[loom](https://github.com/jkk/loom) graph, which you can then traverse
with standard graph algorithms like shortest path, etc. You can also view it:

	(def dg (dependency-graph "I like cheese."))
	(use 'loom.io)
	(view dg)

This requires GraphViz to be installed.

## License

Copyright (C) 2011 Cory Giles (Clojure code only)

Distributed under the Eclipse Public License, the same as Clojure.
