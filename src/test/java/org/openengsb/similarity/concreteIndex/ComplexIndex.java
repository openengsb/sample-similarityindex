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

package org.openengsb.similarity.concreteIndex;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.similarity.internal.AbstractIndex;

/**
 * 
 * This class represents possible index-configurations
 * 
 */
public class ComplexIndex extends AbstractIndex {

    public ComplexIndex() throws IOException {
        super("data/similarity/complex");
    }

    @Override
    protected void addDocument(EDBObject content) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            doc.add(new Field(entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        }

        doc.add(new Field("complexKey", content.get("key1").toString() + "#" + content.get("key2").toString()
                + "#" + content.get("key3").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        this.writer.updateDocument(new Term("oid", content.getOID()), doc);
    }

    @Override
    protected String buildQueryString(EDBObject sample) {

        String result =
            "complexKey:" + sample.getString("key1") + "#" + sample.getString("key2") + "#" + sample.getString("key3");

        return result;
    }
}
