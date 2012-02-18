package org.openengsb.similarity.standard.impl;

import java.io.IOException;
import java.util.Map;

import org.openengsb.core.api.edb.EDBObject;

public class StandardSearcher extends AbstractSearcher {

    public StandardSearcher() throws IOException {
        super();
    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        // ConcreteModel model = edbConverter.convertEDBObjectToModel(ConcreteModel.class, sample);

        String result = "";
        for (Map.Entry<String, Object> entry : sample.entrySet()) {
            if (result.length() != 0) {
                result += " AND ";
            }
            result += "\"" + entry.getKey().toString() + ":" + entry.getValue().toString() + "\"";
            return result;
        }

        return result;
    }

}
