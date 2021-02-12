(ns org.clojurenlp.annotations
  (:import 
   (edu.stanford.nlp.dcoref 
    CorefCoreAnnotations$CorefChainAnnotation) 
   (edu.stanford.nlp.ling
    CoreAnnotations
    CoreAnnotations$AfterAnnotation
    CoreAnnotations$AuthorAnnotation
    CoreAnnotations$BeforeAnnotation
    CoreAnnotations$CharacterOffsetBeginAnnotation
    CoreAnnotations$CharacterOffsetEndAnnotation
    CoreAnnotations$DocDateAnnotation
    CoreAnnotations$DocIDAnnotation
    CoreAnnotations$DocSourceTypeAnnotation
    CoreAnnotations$DocTypeAnnotation
    CoreAnnotations$IndexAnnotation
    CoreAnnotations$KBPTriplesAnnotation
    CoreAnnotations$LineNumberAnnotation
    CoreAnnotations$LocationAnnotation
    CoreAnnotations$MentionsAnnotation
    CoreAnnotations$NamedEntityTagAnnotation
    CoreAnnotations$NormalizedNamedEntityTagAnnotation
    CoreAnnotations$PartOfSpeechAnnotation
    CoreAnnotations$OriginalTextAnnotation
    CoreAnnotations$QuotationIndexAnnotation
    CoreAnnotations$QuotationsAnnotation
    CoreAnnotations$SectionDateAnnotation
    CoreAnnotations$SectionsAnnotation
    CoreAnnotations$SentencesAnnotation
    CoreAnnotations$SentenceBeginAnnotation
    CoreAnnotations$SentenceEndAnnotation
    CoreAnnotations$SentenceIDAnnotation
    CoreAnnotations$SentenceIndexAnnotation
    CoreAnnotations$SpeakerAnnotation
    CoreAnnotations$TextAnnotation
    CoreAnnotations$TokensAnnotation
    CoreAnnotations$TokenBeginAnnotation
    CoreAnnotations$TokenEndAnnotation
    CoreAnnotations$TrueCaseAnnotation
    CoreAnnotations$TrueCaseTextAnnotation
    CoreAnnotations$WikipediaEntityAnnotation)
   (edu.stanford.nlp.naturalli
    NaturalLogicAnnotations 
    NaturalLogicAnnotations$RelationTriplesAnnotation)
   (edu.stanford.nlp.neural.rnn 
    RNNCoreAnnotations)
   (edu.stanford.nlp.pipeline
    Annotation
    QuoteAnnotator
    QuoteAttributionAnnotator$SpeakerAnnotation)
   (edu.stanford.nlp.semgraph 
    SemanticGraphCoreAnnotations$BasicDependenciesAnnotation
    SemanticGraphCoreAnnotations$EnhancedPlusPlusDependenciesAnnotation
    SemanticGraphCoreAnnotations$EnhancedDependenciesAnnotation)
   (edu.stanford.nlp.sentiment 
    SentimentCoreAnnotations$SentimentAnnotatedTree
    SentimentCoreAnnotations$SentimentClass)
   (edu.stanford.nlp.time TimeAnnotations$TimexAnnotation)
   (edu.stanford.nlp.trees 
    TreeCoreAnnotations$TreeAnnotation)
   (edu.stanford.nlp.util CoreMap)
   (org.ejml.simple SimpleMatrix)))



;;; Text

(defn get-text [ann] (.get ann CoreAnnotations$TextAnnotation))

(defn get-original-text [ann] 
  (.get ann CoreAnnotations$OriginalTextAnnotation))

(defn get-index [ann] 
  (.get ann CoreAnnotations$IndexAnnotation))

(defn get-character-offset-begin [ann] 
  (.get ann CoreAnnotations$CharacterOffsetBeginAnnotation))

(defn get-character-offset-end [ann] 
  (.get ann CoreAnnotations$CharacterOffsetEndAnnotation))

;; pos

(defn get-part-of-speech [ann] 
  (.get ann CoreAnnotations$PartOfSpeechAnnotation))

;;; Tokens

(defn get-tokens [^Annotation ann] 
  (.get ann CoreAnnotations$TokensAnnotation))

(defn get-token-begin [ann]
  (.get ann CoreAnnotations$TokenBeginAnnotation))

(defn get-token-end [ann]
  (.get ann CoreAnnotations$TokenEndAnnotation))

(defn get-speaker [token]
  (.get token CoreAnnotations$SpeakerAnnotation))

(defn get-true-case [token]
  (.get token CoreAnnotations$TrueCaseAnnotation))
    
(defn get-true-case-text [token]
  (.get token CoreAnnotations$TrueCaseTextAnnotation))            
                     ;
(defn get-before [token]
  (.get token CoreAnnotations$BeforeAnnotation))

(defn get-after [token]
  (.get token CoreAnnotations$AfterAnnotation))

                  
;;; Document

(defn get-doc-id [doc] (.get doc CoreAnnotations$DocIDAnnotation))
(defn get-doc-date [doc] (.get doc CoreAnnotations$DocDateAnnotation))
(defn get-doc-source-type [doc] (.get doc CoreAnnotations$DocSourceTypeAnnotation))
(defn get-doc-type [doc] (.get doc CoreAnnotations$DocTypeAnnotation))
(defn get-author [doc] (.get doc CoreAnnotations$AuthorAnnotation))
(defn get-location [doc] (.get doc CoreAnnotations$LocationAnnotation))

;;; Sentences

(defn get-sentences [ann] (.get ann CoreAnnotations$SentencesAnnotation))
#_(defn get-before [sentence] edu.stanford.nlp.ling.CoreAnnotations$BeforeAnnotation)
#_(defn get-after [sentence] CoreAnnotations$AfterAnnotation)

(defn get-sentence-id [sentence] 
  (.get sentence CoreAnnotations$SentenceIDAnnotation))
(defn get-sentence-index [sentence] 
  (.get sentence CoreAnnotations$SentenceIndexAnnotation))
(defn get-line-number [sentence] 
  (.get sentence CoreAnnotations$LineNumberAnnotation))
(defn get-tree [sentence]
  (.get sentence TreeCoreAnnotations$TreeAnnotation)) ; note the "Tree"Core.
(defn get-basic-dependencies [sentence] 
  (.get sentence SemanticGraphCoreAnnotations$BasicDependenciesAnnotation))
(defn get-enhanced-dependencies [sentence]
  (.get sentence SemanticGraphCoreAnnotations$EnhancedDependenciesAnnotation))
(defn get-enhanced-plus-plus-dependencies [sentence]
  (.get sentence SemanticGraphCoreAnnotations$EnhancedPlusPlusDependenciesAnnotation))

;; Sentiment

(defn get-sentiment-class [sentence] 
  (.get sentence SentimentCoreAnnotations$SentimentClass))

(defn get-sentiment-annotated-tree [sentence]
  (.get sentence SentimentCoreAnnotations$SentimentAnnotatedTree))

(defn get-sentiment [sentiment-tree]
  (RNNCoreAnnotations/getPredictedClass sentiment-tree))

#_(defn get-sentiment-predictions [sentiment-tree]
  (RNNCoreAnnotations/getPredictionsAsStringList sentiment-tree))

;; OpenIE
(defn get-relation-triples [sentence] 
  (.get sentence NaturalLogicAnnotations$RelationTriplesAnnotation)) 

;; KBP
(defn get-kbp-triples [sentence] 
  (.get sentence CoreAnnotations$KBPTriplesAnnotation))

;; Entity mentions
(defn get-mentions [sentence] 
  (.get sentence CoreAnnotations$MentionsAnnotation))
(defn get-named-entity-tag [mention]
  (.get mention CoreAnnotations$NamedEntityTagAnnotation))
(defn get-normalized-named-entity-tag [mention]
  (.get mention CoreAnnotations$NormalizedNamedEntityTagAnnotation))
(defn get-wikipedia-entity [mention]
  (.get mention CoreAnnotations$WikipediaEntityAnnotation))
(defn get-time [ann] (.get ann TimeAnnotations$TimexAnnotation))

;;; Quotes

(defn get-quotations [ann] 
  (.get ann CoreAnnotations$QuotationsAnnotation))

(defn gather-quotes [ann] (QuoteAnnotator/gatherQuotes ann))

(defn get-quotation-index [quote] 
  (.get quote CoreAnnotations$QuotationIndexAnnotation))

(defn get-sentence-begin [quote] 
  (.get quote CoreAnnotations$SentenceBeginAnnotation))

(defn get-sentence-end [quote] 
  (.get quote CoreAnnotations$SentenceEndAnnotation))

(defn get-speaker [quote]
  (.get quote QuoteAttributionAnnotator$SpeakerAnnotation))

;; Sections
(defn get-sections [ann] (.get ann CoreAnnotations$SectionsAnnotation))
(defn get-section-date [section] 
  (.get section CoreAnnotations$SectionDateAnnotation))

;; corefs 
(defn get-coref-chain [doc]
 (.get doc CorefCoreAnnotations$CorefChainAnnotation))

