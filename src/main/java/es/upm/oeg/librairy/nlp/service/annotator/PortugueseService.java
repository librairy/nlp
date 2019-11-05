package es.upm.oeg.librairy.nlp.service.annotator;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import dictionary.DictionaryLoadException;
import es.upm.oeg.librairy.nlp.annotators.ixa.IXAPoSTranslator;
import es.upm.oeg.librairy.nlp.annotators.nlpport.PortuguesePoSTranslator;
import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorEN;
import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorPT;
import ixa.kaflib.Span;
import ixa.kaflib.Term;
import ixa.kaflib.WF;
import lemma.LemmatizeException;
import lemma.Lemmatizer;
import org.apache.commons.lang.StringUtils;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pos.POSTagger;
import rank.WordRankingLoadException;
import split.SentenceSplitter;
import token.Tokenizer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class PortugueseService implements AnnotatorService {

    private static final Logger LOG = LoggerFactory.getLogger(PortugueseService.class);
    private final Tokenizer tokenizer;
    private final POSTagger tagger;
    private final SentenceSplitter splitter;
    private final Lemmatizer lemmatizer;
    private final WordnetAnnotatorPT wordnetAnnotator;

    public PortugueseService(String resourceFolder) throws Exception {
        LOG.info("preparing portuguese lemmatizer..");
        this.tokenizer = new Tokenizer();
        this.tagger = new POSTagger();
        this.splitter = new SentenceSplitter();
        this.lemmatizer = new Lemmatizer();
        this.wordnetAnnotator = new WordnetAnnotatorPT(resourceFolder);
        LOG.info("Portuguese Annotator ready");
    }

    @Override
    public String tokens(String text, List<PoS> filter, Form form) {
        return analyze(text,filter,false).stream().map( a -> (form.equals(Form.LEMMA))? a.getToken().getLemma() : a.getToken().getTarget()).collect(Collectors.joining(" "));
    }

    @Override
    public List<Annotation> annotations(String text, List<PoS> filter, Boolean synsets) {
        List<Annotation> annotations = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)",Pattern.MULTILINE).matcher(text);
        int groupIndex = 0;
        while (matcher.find()){
            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            List<Annotation> partialAnnotation = analyze(partialContent, filter, synsets);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated by NLPPort in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs '" + partialContent + "'");

            annotations.addAll(partialAnnotation);
            groupIndex++;
        }
        return annotations;
    }

    private List<Annotation> analyze(String text, List<PoS> filter, Boolean synsets){

        List<Annotation> annotations = new ArrayList<>();

        String[] sentences = splitter.split(text);
        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence, true);
            String[] tags = tagger.tag(tokens);
            String[] lemmas = new String[0];
            try {
                lemmas = lemmatizer.lemmatize(tokens, tags);

                for(int i=0;i<tokens.length;i++){

                    if (!filter.isEmpty()){
                        String pos = StringUtils.substringBefore(tags[i].toUpperCase(), "-");
                        if (!filter.contains(PortuguesePoSTranslator.toPoSTag(pos))) continue;
                    }

                    Token token = new Token();
                    token.setTarget(tokens[i]);
                    token.setLemma(!Strings.isNullOrEmpty(lemmas[i])?lemmas[i]:"");

                    if ((CharMatcher.javaLetter().matchesAllOf(tokens[i])) && CharMatcher.javaDigit().matchesAllOf(lemmas[i])){
                        // special case:
                        // target = first
                        // lemma = 1
                        token.setLemma(tokens[i]);
                    }

                    token.setMorphoFeat("");
                    token.setPos(!Strings.isNullOrEmpty(tags[i])? PortuguesePoSTranslator.toPoSTag(tags[i]):PoS.SYMBOL);
                    token.setType("");

                    Annotation annotation = new Annotation();
                    annotation.setToken(token);
                    annotation.setOffset(0l);

                    if (synsets){
                        List<String> val = wordnetAnnotator.getSynset(token.getLemma());
                        if (val.isEmpty()) val = wordnetAnnotator.getSynset(token.getTarget());
                        annotation.setSynset(val);
                    }

                    annotations.add(annotation);

                }
            } catch (LemmatizeException e) {
                LOG.error("Lemmatizer error",e);
            }

        }

        return annotations;
    }
}
