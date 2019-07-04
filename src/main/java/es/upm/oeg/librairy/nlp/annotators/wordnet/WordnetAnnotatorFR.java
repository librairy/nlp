package es.upm.oeg.librairy.nlp.annotators.wordnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetAnnotatorFR extends WordnetAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetAnnotatorFR.class);

    public WordnetAnnotatorFR(String resourceFolder) {
        super(resourceFolder, "french");
    }
}
