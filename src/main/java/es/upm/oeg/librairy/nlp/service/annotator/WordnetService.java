package es.upm.oeg.librairy.nlp.service.annotator;

import es.upm.oeg.librairy.nlp.annotators.wordnet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class WordnetService {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetService.class);
    private final String lang;

    protected WordnetAnnotator annotator;

    public WordnetService(String resourceFolder, String lang) {
        this.lang = lang.toLowerCase();
        switch (this.lang){
            case "en":
                annotator = new WordnetAnnotatorEN(resourceFolder);
                break;
            case "es":
                annotator = new WordnetAnnotatorES(resourceFolder);
                break;
            case "de":
                annotator = new WordnetAnnotatorDE(resourceFolder);
                break;
            case "fr":
                annotator = new WordnetAnnotatorFR(resourceFolder);
                break;
            case "it":
                annotator = new WordnetAnnotatorIT(resourceFolder);
                break;
        }
    }

    public List<String> getSynsets(String word) {
        return annotator.getSynset(word);
    }
}
