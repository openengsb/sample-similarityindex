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
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.core.api.edb.EngineeringDatabaseService;
import org.openengsb.similarity.Index;

public abstract class AbstractIndex implements Index {

    protected final int maxNumberOfHits = 50;
    protected Version luceneVersion = Version.LUCENE_35;
    protected String path = "";

    protected EngineeringDatabaseService edbService;

    protected IndexWriter writer;
    protected IndexReader reader;
    protected Directory index;

    protected abstract void addDocument(EDBObject c) throws IOException;

    protected abstract String buildQueryString(EDBObject sample);

    public AbstractIndex(String path) {
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

    private void initReader() {
        try {
            this.index = FSDirectory.open(new File(path));
            this.reader = IndexReader.open(index);
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
    public List<String> findCollisions(EDBObject sample) {
        List<String> result = search(sample);

        close();

        return result;
    }

    @Override
    public List<ArrayList<String>> findCollisions(List<EDBObject> samples) {
        List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (EDBObject sample : samples) {
            result.add(search(sample));
        }

        close();

        return result;
    }

    @Override
    public ArrayList<String> query(String searchString) {
        initReader();
        ArrayList<String> result = new ArrayList<String>();

        try {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser =
                new QueryParser(luceneVersion, "", new WhitespaceAnalyzer(luceneVersion));
            parser.setAllowLeadingWildcard(true);
            parser.setLowercaseExpandedTerms(false);
            Query query = parser.parse(searchString);
            ScoreDoc[] results = searcher.search(query, maxNumberOfHits).scoreDocs;

            for (ScoreDoc result2 : results) {
                int docId = result2.doc;
                Document document = searcher.doc(docId);
                result.add(document.get("oid"));
            }
            searcher.close();
            reader.close();

        } catch (ParseException e) {
            return new ArrayList<String>();
        } catch (IOException e) {
            return new ArrayList<String>();
        }
        return result;
    }

    protected ArrayList<String> search(EDBObject sample) {
        String searchString = buildQueryString(sample);
        return query(searchString);
    }

    private void close() {
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
