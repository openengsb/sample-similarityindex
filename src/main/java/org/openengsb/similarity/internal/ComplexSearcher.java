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
            "combinedkey:" + sample.getString("region") + "" + sample.getString("kks0") + ""
                    + sample.getString("kks1") + "" + sample.getString("kks2") + "#" + sample.getString("kks3");

        return result;
    }
}
