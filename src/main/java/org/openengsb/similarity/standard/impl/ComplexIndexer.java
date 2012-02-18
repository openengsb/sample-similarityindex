package org.openengsb.similarity.standard.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;

public class ComplexIndexer extends AbstractIndexer {

    public ComplexIndexer() throws IOException {
        super("complex");
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            doc.add(new Field(entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        }

        doc.add(new Field("complexKey", content.get("key1").toString() + "#" + content.get("key2").toString()
                + "#" + content.get("key3").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);
    }
}
