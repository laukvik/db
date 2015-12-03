package org.laukvik.db.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class TextStatement implements Statement {

    private final TextConnection connection;
    private Manager mgr = new Manager();

    public TextStatement(TextConnection connection, File home) {
        this.connection = connection;
        this.mgr = new Manager(home);
    }

    public void addBatch(String sql) throws SQLException {
    }

    public void cancel() throws SQLException {
    }

    public void clearBatch() throws SQLException {
    }

    public void clearWarnings() throws SQLException {
    }

    public void close() throws SQLException {
    }

    public boolean execute(String sql) throws SQLException {
        return false;
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    public int[] executeBatch() throws SQLException {
        return null;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
//		System.out.println( "executeQuery: " + mgr.home );
        return mgr.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        return mgr.executeUpdate(sql);
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public int getFetchDirection() throws SQLException {
        return 0;
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    public int getMaxRows() throws SQLException {
        return 0;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    public int getResultSetType() throws SQLException {
        return 0;
    }

    public int getUpdateCount() throws SQLException {
        return 0;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void setCursorName(String name) throws SQLException {
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
    }

    public void setFetchDirection(int direction) throws SQLException {
    }

    public void setFetchSize(int rows) throws SQLException {
    }

    public void setMaxFieldSize(int max) throws SQLException {
    }

    public void setMaxRows(int max) throws SQLException {
    }

    public void setQueryTimeout(int seconds) throws SQLException {
    }

    public boolean isClosed() throws SQLException {
        return false;
    }

    public boolean isPoolable() throws SQLException {
        return false;
    }

    public void setPoolable(boolean arg0) throws SQLException {
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}