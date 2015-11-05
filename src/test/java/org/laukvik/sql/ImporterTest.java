package org.laukvik.sql;

import java.io.File;
import org.junit.Test;

/**
 * Created by morten on 16.10.2015.
 *
 */
public class ImporterTest {

    public static File getTestFolder() {
        ClassLoader classLoader = org.laukvik.sql.ImporterTest.class.getClassLoader();
        return new File(classLoader.getResource("").getFile());
    }

    public static File getResource(String filename) {
        ClassLoader classLoader = org.laukvik.sql.ImporterTest.class.getClassLoader();
        return new File(classLoader.getResource(filename).getFile());
    }

    @Test
    public void importEmployees() throws Exception {
        DatabaseConnection db = DatabaseConnection.read("test");
        Importer importer = new Importer(db);
        //importer.installFromCsvFile( org.laukvik.sql.ImporterTest.getTestFolder() );
    }

    @Test
    public void importPresidents() throws Exception {
        DatabaseConnection db = DatabaseConnection.read("test");
        Importer importer = new Importer(db);
        //importer.installFromCsvFile( org.laukvik.sql.ImporterTest.getTestFolder(), "presidents");
    }

}
