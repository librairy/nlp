package es.upm.oeg.librairy.nlp.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class LanguageNotFoundException  extends RuntimeException{

    public LanguageNotFoundException(String msg){
        super(msg);
    }
}
