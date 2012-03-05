package org.openengsb.similarity.internal;

import java.io.IOException;

import org.openengsb.core.api.edb.EDBObject;

public class ComplexSearcher extends AbstractSearcher {

    public ComplexSearcher() throws IOException {
        super("data/similarity/complex");
    }

    @Override
    protected String buildQueryString(EDBObject sample) {

        String result =
            "complexKey:" + sample.getString("key1") + "#" + sample.getString("key2") + "#" + sample.getString("key3");

        return result;
    }
}
