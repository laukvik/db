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
package org.laukvik.db.sql.swing;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.laukvik.db.ddl.Function;
import org.laukvik.db.ddl.Schema;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.ddl.View;
import org.laukvik.db.sql.Analyzer;
import org.laukvik.db.sql.DatabaseConnection;
import org.laukvik.db.sql.swing.icons.ResourceManager;

/**
 * A model for the database connection and a renderer for tree nodes
 *
 * @author morten
 */
public class TreeModel extends DefaultTreeCellRenderer implements javax.swing.tree.TreeModel {

    private static final Logger LOG = Logger.getLogger(TreeModel.class.getName());
    private final List<TreeModelListener> listeners;
    private DatabaseConnection db;
    private Schema schema;
    private String tables;
    private String views;
    private String functions;
    private String systemFunctions;
    private String timeFunctions;
    private String stringFunctions;
    private String numericFunctions;
    private Icon ICON_TABLE = ResourceManager.getIcon("table.gif");
    private Icon ICON_DATABASE = ResourceManager.getIcon("db.gif");
    private Icon ICON_VIEW = ResourceManager.getIcon("view.gif");
    private Icon ICON_FUNCTION = ResourceManager.getIcon("table.gif");

    public TreeModel() {
        super();
        listeners = new ArrayList<>();
        tables = "Tables";
        views = "Views";
        functions = "Functions";
        systemFunctions = "System Functions";
        timeFunctions = "Time Functions";
        stringFunctions = "String Functions";
        numericFunctions = "Numeric Functions";
        schema = new Schema();
    }

    public Schema getSchema() {
        return schema;
    }

    public String getTableRootNode() {
        return tables;
    }

    /**
     *
     *
     * @param db
     */
    public void setDatabaseConnection(DatabaseConnection db) {
        this.db = db;
        if (db.isEmpty()) {

        } else {
            try {
                Analyzer a = new Analyzer();
                schema = a.findSchema(db.getSchema(), db);
                TreeModelEvent evt = new TreeModelEvent(this, new TreePath(schema));
                for (TreeModelListener l : listeners) {
                    l.treeStructureChanged(evt);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getRoot() {
        return schema;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == schema) {
            return 7;

        } else if (parent == tables) {
            return schema.getTables().size();

        } else if (parent == views) {
            return schema.getViews().size();

        } else if (parent == functions) {
            return schema.getFunctions().size();

        } else if (parent == systemFunctions) {
            return schema.getSystemFunctions().size();
        } else if (parent == timeFunctions) {
            return schema.getTimeFunctions().size();
        } else if (parent == stringFunctions) {
            return schema.getStringFunctions().size();
        } else if (parent == numericFunctions) {
            return schema.getNumericFunctions().size();

        } else {
            return 0;
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == schema) {
            switch (index) {
                case 0:
                    return tables;
                case 1:
                    return views;
                case 2:
                    return functions;

                case 3:
                    return systemFunctions;
                case 4:
                    return timeFunctions;
                case 5:
                    return stringFunctions;
                case 6:
                    return numericFunctions;
                default:
                    return null;
            }
        } else if (parent == tables) {
            return schema.getTables().get(index);

        } else if (parent == views) {
            return schema.getViews().get(index);

        } else if (parent == functions) {
            return schema.getFunctions().get(index);

        } else if (parent == systemFunctions) {
            return schema.getSystemFunctions().get(index);
        } else if (parent == timeFunctions) {
            return schema.getTimeFunctions().get(index);
        } else if (parent == stringFunctions) {
            return schema.getStringFunctions().get(index);
        } else if (parent == numericFunctions) {
            return schema.getNumericFunctions().get(index);

        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object node) {
        //LOG.info("getIndexOfChild: " + parent + " child: " + node);
        if (parent == schema) {

            if (node == tables) {
                return 0;
            } else if (node == views) {
                return 1;
            } else if (node == functions) {
                return 2;

            } else if (node == systemFunctions) {
                return 3;
            } else if (node == timeFunctions) {
                return 4;
            } else if (node == stringFunctions) {
                return 5;
            } else if (node == numericFunctions) {
                return 6;

            } else {
                return -1;
            }

        } else if (parent == tables) {
            return schema.getTables().indexOf(node);

        } else if (parent == views) {
            return schema.getViews().indexOf(node);

        } else if (parent == functions) {
            return schema.getFunctions().indexOf(node);

        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object parent) {
        if (parent == schema) {
            return false;

        } else if (parent == tables) {
            return false;

        } else if (parent == views) {
            return false;

        } else if (parent == functions) {
            return false;

        } else if (parent == systemFunctions) {
            return false;
        } else if (parent == timeFunctions) {
            return false;
        } else if (parent == stringFunctions) {
            return false;
        } else if (parent == numericFunctions) {
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setBackground(sel ? getBackgroundSelectionColor() : tree.getBackground());

        setOpaque(true);

        if (value == schema) {
            setIcon(ICON_DATABASE);
            setText(schema.isDefault() ? "default" : schema.getName());

        } else if (value == views) {
            setText("Views");

        } else if (value == tables) {
            setText("Tabeller");

        } else if (value == functions) {
            setText("Functions");

        } else if (value == systemFunctions) {
            setText("System");
        } else if (value == stringFunctions) {
            setText("String");
        } else if (value == timeFunctions) {
            setText("Time");
        } else if (value == numericFunctions) {
            setText("Numeric");

        } else if (value instanceof Table) {
            setText(((Table) value).getName());
            setIcon(ICON_TABLE);

        } else if (value instanceof View) {
            setText(((View) value).getName());

        } else if (value instanceof Function) {
            setText(((Function) value).getName());

        } else {
            setText(value.toString());
        }

        return this;
    }

}
