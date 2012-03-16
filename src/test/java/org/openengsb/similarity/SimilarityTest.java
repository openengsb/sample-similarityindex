package org.openengsb.similarity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openengsb.core.api.edb.EDBObject;
import org.openengsb.similarity.internal.ComplexIndexer;
import org.openengsb.similarity.internal.StandardIndexer;

public class SimilarityTest {

    private StandardIndexer standardIndex;
    private ComplexIndexer complexIndex;
    private static List<EDBObject> inserts;
    private static List<EDBObject> deletes;
    private static List<EDBObject> updates;

    @BeforeClass
    public static void generateLists() {
        inserts = TestHelper.buildEDBObjects(100, 30);
        deletes = inserts.subList(0, 10);
        updates = inserts.subList(20, 30);

        Integer counter = 20;

        for (EDBObject update : updates) {
            update.put(counter.toString(), counter);
        }
    }

    @Before
    public void setUp() throws IOException {
        standardIndex = new StandardIndexer();
        complexIndex = new ComplexIndexer();
    }

    @After
    public void tearDown() {
        TestHelper.pruneIndex(new File(standardIndex.getPath()));
        TestHelper.pruneIndex(new File(complexIndex.getPath()));
    }

    @Test
    public void testIndexModificationInsert() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getNumberOfDocs());
    }

    @Test
    public void testIndexModificationUpdate() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(null, updates, null);
        assertEquals(100, standardIndex.getNumberOfDocs());

        assertEquals(1, standardIndex.query(TestHelper.generateSearchString(updates.get(0))).size());
        assertEquals(updates.get(0).getOID(), standardIndex.query(TestHelper.generateSearchString(updates.get(0)))
            .get(0));
    }

    @Test
    public void testIndexModificatioDelete() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(null, null, deletes);
        assertEquals(90, standardIndex.getNumberOfDocs());

        assertEquals(new ArrayList<String>(), standardIndex.query(TestHelper.generateSearchString(deletes.get(0))));
    }

    @Test
    public void findCollissionSuccessforSimpleIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(standardIndex);

        object1.put("key1", "Xalue1");
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);
        assertEquals(1, standardIndex.findCollisions(sample).size());
    }

    @Test
    public void findCollissionFailsforSimpleIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(standardIndex);

        object1.put("key1", "valuXX");
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);
        assertEquals(0, standardIndex.findCollisions(sample).size());
    }

    @Test
    public void findCollissionSuccessforComplexIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(complexIndex);
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);

        assertEquals(1, complexIndex.findCollisions(sample).size());
    }

}
