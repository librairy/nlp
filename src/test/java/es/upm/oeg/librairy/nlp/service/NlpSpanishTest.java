package es.upm.oeg.librairy.nlp.service;

import es.upm.oeg.librairy.nlp.service.annotator.CoreNLPService;
import es.upm.oeg.librairy.nlp.service.annotator.IXAService;
import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class NlpSpanishTest {

    private static final Logger LOG = LoggerFactory.getLogger(NlpSpanishTest.class);

    CoreNLPService coreNLPService;
    private IXAService ixaService;

    @Before
    public void setup(){

        String resourceFolder = "src/main/bin";
        ixaService      = new IXAService(resourceFolder,"es",false);
    }



    @Test
    public void IXAannotation() throws IOException {

        //String text = "Los libros fueron recogidos la semana pasada.";

        String text = "Esto es un ejemplo.";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations2 = ixaService.annotations(text, filter, false);
        annotations2.forEach(annotation -> System.out.println("Annotation2: " + annotation));


    }

}
