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

    private List<String> availableLangs = Arrays.asList(new String[]{"en","es","fr","de","it","pt"});

    @PostConstruct
    public void setup() throws IOException {
        //load all languages:
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

        this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                //.minimalConfidence(0.9)
                .withProfiles(languageProfiles)
                .build();


        //create a text object factory
        this.textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    }


    public String getLanguage(String text){
        if (Strings.isNullOrEmpty(text)) return DEFAULT_LANG;

        TextObject textObject   = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        return (!lang.isPresent() || !availableLangs.contains(lang.get().getLanguage()))? "unknown" : lang.get().getLanguage();
    }


    public static void main(String[] args) throws IOException {
        LanguageService service = new LanguageService();
        service.setup();

        String txt = "1.\\n   \\n\\n    Öffentlicher Auftraggeber (Vergabestelle)\\n   \\n\\n\\n\\n       a) \\n     \\n\\n      Hauptauftraggeber (zur Angebotsabgabe auffordernde Stelle)\\n     \\n\\n\\nName:\\nBundesrepublik Deutschland, vertreten durch das Beschaffungsamt des Bundesministeriums des Innern\\n\\n\\nStraße, Hausnummer:\\nBrühler Straße 3\\n\\n\\nPostleitzahl (PLZ):\\n53119\\n\\n\\nOrt:\\nBonn\\n\\n\\nTelefon:\\n+49 22899610-2925\\n\\n\\nTelefax:\\n+49 2289910610-2925\\n\\n\\nE-Mail:\\n B14.38@bescha.bund.de \\n\\n\\nInternet-Adresse:\\n http://www.bescha.bund.de \\n\\n\\n\\n\\n\\n      b)\\n     \\n\\n      Zuschlag erteilende Stelle\\n     \\n\\nWie Hauptauftraggeber siehe a)\\n\\n\\n\\n\\n\\n\\n\\n\\n    2.\\n   \\n\\n    Angaben zum Verfahren\\n   \\n\\n\\n\\n      a)\\n     \\n\\n      Verfahrensart\\n     \\n\\n      Öffentliche Ausschreibung nach UVgO\\n     \\n\\n\\n\\n      b)\\n     \\n\\n      Vertragsart\\n     \\n\\n      Liefer- / Dienstleistungsauftrag\\n     \\n\\n\\n\\n      c)\\n     \\n\\n      Geschäftszeichen\\n     \\n\\n      B 14.38 - 0270/20/VV : 1\\n     \\n\\n\\n\\n\\n\\n\\n\\n    3.\\n   \\n\\n    Angaben zu Angeboten\\n   \\n\\n\\n\\n      a)\\n     \\n\\n      Form der Angebote\\n     \\n\\n\\nelektronisch\\n        \\nohne elektronische Signatur (Textform)\\nmit fortgeschrittener elektronischer Signatur / fortgeschrittenem elektronischen Siegel\\nmit qualifizierter elektronischer Signatur / qualifiziertem elektronischen Siegel\\n \\n\\n\\n\\n\\n\\n\\n      b)\\n     \\n\\n      Fristen\\n     \\n\\n\\n\\n        Ablauf der Angebotsfrist\\n       \\n\\n        14.05.2020 - 11:30 Uhr \\n       \\n\\n\\n\\n        Ablauf der Bindefrist\\n       \\n\\n        15.06.2020\\n       \\n\\n\\n\\n\\n\\n      c)\\n     \\n\\n      Sprache\\n     \\n\\n      deutsch\\n     \\n\\n\\n\\n\\n\\n\\n\\n    4.\\n   \\n\\n    Angaben zu Vergabeunterlagen\\n   \\n\\n\\n\\n      a)\\n     \\n\\n      Vertraulichkeit\\n     \\n\\n      Die Vergabeunterlagen stehen für einen uneingeschränkten und vollständigen direkten Zugang gebührenfrei zur Verfügung unter\\n      \\nhttps://www.evergabe-online.de/tenderdetails.html?id=325728\\n\\n\\n\\n\\n      b)\\n     \\n\\n      Zugriff auf die Vergabeunterlagen\\n     \\n\\n      Vergabeunterlagen werden nur elektronisch zur Verfügung gestellt\\n     \\n\\n\\n\\n\\n\\n\\n\\n    5.\\n   \\n\\n    Angaben zur Leistung\\n   \\n\\n\\n\\n      a)\\n     \\n\\n      Art und Umfang der Leistung\\n     \\n\\n      Lizenzen und Supportverlängerungen IDA Pro\\n      siehe Leistungsbeschreibung\\n     \\n\\n\\n\\n      b)\\n     \\n\\n      CPV-Codes\\n     \\n\\nHauptteil (1): \\nBereitstellung von Software (72268000) \\n\\n\\n\\n\\n      c)\\n     \\n\\n      Ort der Leistungserbringung\\n     \\n\\n      Bonn\\n     \\n\\n\\n\\n\\n\\n\\n\\n    6.\\n   \\n\\n    Angaben zu Losen\\n   \\n\\n\\n\\n      a)\\n     \\n\\n      Anzahl, Größe und Art der Lose\\n     \\n\\n      keine Losaufteilung\\n     \\n\\n\\n\\n\\n\\n\\n\\n    7.\\n   \\n\\n    Zulassung von Nebenangeboten\\n   \\n\\n    Nein\\n   \\n\\n\\n\\n\\n\\n    8.\\n   \\n\\n    Bestimmungen über die Ausführungsfrist\\n   \\n\\n    Beginn: ..\\n    Ende: ..\\n   \\n\\n\\n\\n\\n\\n    10.\\n   \\n\\n    Wesentliche Zahlungsbedingungen\\n   \\n\\n    Allgemeinen Geschäftsbedingungen des Beschaffungsamtes des BMI in der in den Vergabeunterlagen genannten Fassung, Allgemeine Vertragsbedingungen für die Ausführung von Leistungen (VOL/B) in der Fassung vom 05. August 2003\\n   \\n\\n\\n\\n\\n\\n    11.\\n   \\n\\n    Unterlagen zur Beurteilung der Eignung des Bieters und des Nichtvorliegens von Ausschlussgründen\\n   \\n\\n    k.A.\\n   \\n\\n\\n\\n\\n\\n    12.\\n   \\n\\n    Zuschlagskriterien\\n   \\n\\n    siehe Vergabeunterlagen\\n   \\n\\n\\n\\n\\n\\n    13.\\n   \\n\\n    Rechtsform, die eine Bietergemeinschaft nach Zuschlagserteilung annehmen muss\\n   \\n\\n    Keine besondere Rechtsform; BGB-Gesellschaften haben einen bevollmächtigten Vertreter zu benennen und sich zur gesamtschuldnerischen Haftung zu verpflichten.";

        LOG.info("Language identified: " + service.getLanguage(txt));
    }

}
