package org.openengsb.similarity.standard.internal;

import java.io.IOException;

import org.openengsb.core.api.edb.EDBObject;

public class ComplexSearcher extends AbstractSearcher {

    public ComplexSearcher() throws IOException {
        super("complex");
    }

    @Override
    protected String buildQueryString(EDBObject sample) {

        String result =
            "complexKey:" + sample.getString("key1") + "#" + sample.getString("key2") + "#" + sample.getString("key3");

        return result;
    }
}
