package org.laukvik.db.cmd;

import java.util.Map;

/**
 * Created by morten on 17.10.2015.
 */
public interface Command {

    int EXCEPTION = -1;
    int SUCCESS = 0;
    int ERROR = 1;

    String getAction();

    String getDescription();

    String getParameter();

    int run(String value, Map<String, String> props);

}
