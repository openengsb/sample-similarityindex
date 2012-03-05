package org.openengsb.similarity.internal;

import java.io.IOException;

import org.openengsb.core.api.edb.EDBObject;

public class StandardSearcher extends AbstractSearcher {

    public StandardSearcher() throws IOException {
        super("data/similarity/standard");
    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        String result = "";
        result += "key1:" + sample.getString("key1") + "~0.8 AND ";
        result += "key2:" + sample.getString("key2") + "~0.8 AND ";
        result += "key3:" + sample.getString("key3") + "~0.8";

        return result;
    }

}
