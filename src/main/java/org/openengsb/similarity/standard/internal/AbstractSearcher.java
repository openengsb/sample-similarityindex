package org.openengsb.similarity.standard.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.core.api.ekb.QueryInterface;
import org.openengsb.core.ekb.internal.EDBConverter;
import org.openengsb.similarity.standard.Searcher;

public abstract class AbstractSearcher implements Searcher {

    protected String path = "";
    protected final int MAX_NUMBER_OF_HITS = 50;

    // TODO load EDB converter & EDB (JPA) service
    protected EDBConverter edbConverter;
    protected QueryInterface queryInterfaceService;

    protected IndexReader reader;
    protected Directory index;
    protected IndexWriterConfig indexConfig;

    protected Version luceneVersion = Version.LUCENE_35;

    abstract protected String buildQueryString(EDBObject sample);

    public AbstractSearcher(String path) {
        this.path = path;
        try {
            indexConfig = new IndexWriterConfig(Version.LUCENE_35, new WhitespaceAnalyzer(Version.LUCENE_35));
            this.index = FSDirectory.open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ArrayList<String> result = new ArrayList<String>();

        try {
            reader = IndexReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser =
                new QueryParser(luceneVersion, "", new WhitespaceAnalyzer(luceneVersion));
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

    protected void close() {
        try {
            this.index.close();
        } catch (CorruptIndexException e) {
            // FIXME delete the entire index and recreate
        } catch (IOException e) {
            // TODO most certainly File-I/O probs but something should be done
        }
    }

    public void setEdbConverter(EDBConverter edbConverter) {
        this.edbConverter = edbConverter;
    }

    public void setQueryInterfaceService(QueryInterface queryInterfaceService) {
        this.queryInterfaceService = queryInterfaceService;
    }

}
