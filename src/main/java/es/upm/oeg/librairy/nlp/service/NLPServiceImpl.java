package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.CharMatcher;
import es.upm.oeg.librairy.nlp.service.annotator.AnnotatorService;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.Group;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.librairy.service.nlp.facade.model.PoS.ADVERB;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class NLPServiceImpl implements org.librairy.service.nlp.facade.model.NlpService {

    private static final Logger LOG = LoggerFactory.getLogger(NLPServiceImpl.class);

    @Autowired
    ServiceManager serviceManager;

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public String tokens(String text, List<PoS> filter, Form form, boolean multigrams, String lang) throws AvroRemoteException {
        List<Annotation> annotations = annotate(text, filter, multigrams, false, false, lang);

        return annotations.stream().map(a -> form.equals(Form.LEMMA)? a.getToken().getLemma() : a.getToken().getTarget().contains(" ")? a.getToken().getLemma() : a.getToken().getTarget()).collect(Collectors.joining(" "));
    }

    @Override
    public List<Annotation> annotations(String text, List<PoS> filter, Boolean multigrams, Boolean references, Boolean synsets, String lang) throws AvroRemoteException {

        List<Annotation> annotations = annotate(text, filter, multigrams, references, synsets, lang);

        return annotations.stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.toList());
    }

    // MODIFIED
    @Override
    public List<Group> groups(String text, List<PoS> filter, Boolean multigrams, Boolean references, Boolean synsets, String lang) throws AvroRemoteException {

        List<Annotation> annotations = annotate(text, filter, multigrams==null?false:multigrams, references==null?false:references, synsets==null?false:synsets, lang);

        Map<Annotation, Long> grouped = annotations.stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        List<Group> processedGroups = grouped.entrySet().stream().map( entry -> {
            org.librairy.service.nlp.facade.model.Annotation annotation = entry.getKey();
            Group group = new Group();
            group.setToken(annotation.getToken().getLemma());
            group.setUri(annotation.getUri());
            group.setPos(annotation.getToken().getPos());
            group.setFreq(entry.getValue());
            return group;
        }).sorted((a,b) -> -a.getFreq().compareTo(b.getFreq())).collect(Collectors.toList());

        if (text != null) {
            // Checking if the text contains not recognized adverbs
            // If it does, returns exactly what adverbs of a list is not recognizing
            String[] arrayOfNotRecognizedAdverbs = getArrayOfNotRecognizedAdverbs(text);

            // If it doesn't return any adverb, finish the process
            if (arrayOfNotRecognizedAdverbs.length > 0) {
                // Checking if the AI has detected anything
                if (!processedGroups.isEmpty()) {
                    // Checking if each of the 'not recognized adverb' is not a false positive
                    // If it is, it will appear as a token inside a group

                    // Initialize a HashMap of all not-recognized-adverbs
                    HashMap<String, Boolean> mapOfFalsePositives = new HashMap<String, Boolean>();

                    // Initialize each of them as 'not false positive' for the moment
                    for (String notRecognizedAdverb : arrayOfNotRecognizedAdverbs) {
                        mapOfFalsePositives.put(notRecognizedAdverb, false);
                    }

                    // Checking each group to identify the false positives
                    processedGroups.forEach(group -> {
                        for (String notRecognizedAdverb : arrayOfNotRecognizedAdverbs) {
                            // If the name of the token of a group is the same as the name of one of the not-recognized
                            // adverbs, we've encountered a false positive
                            if (group.getToken().equals(notRecognizedAdverb)) {
                                // Put it as 'false positive' in the HashMap
                                mapOfFalsePositives.put(notRecognizedAdverb, true);

                                // Check if the frequency of appearence is correct
                                if (!group.getFreq().equals((long) getFrequency(notRecognizedAdverb, arrayOfNotRecognizedAdverbs))) {
                                    // If it is not, overwrite it
                                    group.setFreq((long) getFrequency(notRecognizedAdverb, arrayOfNotRecognizedAdverbs));
                                }
                            }
                        }
                    });

                    // Now, for each one of the not-recognized-adverbs, taking in consideration the false positives
                    // detected before...
                    mapOfFalsePositives.forEach((key, value) -> {
                        // If it is not a false positive
                        if (!value) {
                            // Add a new group with its name as token, correct PoS and frequency
                            processedGroups.add(new Group(key, ADVERB, null, (long) getFrequency(key, arrayOfNotRecognizedAdverbs)));
                        }
                    });
                } else {
                    // Happily, we don't have to check for false positives
                    // For each one of the not-recognized-adverbs
                    for (String notRecognizedAdverb : arrayOfNotRecognizedAdverbs) {
                        // Add a new group with its name as token, correct PoS and frequency
                        processedGroups.add(new Group(notRecognizedAdverb, ADVERB, null, (long) getFrequency(notRecognizedAdverb, arrayOfNotRecognizedAdverbs)));
                    }
                }
            }
        }

        return processedGroups;
    }

    // NEW
    private int getFrequency(String adverb, String[] arrayOfAdverbsWithPossibleDuplicates) {
        int frequency = 0;
        for (int i = 0; i < arrayOfAdverbsWithPossibleDuplicates.length; i++) {
            if (arrayOfAdverbsWithPossibleDuplicates[i].equals(adverb)) {
                frequency += 1;
            }
        }
        return frequency;
    }

    // NEW
    private String[] getArrayOfNotRecognizedAdverbs(String text) {
        String [] notRecognizedAdverbs = {"atinadamente", "completamente", "consecuentemente", "convencionalmente", "corrientemente", "cortésmente", "cronológicamente", "eficazmente", "exclusivamente", "experimentalmente", "extremadamente", "frecuentemente", "generalmente", "grandemente", "horizontalmente", "idiomáticamente", "igualmente", "incesantemente", "incidentalmente", "indeleblemente", "indelicadamente", "indirectamente", "indisolublemente", "indudablemente", "ingenuamente", "insistentemente", "instantáneamente", "internamente", "inútilmente", "irregularmente", "irresistiblemente", "lealmente", "libremente", "longitudinalmente", "meramente", "normalmente", "ortográficamente", "parcialmente", "patrióticamente", "precisamente", "preferentemente", "propiamente", "recientemente", "rígidamente", "rudamente", "sintácticamente", "socialmente", "suficientemente", "totalmente"};
        int textLength = text.length();
        int notRecognizedAdverbsLength = 0;
        boolean foundItOnce = false;
        ArrayList<String> notRecognizedAdverbsInText = new ArrayList<String>();
        int numberOfNotRecognizedAdverbsInText = 0;
        int countOfRepeatedElements = 0;
        for (String notRecognizedAdverb : notRecognizedAdverbs) {
            notRecognizedAdverbsLength = notRecognizedAdverb.length();
            foundItOnce = false;
            countOfRepeatedElements = 0;
            for (int i = 0; i <= (textLength - notRecognizedAdverbsLength); i++) {
                if (text.regionMatches(i, notRecognizedAdverb, 0, notRecognizedAdverbsLength)) {
                    foundItOnce = true;
                    notRecognizedAdverbsInText.add(text.substring(i, i + notRecognizedAdverbsLength));
                    countOfRepeatedElements += 1;
                }
            }
            if (foundItOnce)
                numberOfNotRecognizedAdverbsInText += countOfRepeatedElements;
        }

        String [] notRecognizedAdverbsInTextStringArray = new String[numberOfNotRecognizedAdverbsInText];

        for (int i = 0; i < numberOfNotRecognizedAdverbsInText; i++) {
            notRecognizedAdverbsInTextStringArray[i] = notRecognizedAdverbsInText.get(i);
        }

        return notRecognizedAdverbsInTextStringArray;
    }

    private List<Annotation> annotate(String text, List<PoS> filter, Boolean multigrams, Boolean references, Boolean synsets, String lang) throws AvroRemoteException {

        Thread thread = Thread.currentThread();

        AnnotatorService annotator = serviceManager.getAnnotator(thread, lang, multigrams ==null? false:multigrams, references==null?false:references);

        List<Annotation> annotations = annotator.annotations(text,filter, synsets==null?false:synsets);

        return annotations;
    }
}