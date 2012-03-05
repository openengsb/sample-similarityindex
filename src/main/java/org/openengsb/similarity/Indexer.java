package org.openengsb.similarity;

import java.util.List;

import org.openengsb.core.api.edb.EDBObject;

public interface Indexer {

    /**
     * (re)Builds the Index from scratch
     */
    void buildIndex();

    /**
     * Updates the Index
     */
    void updateIndex(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes);

    /**
     * closes the index
     */
    void close();

}
