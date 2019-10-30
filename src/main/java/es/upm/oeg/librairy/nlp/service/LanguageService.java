package es.upm.oeg.librairy.nlp.service;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.BuiltInLanguages;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class LanguageService {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageService.class);

    private static final String DEFAULT_LANG = "en";

    private LanguageDetector languageDetector;
    private TextObjectFactory textObjectFactory;

    private List<String> availableLangs = Arrays.asList(new String[]{"en","es","fr","de","it"});

    @PostConstruct
    public void setup() throws IOException {
        //load all languages:
        LanguageProfileReader langReader = new LanguageProfileReader();

        List<LanguageProfile> languageProfiles = new ArrayList<>();

        Iterator it = BuiltInLanguages.getLanguages().iterator();

        while(it.hasNext()) {
            LdLocale locale = (LdLocale)it.next();
            if (availableLangs.contains(locale.getLanguage())){
                languageProfiles.add(langReader.readBuiltIn(locale));
                LOG.info("language added: " + locale);
            }
        }


        //build language detector:
        this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .minimalConfidence(0.75)
                .withProfiles(languageProfiles)
                .build();

        //create a text object factory
        this.textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    }


    public String getLanguage(String text){
        if (Strings.isNullOrEmpty(text)) return DEFAULT_LANG;

        TextObject textObject   = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        return (!lang.isPresent())? "unknown" : lang.get().getLanguage();
    }


    public static void main(String[] args) throws IOException {
        LanguageService service = new LanguageService();
        service.setup();

        String txt = "Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 1 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 2 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 3 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 4 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 5 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 6 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 7 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 8 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 9 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 10 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 11 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 12 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 13 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 14 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 15 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 16 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 17 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 18 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 19 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 20 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 21 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 22 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 23 do SIWZ .   Przedmiot zamowienia nalezy zrealizowac w oparciu o zalozenia okreslone w opisie przedmiotu zamowienia zawartym w Specyfikacji asortymentowo-cenowej stanowiacej zalacznik nr 2 . 24 do SIWZ ";

        LOG.info(service.getLanguage(txt));
    }

}
