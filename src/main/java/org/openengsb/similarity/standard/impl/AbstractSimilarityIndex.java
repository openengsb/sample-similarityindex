package org.openengsb.similarity.standard.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.core.api.edb.EngineeringDatabaseService;
import org.openengsb.similarity.standard.SimilarityIndex;

public abstract class AbstractSimilarityIndex implements SimilarityIndex {

    // http://blog.markwshead.com/966/lucene-morelikethis-example-code/

    // TODO restablish simple tokenizer

    protected final String PATH = "default";
    protected final int MAX_NUMBER_OF_HITS = 50;

    protected final IndexWriter writer;
    protected final Directory index;
    protected IndexWriterConfig indexConfig;

    // TODO load EDB converter & EDB (JPA) service
    protected EDBConverter edbConverter;

    protected EngineeringDatabaseService edbService;

    public AbstractSimilarityIndex() throws IOException {
        // TODO index config
        indexConfig = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
        this.writer = new IndexWriter(index, indexConfig);
        this.index = FSDirectory.open(new File(PATH));
        BooleanQuery.setMaxClauseCount(MAX_NUMBER_OF_HITS);
    }

    abstract protected String buildQueryString(EDBObject sample);

    abstract protected void addDocument(EDBObject c) throws IOException;

    @Override
    public void buildIndex() {
        // TODO query for all objects

        try {

            for (EDBObject c : edbService.query("", null)) {
                addDocument(c);
            }

            commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIndex(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes) {

        try {
            if (inserts != null) {
                for (EDBObject c : inserts) {
                    addDocument(c);
                }
            }

            if (updates != null) {
                for (EDBObject c : updates) {
                    deleteDocument(c);
                    addDocument(c);
                }
            }

            if (deletes != null) {
                for (EDBObject c : deletes) {
                    deleteDocument(c);
                }
            }

            this.writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
            // recover
            buildIndex();
        }
    }

    public Directory getIndex() {
        return index;
    }

    public IndexWriter getWriter() {
        return writer;
    }

    public String getPATH() {
        return PATH;
    }

    @Override
    public List<String> findCollisions(EDBObject sample) {
        List<String> result = search(sample);

        cleanupSearch();

        return result;
    }

    @Override
    public List<ArrayList<String>> findCollisions(List<EDBObject> samples) {
        List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (EDBObject sample : samples) {
            result.add(search(sample));
        }

        cleanupSearch();

        return result;
    }

    protected void deleteDocument(EDBObject delete) throws IOException {
        Term searchTerm = new Term("oid", delete.getOID());

        this.writer.deleteDocuments(searchTerm);
    }

    protected void cleanup() {
        try {
            try {
                this.writer.close(true);
            } catch (CorruptIndexException e) {
                // FIXME delete the entire index and recreate
                if (IndexWriter.isLocked(this.writer.getDirectory())) {
                    IndexWriter.unlock(this.writer.getDirectory());
                }
            } catch (IOException e) {
                // TODO most certainly File-I/O probs but something should be
                // done
                if (IndexWriter.isLocked(this.writer.getDirectory())) {
                    IndexWriter.unlock(this.writer.getDirectory());
                }
            }
        } catch (IOException e) {
            // TODO panic, now its really a mess
            e.printStackTrace();
        }
    }

    protected void cleanupSearch() {
        try {
            this.index.close();
        } catch (CorruptIndexException e) {
            // FIXME delete the entire index and recreate
        } catch (IOException e) {
            // TODO most certainly File-I/O probs but something should be done
        }
    }

    @Override
    public void commit() {
        // TODO review behavior
        try {
            this.writer.optimize();
            this.writer.commit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        cleanup();
    }

    protected ArrayList<String> search(EDBObject sample) {
        ArrayList<String> result = new ArrayList<String>();
        String searchString = buildQueryString(sample);

        try {
            IndexSearcher searcher = new IndexSearcher(this.index, true);
            QueryParser parser = new QueryParser(sample.getOID(), new ReallySimpleAnalyzer());
            parser.setAllowLeadingWildcard(true);
            parser.setLowercaseExpandedTerms(false);
            Query query = parser.parse(searchString);
            ScoreDoc[] results = searcher.search(query, MAX_NUMBER_OF_HITS).scoreDocs;

            for (ScoreDoc result2 : results) {
                int docId = result2.doc;
                Document document = searcher.doc(docId);
                result.add(document.get("oid"));
            }
            searcher.close();
        } catch (ParseException e) {
            result = new ArrayList<String>();
        } catch (IOException e) {
            result = new ArrayList<String>();
        }

        return result;
    }

}
