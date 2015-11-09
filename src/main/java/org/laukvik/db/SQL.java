/*
 * Copyright (C) 2014 morten
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
package org.laukvik.db;

import java.util.logging.Logger;
import org.laukvik.db.sql.cmd.App;
import org.laukvik.db.sql.cmd.Backup;
import org.laukvik.db.sql.cmd.CommandManager;
import org.laukvik.db.sql.cmd.DisplayFunction;
import org.laukvik.db.sql.cmd.ExportScripts;
import org.laukvik.db.sql.cmd.ExportTable;
import org.laukvik.db.sql.cmd.ExportTableDDL;
import org.laukvik.db.sql.cmd.Import;
import org.laukvik.db.sql.cmd.ListConnections;
import org.laukvik.db.sql.cmd.ListDateFunctions;
import org.laukvik.db.sql.cmd.ListNumericFunctions;
import org.laukvik.db.sql.cmd.ListStringFunctions;
import org.laukvik.db.sql.cmd.ListSystemFunctions;
import org.laukvik.db.sql.cmd.ListTables;
import org.laukvik.db.sql.cmd.ListUserFunctions;
import org.laukvik.db.sql.cmd.ListViews;
import org.laukvik.db.sql.cmd.Query;
import org.laukvik.db.sql.cmd.Restore;
import org.laukvik.db.sql.cmd.TableRowCount;
import org.laukvik.db.sql.cmd.UniqueValues;
import org.laukvik.db.sql.cmd.Wizard;

/**
 * list unique values in a column list rowcount for each table connection wizard
 * in console html export
 */
public class SQL {

    private static final Logger LOG = Logger.getLogger(SQL.class.getName());

    public static void main(String[] args) {
        CommandManager mgr = new CommandManager("sql");
        mgr.add(new ListConnections());
        mgr.add(new App());
        mgr.add(new Backup());
        mgr.add(new ListDateFunctions());
        mgr.add(new DisplayFunction());
        mgr.add(new ExportScripts());
        mgr.add(new Import());
        mgr.add(new ListTables());
        mgr.add(new ListUserFunctions());
        mgr.add(new ListViews());
        mgr.add(new ListNumericFunctions());
        mgr.add(new Query());
        mgr.add(new Restore());
        mgr.add(new ListStringFunctions());
        mgr.add(new ListSystemFunctions());
        mgr.add(new ExportTableDDL());
        mgr.add(new ExportTable());
        mgr.add(new TableRowCount());
        mgr.add(new Wizard());
        mgr.add(new UniqueValues());
        int status = mgr.run(args);
    }

}
