package org.laukvik.db.sql;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.csv.MetaData;
import org.laukvik.db.csv.Row;
import org.laukvik.db.csv.io.CsvReader;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.swing.BackupMetaDataFileFilter;

/**
 *
 *
 *
 */
public class Importer {

    private final static Logger LOG = Logger.getLogger(Importer.class.getName());
    private DatabaseConnection db;
    private int rowsPrDot = 10;

    public Importer(DatabaseConnection databaseConnection) throws DatabaseReadOnlyException {
        this.db = databaseConnection;
        if (db.isReadOnly()) {
            throw new DatabaseReadOnlyException(db.getFilename());
        }
    }

    public int getRowsPrDot() {
        return rowsPrDot;
    }

    public void setRowsPrDot(int rowsPrDot) {
        this.rowsPrDot = rowsPrDot;
    }

    /**
     * Restores all data found in directory
     *
     * @param directory
     * @param charset
     * @throws DatabaseReadOnlyException
     */
    public void importDirectory(File directory, Charset charset) throws DatabaseReadOnlyException {
        LOG.log(Level.FINE, "Importing directory {0}", directory.getAbsolutePath());
        BackupMetaDataFileFilter ff = new BackupMetaDataFileFilter();

        File[] files = directory.listFiles(ff);
        int max = files.length;

        if (max == 0) {
            System.out.println("No table found in directory " + directory.getAbsolutePath());
            return;
        }

        List<MetaData> metas = new ArrayList<>();
        List<String> tables = new ArrayList<>();

        int x = 0;
        for (File f : files) {
            x++;
            String tableName = BackupMetaDataFileFilter.getName(f);
            try (CsvReader r = new CsvReader(new FileInputStream(f), charset)) {
                System.out.print(x + "/" + max + " " + tableName + " - ");
                MetaData metaData = r.getMetaData();
                // Save table name and meta data for post installation script
                metas.add(metaData);
                tables.add(tableName);

                String sql = metaData.getInstallationScript(tableName);

                // Detect if table already exists
                boolean canImport = false;

                /**
                 * ***********************************************************
                 * Pre Install Script
                 * **********************************************************
                 */
                try (
                        Connection conn = db.getConnection();
                        Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery("SELECT * FROM " + tableName);) {
                    canImport = true;
                    System.out.print("Exists ");
                }
                catch (Exception e) {

                    try (
                            Connection conn = db.getConnection();
                            Statement st = conn.createStatement();) {

                        int results = st.executeUpdate(sql);
                        canImport = true;
                        System.out.print("Created ");
                    }
                    catch (Exception e2) {

                        System.out.println("Failed to create!");

//                        System.out.println("SQL: " + sql);
//                        e2.printStackTrace();
                        canImport = false;

                    }
                }

                /**
                 * ***********************************************************
                 * Importing
                 * **********************************************************
                 */
                canImport = true;
                if (canImport) {
                    System.out.print("Importing ");
                    while (r.hasNext()) {
                        // Iterate through all rows
                        Row row = r.next();
                        try (Connection conn = db.getConnection();
                                Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet rs = st.executeQuery("SELECT * FROM " + tableName);) {

                            installRow(row, metaData, rs);
//                            if (x % rowsPrDot == 0) {
                            System.out.print(".");
//                            }

                        }
                        catch (Exception e2) {
                            System.out.println("Failed to create! ");
                            e2.printStackTrace();
                        }
                    }
                    System.out.println();

                } else {
                    System.out.println("Not importing!");
                }

            }
            catch (Exception e) {
                System.out.println("Failed: " + e.getMessage());
            }

        }

        /**
         * ***********************************************************
         * Post Install Script
         * **********************************************************
         */
        try (Connection conn = db.getConnection();) {
            System.out.println("Installing Post Scripts...");
            for (int y = 0; y < tables.size(); y++) {
                String table = tables.get(y);
                MetaData metaData = metas.get(y);
                List<String> autoNumberScripts = metaData.getAutoNumberScript(table, db);
                System.out.println((y + 1) + "/" + tables.size() + " Table: " + table + " AutoNumberScripts: " + autoNumberScripts.size());
                for (String q : autoNumberScripts) {
                    System.out.println(" SQL: " + q);
                    try (Statement st = conn.createStatement();) {
                        st.executeUpdate(q);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                List<String> constraintsScripts = metaData.getConstraintScript(table, db);
                System.out.println((y + 1) + "/" + tables.size() + " " + table + " ConstraintScripts: " + constraintsScripts.size());
                for (String q : constraintsScripts) {
                    System.out.println(" SQL: " + q);
                    try (Statement st = conn.createStatement();) {
                        st.executeUpdate(q);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Inserts one record of data into database
     *
     * @param row
     * @param metaData
     * @param rs
     * @throws SQLException
     */
    public void installRow(Row row, MetaData metaData, ResultSet rs) throws SQLException {
        // Prepare new row
        rs.moveToInsertRow();

        // Fill data
        for (int x = 0; x < metaData.getColumnCount(); x++) {
            // Get column definition
            Column c = metaData.getColumn(x);
            int columnIndex = x + 1;
            // Each row
            c.updateResultSet(columnIndex, row, rs);
        }

        // Add new row
        rs.insertRow();
    }

//        System.out.println("Running post-installation foreignKey scripts...");
//        for (int x = 0; x < tables.size(); x++) {
//            // Find table
//            Table table = tables.get(x);
//            File data = datas.get(x);
//            boolean wasCreated = postInstallForeignKey(table);
//            System.out.println((x + 1) + "/" + tables.size() + " " + table.getName() + " - ForeignKey: " + (wasCreated ? "Ok" : "Failed"));
//        }
//    }
    /**
     * Run required SQL scripts after installing data
     *
     * @param t
     * @return
     */
    public boolean postInstallAutoNumber(Table t) {
        if (t.isPostInstallRequired()) {
            try (
                    Connection conn = db.getConnection();
                    Statement st = conn.createStatement();) {
                for (String q : t.getPostAutoNumberScript(db)) {
                    try {
                        st.executeUpdate(q);
                    }
                    catch (Exception e) {
                        System.out.println(t.getName() + ": AutoNumber failed! Script: " + q + " Error: " + e.getMessage());
                        return false;
                    }
                }
                return true;
            }
            catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean postInstallForeignKey(Table t) {
        if (t.isPostInstallRequired()) {
            try (
                    Connection conn = db.getConnection();
                    Statement st = conn.createStatement();) {
                for (String q : t.getPostConstraintScript(db)) {
                    try {
                        st.executeUpdate(q);
                        System.out.println("Updated FK: " + q);
                    }
                    catch (Exception e) {
                        System.out.println(t.getName() + ": Constraint failed! Script: " + q + " Error: " + e.getMessage());
                        return false;
                    }
                }
                return true;
            }
            catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

}
