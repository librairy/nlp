package es.upm.oeg.librairy.nlp.annotators.opennlp;

import org.librairy.service.nlp.facade.model.Token;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface OpenNLPAnnotator {

    List<Token> tokenize(String text, Boolean multigrams);
}
