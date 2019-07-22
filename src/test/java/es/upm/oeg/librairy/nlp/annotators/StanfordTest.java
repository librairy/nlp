package es.upm.oeg.librairy.nlp.annotators;

import edu.stanford.nlp.pipeline.Annotation;
import es.upm.oeg.librairy.nlp.annotators.stanford.StanfordAnnotatorES;
import es.upm.oeg.librairy.nlp.annotators.stanford.StanfordAnnotatorFR;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class StanfordTest {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordTest.class);


    public void fr(){
        StanfordAnnotatorFR annotator = new StanfordAnnotatorFR();

        String text = "reseaux telecommunications";

        Annotation result = annotator.annotate(text);
        LOG.info("Result: " + result);

        List<org.librairy.service.nlp.facade.model.Annotation> tokens = annotator.tokenize(result);

        String lemmas = tokens.stream().map(a -> a.getToken().getLemma()).collect(Collectors.joining(" "));

        String successResult = "reseau telecommunication";

        assertEquals(successResult, lemmas);


    }
}
