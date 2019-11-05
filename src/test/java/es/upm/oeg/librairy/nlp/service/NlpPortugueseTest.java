package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.service.annotator.CoreNLPService;
import es.upm.oeg.librairy.nlp.service.annotator.IXAService;
import es.upm.oeg.librairy.nlp.service.annotator.PortugueseService;
import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class NlpPortugueseTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpPortugueseTest.class);
    private PortugueseService service;

    @Before
    public void setup() throws Exception {

        String resourceFolder = "src/main/bin";
        service      = new PortugueseService(resourceFolder);
    }



    @Test
    public void annotation() throws IOException {

        String text = "este é um exemplo";

        List<PoS> filter = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADJECTIVE});

        List<Annotation> annotations2 = service.annotations(text, filter, true);
        annotations2.forEach(annotation -> System.out.println("Annotation: " + annotation));


    }

    @Test
    public void tokens() throws IOException {

        String text = "este é um exemplo";

        List<PoS> filter = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADJECTIVE});

        String tokens = service.tokens(text, filter, Form.LEMMA);
        System.out.println(tokens);


    }

}
