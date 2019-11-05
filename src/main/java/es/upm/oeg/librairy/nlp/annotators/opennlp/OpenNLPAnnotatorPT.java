package es.upm.oeg.librairy.nlp.annotators.opennlp;

import es.upm.oeg.librairy.nlp.annotators.nlpport.LemmatizeException;
import es.upm.oeg.librairy.nlp.annotators.nlpport.Lemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class OpenNLPAnnotatorPT implements OpenNLPAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenNLPAnnotatorPT.class);
    private final Lemmatizer lemmatizer;
    private TokenizerME tokenizer;
    private SentenceDetectorME sentenceDetector;
    private NameFinderME nameFinder;
    private POSTaggerME tagger;


    public OpenNLPAnnotatorPT(String resourceFolder) throws Exception {
        String tokenModel       = Paths.get(resourceFolder, "opennlp-models", "pt", "pt-token.bin").toFile().getAbsolutePath();
        String sentModel        = Paths.get(resourceFolder, "opennlp-models", "pt", "pt-sent.bin").toFile().getAbsolutePath();
        String nerModel         = Paths.get(resourceFolder, "opennlp-models", "pt", "pt-ner.bin").toFile().getAbsolutePath();
        String posModel         = Paths.get(resourceFolder, "opennlp-models", "pt", "pt-pos-maxent.bin").toFile().getAbsolutePath();

        try (InputStream modelIn = new FileInputStream(tokenModel)) {
            TokenizerModel model = new TokenizerModel(modelIn);
            this.tokenizer = new TokenizerME(model);
        }
        try (InputStream modelIn = new FileInputStream(sentModel)) {
            SentenceModel model = new SentenceModel(modelIn);
            this.sentenceDetector = new SentenceDetectorME(model);
        }

        try (InputStream modelIn = new FileInputStream(nerModel)){
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            this.nameFinder = new NameFinderME(model);
        }

        try (InputStream modelIn = new FileInputStream(posModel)){
            POSModel model  = new POSModel(modelIn);
            this.tagger     = new POSTaggerME(model);
        }

        this.lemmatizer = new Lemmatizer();


    }

    @Override
    public List<Token> tokenize(String text, Boolean multigrams) {
        List<Token> tokenList = new ArrayList<>();
        for(String sentence: sentences(text)){

            String[] tokens = tokens(sentence);
            if (multigrams){
                List<Span> entities = entities(tokens);
                for(Span span: entities){
                    LOG.debug("span: " + span);
                    //TODO merge tokens by span
                }
            }
            String[] tags = tags(tokens);
            String[] lemmas = lemma(tokens, tags);

            for(int i=0;i<tokens.length;i++){
                Token token = new Token();
                token.setTarget(tokens[i]);
                token.setMorphoFeat("");
                token.setType("");
                token.setLemma(lemmas[i]);
                token.setPos(OpenNLPPoSTranslator.toPoSTag(tags[i]));
                tokenList.add(token);
                //TODO add offset
            }

        }
        return tokenList;
    }

    public String[] tokens(String sentence){
        return this.tokenizer.tokenize(sentence);
    }

    public List<String> sentences(String text) {
        return Arrays.asList(this.sentenceDetector.sentDetect(text));
    }

    public List<Span> entities(String[] tokens) {
        return Arrays.asList(this.nameFinder.find(tokens));
    }

    public String[] tags(String[] tokens) {
        return this.tagger.tag(tokens);
    }

    public String[] lemma(String[] tokens, String[] tags){
        try {
            return this.lemmatizer.lemmatize(tokens, tags);
        } catch (LemmatizeException e) {
            LOG.error("Error on pt lemmatizer",e);
            return new String[]{};
        }
    }
}
