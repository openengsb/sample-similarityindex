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

package org.openengsb.similarity.internal;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;

public class StandardIndex extends AbstractIndex {

    public StandardIndex() throws IOException {
        super("data/similarity/standard");
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            doc.add(new Field(entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        }

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);

    }

    @Override
    protected String buildQueryString(EDBObject sample) {
        String result = "";
        result += "key1:" + sample.getString("key1") + "~0.8 AND ";
        result += "key2:" + sample.getString("key2") + "~0.8 AND ";
        result += "key3:" + sample.getString("key3") + "~0.8";

        return result;
    }

}
