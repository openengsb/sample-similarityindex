package org.openengsb.similarity.standard;

import java.util.ArrayList;
import java.util.List;

import org.openengsb.core.api.edb.EDBObject;

public interface Searcher {

    /**
     * Checks the Index if there are colliding objects for one sample
     */
    public List<String> findCollisions(EDBObject sample);

    /**
     * Checks the Index if there are colliding objects for a list of samples
     */
    public List<ArrayList<String>> findCollisions(List<EDBObject> samples);

    /**
     * query the index with lucene syntax
     */
    public ArrayList<String> query(String searchString);

}
