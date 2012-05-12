/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.similarity;

import java.util.List;

import org.openengsb.core.api.edb.EDBCommit;
import org.openengsb.core.api.edb.EDBObject;

public interface Index {
    /**
     * (re)builds the Index from scratch
     */
    void buildIndex();

    /**
     * updates the Index with a given EDBCommit, that contains all new and deleted EDBObjects
     */
    void updateIndex(EDBCommit commit);

    /**
     * checks the Index if there are similar EDBObjects for one sample and returns a list with oids from similar
     * EDBObjects.
     */
    List<String> findCollisions(EDBObject sample);

    /**
     * The findCollisions method takes a list of EDBObjects. The result of this method is a list of lists, while the
     * inner lists contain the oids of the similar EDBObjects. In the result there is an inner list for every given
     * sample EDBObject, that can be either empty (no similar EDB Object found), contains 1 oid (1 similar EDBObject
     * found) or multiple EDBObjects ordered descending by their similarity with the sample.
     */
    List<List<String>> findCollisions(List<EDBObject> samples);

    /**
     * the query method provides the functionality to search the Index for EDBObjects based on a searchString. The
     * result is a list of oids that represent the EDBObjects. The syntax of these queries is the same as for Lucene
     * queries.
     */
    List<String> query(String searchString);
}
