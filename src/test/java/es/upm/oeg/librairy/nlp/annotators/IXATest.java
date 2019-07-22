package es.upm.oeg.librairy.nlp.annotators;

import edu.stanford.nlp.pipeline.Annotation;
import es.upm.oeg.librairy.nlp.annotators.ixa.IXAAnnotatorFR;
import es.upm.oeg.librairy.nlp.annotators.stanford.StanfordAnnotatorFR;
import es.upm.oeg.librairy.nlp.service.annotator.IXAService;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class IXATest {

    private static final Logger LOG = LoggerFactory.getLogger(IXATest.class);


    @Test
    public void fr(){


        IXAService ixaService = new IXAService("src/main/bin","fr",false);

        String text = "reseaux telecommunications";

        String tokens = ixaService.tokens(text, Collections.emptyList(), Form.LEMMA);

        String successResult = "reseau telecommunication";

        assertEquals(successResult, tokens);


    }
}
