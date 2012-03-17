package org.openengsb.similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openengsb.core.api.edb.EDBCommit;
import org.openengsb.core.api.edb.EDBObject;

public class TestHelper {

    /**
     * generates an amount of random EDBObjects, based on a given number the size of the EDBObjects is defined by the
     * fieldCount
     * 
     */
    public static List<EDBObject> buildEDBObjects(int number, int fieldCount) {
        List<EDBObject> result = new ArrayList<EDBObject>();
        for (int i = 0; i < number; i++) {
            Map<String, Object> randomData = new HashMap<String, Object>();

            for (int j = 0; j < fieldCount; j++) {
                randomData.put("key" + String.valueOf(j), UUID.randomUUID().toString());
            }

            EDBObject e = new EDBObject(UUID.randomUUID().toString(), randomData);
            result.add(e);
        }
        return result;
    }

    public static boolean pruneIndex(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    pruneIndex(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }

    public static String generateSearchString(EDBObject sample) {
        String result = "";
        for (Map.Entry<String, Object> entry : sample.entrySet()) {
            if (result.length() != 0) {
                result += " AND ";
            }
            result += entry.getKey().toString() + ":" + entry.getValue().toString();
        }

        return result;
    }

    public static Map<String, Object> setupConcreteData(Index index) {
        Map<String, Object> object1 = new HashMap<String, Object>();
        object1.put("key1", "value1");
        object1.put("key2", "value2");
        object1.put("key3", "value3");
        EDBObject edbOb1 = new EDBObject(UUID.randomUUID().toString(), object1);

        Map<String, Object> object2 = new HashMap<String, Object>();
        object2.put("key1", "value4");
        object2.put("key2", "value5");
        object2.put("key3", "value6");
        EDBObject edbOb2 = new EDBObject(UUID.randomUUID().toString(), object2);

        List<EDBObject> data = new ArrayList<EDBObject>(Arrays.asList(edbOb1, edbOb2));

        EDBCommit commit = new DummyEDBCommit(data, null);

        index.updateIndex(commit);
        return object1;
    }

}
