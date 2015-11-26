/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.ForeignKey;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.ddl.VarCharColumn;
import org.laukvik.db.sql.swing.TablePosition;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class DiagramApp extends Application {

    private static final Logger LOG = Logger.getLogger(DiagramApp.class.getName());

    private Color BACKGROUND = Color.LIGHTGRAY;
    private Color TABLE_OUTLINE = Color.BLACK;
    private Color SELECTED_HEADER_BACKGROUND = Color.BLUE;
    private Color TABLE_HEADER_BACKGROUND = Color.ORANGE;
    private Color TABLE_HEADER_FOREGROUND = Color.WHITE;
    private Color TABLE_TEXT = Color.BLACK;
    private Color TABLE_FILL = Color.WHITE;
    private Color FOREIGNKEY = Color.BLACK;
    private Image PKICON;
    private Image FKICON;
    final static int rowHeight = 20;
    final static int headerHeight = 20;
    final static int tableWidth = 150;
    final static int padding = 0;

    private File file;

    private List<TablePosition> positions;
    private TablePosition tablePosition;
    private List<Column> foreignKeys;
    private int deltaX, deltaY;

    private Stage stage;
    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        positions = new ArrayList<>();
        foreignKeys = new ArrayList<>();
        this.file = null;
        try {
            PKICON = new Image("./sql/swing/icons/pk_decorate.gif", false);
            FKICON = new Image("./sql/swing/icons/pkfk_decorate.gif", false);
        }
        catch (Exception e) {
            LOG.info("Could not load image " + e.getMessage());
        }

        mockData();

        stage.setTitle("Diagram");
        Group root = new Group();
        this.canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
//        drawShapes(gc);
        paint(gc);
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        stage.show();

    }

    private Dimension calculateSize() {
        int rightMost = 0;
        int bottomMost = 0;
        int x = 0;
        for (TablePosition tp : positions) {
            Rectangle r = getRectangle(tp);
            if (r.x + r.width > rightMost) {
                rightMost = r.x + r.width;
            }
            if (r.y + r.height > bottomMost) {
                bottomMost = r.y + r.height;
            }
            x++;
        }
        return new Dimension(rightMost, bottomMost);
    }

    private Rectangle getRectangle(TablePosition table) {
        return new Rectangle(table.getPoint(), new Dimension(tableWidth, (table.getTable().getMetaData().getColumnCount() + 1) * rowHeight));
    }

    public void mockData() {
        Table employee = new Table("Employee");

        Column eID = new IntegerColumn("employeeID");
        eID.setPrimaryKey(true);
        eID.setAllowNulls(true);
        employee.getMetaData().addColumn(eID);

        employee.addColumn(new VarCharColumn("firstName"));
        employee.addColumn(new VarCharColumn("lastName"));
        employee.addColumn(new VarCharColumn("email"));

        IntegerColumn employeeCompanyID = new IntegerColumn("companyID");
        employeeCompanyID.setForeignKey(new ForeignKey("Company", "companyID"));

        IntegerColumn employeeDepartmentID = new IntegerColumn("departmentID");
        employeeDepartmentID.setForeignKey(new ForeignKey("Department", "departmentID"));

        employee.addColumn(employeeCompanyID);
        employee.addColumn(employeeDepartmentID);

        // Company
        Table company = new Table("Company");
        Column cID = new IntegerColumn("companyID");
        cID.setPrimaryKey(true);

        company.addColumn(cID);
        company.addColumn(new VarCharColumn("name"));

        // Department
        Table department = new Table("Department");
        Column dID = new IntegerColumn("departmentID");
        dID.setPrimaryKey(true);
        dID.setAllowNulls(false);
        department.addColumn(dID);
        department.addColumn(new VarCharColumn("name"));
        department.addColumn(new VarCharColumn("contact"));

        IntegerColumn companyID = new IntegerColumn("companyID");
        ForeignKey contactFK = new ForeignKey("Company", "companyID");
        companyID.setForeignKey(contactFK);

        addTable(employee);
        addTable(company);
        addTable(department);

        setTableLocation(new Point(10, 50), employee);
        setTableLocation(new Point(150, 100), company);
        setTableLocation(new Point(300, 300), department);
    }

    public void setTableLocation(Point point, Table table) {
        LOG.log(Level.FINE, "Setting location for table {0} to {1}", new Object[]{table, point});
        TablePosition tp = findTable(table);
        tp.setPoint(point);
        fireTablesChanged();
    }

    public void autoLayout(int panelWidth) {
        for (int index = 0; index < positions.size(); index++) {
            TablePosition tp = positions.get(index);
            int blockWidth = tableWidth + padding;
            int blockHeight = 200;
            int tablesPrRow = panelWidth / blockWidth;
            tp.setPoint(new Point((index % tablesPrRow) * blockWidth, (index / tablesPrRow) * blockHeight));
        }
        //        setSize(calculateSize());
    }

    public void addTable(Table table) {
        LOG.log(Level.FINE, "Added table: {0}", table);
        positions.add(new TablePosition(table, new Point(0, 0)));
        fireTablesChanged();
    }

    private void fireTablesChanged() {
        Dimension size = calculateSize();
//        setPreferredSize(size);
//        setMinimumSize(size);
//        setSize(size);
//        canvas.setWidth(size.getWidth());
//        canvas.setHeight(size.getHeight());

        findForeignKeys();

    }

    private void findForeignKeys() {
        foreignKeys.clear();
        for (TablePosition tp : positions) {
            Table t = tp.getTable();
            for (int x = 0; x < t.getMetaData().getColumnCount(); x++) {
                Column c = t.getMetaData().getColumn(x);
                if (c.getForeignKey() != null) {
                    Column primaryKey = findPrimaryKey(c.getForeignKey());
                    foreignKeys.add(c);
                }
            }
        }
    }

    private Column findPrimaryKey(ForeignKey fk) {
        TablePosition tp = findTableByName(fk.getTable());
        if (tp == null) {
            return null;
        }
        Table t = tp.getTable();
        for (int x = 0; x < t.getMetaData().getColumnCount(); x++) {
            Column c = t.getMetaData().getColumn(x);
            if (c.getName().equalsIgnoreCase(fk.getColumn())) {
                return c;
            }
        }
        return null;
    }

    private TablePosition findTableByName(String table) {
        for (TablePosition tp : positions) {
            if (tp.getTable().getName().equalsIgnoreCase(table)) {
                return tp;
            }
        }
        return null;
    }

    private void drawShapes(GraphicsContext g) {
        g.setFill(Color.GREEN);
        g.setStroke(Color.BLUE);
        g.setLineWidth(5);
        g.strokeLine(40, 10, 10, 40);
        g.fillOval(10, 60, 30, 30);
        g.strokeOval(60, 60, 30, 30);
        g.fillRoundRect(110, 60, 30, 30, 10, 10);
        g.strokeRoundRect(160, 60, 30, 30, 10, 10);
        g.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        g.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        g.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        g.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        g.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        g.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        g.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        g.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        g.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    private TablePosition findTable(Table table) {
        for (TablePosition tp : positions) {
            if (tp.getTable().equals(table)) {
                return tp;
            }
        }
        return null;
    }

    private int getIndex(Table table) {
        TablePosition tp = findTable(table);
        return positions.indexOf(tp);
    }

    private Column findColumnTarget(ForeignKey fk) {
        TablePosition tp = findTableByName(fk.getTable());
        Column c = tp.getTable().getMetaData().getColumn(fk.getColumn());
        return c;
    }

    private void paintTable(TablePosition table, GraphicsContext g) {
//        g.setStroke(new BasicStroke(1));
        g.setLineWidth(1);
        Point p = table.getPoint();
        int x = p.x;
        int y = p.y;
        int height = rowHeight * (table.getTable().getMetaData().getColumnCount() + 1);

        g.setFill(TABLE_OUTLINE);
        g.fillRoundRect(x, y, tableWidth, height, 10, 10);
        g.setFill(TABLE_FILL);
        g.fillRoundRect(x + 1, y + 1, tableWidth - 2, height - 2, 10, 10);

        /* Draw table name centered */
//        int textWidth = getFontMetrics(getFont()).stringWidth(table.getTable().getName());
//        int textHeight = getFontMetrics(getFont()).getHeight();
        int textWidth = 200;
        int textHeight = 20;
        g.setFill(table.isSelected() ? SELECTED_HEADER_BACKGROUND : TABLE_HEADER_BACKGROUND);
        g.fillRoundRect(x + 1, y + 1, tableWidth - 2, 20, 10, 10);
        g.fillRect(x + 1, y + 12, tableWidth - 2, 10);

        g.setFill(TABLE_HEADER_FOREGROUND);
//        g.fillText(table.getTable().getName(), p.x + ((150 - textWidth) / 2), p.y + textHeight);
        g.fillText(table.getTable().getName(), p.x + 10, p.y + 15);


        /* Paint columns */
        g.setFill(TABLE_TEXT);
        for (int n = 0; n < table.getTable().getMetaData().getColumnCount(); n++) {
            Column c = table.getTable().getMetaData().getColumn(n);
            if (c.isPrimaryKey()) {
                if (PKICON != null) {
                    g.drawImage(PKICON, x + 5, y + 20 + n * rowHeight);
                }
            }
            g.fillText(c.getName(), x + 25, y + 35 + n * rowHeight);

            if (c.getForeignKey() != null) {
                if (FKICON != null) {
                    g.drawImage(FKICON, x + 130, y + 20 + n * rowHeight);
                }

            }
        }
    }

    /**
     * Paints a foreignKey line between two points
     *
     * @param g
     * @param start
     * @param end
     */
    private void paintForeignKeyLine(GraphicsContext g, Point start, Point end) {
        g.setFill(FOREIGNKEY);
//        g.drawLine(start.x, start.y, end.x, end.y);
        g.moveTo(start.x, start.y);
        g.lineTo(end.x, end.y);
    }

    private void paintForeignKey(Column column, GraphicsContext g) {
        // Get start table
        TablePosition tpStart = findTable(column.getTable());
//        int tableIndex = getIndex(column.getTable());
        if (tpStart == null) {
            LOG.fine("Cant paint foreignKey " + column + " because table target isnt found!");
            return;
        }
        if (column.getForeignKey() == null) {
            // No foreign key
            LOG.fine("Cant paint foreignKey " + column + " because target isnt specified!");
        } else {
            Column pk = findColumnTarget(column.getForeignKey());
            if (pk == null) {
                // Should never happen
                LOG.fine("Cant paint foreignKey " + column + " because target is null!");
            } else {
                TablePosition tpEnd = findTable(pk.getTable());
                if (tpEnd == null) {
                    LOG.fine("Cant paint foreignKey " + column + " because target cant be found!");
                } else {
                    LOG.fine("Painting fk: " + column + " to " + column.getForeignKey());
                    Point start = tpStart.getPoint();
                    start.y += column.indexOf() * rowHeight + headerHeight + (rowHeight / 2);
                    start.x += tableWidth;

                    Point end = tpEnd.getPoint();
                    end.y += pk.indexOf() * rowHeight + headerHeight + (rowHeight / 2);

                    paintForeignKeyLine(g, start, end);
                }

            }
        }
    }

    /**
     *
     *
     * @param g
     */
    public void paint(GraphicsContext g) {
        /* Paint the foreign key lines */
        for (Column fk : foreignKeys) {
            paintForeignKey(fk, g);
        }

        /* Paint each table */
        for (TablePosition tp : positions) {
            paintTable(tp, g);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
