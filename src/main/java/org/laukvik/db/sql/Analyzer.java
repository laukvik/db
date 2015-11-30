package org.laukvik.db.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.csv.swing.Unique;
import org.laukvik.db.ddl.AutoIncrementColumn;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.ForeignKey;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.ddl.FunctionParameter;
import org.laukvik.db.ddl.Schema;
import org.laukvik.db.ddl.SizeColumn;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.ddl.View;
import org.laukvik.db.sql.swing.DatabaseConnectionFileFilter;

/**
 * Created by morten on 14.10.2015.
 */
public class Analyzer {

    private final static Logger LOG = Logger.getLogger(Analyzer.class.getName());

    public Analyzer() {
    }

    public static List<DatabaseConnection> findDatabaseConnections() {
        File home = getConnectionsHome();
        List<DatabaseConnection> items = new ArrayList<>();
        for (File f : home.listFiles(new DatabaseConnectionFileFilter())) {
            DatabaseConnection db = new DatabaseConnection();
            String name = f.getName();
            db.setFilename(name.substring(0, name.length() - ".properties".length()));
            items.add(db);
        }
        return items;
    }

    public static File getDiagramFile(DatabaseConnection db) {
        File home = new File(getConnectionsHome(), db.getFilename() + ".dgm");
        return home;
    }

    public static File getLibraryHome() {
        File home = new File(System.getProperty("user.home"), "Library");
        if (!home.exists()) {
            home.mkdir();
        }
        return home;
    }

    public static File getConnectionsHome() {
        File home = new File(getLibraryHome(), "org.laukvik.db");
        if (!home.exists()) {
            home.mkdir();
        }
        return home;
    }

    /**
     * Find all schemas in database
     *
     * @return
     */
    public List<Schema> findSchemas(DatabaseConnection db) throws IOException {
        LOG.log(Level.FINE, "Finding all schemas in ''{0}''...", db.getFilename());
        List<Schema> list = new ArrayList<>();
        try {
            DatabaseMetaData dbmd = db.getConnection().getMetaData();
            try (ResultSet rs = dbmd.getSchemas()) {
                while (rs.next()) {
                    list.add(new Schema(rs.getString(3)));
                }
            }
            catch (SQLException e) {
            }
        }
        catch (SQLException e) {
            LOG.warning(e.getMessage());
        }
        return list;
    }

    public Schema findDefaultSchema(DatabaseConnection db) throws IOException {
        return findSchema(db.getSchema(), db);
    }

    /**
     * Finds all details in schema
     *
     * @param schemaName
     * @param db
     * @return
     */
    public Schema findSchema(String schemaName, DatabaseConnection db) throws IOException {
        Schema schema = new Schema(schemaName);
        for (Table t : findTables("", schemaName, db)) {
            schema.addTable(t);
        }

        for (View v : findViews(schemaName, db)) {
            schema.addView(v);
        }
        try {
            for (Function f : findUserFunctions(schemaName, db)) {
                schema.addFunction(f);
            }
        }
        catch (Exception e) {
//            e.printStackTrace();
        }

        for (Function f : listSystemFunctions(db)) {
            schema.addSystemFunction(f);
        }
        for (Function f : listStringFunctions(db)) {
            schema.addStringFunction(f);
        }
        for (Function f : listTimeDateFunctions(db)) {
            schema.addTimeFunction(f);
        }
        for (Function f : listNumbericFunctions(db)) {
            schema.addNumericFunction(f);
        }
        return schema;
    }

    /**
     * Finds all tables in database
     *
     * @param catalog catalog a catalog name; must match the catalog name as it
     * is stored in the database; "" retrieves those without a catalog;
     * <code>null</code> means that the catalog name should not be used to
     * narrow the search
     *
     * @param schemaPattern schemaPattern a schema name pattern; must match the
     * schema name as it is stored in the database; "" retrieves those without a
     * schema; <code>null</code> means that the schema name should not be used
     * to narrow the search
     * @param db
     * @return
     */
    public List<Table> findTables(String catalog, String schemaPattern, DatabaseConnection db) {
        LOG.log(Level.FINE, "Finding tables in {0}", db);
        List<Table> tables = new ArrayList<>();
        //
        try (Connection conn = db.getConnection()) {
            // Find all tables
            String tableNamePattern = "%";
            String[] types = {"TABLE"};
            schemaPattern = null;
            catalog = null;
            try (ResultSet rs = conn.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, types)) {
                while (rs.next()) {
//                    System.out.println(rs.getString("TABLE_NAME") + " Type=" + rs.getString("TABLE_TYPE") + " Catalog=" + rs.getString("TABLE_CAT") + " Schema=" + rs.getString("TABLE_SCHEM") + " TYPE_SCHEM=" + rs.getString("SELF_REFERENCING_COL_NAME"));
                    Table t = new Table(rs.getString("TABLE_NAME"));
                    tables.add(t);
                }
                LOG.log(Level.FINE, "Found {0} tables in {1}", new Object[]{tables.size(), db});
            }
            catch (SQLException e) {
                LOG.log(Level.WARNING, "Could not find tables! Message: {0}", e.getMessage());
            }
            /**
             * Column definitions
             *
             */
            for (Table t : tables) {
                try (ResultSet rs = conn.getMetaData().getColumns(catalog, schemaPattern, t.getName(), null)) {
                    while (rs.next()) {
                        // Find datatype
                        int dataType = rs.getInt(5);
                        String columnName = rs.getString(4);
                        Column c = Column.parse(dataType, columnName);
                        // Size
                        if (c instanceof SizeColumn) {
                            SizeColumn sc = (SizeColumn) c;
                            sc.setSize(rs.getInt(7));
                        }
                        //
                        // Comments
                        //
                        c.setComments(rs.getString("REMARKS"));
                        //
                        // Auto Increment
                        //
                        if (c instanceof AutoIncrementColumn) {
                            try {
                                AutoIncrementColumn ic = (AutoIncrementColumn) c;
                                String auto = rs.getString("IS_AUTOINCREMENT");
                                ic.setAutoIncrement(auto.toLowerCase().equalsIgnoreCase("y"));
                            }
                            catch (Exception e) {
                                LOG.log(Level.WARNING, "Failed to get autoincrement! Error: {0}", e.getMessage());
                            }
                        }
                        // Allow nulls
                        c.setAllowNulls(rs.getInt("NULLABLE") == 1);
                        // Default values
                        String defValue = rs.getString("COLUMN_DEF");
                        c.setDefaultValue(rs.wasNull() ? null : defValue);
                        //
                        t.getMetaData().addColumn(c);
                    }
                }
                catch (Exception e) {
                    LOG.warning(e.getMessage());
                }
            }

            /**
             * Foreign Keys
             *
             */
            for (Table t : tables) {
                try (ResultSet rs = conn.getMetaData().getImportedKeys(catalog, schemaPattern, t.getName())) {
                    while (rs.next()) {
                        String pkTable = rs.getString("PKTABLE_NAME");
                        String pkColumn = rs.getString("PKCOLUMN_NAME");
                        String fkTable = rs.getString("FKTABLE_NAME");
                        String fkColumn = rs.getString("FKCOLUMN_NAME");

                        LOG.log(Level.FINE, "ForeignKey {0}: {1}.{2} => {3}.{4}", new Object[]{t.getName(), fkTable, fkColumn, pkTable, pkColumn});
                        Column c = t.getMetaData().getColumn(fkColumn);
                        if (c != null) {
                            c.setForeignKey(new ForeignKey(pkTable, pkColumn));
                        }
                    }
                }
                catch (Exception e) {
                    LOG.log(Level.WARNING, "Could not find foreignKeys for table {0}", t.getName());
                }
            }

            // Primary keys
            for (Table t : tables) {
                try (ResultSet rs = conn.getMetaData().getPrimaryKeys(catalog, schemaPattern, t.getName())) {
                    while (rs.next()) {
                        LOG.log(Level.FINE, "PrimaryKey: {0} {1}", new Object[]{rs.getString(3), rs.getString(4)});
                        Column c = t.getMetaData().getColumn(rs.getString(4));
                        if (c != null) {
                            c.setPrimaryKey(true);
                        }
                        //System.out.println("Column: " + rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(6) + " ");
                    }
                }
                catch (Exception e) {
                    LOG.log(Level.WARNING, "Could not find primaryKeys for table {0}", t.getName());
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Find all user functions
     *
     * @param schema
     * @param db
     * @return
     */
    public List<Function> findUserFunctions(String schema, DatabaseConnection db) {
        List<Function> list = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String catalog = null;
            try (ResultSet rs = dbmd.getFunctions(catalog, schema, "%")) {
                while (rs.next()) {
                    Function f = new Function(rs.getString(1));
                    f.setUserFunction(true);
                    list.add(f);
                }
            }
            catch (SQLException e2) {
                LOG.log(Level.WARNING, "Could not find user functions: {0}", e2.getMessage());
            }

        }
        catch (Exception e) {
            LOG.warning("Could not find user functions: " + e.getMessage());
        }
        return list;
    }

    /**
     * Finds all views
     *
     * @param schema
     * @param db
     * @return
     */
    public List<View> findViews(String schema, DatabaseConnection db) {
        List<View> list = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] types = {"VIEW"};
            try (ResultSet rs = dbmd.getTables(null, null, "%", types)) {
                while (rs.next()) {
                    list.add(new View(rs.getString(3)));
                }
            }
            catch (SQLException e) {
                LOG.info("Could not find views: " + e.getMessage());
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Finds all String functions
     *
     * @return
     */
    public List<Function> listStringFunctions(DatabaseConnection db) {
        List<Function> items = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] arr = dbmd.getStringFunctions().split(",\\s*");
            for (String f : arr) {
                items.add(new Function(f));
            }
        }
        catch (SQLException e) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Finds all numeric functions
     *
     * @param db
     * @return
     */
    public List<Function> listNumbericFunctions(DatabaseConnection db) {
        List<Function> items = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] arr = dbmd.getNumericFunctions().split(",\\s*");
            for (String f : arr) {
                Function func = new Function(f);
                func = findFunctionDetails(func, db);
                items.add(func);
            }
        }
        catch (SQLException e) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Finds all system functions
     *
     * @param db
     * @return
     */
    public List<Function> listSystemFunctions(DatabaseConnection db) {
        List<Function> items = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] arr = dbmd.getSystemFunctions().split(",\\s*");
            for (String f : arr) {
                Function func = new Function(f);
                func = findFunctionDetails(func, db);
                items.add(func);
            }
        }
        catch (SQLException e) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Find all time and date functions
     *
     * @param db
     * @return
     */
    public List<Function> listTimeDateFunctions(DatabaseConnection db) {
        List<Function> items = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] arr = dbmd.getTimeDateFunctions().split(",\\s*");
            for (String f : arr) {
                Function func = new Function(f);
                func = findFunctionDetails(func, db);
                items.add(func);
            }
        }
        catch (SQLException e) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     *
     * http://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html#getProcedureColumns(java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String)
     *
     * @param function
     * @return
     * @throws SQLException
     */
    public Function findFunctionDetails(Function function, DatabaseConnection db) throws SQLException, IOException {
        if (!function.isUserFunction()) {
            return function;
        }
        //LOG.info("Getting function details for " + function.getName() );
        try (
                Connection conn = db.getConnection();
                ResultSet rs = conn.getMetaData().getProcedureColumns("", "%", function.getName(), null)) {

            /**
             *
             * PROCEDURE_CAT, PROCEDURE_SCHEM, PROCEDURE_NAME, COLUMN_NAME,
             * COLUMN_TYPE, DATA_TYPE, TYPE_NAME, PRECISION, LENGTH, SCALE,
             * RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE,
             * SQL_DATETIME_SUB, CHAR_OCTECT_LENGTH, ORDINAL_POSITION,
             * IS_NULLABLE, SPECIFIC_NAM
             *
             */

            /* Iterate all columns */
            while (rs.next()) {
                //LOG.info("Function: " + function.getName() + " Parameter: " + rs.getString("REMARKS"));
                try {
                    FunctionParameter p = new FunctionParameter(rs.getString("COLUMN_NAME"));
                    p.setComments(rs.getString("REMARKS"));
                    function.addParameter(p);
                }
                catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to get function details for {0}", function.getName());
                }
            }

        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to find function details for {0}", function.getName());
        }

        /*
         int numCols = rs.getMetaData().getColumnCount();
         for (int i = 1; i <= numCols; i++) {
         if (i > 1) {
         System.out.print(", ");
         }
         System.out.print(rs.getMetaData().getColumnLabel(i));
         }
         System.out.println("");
         while (rs.next()) {
         function.setComments( rs.getString("REMARKS"));
         for (int i = 1; i <= numCols; i++) {
         if (i > 1) {
         System.out.print(", ");
         }
         System.out.print(rs.getString(i));
         }
         System.out.println("");
         }

         System.out.println("Comments: " + function.getComments());
         */
        return function;
    }

    public List<Unique> findUnique(String table, String column, DatabaseConnection db) {
        List<Unique> list = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            String catalog = null;
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT " + column + ", count(" + column + ") FROM " + table + " GROUP BY " + column + " ORDER BY " + column + " ASC")) {
                while (rs.next()) {
                    Unique<String> u = new Unique(rs.getString(1), rs.getInt(2));
                    list.add(u);
                }
            }
            catch (SQLException e2) {
                LOG.log(Level.INFO, "Could not find unique values in column {0} in table {1}. Exception: {2}", new Object[]{column, table, e2.getMessage()});
            }
        }
        catch (Exception e) {
            LOG.log(Level.INFO, "Could not find unique values in column {0} in table {1}. Exception: {2}", new Object[]{column, table, e.getMessage()});
        }
        return list;
    }

    /**
     * Returns the table name and its row count for each table in database
     *
     * @param db
     * @return
     */
    public List<Unique> findRowCount(DatabaseConnection db) {
        List<Unique> list = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            String catalog = null;
            String schema = null;
            for (Table t : findTables(catalog, schema, db)) {
                try (ResultSet rs = conn.createStatement().executeQuery("SELECT count(*) FROM " + t.getName())) {
                    while (rs.next()) {
                        Unique<String> u = new Unique(t.getName(), rs.getInt(1));
                        list.add(u);
                    }
                }
                catch (SQLException e2) {
                    LOG.log(Level.INFO, "Could not find row count in table {0}. Exception: {1}", new Object[]{t.getName(), e2.getMessage()});
                }
            }

        }
        catch (Exception e) {
            LOG.log(Level.INFO, "Could not find row counts. Exception: {0}", new Object[]{e.getMessage()});
        }
        return list;
    }

}
