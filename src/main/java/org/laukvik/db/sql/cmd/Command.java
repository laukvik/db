package org.laukvik.db.sql.cmd;

/**
 * Created by morten on 17.10.2015.
 */
public interface Command {

    final int EXCEPTION = -1;
    final int SUCCESS = 0;
    final int ERROR = 1;

    public String getAction();

    public String getDescription();

    public String getParameter();

    public int run(String value);

}
