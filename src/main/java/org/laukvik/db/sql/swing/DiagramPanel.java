package org.laukvik.db.sql.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.ForeignKey;
import org.laukvik.db.ddl.Table;
import org.laukvik.db.sql.swing.icons.ResourceManager;

public class DiagramPanel extends JPanel implements MouseListener, MouseMotionListener {

    private final Logger LOG = Logger.getLogger(DiagramPanel.class.getName());

    private Color BACKGROUND = new Color(242, 242, 242);
    private Color TABLE_OUTLINE = new Color(160, 160, 160);
    private Color TABLE_HEADER_BACKGROUND = new Color(240, 156, 49);
    private Color TABLE_HEADER_FOREGROUND = new Color(255, 255, 255);
    private Color TABLE_TEXT = new Color(0, 0, 0);
    private Color TABLE_FILL = new Color(255, 255, 255);
    private Color FOREIGNKEY = new Color(170, 170, 170);
    final static Icon PKICON = ResourceManager.getIcon("pk_decorate.gif");
    final static Icon FKICON = ResourceManager.getIcon("pkfk_decorate.gif");
    final static int rowHeight = 20;
    final static int headerHeight = 20;
    final static int tableWidth = 150;
    final static int padding = 0;

    private List<Column> foreignKeys;
    private File file;

    private List<TablePosition> positions;

    public DiagramPanel() {
        super();
        this.file = null;
        setAutoscrolls(true);
        setBackground(BACKGROUND);
        positions = new ArrayList<>();
        foreignKeys = new ArrayList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void removeTables() {
        positions.clear();
        fireTablesChanged();
        repaint();
    }

    /**
     * Adds a table
     *
     * @param table
     */
    public void addTable(Table table) {
        LOG.log(Level.FINE, "Added table: {0}", table);
        positions.add(new TablePosition(table, new Point(0, 0)));
        fireTablesChanged();
    }

    /**
     * Creates a grid locations for all tables
     *
     * @param panelWidth
     */
    public void autoLayout(int panelWidth) {
        for (int index = 0; index < positions.size(); index++) {
            TablePosition tp = positions.get(index);
            int blockWidth = tableWidth + padding;
            int blockHeight = 200;
            int tablesPrRow = panelWidth / blockWidth;
            tp.setPoint(new Point((index % tablesPrRow) * blockWidth, (index / tablesPrRow) * blockHeight));
        }
        setSize(calculateSize());
    }

    private TablePosition findTable(Table table) {
        for (TablePosition tp : positions) {
            if (tp.getTable() == table) {
                return tp;
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

    public void removeTable(Table table) {
        TablePosition tp = findTable(table);
        positions.remove(tp);
        fireTablesChanged();
    }

    public void fireTablesChanged() {
        setPreferredSize(calculateSize());
        setSize(calculateSize());
        findForeignKeys();
    }

    /**
     *
     *
     * @param point
     * @param table
     */
    public void setTableLocation(Point point, Table table) {
        LOG.log(Level.FINE, "Setting location for table {0} to {1}", new Object[]{table, point});
        TablePosition tp = findTable(table);
        tp.setPoint(point);
//        int index = tables.indexOf(table);
//        if (index == -1) {
//            LOG.log(Level.FINEST, "Table {0} was not found!", table.getName());
//        } else {
//            locations.get(index).x = point.x;
//            locations.get(index).y = point.y;
//        }
        fireTablesChanged();
    }

    private Column findPrimaryKey(ForeignKey fk) {
        TablePosition tp = findTableByName(fk.getTable());
        Table t = tp.getTable();
        for (int x = 0; x < t.getMetaData().getColumnCount(); x++) {
            Column c = t.getMetaData().getColumn(x);
            if (c.getName().equalsIgnoreCase(fk.getColumn())) {
                return c;
            }
        }
        return null;
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

    public int getIndex(Table table) {
        TablePosition tp = findTable(table);
        return positions.indexOf(tp);
    }

    private Column findColumnTarget(ForeignKey fk) {
        TablePosition tp = findTableByName(fk.getTable());
        Column c = tp.getTable().getMetaData().getColumn(fk.getColumn());
        return c;
    }

    /**
     * Paints the foreign key line connecting the two tables
     *
     * @param column
     * @param g
     */
    private void paintForeignKey(Column column, Graphics g) {
        int tableIndex = getIndex(column.getTable());
        if (tableIndex == -1) {
            return;
        }
        if (column.getForeignKey() == null) {
            // No foreign key
        } else {
            Column pk = findColumnTarget(column.getForeignKey());

            if (pk == null) {
                // Should never happen
            } else {
                int endTableIndex = getIndex(pk.getTable());

                TablePosition tpStart = positions.get(tableIndex);
                TablePosition tpEnd = positions.get(endTableIndex);

                Point start = tpStart.getPoint();
                start.y += column.indexOf() * rowHeight + headerHeight + (rowHeight / 2);
                start.x += tableWidth;

                Point end = tpEnd.getPoint();
                end.y += pk.indexOf() * rowHeight + headerHeight + (rowHeight / 2);

                paintForeignKeyLine(g, start, end);
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
    private void paintForeignKeyLine(Graphics g, Point start, Point end) {
        g.setColor(FOREIGNKEY);
        g.drawLine(start.x, start.y, end.x, end.y);
    }

    /**
     * Paints the whole component both tables, icons and foreign key connectors
     *
     */
    public void paint(Graphics g) {
        /* Turn anti-aliasing on to smooth corners and lines */
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g2.setBackground(BACKGROUND);
        //g2.clearRect(0,0,getWidth(),getHeight());

        /* Paint the foreign key lines */
        for (Column fk : foreignKeys) {
            paintForeignKey(fk, g);
        }

        /* Paint each table */
        for (TablePosition tp : positions) {
            paintTable(tp, g);
        }
    }

    public Dimension calculateSize() {
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
        return new Rectangle(table.point, new Dimension(tableWidth, (table.getTable().getMetaData().getColumnCount() + 1) * rowHeight));
    }

    private void paintTable(TablePosition table, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));

        Point p = table.getPoint();
        int x = p.x;
        int y = p.y;
        int height = rowHeight * (table.getTable().getMetaData().getColumnCount() + 1);
        g.setColor(TABLE_OUTLINE);
        g.fillRoundRect(x, y, tableWidth, height, 10, 10);
        g.setColor(TABLE_FILL);
        g.fillRoundRect(x + 1, y + 1, tableWidth - 2, height - 2, 10, 10);

        /* Draw table name centered */
        int textWidth = getFontMetrics(getFont()).stringWidth(table.getTable().getName());
        int textHeight = getFontMetrics(getFont()).getHeight();

        g.setColor(TABLE_HEADER_BACKGROUND);
        g.fillRoundRect(x + 1, y + 1, tableWidth - 2, 20, 10, 10);
        g.fillRect(x + 1, y + 12, tableWidth - 2, 10);

        g.setColor(TABLE_HEADER_FOREGROUND);
        g.drawString(table.getTable().getName(), p.x + ((150 - textWidth) / 2), p.y + textHeight);

        /* Paint columns */
        g.setColor(TABLE_TEXT);
        for (int n = 0; n < table.getTable().getMetaData().getColumnCount(); n++) {
            Column c = table.getTable().getMetaData().getColumn(n);
            if (c.isPrimaryKey()) {
                PKICON.paintIcon(this, g, x + 5, y + 20 + n * rowHeight);
            }
            g.drawString(c.getName(), x + 25, y + 35 + n * rowHeight);

            if (c.getForeignKey() != null) {
                FKICON.paintIcon(this, g, x + 130, y + 20 + n * rowHeight);
            }
        }
    }

    /**
     * DRAG N DROP STUFF *
     */
    Point startPoint, endPoint;
    boolean isDragging = false;
    Table tableDrag;
    int minusX, minusY;

    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            try {
                write(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception ex) {
                Logger.getLogger(DiagramPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        LOG.log(Level.FINEST, "mouseReleased: {0},{1}", new Object[]{e.getX(), e.getY()});
        if (isDragging) {
            endPoint = e.getPoint();
            try {
                write(file);
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            catch (Exception ex) {
                Logger.getLogger(DiagramPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        isDragging = false;
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseDragged(MouseEvent e) {
        LOG.log(Level.FINEST, "mouseDragged: {0},{1}", new Object[]{e.getX(), e.getY()});
        if (tableDrag != null) {
            isDragging = true;
            Point p = new Point(e.getPoint());
            p.x -= minusX;
            p.y -= minusY;
            setTableLocation(p, tableDrag);
            setPreferredSize(calculateSize());
            repaint();
        }
    }

    /**
     * Mouse moved event listener
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        LOG.log(Level.FINEST, "mouseMoved: {0},{1}", new Object[]{e.getX(), e.getY()});
        for (TablePosition tp : positions) {
            Point p = tp.getPoint();
            Table t = tp.getTable();
            Rectangle r = new Rectangle(p.x, p.y, tableWidth, rowHeight);
            if (r.contains(e.getPoint())) {
                tableDrag = t;
                startPoint = e.getPoint();
                minusX = startPoint.x - p.x;
                minusY = startPoint.y - p.y;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                LOG.log(Level.FINEST, "mouseOver: {0}", t.getName());
                return;
            }
        }

//        for (int x = 0; x < locations.size(); x++) {
//            Point p = locations.get(x);
//            Table t = tables.get(x);
//            Rectangle r = new Rectangle(p.x, p.y, tableWidth, rowHeight);
//            if (r.contains(e.getPoint())) {
//                tableDrag = t;
//                startPoint = e.getPoint();
//                minusX = startPoint.x - p.x;
//                minusY = startPoint.y - p.y;
//                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                LOG.log(Level.FINEST, "mouseOver: {0}", t.getName());
//                return;
//            }
//        }
        tableDrag = null;
        startPoint = null;
        endPoint = null;

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Writes the diagram to file - the name and location of each table
     *
     * @param file
     * @throws IOException
     */
    public void write(File file) throws IOException, Exception {
        LOG.log(Level.FINE, "Writing diagram to file {0}", file.getAbsolutePath());
        DiagramManager.write(positions, file);
    }

    /**
     * Reads diagram from file - the name and location of each table
     *
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void read(File file) throws Exception {
        LOG.log(Level.FINE, "Reading diagram from file {0}", file.getAbsolutePath());
        DiagramManager.read(positions, file);
        this.file = file;
        fireTablesChanged();
    }

}
