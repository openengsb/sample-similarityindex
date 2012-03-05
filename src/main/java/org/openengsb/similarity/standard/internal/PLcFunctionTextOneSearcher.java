package org.openengsb.similarity.standard.internal;

import java.io.IOException;

import org.openengsb.core.api.edb.EDBObject;

public class PLcFunctionTextOneSearcher extends AbstractSearcher {

    public PLcFunctionTextOneSearcher() throws IOException {
        super("standard");
    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        return "functiontextone:" + sample.getString("functiontextone") + "~0.8";
    }

}
