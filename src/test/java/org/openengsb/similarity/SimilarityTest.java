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
import org.openengsb.similarity.concreteIndex.ComplexIndex;
import org.openengsb.similarity.concreteIndex.StandardIndex;

public class SimilarityTest {

    private StandardIndex standardIndex;
    private ComplexIndex complexIndex;
    private static DummyEDBCommit inserts;
    private static DummyEDBCommit deletes;
    private static DummyEDBCommit updates;

    @BeforeClass
    public static void generateLists() {
        List<EDBObject> insert = TestHelper.buildEDBObjects(100, 30);
        List<EDBObject> delete = insert.subList(0, 10);
        List<EDBObject> update = insert.subList(20, 30);

        Integer counter = 20;

        for (EDBObject up : update) {
            up.put(counter.toString(), counter);
        }

        inserts = new DummyEDBCommit(insert, null);
        deletes = new DummyEDBCommit(null, delete);
        updates = new DummyEDBCommit(update, null);

    }

    @Before
    public void setUp() throws IOException {
        standardIndex = new StandardIndex();
        complexIndex = new ComplexIndex();
    }

    @After
    public void tearDown() {
        TestHelper.pruneIndex(new File(standardIndex.getPATH()));
        TestHelper.pruneIndex(new File(complexIndex.getPATH()));
    }

    @Test
    public void testIndexModificationInsert() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts);
        assertEquals(100, standardIndex.getNumberOfDocs());
    }

    @Test
    public void testIndexModificationUpdate() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts);
        assertEquals(100, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(updates);
        assertEquals(100, standardIndex.getNumberOfDocs());

        assertEquals(1, standardIndex.query(TestHelper.generateSearchString(updates.getObjects().get(0))).size());
        assertEquals(updates.getObjects().get(0).getOID(),
            standardIndex.query(TestHelper.generateSearchString(updates.getObjects().get(0)))
                .get(0));
    }

    @Test
    public void testIndexModificatioDelete() throws IOException {
        assertEquals(0, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(inserts);
        assertEquals(100, standardIndex.getNumberOfDocs());
        standardIndex.updateIndex(deletes);
        assertEquals(90, standardIndex.getNumberOfDocs());

        assertEquals(new ArrayList<String>(),
            standardIndex.query(TestHelper.generateSearchString(deletes.getDeletionsOriginal().get(0))));
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
