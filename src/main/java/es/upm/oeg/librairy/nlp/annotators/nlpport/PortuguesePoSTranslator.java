package es.upm.oeg.librairy.nlp.annotators.nlpport;

import org.apache.commons.lang.StringUtils;
import org.librairy.service.nlp.facade.model.PoS;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class PortuguesePoSTranslator {

    /**
     * adj: Adjective
     * adv: Adverb
     * art: Article
     * n: Noun
     * num: Numeral
     * pron: Pronoun
     * prop: ProperNoun
     * prp: Preposition
     * punc: Punctuation
     * v: Verb
     * @param termPoS
     * @return
     */
    public static PoS toPoSTag(String termPoS){
        String key = StringUtils.substringBefore(termPoS, "-");
        switch (key.toUpperCase()){
            case "ADJ":     return PoS.ADJECTIVE;
            case "ADV":     return PoS.ADVERB;
            case "ART":     return PoS.ARTICLE;
            case "N":     return PoS.NOUN;
            case "PRON":     return PoS.PRONOUN;
            case "PROP":     return PoS.PROPER_NOUN;
            case "PRP":     return PoS.PREPOSITION;
            case "V":     return PoS.VERB;
            case "NUM":     return PoS.NUMBER;
            case "PUNC":     return PoS.PUNCTUATION_MARK;
            default:      throw new RuntimeException("Term PoS not handled: " + termPoS);
        }
    }

    public static List<String> toTermPoS(PoS type){
        switch (type){
            case ADJECTIVE:     return Arrays.asList(new String[]{"ADJ"});
            case ADVERB:        return Arrays.asList(new String[]{"ADV"});
            case ARTICLE:       return Arrays.asList(new String[]{"ART"});
            case NOUN:          return Arrays.asList(new String[]{"N"});
            case PRONOUN:       return Arrays.asList(new String[]{"PRON"});
            case PROPER_NOUN:   return Arrays.asList(new String[]{"PROP"});
            case PREPOSITION:   return Arrays.asList(new String[]{"PRP"});
            case VERB:          return Arrays.asList(new String[]{"V"});
            case NUMBER:          return Arrays.asList(new String[]{"NUM"});
            case PUNCTUATION_MARK:return Arrays.asList(new String[]{"PUNC"});
            default:            return Arrays.asList(new String[]{});
        }
    }
}
