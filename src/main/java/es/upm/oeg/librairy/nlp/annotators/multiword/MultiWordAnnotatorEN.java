package es.upm.oeg.librairy.nlp.annotators.multiword;

import es.upm.oeg.librairy.nlp.annotators.stanford.StanfordAnnotatorEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class MultiWordAnnotatorEN extends StanfordAnnotatorEN {

    private static final Logger LOG = LoggerFactory.getLogger(MultiWordAnnotatorEN.class);

    private final String resourceFolder;

    public MultiWordAnnotatorEN(String resourceFolder) {
        this.resourceFolder = resourceFolder;

        props.put("annotators", "tokenize, ssplit, pos, lemma, jmwe"); //"tokenize, ssplit, pos,

        // jMWE
        //props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe", "es.upm.oeg.librairy.nlp.annotators.multiword.CustomMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "false");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", Paths.get(resourceFolder,"mwe","en","mweindex_wordnet3.0_semcor1.6.txt").toFile().getAbsolutePath());
        props.setProperty("customAnnotatorClass.jmwe.detector", "CompositeConsecutiveProperNouns");

    }

}