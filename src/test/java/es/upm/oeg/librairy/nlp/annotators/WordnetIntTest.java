package es.upm.oeg.librairy.nlp.annotators;

import es.upm.oeg.librairy.nlp.data.IndexReader;
import es.upm.oeg.librairy.nlp.data.IndexWriter;
import es.upm.oeg.librairy.nlp.utils.ReaderUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetIntTest.class);

    @Test
    public void query() throws IOException {

        File idxFile = new File("src/main/bin/wordnet/portuguese.idx");
        IndexReader indexReader = new IndexReader(idxFile);


        List<String> synsets = indexReader.get("rede");
        LOG.info(String.valueOf(synsets));

    }

    @Test
    public void createIndexes() throws IOException {
        BufferedReader reader = ReaderUtils.from("src/test/resources/wordnet/wn-data-por.tab");

        String row = null;

        File idxFile = new File("src/main/bin/wordnet/portuguese.idx");

        IndexWriter index = new IndexWriter(idxFile);
        Optional<String> synset = Optional.empty();
        List<String> content = new ArrayList<>();
        int counter = 0;
        while((row = reader.readLine()) != null){
            if (row.startsWith("#")) continue;
            String[] data = row.split("\t");
            if (synset.isPresent() && synset.get().equals(data[0])){
                content.add(data[1]);
                content.add(cleanString(data[1]));
                continue;
            }else if (synset.isPresent()){
                // save data
                //LOG.info("saved: '" + synset.get() + "' -> " + content);
                String terms = content.stream().distinct().collect(Collectors.joining(" "));
                index.add(synset.get(),terms);
                counter++;
            }

            // initialize data
            synset = Optional.of(data[0]);
            content.clear();
            content.add(data[1]);
            content.add(cleanString(data[1]));
        }

        index.commit();

        reader.close();
        LOG.info(counter + " synsets indexed at " + idxFile.getAbsolutePath());
    }

    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

}
