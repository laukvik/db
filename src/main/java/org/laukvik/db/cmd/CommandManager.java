package org.laukvik.db.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.DatabaseConnectionInvalidException;
import org.laukvik.db.sql.DatabaseConnectionNotFoundException;

/**
 *
 *
 */
public class CommandManager {

    private static final Logger LOG = Logger.getLogger(CommandManager.class.getName());
    private final List<Command> commands;
    private final String filename;
    private ResourceBundle bundle;

    public CommandManager(String filename) {
        this.filename = filename;
        //bundle = ResourceBundle.getBundle("messages");
        commands = new ArrayList<>();
    }

    public void add(Command command) {
        commands.add(command);
    }

    public Map<String, String> getActions() {
        Map<String, String> map = new HashMap<>();

        return map;
    }

    /**
     * Runs the specified named command
     *
     * @param args
     * @return
     */
    public int run(String... args) {
        Map<String, String> map = new HashMap<>();

        //
        String namedConnection = null;
        String action = null;
        String parameter = null;
        String encoding = args.length > 2 ? args[1] : null;

        map.put("encoding", encoding);

        //
        for (String a : args) {
            if (a.startsWith("-")) {
                // Found option
                action = a.substring(1);
                if (action.contains("=")) {
                    // Contains sub option with equals sign
                    String[] arr = action.split("=");
                    action = arr[0];
                    parameter = arr[1];
                }
            } else {
                // Not option - must be named connection
                namedConnection = a;
            }
        }

        LOG.log(Level.FINE, "Connection={0} action={1} parameter={2}", new Object[]{namedConnection, action, parameter});

        try {
            Command cmd = getCommandByName(action);
            LOG.log(Level.FINE, "Found named command {0}. Option={1}", new Object[]{action, parameter});
            if (cmd instanceof SqlCommand) {
                if (namedConnection == null || namedConnection.trim().isEmpty()) {
                    System.out.println("Please specify connection name");
                    return Command.ERROR;
                }
                try {
                    DatabaseConnection db = DatabaseConnection.read(namedConnection);
                    SqlCommand sqlCommand = (SqlCommand) cmd;
                    sqlCommand.setDatabaseConnection(db);
                    return sqlCommand.run(parameter, map);
                }
                catch (DatabaseConnectionNotFoundException e) {
                    LOG.log(Level.FINE, "Failed to connect to {0}", namedConnection);
                    System.out.println("Could not find connection with name '" + namedConnection + "'");
                    return Command.ERROR;
                }
                catch (DatabaseConnectionInvalidException e) {
                    LOG.log(Level.FINE, "Failed to read named conneciton {0}!", namedConnection);
                    return Command.ERROR;
                }

            } else {
                return cmd.run(parameter, map);
            }
        }
        catch (Exception e) {
            usage(null);
            return Command.ERROR;
        }
    }

    /**
     * Returns a named command
     *
     * @param name
     * @return
     */
    public Command getCommandByName(String name) throws org.laukvik.db.cmd.CommandNotFoundException {
        for (Command c : commands) {
            if (c.getAction().equalsIgnoreCase(name)) {
                return c;
            }
        }
        throw new CommandNotFoundException(name);
    }

    /**
     * Display application parameters
     *
     */
    public void usage(String action) {
        if (action != null) {
            System.out.println("sql: Illegal option " + action);
        }
        System.out.println("Usage: " + filename + " <action> <encoding> <connection>");
        for (Command c : commands) {
            if (c.getParameter() == null) {
                System.out.printf("\t-%-20s\t%s\n", c.getAction(), c.getDescription());
            } else {
                System.out.printf("\t-%-20s\t%s\n", c.getAction() + "=" + c.getParameter(), c.getDescription());
            }
        }
    }

}
