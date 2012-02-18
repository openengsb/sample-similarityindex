package org.openengsb.similarity.standard;

import org.openengsb.core.api.model.OpenEngSBModel;
import org.openengsb.core.api.model.OpenEngSBModelId;

public interface ConcreteModel extends OpenEngSBModel {

    @OpenEngSBModelId
    void setId(String id);

    String getId();

    void setKey1(String key1);

    String getKey1();

    void setKey2(String key2);

    String getKey2();

    void setKey3(String key3);

    String getKey3();

    void setValue1(String value1);

    String getValue1();

    void setValue2(String value2);

    String getValue2();

    void setValue3(String value3);

    String getValue3();

}
