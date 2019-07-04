package es.upm.oeg.librairy.nlp.annotators.wordnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetAnnotatorES extends WordnetAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetAnnotatorES.class);

    public WordnetAnnotatorES(String resourceFolder) {
        super(resourceFolder, "spanish");
    }
}
