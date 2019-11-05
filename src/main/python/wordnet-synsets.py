#!/usr/share/python
# -*- encoding: utf-8 -*-
#
# Extract synset-word pairs from the Princeton Wordnet
# Replace '_' with ' '

import nltk, codecs
nltk.download('wordnet')
nltk.download('omw')
from nltk.corpus import wordnet as w

wnname = "Open MultiLingual WordNet"
wnurl = "http://wordnet.princeton.edu/"
wnlicense = "wordnet"
languages = ["eng","spa","fra","ita","por"]
for wnlang in languages:
    outfile = "wn-data-%s.tab" % wnlang
    o = codecs.open(outfile, "w", "utf-8" )
    log = codecs.open("log",  "w", "utf-8")

    o.write("# %s\t%s\t%s\t%s \n" % (wnname, wnlang, wnurl, wnlicense))

    for s in w.all_synsets():
        #for l in s.lemmas():
        for l in s.lemma_names(wnlang):
            synset = str(s)[8:-2]
            o.write("%s\t%s\n" %  (synset,l.replace('_',' ')))
