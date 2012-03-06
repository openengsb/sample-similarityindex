/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.similarity.internal;

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
import org.openengsb.similarity.Indexer;

public abstract class AbstractIndexer implements Indexer {

    protected String path = "";

    protected EngineeringDatabaseService edbService;

    protected IndexWriter writer;
    protected Directory index;

    protected Version luceneVersion = Version.LUCENE_35;

    protected abstract void addDocument(EDBObject c) throws IOException;

    public AbstractIndexer(String path) {
        this.path = path;
    }

    private void init() {
        try {
            this.index = FSDirectory.open(new File(path));
            this.writer =
                new IndexWriter(index, new IndexWriterConfig(luceneVersion, new WhitespaceAnalyzer(luceneVersion)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildIndex() {
        try {
            init();
            writer.deleteAll();
            writer.commit();

            for (EDBObject c : edbService.getHead()) {
                addDocument(c);
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIndex(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes) {
        try {
            init();
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
            close();
        } catch (IOException e) {
            e.printStackTrace();
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
                this.index.close();
            } catch (CorruptIndexException e) {
                buildIndex();
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

    public int getNumberOfDocs() throws IOException {
        init();
        int number = writer.numDocs();
        close();
        return number;
    }

    public String getPath() {
        return path;
    }
}
