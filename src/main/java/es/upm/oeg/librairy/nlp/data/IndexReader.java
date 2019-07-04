package es.upm.oeg.librairy.nlp.data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class IndexReader {

    private static final Logger LOG = LoggerFactory.getLogger(IndexReader.class);
    private final FSDirectory idx;
    private DirectoryReader reader;
    private AtomicInteger counter = new AtomicInteger();
    private IndexSearcher searcher;
    RepositoryAnalyzer analyzer = new RepositoryAnalyzer();

    public IndexReader(File outputFile) {
        try {
            idx         = FSDirectory.open(outputFile.toPath());
            reader      = DirectoryReader.open(idx);
            searcher    = new IndexSearcher(reader);

        } catch (IOException e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }

    public int getSize(){
        return getReader().numDocs();
    }


    public Document getDocument(int docId){
        try {
            return reader.document(docId);

        } catch (IOException e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }

    public List<String> get(String text){
        try {
//            Query query = new QueryParser("body", analyzer).parse(text);
            Query query = new TermQuery(new Term("body", text));

            TopDocs topDocs = searcher.search(query, 25);
            List<String> docs = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = reader.document(scoreDoc.doc);
                String id = String.format(doc.get("name"));
                LOG.info(""+String.format(doc.get("name")+" - " + String.format(doc.get("body"))));
                docs.add(id);
            }

            return docs;

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }


    public DirectoryReader getReader(){
        try {
            return DirectoryReader.open(idx);

        } catch (IOException e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }

    public class RepositoryAnalyzer extends Analyzer {

        @Override
        protected TokenStreamComponents createComponents(String s) {
            Tokenizer tokenizer = new WhitespaceTokenizer();
            return new TokenStreamComponents(tokenizer);
        }
    }
}
