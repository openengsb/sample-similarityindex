package org.openengsb.similarity.standard.impl;

import java.io.IOException;

import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.similarity.standard.ConcreteModel;

public class ComplexSearcher extends AbstractSearcher {

    public ComplexSearcher() throws IOException {
        super("complex");
    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        // ConcreteModel model = edbConverter.convertEDBObjectToModel(ConcreteModel.class, sample);

        ConcreteModel model = null;

        // String modelBasedQuery = "complexKey:" + model.getKey1() + "#" + model.getKey2() + "#" + model.getKey3();

        String result = "";
        result +=
            "complexKey:" + sample.getString("key1") + "#" + sample.getString("key2") + "#" + sample.getString("key3");

        return result;
    }
}
