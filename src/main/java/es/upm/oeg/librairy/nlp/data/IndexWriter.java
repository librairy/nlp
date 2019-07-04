package es.upm.oeg.librairy.nlp.data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
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

public class IndexWriter {

    private static final Logger LOG = LoggerFactory.getLogger(IndexWriter.class);
    private final org.apache.lucene.index.IndexWriter writer;
    private final FSDirectory idx;
    private DirectoryReader reader;
    private AtomicInteger counter = new AtomicInteger();
    private IndexSearcher searcher;
    RepositoryAnalyzer analyzer = new RepositoryAnalyzer();

    public IndexWriter(File outputFile) {
        try {
            this.idx = FSDirectory.open(outputFile.toPath());
            IndexWriterConfig writerConfig = new IndexWriterConfig(new RepositoryAnalyzer());
            writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writerConfig.setRAMBufferSizeMB(5.0);
            this.writer = new org.apache.lucene.index.IndexWriter(idx, writerConfig);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }

    public synchronized void add(String title, String content){
        try {
            Document doc = new Document();

            // Add the title as an unindexed field...
            doc.add(new TextField("name", title, Field.Store.YES));
            doc.add(new TextField("body", content, Field.Store.YES));

            writer.addDocument(doc);
            if (counter.incrementAndGet() % 100 == 0 ) {
                commit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSize(){
        return getReader().numDocs();
    }

    public void commit(){
        try {
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document getDocument(int docId){
        try {
            close();
            return reader.document(docId);

        } catch (IOException e) {
            throw new RuntimeException("Unexpected error",e);
        }
    }

    public List<String> get(String text){
        try {
            close();

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

    private synchronized void close() throws IOException {
        if (writer.isOpen()) {
            writer.commit();
            writer.close();
            reader = DirectoryReader.open(idx);
            searcher  = new IndexSearcher(reader);
        }
    }

    public DirectoryReader getReader(){
        try {
            close();
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
