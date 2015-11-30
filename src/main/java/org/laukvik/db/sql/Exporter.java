package org.laukvik.db.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.csv.io.CsvWriter;
import org.laukvik.db.ddl.Schema;
import org.laukvik.db.ddl.Table;

/**
 *
 *
 */
public class Exporter {

    private static final Logger LOG = Logger.getLogger(Exporter.class.getName());
    private final DatabaseConnection databaseConnection;

    public Exporter(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Exports all table
     *
     * @param directory
     * @param charset
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws DatabaseConnectionNotFoundException
     */
    public boolean backupCSV(File directory, Charset charset) throws IOException, SQLException, DatabaseConnectionNotFoundException {
        LOG.log(Level.FINE, "Creating CSV backup to {0}", directory.getAbsolutePath());
        Analyzer a = new Analyzer();
        Schema s = a.findSchema(databaseConnection.getSchema(), databaseConnection);
        int max = s.getTables().size();
        int counter = 0;
        if (max == 0) {
            return false;
        }
        for (Table t : s.getTables()) {
            counter++;
            LOG.log(Level.FINE, "{0}/{1} {2}", new Object[]{counter, max, t.getName()});
            System.out.println(counter + "/" + max + " " + t.getName());
            File dataFile = new File(directory.getAbsolutePath(), t.getName() + ".csv");
            exportTableCSV(t, dataFile, charset);
        }
        return true;
    }

    /**
     * Exports table data to file
     *
     * @param table
     * @param file
     * @param charset
     * @throws FileNotFoundException
     */
    public void exportTableCSV(Table table, File file, Charset charset) throws FileNotFoundException {
        LOG.log(Level.FINE, "Exporting table ''{0}'' to file {1}", new Object[]{table, file.getAbsolutePath()});
        try (
                Connection conn = databaseConnection.getConnection();
                OutputStream out = new FileOutputStream(file);
                CsvWriter writer = new CsvWriter(out, charset);
                ResultSet rs = conn.createStatement().executeQuery(table.getSelectTable())) {
            int columnCount = rs.getMetaData().getColumnCount();
            LOG.log(Level.FINE, "Found {0} columns in table {1}", new Object[]{columnCount, table.getName()});
//            System.out.println("Found " + columnCount + " columns in table " + table.getName());

            writer.writeMetaData(table.getMetaData());

            int rowCounter = 0;
            while (rs.next()) {
                rowCounter++;
                LOG.log(Level.FINE, "Row: {0}", rowCounter);
                System.out.print(".");
                String[] values = new String[columnCount];
                for (int x = 0; x < columnCount; x++) {
                    Object o = rs.getObject(x + 1);
                    values[x] = o == null ? "" : o.toString();
                }
                writer.writeRow(values);
            }
            System.out.println();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportScript(Schema schema, File file) {
        LOG.log(Level.INFO, "Exporting database to file: {0}", file.getAbsolutePath());
        try (FileOutputStream out = new FileOutputStream(file)) {
            List<Table> tables = schema.getTables();
            for (int z = 0; z < tables.size(); z++) {
                Table table = tables.get(z);
                System.out.print(table.getName() + ":");
                out.write(table.getDDL().getBytes());
                try (Connection conn = databaseConnection.getConnection();
                        ResultSet rs = conn.createStatement().executeQuery(table.getSelectTable())) {
                    int cols = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        out.write(table.getInsertSQL(rs).getBytes());
                        out.write("\n".getBytes());
                    }
                    System.out.print("#");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
            out.flush();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports all tables to System.out
     *
     * @param schemaName
     * @throws DatabaseExportFailedException
     * @throws IOException
     */
    public void exportDatabaseScripts(String schemaName) throws DatabaseExportFailedException, IOException {
        Analyzer a = new Analyzer();
        Schema schema = a.findSchema(schemaName, databaseConnection);
        List<Table> tables = schema.getTables();
        for (int z = 0; z < tables.size(); z++) {
            Table table = tables.get(z);
            System.out.println(table.getDDL());
            try (
                    Connection conn = databaseConnection.getConnection();
                    ResultSet rs = conn.createStatement().executeQuery(table.getSelectTable())) {
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    System.out.println(table.getInsertSQL(rs));
                }
            }
            catch (SQLException e) {
                throw new DatabaseExportFailedException(e, databaseConnection);
            }
            catch (IOException e) {
                throw new DatabaseExportFailedException(e, databaseConnection);
            }
        }
    }

    public void listQuery(String query) {
        try (
                Connection conn = databaseConnection.getConnection();
                ResultSet rs = conn.createStatement().executeQuery(query)) {
            int cols = rs.getMetaData().getColumnCount();
            for (int x = 0; x < cols; x++) {
                System.out.print(x > 0 ? "," : "");
                System.out.print(rs.getMetaData().getColumnLabel(x + 1));
            }
            while (rs.next()) {
                for (int x = 0; x < cols; x++) {
                    System.out.print(x > 0 ? "," : "");
                    System.out.print(rs.getObject(x + 1));
                }
                System.out.println();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
