package es.upm.oeg.librairy.nlp.service.annotator;

import com.google.common.base.Strings;
import es.upm.oeg.librairy.nlp.annotators.opennlp.OpenNLPAnnotator;
import es.upm.oeg.librairy.nlp.annotators.opennlp.OpenNLPAnnotatorPT;
import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotator;
import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorEN;
import es.upm.oeg.librairy.nlp.annotators.wordnet.WordnetAnnotatorPT;
import es.upm.oeg.librairy.nlp.error.LanguageNotFoundException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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
public class OpenNLPService implements AnnotatorService {

    private static final Logger LOG = LoggerFactory.getLogger(OpenNLPService.class);


    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    private String lang;

    private Boolean multigrams;

    private OpenNLPAnnotator annotator;

    private WordnetAnnotator wordnetAnnotator;



    public OpenNLPService(String resourceFolder, String lang, Boolean multigrams) throws Exception {
        this.resourceFolder = resourceFolder;
        this.lang = lang.toLowerCase();
        this.multigrams = multigrams;
        switch (lang.toLowerCase()){
            case "pt":
                annotator = new OpenNLPAnnotatorPT(resourceFolder);
                wordnetAnnotator = new WordnetAnnotatorPT(resourceFolder);
                break;
            default: throw new LanguageNotFoundException(this.lang);
        }
    }

    public String tokens(String text, List<PoS> filter, Form form)  {

        return annotate(text,filter).stream()
                .map(annotation-> {
                    Token token = annotation.getToken();
                    switch (form){
                        case LEMMA: return Strings.isNullOrEmpty(token.getLemma())? token.getTarget() : token.getLemma();
                        default: return token.getTarget();
                    }
                })
                .collect(Collectors.joining(" "));
    }


    public List<Annotation> annotations(String text, List<PoS> filter, Boolean synsets) {
        List<Annotation> annotations = annotate(text, filter);
        return annotations.stream()
                .map(annotation -> {
                    if (synsets){
                        Token token = annotation.getToken();
                        List<String> val = wordnetAnnotator.getSynset(token.getLemma());
                        if (val.isEmpty()) val = wordnetAnnotator.getSynset(token.getTarget());
                        annotation.setSynset(val);
                    }

                    return annotation;
                })
                .collect(Collectors.toList());
    }

    private List<Annotation> annotate(String text, List<PoS> filter){
        List<Annotation> annotations = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(\\.|.$)",Pattern.MULTILINE).matcher(text);
        long offset = 0l;
        while (matcher.find()){
            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            List<Token> tokens = annotator.tokenize(text, multigrams);
            for(Token token: tokens){
                offset++;
                if (!filter.isEmpty() && !filter.contains(token.getPos())){
                    continue;
                }
                Annotation annotation = new Annotation();
                annotation.setToken(token);
                annotation.setOffset(offset);
                annotations.add(annotation);
            }
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated by OpenNLP in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs '" + partialContent + "'");

        }

        return annotations;
    }
}
