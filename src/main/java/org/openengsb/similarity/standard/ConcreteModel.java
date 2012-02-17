package org.openengsb.similarity.standard;

import org.openengsb.core.api.model.OpenEngSBModel;
import org.openengsb.core.api.model.OpenEngSBModelId;

public interface ConcreteModel extends OpenEngSBModel {

    @OpenEngSBModelId
    void setId(String id);

    String getId();

}
