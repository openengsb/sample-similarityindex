package org.openengsb.similarity.internal;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;

public class PLCCombinedIndexer extends AbstractIndexer {

    public PLCCombinedIndexer() throws IOException {
        super("data/similarity/standard");
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        doc.add(new Field("combinedkey",
            content.get("region").toString() + "" + content.get("kks0").toString() + ""
                    + content.get("kks1").toString() + "" + content.get("kks2").toString() + ""
                    + content.get("kks3").toString(),
            Field.Store.YES, Field.Index.NOT_ANALYZED));

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);

    }

}
