package org.openengsb.similarity.standard;

import java.util.List;

import org.openengsb.core.api.model.OpenEngSBModelEntry;

public class ConcreteModelImpl implements ConcreteModel {

    private String id;

    private String key1;
    private String key2;
    private String key3;

    private String value1;
    private String value2;
    private String value3;

    @Override
    public String getKey1() {
        return key1;
    }

    @Override
    public void setKey1(String key1) {
        this.key1 = key1;
    }

    @Override
    public String getKey2() {
        return key2;
    }

    @Override
    public void setKey2(String key2) {
        this.key2 = key2;
    }

    @Override
    public String getKey3() {
        return key3;
    }

    @Override
    public void setKey3(String key3) {
        this.key3 = key3;
    }

    @Override
    public String getValue1() {
        return value1;
    }

    @Override
    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @Override
    public String getValue2() {
        return value2;
    }

    @Override
    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public String getValue3() {
        return value3;
    }

    @Override
    public void setValue3(String value3) {
        this.value3 = value3;
    }

    @Override
    public void addOpenEngSBModelEntry(OpenEngSBModelEntry arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<OpenEngSBModelEntry> getOpenEngSBModelEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeOpenEngSBModelEntry(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
