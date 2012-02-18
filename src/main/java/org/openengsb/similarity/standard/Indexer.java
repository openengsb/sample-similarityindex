package org.openengsb.similarity.standard;

import java.util.List;

import org.openengsb.core.api.edb.EDBObject;

public interface Indexer {

    /**
     * (re)Builds the Index from scratch
     */
    public void buildIndex();

    /**
     * Updates the Index
     */
    public void updateIndex(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes);

    /**
     * closes the index
     */
    public void close();

}
