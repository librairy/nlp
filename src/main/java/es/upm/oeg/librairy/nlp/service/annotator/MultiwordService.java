package es.upm.oeg.librairy.nlp.service.annotator;

import es.upm.oeg.librairy.nlp.annotators.multiword.MultiWordAnnotatorEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class MultiwordService extends CoreNLPService{

    private static final Logger LOG = LoggerFactory.getLogger(MultiwordService.class);

    private final String lang;

    public MultiwordService(String resourceFolder, String lang) {
        super(lang.toLowerCase(),resourceFolder);
        this.lang = lang.toLowerCase();
        stanfordAnnotator =  new MultiWordAnnotatorEN(resourceFolder);
    }

}
