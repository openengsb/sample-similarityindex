package org.openengsb.similarity.standard.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.similarity.standard.ConcreteModel;

public class DefaultSimilarityIndex extends AbstractSimilarityIndex {

    public DefaultSimilarityIndex() throws IOException {
        super();
    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        ConcreteModel model = edbConverter.convertEDBObjectToModel(ConcreteModel.class, sample);

        return "";
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            doc.add(new Field(entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        }

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);

    }
}
