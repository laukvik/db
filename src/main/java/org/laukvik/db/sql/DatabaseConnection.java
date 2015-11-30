/*
 * Copyright (C) 2015 Morten Laukvik <morten@laukvik.no>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.laukvik.db.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.laukvik.db.DatabaseType;

/**
 *
 *
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class DatabaseConnection {

    private final static Logger LOG = Logger.getLogger(DatabaseConnection.class.getName());

    private String name;
    private String url;
    private String user;
    private String password;
    private String server;
    private String port;
    private String driver;
    private String database;
    private String schema;
    private boolean readOnly;

    public DatabaseConnection() {
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.parseFuzzy(driver);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getConnectionURL() {
        return "jdbc:" + driver + "://" + server + ":" + port + "/" + database;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getFilename() {
        return name;
    }

    public void setFilename(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Connection getConnection() throws SQLException, IOException {
        if (url == null || url.trim().isEmpty()) {
            return DriverManager.getConnection(getConnectionURL(), getUser(), getPassword());
        } else {
            return DriverManager.getConnection(url, getUser(), getPassword());
        }
    }

    public String toString() {
        return "named connection '" + name + "'";
    }

    public boolean canConnect() {
        try (
                Connection conn = getConnection()) {
            conn.getMetaData().getDatabaseProductName();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean isMissingDriver() {
        try (
                Connection conn = getConnection()) {
            conn.getMetaData().getDatabaseProductName();
            return false;
        }
        catch (Exception e) {
            return e.getMessage().toLowerCase().contains("no suitable driver found");
        }
    }

    public static DatabaseConnection read(String namedConnection) throws DatabaseConnectionNotFoundException, DatabaseConnectionInvalidException {
        File f = new File(Analyzer.getConnectionsHome(), namedConnection + ".properties");
        if (!f.exists()) {
            throw new DatabaseConnectionNotFoundException(namedConnection);
        }
        return DatabaseConnection.read(f);
    }

    public static DatabaseConnection read(File f) throws DatabaseConnectionNotFoundException, DatabaseConnectionInvalidException {
        if (!f.exists()) {
            throw new DatabaseConnectionNotFoundException(f);
        }
        DatabaseConnection db = new DatabaseConnection();
        // Read settings file
        Properties p = new Properties();
        String namedConnection = f.getName();
        try {
            p.load(new FileInputStream(f));
            db.setFilename(namedConnection);
            db.setUrl(p.getProperty("url"));
            db.setUser(p.getProperty("user"));
            db.setPassword(p.getProperty("password"));
            db.setPort(p.getProperty("port"));
            db.setServer(p.getProperty("server"));
            db.setDatabase(p.getProperty("database"));
            db.setDriver(p.getProperty("driver"));
            db.setSchema(p.getProperty("schema"));
            String r = p.getProperty("readonly");
            if (r == null) {
                db.setReadOnly(true);

            } else if (p.getProperty("readonly").equalsIgnoreCase("true")) {
                db.setReadOnly(true);

            } else if (p.getProperty("readonly").equalsIgnoreCase("no")) {
                db.setReadOnly(false);
            }
        }
        catch (FileNotFoundException e) {
            throw new DatabaseConnectionNotFoundException(namedConnection);
        }
        catch (IOException e) {
            throw new DatabaseConnectionInvalidException(namedConnection);
        }
        return db;
    }

    public boolean isEmpty() {
        return server == null;
    }
}
