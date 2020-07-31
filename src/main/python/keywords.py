import spacy
import pytextrank
import pysolr
from langdetect import detect

class KeywordsProcessor:

    def get_language(self, text):
        try:
            lang = detect(text)
            return lang
        except:
            return "en"

    def get_keywords(self, text):
        lang = self.get_language(text)
        model = lang +"_core_news_sm"
        print("loading model: '" + model + "'")
        nlp = spacy.load(lang)
        tr = pytextrank.TextRank()
        nlp.add_pipe(tr.PipelineComponent, name="textrank", last=True)
        doc = nlp(text)
        key_words=[]
        for p in doc._.phrases:
            print("{:.4f} {:5d}  {}".format(p.rank, p.count, p.text))
            key_words.append(p.text)
        return key_words[:10]
