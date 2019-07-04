package es.upm.oeg.librairy.nlp.annotators.wordnet;

import com.google.common.base.Strings;
import es.upm.oeg.librairy.nlp.data.IndexReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetAnnotator.class);
    private final IndexReader indexReader;

    public WordnetAnnotator(String resourceFolder, String language) {
        indexReader = new IndexReader(Paths.get(resourceFolder,"wordnet",language+".idx").toFile());
        LOG.info( language + " Wordnet load!");
    }

    public List<String> getSynset(String word){
        if (Strings.isNullOrEmpty(word)) return Collections.emptyList();
        return indexReader.get(word);
    }
}
