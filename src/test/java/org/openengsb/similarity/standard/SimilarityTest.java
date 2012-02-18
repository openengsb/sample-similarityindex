package org.openengsb.similarity.standard;

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
import org.openengsb.similarity.standard.impl.ComplexIndexer;
import org.openengsb.similarity.standard.impl.ComplexSearcher;
import org.openengsb.similarity.standard.impl.StandardIndexer;
import org.openengsb.similarity.standard.impl.StandardSearcher;

public class SimilarityTest {

    private StandardIndexer standardIndex;
    private StandardSearcher standardSearcher;
    private ComplexIndexer complexIndex;
    private ComplexSearcher complexSearcher;
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
        standardSearcher = new StandardSearcher();
        complexIndex = new ComplexIndexer();
        complexSearcher = new ComplexSearcher();
    }

    @After
    public void tearDown() {
        standardIndex.close();
        complexIndex.close();
        TestHelper.pruneIndex(new File(standardIndex.getPath()));
        TestHelper.pruneIndex(new File(complexIndex.getPath()));
    }

    @Test
    public void testIndexModificationInsert() throws IOException {
        assertEquals(0, standardIndex.getWriter().numDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getWriter().numDocs());
        standardIndex.close();
    }

    @Test
    public void testIndexModificationUpdate() throws IOException {
        assertEquals(0, standardIndex.getWriter().numDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getWriter().numDocs());
        standardIndex.updateIndex(null, updates, null);
        assertEquals(100, standardIndex.getWriter().numDocs());
        standardIndex.close();

        assertEquals(1, standardSearcher.query(TestHelper.generateSearchString(updates.get(0))).size());
        assertEquals(updates.get(0).getOID(), standardSearcher.query(TestHelper.generateSearchString(updates.get(0)))
            .get(0));
    }

    @Test
    public void testIndexModificatioDelete() throws IOException {
        assertEquals(0, standardIndex.getWriter().numDocs());
        standardIndex.updateIndex(inserts, null, null);
        assertEquals(100, standardIndex.getWriter().numDocs());
        standardIndex.updateIndex(null, null, deletes);
        assertEquals(90, standardIndex.getWriter().numDocs());
        standardIndex.close();

        assertEquals(new ArrayList<String>(), standardSearcher.query(TestHelper.generateSearchString(deletes.get(0))));
    }

    @Test
    public void findCollissionSuccessforSimpleIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(standardIndex);

        object1.put("key1", "Xalue1");
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);
        assertEquals(1, standardSearcher.findCollisions(sample).size());
    }

    @Test
    public void findCollissionFailsforSimpleIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(standardIndex);

        object1.put("key1", "valuXX");
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);
        assertEquals(0, standardSearcher.findCollisions(sample).size());
    }

    @Test
    public void findCollissionSuccessforComplexIndex() {
        Map<String, Object> object1 = TestHelper.setupConcreteData(complexIndex);
        EDBObject sample = new EDBObject(UUID.randomUUID().toString(), object1);

        assertEquals(1, complexSearcher.findCollisions(sample).size());
    }

}
