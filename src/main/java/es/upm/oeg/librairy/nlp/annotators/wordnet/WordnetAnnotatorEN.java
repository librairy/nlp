package es.upm.oeg.librairy.nlp.annotators.wordnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetAnnotatorEN extends WordnetAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetAnnotatorEN.class);

    public WordnetAnnotatorEN(String resourceFolder) {
        super(resourceFolder, "english");
    }
}
