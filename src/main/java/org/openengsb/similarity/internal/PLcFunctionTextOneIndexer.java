package org.openengsb.similarity.internal;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;

public class PLcFunctionTextOneIndexer extends AbstractIndexer {

    public PLcFunctionTextOneIndexer() throws IOException {
        super("data/similarity/PLcFunctionTextOne");
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        doc.add(new Field("functiontextone", content.get("functiontextone").toString(), Field.Store.YES,
            Field.Index.NOT_ANALYZED));

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);

    }

}
