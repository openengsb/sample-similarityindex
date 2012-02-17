package org.openengsb.similarity.standard;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.similarity.standard.impl.DefaultSimilarityIndex;

public class SimilarityTest {

    private DefaultSimilarityIndex index;
    private static List<EDBObject> inserts;
    private static List<EDBObject> deletes;
    private static List<EDBObject> updates;

    @BeforeClass
    public static void generateLists() {
        inserts = buildEDBObjects(100, 30);
        deletes = inserts.subList(0, 10);
        updates = inserts.subList(20, 30);

        Integer counter = 20;

        for (EDBObject update : updates) {
            update.put(counter.toString(), counter);
        }
    }

    @Before
    public void setUp() throws IOException {
        index = new DefaultSimilarityIndex();
    }

    @After
    public void tearDown() {
        pruneIndex(new File(index.getPATH()));
    }

    @Test
    public void testIndexModificationInsert() throws IOException {
        Assert.assertEquals(0, index.getWriter().numDocs());
        index.updateIndex(inserts, null, null);
        Assert.assertEquals(100, index.getWriter().numDocs());
        index.commit();
    }

    @Test
    public void testIndexModificationUpdate() throws IOException {
        Assert.assertEquals(0, index.getWriter().numDocs());
        index.updateIndex(inserts, null, null);
        Assert.assertEquals(100, index.getWriter().numDocs());
        index.updateIndex(null, updates, null);
        Assert.assertEquals(100, index.getWriter().numDocs());

        // TODO check if only 20-30 are updated
        index.commit();
        fail("Not yet implemented");
    }

    @Test
    public void testIndexModificatioDelete() throws IOException {
        Assert.assertEquals(0, index.getWriter().numDocs());
        index.updateIndex(inserts, null, null);
        Assert.assertEquals(100, index.getWriter().numDocs());
        index.updateIndex(null, null, deletes);
        Assert.assertEquals(90, index.getWriter().numDocs());

        // TODO check if only 1-10 are deleted

        fail("Not yet implemented");
    }

    @Test
    public void findCollissionFails() {
        fail("Not yet implemented");
    }

    @Test
    public void findCollissionSuccess() {
        fail("Not yet implemented");
    }

    /**
     * generates an amount of random EDBObjects, based on a given number the size of the EDBObjects is defined bey the
     * fieldCount
     * 
     * @param number
     * @param fieldCount
     * @return
     */
    private static List<EDBObject> buildEDBObjects(int number, int fieldCount) {
        List<EDBObject> result = new ArrayList<EDBObject>();
        for (int i = 0; i < number; i++) {
            Map<String, Object> randomData = new HashMap<String, Object>();

            for (int j = 0; j < fieldCount; j++) {
                randomData.put(String.valueOf(j), UUID.randomUUID());
            }

            EDBObject e = new EDBObject(UUID.randomUUID().toString(), randomData);
            result.add(e);
        }
        return result;
    }

    private boolean pruneIndex(File path) {
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
        return (path.delete());
    }

}
