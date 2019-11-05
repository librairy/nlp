package es.upm.oeg.librairy.nlp.annotators.wordnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetAnnotatorPT extends WordnetAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetAnnotatorPT.class);

    public WordnetAnnotatorPT(String resourceFolder) {
        super(resourceFolder, "portuguese");
    }
}
