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

package org.openengsb.similarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.openengsb.core.api.edb.EDBCommit;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.core.api.edb.EngineeringDatabaseService;

/**
 * The two methods addDocument(EDBCommit) and buildQueryString(EDBObject) should be replaced in concrete
 * implementations, otherwise the index indexes all fields of the EDBObject and the collision only uses the levenstein
 * distance.
 */
public class AbstractIndex implements Index {

    private static final Logger LOGGER = Logger.getLogger(AbstractIndex.class);
    protected static final int MAX_NUMBER_OF_HITS = 50;
    protected static Version LUCENE_VERSION = Version.LUCENE_35;
    protected static String PATH = "";

    protected EngineeringDatabaseService edbService;

    protected IndexWriter writer;
    protected IndexReader reader;
    protected Directory index;

    public AbstractIndex(String PATH) {
        this.PATH = PATH;
    }

    private void init() {
        try {
            LOGGER.debug("initialize index writer (" + PATH + ")");
            this.index = FSDirectory.open(new File(PATH));
            this.writer =
                new IndexWriter(index, new IndexWriterConfig(LUCENE_VERSION, new WhitespaceAnalyzer(LUCENE_VERSION)));
        } catch (IOException e) {
            LOGGER.error("could not initialize index writer (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
        }
    }

    private void initReader() {
        try {
            LOGGER.debug("initialize index reader (" + PATH + ")");
            this.index = FSDirectory.open(new File(PATH));
            this.reader = IndexReader.open(index);
        } catch (IOException e) {
            LOGGER.error("could not initialize index reader (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
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
            LOGGER.error("index could not be created from cratch (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
        }
    }

    @Override
    public void updateIndex(EDBCommit commit) {
        try {
            init();

            if (commit.getDeletions() != null) {
                for (String c : commit.getDeletions()) {
                    deleteDocument(c);
                }
            }

            if (commit.getObjects() != null) {
                for (EDBObject c : commit.getObjects()) {
                    deleteDocument(c.getOID());
                    addDocument(c);
                }
            }

            this.writer.commit();
            close();
        } catch (IOException e) {
            LOGGER.error("index could not be updated (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
            buildIndex();
            close();
        }
    }

    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            doc.add(new Field(entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        }

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);
        LOGGER.debug("new document added (" + PATH + ")");
    }

    protected String buildQueryString(EDBObject sample) {
        String result = "";
        for (Map.Entry<String, Object> entry : sample.entrySet()) {
            if (result.length() != 0) {
                result += " AND ";
            }
            result += entry.getKey().toString() + ":" + entry.getValue().toString() + "~0.8";
        }

        return result;
    }

    protected void deleteDocument(String oid) throws IOException {
        Term searchTerm = new Term("oid", oid);

        this.writer.deleteDocuments(searchTerm);
        LOGGER.debug("document with oid " + oid + " was deleted (" + PATH + ")");
    }

    @Override
    public List<String> findCollisions(EDBObject sample) {
        List<String> result = search(sample);
        close();
        return result;
    }

    @Override
    public List<List<String>> findCollisions(List<EDBObject> samples) {
        List<List<String>> result = new ArrayList<List<String>>();

        for (EDBObject sample : samples) {
            result.add(search(sample));
        }

        close();
        return result;
    }

    @Override
    public List<String> query(String searchString) {
        initReader();
        ArrayList<String> result = new ArrayList<String>();

        try {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser =
                new QueryParser(LUCENE_VERSION, "", new WhitespaceAnalyzer(LUCENE_VERSION));
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
            reader.close();

        } catch (ParseException e) {
            LOGGER.error("the query could not be parsed: " + searchString);
            LOGGER.debug(e.getStackTrace());
            return new ArrayList<String>();
        } catch (IOException e) {
            LOGGER.error("the query could not be executed (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
            return new ArrayList<String>();
        }
        return result;
    }

    protected List<String> search(EDBObject sample) {
        String searchString = buildQueryString(sample);
        return query(searchString);
    }

    private void close() {
        try {
            this.writer.close(true);
            this.index.close();
        } catch (IOException e) {
            restoreIndex();
        }
    }

    private void restoreIndex() {
        try {
            if (IndexWriter.isLocked(this.writer.getDirectory())) {
                IndexWriter.unlock(this.writer.getDirectory());
            }
            buildIndex();
            LOGGER.info("the index was restored (" + PATH + ")");
        } catch (IOException e) {
            LOGGER.error("the index could not be restored (" + PATH + ")");
            LOGGER.debug(e.getStackTrace());
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

    public String getPATH() {
        return PATH;
    }
}
