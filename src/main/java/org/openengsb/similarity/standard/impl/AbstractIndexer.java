package org.openengsb.similarity.standard.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.core.api.edb.EngineeringDatabaseService;
import org.openengsb.similarity.standard.Indexer;

public abstract class AbstractIndexer implements Indexer {

    protected String path = "";

    protected EngineeringDatabaseService edbService;

    protected IndexWriter writer;
    protected Directory index;
    protected IndexWriterConfig indexConfig;

    protected Version luceneVersion = Version.LUCENE_35;

    abstract protected void addDocument(EDBObject c) throws IOException;

    public AbstractIndexer(String path) {
        this.path = path;
        indexConfig = new IndexWriterConfig(luceneVersion, new WhitespaceAnalyzer(luceneVersion));
        try {
            this.index = FSDirectory.open(new File(path));
            this.writer = new IndexWriter(index, indexConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildIndex() {

        // TODO query for all objects

        try {
            writer.deleteAll();
            writer.commit();

            for (EDBObject c : edbService.query("", null)) {
                addDocument(c);
            }

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
            close();
        }
    }

    protected void deleteDocument(EDBObject delete) throws IOException {
        Term searchTerm = new Term("oid", delete.getOID());

        this.writer.deleteDocuments(searchTerm);
    }

    @Override
    public void close() {
        try {
            try {
                this.writer.close(true);
            } catch (CorruptIndexException e) {
                // FIXME delete the entire index and recreate
                if (IndexWriter.isLocked(this.writer.getDirectory())) {
                    IndexWriter.unlock(this.writer.getDirectory());
                }
                buildIndex();
            } catch (IOException e) {
                // TODO most certainly File-I/O probs but something should be
                // done
                if (IndexWriter.isLocked(this.writer.getDirectory())) {
                    IndexWriter.unlock(this.writer.getDirectory());
                }
            }
        } catch (IOException e) {
            // panic, now its really a mess
            e.printStackTrace();
        }
    }

    public void setEdbService(EngineeringDatabaseService edbService) {
        this.edbService = edbService;
    }

    public IndexWriter getWriter() {
        return writer;
    }

    public String getPath() {
        return path;
    }

}
