package org.openengsb.similarity.standard;

import java.util.ArrayList;
import java.util.List;

import org.openengsb.core.api.edb.EDBObject;

public interface SimilarityIndex {

    /**
     * (re)Builds the Index from scratch
     */
    public void buildIndex();

    /**
     * Updates the Index
     */
    public void updateIndex(List<EDBObject> inserts, List<EDBObject> updates, List<EDBObject> deletes);

    /**
     * Checks the Index if there are colliding objects for one sample
     */
    public List<String> findCollisions(EDBObject sample);

    /**
     * Checks the Index if there are colliding objects for a list of samples
     */
    public List<ArrayList<String>> findCollisions(List<EDBObject> samples);

    /**
     * closes the source and optimizes the index
     */
    public void commit();

}
