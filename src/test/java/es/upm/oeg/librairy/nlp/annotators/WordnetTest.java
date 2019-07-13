package es.upm.oeg.librairy.nlp.annotators;

import es.upm.oeg.librairy.nlp.data.IndexReader;
import es.upm.oeg.librairy.nlp.data.IndexWriter;
import es.upm.oeg.librairy.nlp.data.LuceneIndex;
import es.upm.oeg.librairy.nlp.utils.ReaderUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetTest {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetTest.class);

    @Test
    public void query() throws IOException {

        File idxFile = new File("src/main/bin/wordnet/spanish.idx");
        IndexReader indexReader = new IndexReader(idxFile);


        List<String> synsets = indexReader.get("biotecnologia");
        LOG.info(String.valueOf(synsets));

    }

    @Test
    public void createIndexes() throws IOException {
        BufferedReader reader = ReaderUtils.from("src/test/resources/wordnet/wn-wikt-deu.tab");

        String row = null;

        File idxFile = new File("src/main/bin/wordnet/german.idx");

        IndexWriter index = new IndexWriter(idxFile);
        Optional<String> synset = Optional.empty();
        StringBuffer content = new StringBuffer();
        int counter = 0;
        while((row = reader.readLine()) != null){
            if (row.startsWith("#")) continue;
            String[] data = row.split("\t");
            if (synset.isPresent() && synset.get().equals(data[0])){
                content.append(data[1]).append(" ");
                continue;
            }else if (synset.isPresent()){
                // save data
                //LOG.info("saved: '" + synset.get() + "' -> " + content);
                index.add(synset.get(),content.toString());
                counter++;
            }

            // initialize data
            synset = Optional.of(data[0]);
            content.delete(0,content.length());
            content.append(data[1]).append(" ");
        }

        index.commit();

        reader.close();
        LOG.info(counter + " synsets indexed at " + idxFile.getAbsolutePath());
    }

}
