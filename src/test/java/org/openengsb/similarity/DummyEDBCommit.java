package org.openengsb.similarity;

import java.util.ArrayList;
import java.util.List;

import org.openengsb.core.api.edb.EDBCommit;
import org.openengsb.core.api.edb.EDBException;
import org.openengsb.core.api.edb.EDBObject;

public class DummyEDBCommit implements EDBCommit {

    private final List<String> deletion;
    private final List<EDBObject> insertion;
    private final List<EDBObject> deletionsorig;

    public DummyEDBCommit(List<EDBObject> insertion, List<EDBObject> deletion) {
        List<String> deletions = new ArrayList<String>();
        if (deletion != null) {
            for (EDBObject obj : deletion) {
                deletions.add(obj.getOID());
            }
        }

        this.deletion = deletions;
        this.deletionsorig = deletion;
        this.insertion = insertion;
    }

    public List<EDBObject> getDeletionsOriginal() {
        return deletionsorig;
    }

    @Override
    public void add(EDBObject obj) throws EDBException {
    }

    @Override
    public void delete(String obj) throws EDBException {
    }

    @Override
    public String getCommitter() {
        return null;
    }

    @Override
    public String getContextId() {
        return null;
    }

    @Override
    public List<String> getDeletions() {
        return deletion;
    }

    @Override
    public List<String> getOIDs() {
        return null;
    }

    @Override
    public List<EDBObject> getObjects() {
        return insertion;
    }

    @Override
    public Long getTimestamp() {
        return null;
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void setCommitted(Boolean committed) {
    }

    @Override
    public void setTimestamp(Long timestamp) {
    }

}
