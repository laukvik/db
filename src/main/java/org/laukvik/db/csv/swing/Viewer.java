/*
 * Copyright 2013 Laukviks Bedrifter.
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
package org.laukvik.db.csv.swing;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.Row;
import org.laukvik.db.csv.io.CsvWriter;
import org.laukvik.db.csv.io.JsonWriter;
import org.laukvik.db.csv.query.Query;
import org.laukvik.db.ddl.Column;
import org.laukvik.db.ddl.DateColumn;
import org.laukvik.db.ddl.DoublePrecisionColumn;
import org.laukvik.db.ddl.FloatColumn;
import org.laukvik.db.ddl.IntegerColumn;
import org.laukvik.db.ddl.VarCharColumn;
import org.laukvik.db.sql.swing.SqlTableHeaderRenderer;

/**
 * Milestone
 *
 * @todo Webside
 * @todo Besøksstatistikk
 * @todo Nedlastingsstatistikk
 *
 * Milestone
 *
 * @todo Sortering ved trykk på kolonneheadere
 * @todo Søk i kolonner
 * @todo Eksport JSON
 *
 * Milestone - Redigeringsmuligheter
 *
 * @todo Encoding
 * @todo Redigering av innhold
 * @todo Eksport av selection
 * @todo Recent files funksjon
 * @todo Åpne tab eller pipe separert
 *
 *
 *
 *
 * @author morten
 */
public class Viewer extends javax.swing.JFrame implements ListSelectionListener, RecentFileListener {

    private static final Logger LOG = Logger.getLogger(Viewer.class.getName());
    private final ResourceBundle bundle;

    private org.laukvik.db.csv.CSV csv = null;
    private File file = null;
    private CSVTableModel model;

    private List<UniqueTableModel> tableModels;
    private final RecentFileModel recentFileModel;
    private final LoadingWorker loadingWorker;

    /**
     * Creates new form Viewer
     */
    public Viewer() {
        super();
        tableModels = new ArrayList<>();
        bundle = ResourceBundle.getBundle("messages"); // NOI18N
        initComponents();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        newMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_N));
        openMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_O));
        saveMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_S));
        printMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_P));
        exitMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_Q));

        gotoMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_G));

        findMenuItem.setAccelerator(getKeystroke(java.awt.event.KeyEvent.VK_F));
        toolsMenu.setVisible(true);
        file = null;
        csv = new org.laukvik.db.csv.CSV();
        model = new CSVTableModel(csv);
        table.setModel(model);

        loadingWorker = new LoadingWorker(this, bundle);
        for (Charset c : Charset.availableCharsets().values()) {
            JMenuItem item = new JMenuItem(c.name());
            item.setActionCommand(c.name());
            item.addActionListener(loadingWorker);
            charsetMenu.add(item);
        }

        /* Recent stuff */
        recentFileModel = new RecentFileModel(recentMenu, this);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        Float width = size.width * 0.8f;
        Float height = size.height * 0.7f;
        Float split = size.width * 0.2f;
        jSplitPane1.setDividerLocation(split.intValue());

        openMenuItem.addActionListener(loadingWorker);
        openMenuItem.setActionCommand(null);
        setSize(width.intValue(), height.intValue());
        importMenuItem.setVisible(false);
        exportMenuItem.setVisible(false);
        cutMenuItem.setVisible(false);
        copyMenuItem.setVisible(false);
        pasteMenuItem.setVisible(false);
        printMenuItem.setVisible(false);
        recentMenu.setVisible(false);
        undoMenuItem.setVisible(false);
        redoMenuItem.setVisible(false);
    }

    public File getFile() {
        return file;
    }

    public void updateStatusBar() {
        boolean hasQuery = csv.getQuery() != null;
        int resultCount = hasQuery ? csv.getQuery().getResultList().size() : 0;
        if (hasQuery) {
            MessageFormat mf = new MessageFormat(bundle.getString("status.results_with_query"));
            Object[] params = {resultCount, csv.getRowCount()};
            statusLabel.setText(mf.format(params));
        } else {
            MessageFormat mf = new MessageFormat(bundle.getString("status.results_empty_query"));
            Object[] params = {csv.getRowCount()};
            statusLabel.setText(mf.format(params));
        }
        setTitle(file == null ? "" : file.getAbsolutePath());
        getRootPane().putClientProperty("Window.documentFile", file);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    public CSVTableModel getModel() {
        return model;
    }

    public void buildQuery() {
        LOG.fine("buildQuery ");
        Query query = csv.findByQuery();
        Query.Where where = query.where();
        int selectionCount = 0;
        for (UniqueTableModel utm : tableModels) {
            Column c = utm.getColumn();

            if (c instanceof IntegerColumn) {

                IntegerColumn ic = (IntegerColumn) c;
                UniqueTableModel<Integer> mod = (UniqueTableModel<Integer>) utm;

                Integer[] arr = new Integer[utm.getSelection().size()];
                int x = 0;
                for (Integer v : mod.getSelection()) {
                    arr[x] = v;
                    x++;
                }

                if (arr.length > 0) {
                    where.column(ic).isIn(arr);
                }
                selectionCount += arr.length;

            } else if (c instanceof FloatColumn) {

                FloatColumn ic = (FloatColumn) c;
                UniqueTableModel<Float> mod = (UniqueTableModel<Float>) utm;

                Float[] arr = new Float[utm.getSelection().size()];
                int x = 0;
                for (Float v : mod.getSelection()) {
                    arr[x] = v;
                    x++;
                }

                if (arr.length > 0) {
                    where.column(ic).isIn(arr);
                }
                selectionCount += arr.length;

            } else if (c instanceof DoublePrecisionColumn) {
                DoublePrecisionColumn ic = (DoublePrecisionColumn) c;
                UniqueTableModel<Double> mod = (UniqueTableModel<Double>) utm;

                Double[] arr = new Double[utm.getSelection().size()];
                int x = 0;
                for (Double v : mod.getSelection()) {
                    arr[x] = v;
                    x++;
                }

                if (arr.length > 0) {
                    where.column(ic).isIn(arr);
                }
                selectionCount += arr.length;

            } else if (c instanceof DateColumn) {
                DateColumn ic = (DateColumn) c;
                UniqueTableModel<Date> mod = (UniqueTableModel<Date>) utm;

                Date[] arr = new Date[utm.getSelection().size()];
                int x = 0;
                for (Date v : mod.getSelection()) {
                    arr[x] = v;
                    x++;
                }

                if (arr.length > 0) {
                    where.column(ic).isIn(arr);
                }
                selectionCount += arr.length;
            } else {
                VarCharColumn ic = (VarCharColumn) c;
                UniqueTableModel<String> mod = (UniqueTableModel<String>) utm;
                String[] arr = new String[utm.getSelection().size()];
                int x = 0;
                for (String v : mod.getSelection()) {
                    arr[x] = v;
                    x++;
                }

                if (arr.length > 0) {
                    where.column(ic).isIn(arr);

                }
                selectionCount += arr.length;
            }

        }
        if (selectionCount == 0) {
            csv.clearQuery();
            createModel(new CSVTableModel(csv));
        } else {
            createModel(new CSVTableModel(csv, query));
        }
        updateStatusBar();
    }

    public void createUniqueModels() {
        LOG.log(Level.INFO, "Adding unique models: {0}", csv.getMetaData().getColumnCount());
        tabbedPane.removeAll();
        tableModels = new ArrayList<>();

        for (int x = 0; x < csv.getMetaData().getColumnCount(); x++) {
            Column c = csv.getMetaData().getColumn(x);

            /**/
            UniqueTableModel model = null;
            if (c instanceof VarCharColumn) {
                VarCharColumn sc = (VarCharColumn) c;
                model = new UniqueTableModel<String>(c);
                for (int y = 0; y < csv.getRowCount(); y++) {
                    Row r = csv.getRow(y);
                    model.addValue(r.getString(sc));
                }
            }
            /*
             else if (c instanceof IntegerColumn) {
             model = new UniqueTableModel<Integer>(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             } else if (c instanceof FloatColumn) {
             model = new UniqueTableModel<Float>(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             } else if (c instanceof IntegerColumn) {
             model = new UniqueTableModel<Integer>(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             } else if (c instanceof DateColumn) {
             model = new UniqueTableModel<Date>(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             } else if (c instanceof UrlColumn) {
             model = new UniqueTableModel(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             } else {
             model = new UniqueTableModel<String>(c);
             for (int y = 0; y < csv.getRowCount(); y++) {
             Row r = csv.getRow(y);
             model.addValue(r.getDate(c));
             }
             }
             */

            /* Build the current values */
            model.buildValues();

            LOG.log(Level.FINE, "Adding unique for column {0}", c.getName());

            tableModels.add(model);

            model.addChangeListener(new UniqueListener() {
                @Override
                public void uniqueSelectionChanged(UniqueTableModel model) {
                    LOG.log(Level.FINE, "Selection: {0}", model);
                    buildQuery();
                }
            });

            JTable t = new JTable(model);
            t.setRowHeight(20);
            t.setTableHeader(null);
            t.setCellSelectionEnabled(false);
            t.setColumnSelectionAllowed(false);
            t.setRowSelectionAllowed(true);
            t.setIntercellSpacing(new Dimension(0, 0));

            t.getColumnModel().getColumn(0).setMinWidth(32);
            t.getColumnModel().getColumn(0).setMaxWidth(32);
            t.getColumnModel().getColumn(0).setPreferredWidth(32);
            t.getColumnModel().getColumn(0).setWidth(32);

            t.getColumnModel().getColumn(2).setMinWidth(32);
            t.getColumnModel().getColumn(2).setMaxWidth(100);
            t.getColumnModel().getColumn(2).setPreferredWidth(64);

            t.setVisible(true);
            JScrollPane scrollPane = new JScrollPane(t);
            scrollPane.setVisible(true);
            tabbedPane.add(c.getName(), scrollPane);
        }
        tabbedPane.invalidate();
    }

    public void addUniqueTable() {
    }

    private void createModel(CSVTableModel model) {
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setRowHeight(20);

        table.setDefaultRenderer(Object.class, new EvenOddRenderer());
        table.setDefaultRenderer(Number.class, new EvenOddRenderer());
        table.setRowHeight(24);

        table.getSelectionModel().addListSelectionListener(this);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setBackground(UIManager.getColor("Label.background"));
        table.getTableHeader().setDefaultRenderer(new SqlTableHeaderRenderer());

        for (int x = 0; x < csv.getMetaData().getColumnCount(); x++) {
            int maxWidth = model.getMaxColumnWidth(csv.getMetaData().getColumn(x));
            LOG.log(Level.FINE, "Max width {0} for column {1}", new Object[]{maxWidth, x});
            if (maxWidth > 20) {
                maxWidth = 20;
            }
            int size = (maxWidth * 10) + 10;

            table.getColumnModel().getColumn(x).setPreferredWidth(size);
            table.getColumnModel().getColumn(x).setWidth(size);
        }

        scroll.setViewportView(table);
    }

    /**
     *
     *
     *
     * @param charset
     */
    public void reloadFile(Charset charset) {
        LOG.log(Level.INFO, "Reopening file with charset: {0}", charset);
    }

    /**
     *
     * @param csv
     * @param file
     */
    public void openCSV(CSV csv, File file) {
        LOG.log(Level.INFO, "Opening csv with columns: {0} rows: {1}", new Object[]{csv.getMetaData().getColumnCount(), csv.getRowCount()});
        this.csv = csv;
        this.file = file;
        createModel(new CSVTableModel(csv));
        setTitle(this.file.getAbsolutePath());
        getRootPane().putClientProperty("Window.documentFile", this.file);
//        recentFileModel.add(new RecentFile(file.getAbsolutePath()));
        createUniqueModels();
        updateStatusBar();
    }

    @Override
    public void openFile(File file) {
        this.file = file;
    }

    /**
     * Returns a cross-platform keystroke that enables the platform behave
     * natively. This is usually a problem for Mac people who uses the Apple
     * button like Windows people use the Control button.
     *
     * @param keyevent the keyevent you want the keystroke for
     * @return a cross-platfrom compatible keystroke
     */
    public static KeyStroke getKeystroke(int keyevent) {
        return KeyStroke.getKeyStroke(keyevent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        statusLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        recentMenu = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        importMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        insertRowMenuItem = new javax.swing.JMenuItem();
        deleteRowMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        gotoMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        insertColumnMenuItem = new javax.swing.JMenuItem();
        deleteColumnMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        findMenuItem = new javax.swing.JMenuItem();
        replaceMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        charsetMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setLeftComponent(tabbedPane);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setCellSelectionEnabled(true);
        table.setShowGrid(true);
        scroll.setViewportView(table);

        jSplitPane1.setRightComponent(scroll);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        statusLabel.setText("Status");
        statusLabel.setToolTipText("");
        statusLabel.setEnabled(false);
        jToolBar2.add(statusLabel);

        getContentPane().add(jToolBar2, java.awt.BorderLayout.PAGE_END);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages"); // NOI18N
        fileMenu.setText(bundle.getString("file")); // NOI18N

        newMenuItem.setText(bundle.getString("file.new")); // NOI18N
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setText(bundle.getString("file.open")); // NOI18N
        openMenuItem.setToolTipText("Opens a CSV file for editing");
        fileMenu.add(openMenuItem);

        recentMenu.setText(bundle.getString("file.open_recent")); // NOI18N
        recentMenu.add(jSeparator7);

        fileMenu.add(recentMenu);
        fileMenu.add(jSeparator8);

        importMenuItem.setText(bundle.getString("file.import")); // NOI18N
        fileMenu.add(importMenuItem);

        exportMenuItem.setText(bundle.getString("file.export")); // NOI18N
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportMenuItem);
        fileMenu.add(jSeparator9);

        saveMenuItem.setText(bundle.getString("file.save")); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText(bundle.getString("file.saveas")); // NOI18N
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        printMenuItem.setText(bundle.getString("file.print")); // NOI18N
        fileMenu.add(printMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setText(bundle.getString("file.exit")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText(bundle.getString("edit")); // NOI18N

        undoMenuItem.setText(bundle.getString("edit.undo")); // NOI18N
        editMenu.add(undoMenuItem);

        redoMenuItem.setText(bundle.getString("edit.redo")); // NOI18N
        editMenu.add(redoMenuItem);
        editMenu.add(jSeparator5);

        cutMenuItem.setText(bundle.getString("edit.cut")); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setText(bundle.getString("edit.copy")); // NOI18N
        editMenu.add(copyMenuItem);

        pasteMenuItem.setText(bundle.getString("edit.paste")); // NOI18N
        editMenu.add(pasteMenuItem);
        editMenu.add(jSeparator10);

        insertRowMenuItem.setText(bundle.getString("edit.newrow")); // NOI18N
        insertRowMenuItem.setToolTipText("");
        insertRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertRowMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(insertRowMenuItem);

        deleteRowMenuItem.setText(bundle.getString("edit.deleterow")); // NOI18N
        deleteRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRowMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteRowMenuItem);
        editMenu.add(jSeparator2);

        gotoMenuItem.setText(bundle.getString("edit.goto_row")); // NOI18N
        gotoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(gotoMenuItem);
        editMenu.add(jSeparator3);

        insertColumnMenuItem.setText(bundle.getString("edit.newcolumn")); // NOI18N
        insertColumnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertColumnMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(insertColumnMenuItem);

        deleteColumnMenuItem.setText(bundle.getString("edit.deletecolumn")); // NOI18N
        deleteColumnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteColumnMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteColumnMenuItem);
        editMenu.add(jSeparator6);

        findMenuItem.setText(bundle.getString("edit.find")); // NOI18N
        editMenu.add(findMenuItem);

        replaceMenuItem.setText(bundle.getString("edit.replace")); // NOI18N
        editMenu.add(replaceMenuItem);

        jMenuBar1.add(editMenu);

        toolsMenu.setText(bundle.getString("tools")); // NOI18N

        charsetMenu.setText("Encoding");
        toolsMenu.add(charsetMenu);

        jMenuBar1.add(toolsMenu);

        helpMenu.setText(bundle.getString("help")); // NOI18N

        aboutMenuItem.setText(bundle.getString("help.about")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed

        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        java.awt.FileDialog fd = new FileDialog(this, "Velg fil", FileDialog.SAVE);
        fd.setFilenameFilter(new CSVFileFilter());
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename == null) {
        } else {
            try {
                File file = new File(fd.getDirectory(), filename);
                csv.write(new JsonWriter(new FileOutputStream(file)));
            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save file!", "", JOptionPane.WARNING_MESSAGE);
            }
            catch (Exception ex) {
                Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDialog d = new AboutDialog(this, true, bundle);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        try {
            csv.write(new CsvWriter(new FileOutputStream(file), Charset.defaultCharset()));
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save file!", "", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void deleteRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRowMenuItemActionPerformed
        int min = table.getSelectedRow();
        int max = min + table.getSelectedRowCount();
        csv.removeRows(min, max);
        table.tableChanged(new TableModelEvent(model));
    }//GEN-LAST:event_deleteRowMenuItemActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        newDocument();
    }//GEN-LAST:event_newMenuItemActionPerformed

    public void newDocument() {
        file = null;
        csv = new org.laukvik.db.csv.CSV();
        model = new CSVTableModel(csv);
        table.setModel(model);
        tabbedPane.removeAll();
        tableModels.clear();
        updateStatusBar();
    }

    public void addColumn() {
        String answer = JOptionPane.showInputDialog(this, "", "", JOptionPane.QUESTION_MESSAGE);
        if (answer != null) {
            int columnIndex = table.getSelectedColumn();
            if (columnIndex == -1) {
                LOG.fine("Inserting column after last");
                csv.addColumn(answer);
            } else {
                LOG.log(Level.FINE, "Inserting column at {0}", columnIndex);
                csv.getMetaData().insertColumn(new VarCharColumn(answer), columnIndex);
            }
            LOG.log(Level.FINE, "Columns: {0}", csv.getMetaData().getColumnCount());
            table.tableChanged(new TableModelEvent(model, TableModelEvent.HEADER_ROW));
            table.tableChanged(new TableModelEvent(model));
        }
    }

    private void insertColumnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertColumnMenuItemActionPerformed
        addColumn();
    }//GEN-LAST:event_insertColumnMenuItemActionPerformed

    public void addRow() {
        int rowIndex = table.getSelectedRow();
        Row emptyRow = csv.addRow();
        if (rowIndex == -1) {
            LOG.fine("Adding empty row at end");
            csv.addRow(emptyRow);
        } else {
            LOG.log(Level.FINE, "Inserting row at {0}", rowIndex);
            csv.insertRow(emptyRow, rowIndex);
        }
        LOG.log(Level.FINE, "Rows after insert: {0}", csv.getRowCount());
        table.tableChanged(new TableModelEvent(model));
        updateStatusBar();
    }

    public void gotoRow() {

        String answer = JOptionPane.showInputDialog(this, bundle.getString("edit.goto_row.specify"), "", JOptionPane.QUESTION_MESSAGE);
        if (csv.getRowCount() == 0) {
            return;
        }
        if (answer != null) {
            Integer row = Integer.parseInt(answer) - 1;
            table.setRowSelectionInterval(row, row);
            Rectangle rect = table.getCellRect(row, 0, true);
            rect.height = (int) scroll.getVisibleRect().getHeight();
            table.scrollRectToVisible(rect);
        }
    }

    private void insertRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRowMenuItemActionPerformed
        addRow();
    }//GEN-LAST:event_insertRowMenuItemActionPerformed

    private void gotoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoMenuItemActionPerformed
        gotoRow();
    }//GEN-LAST:event_gotoMenuItemActionPerformed

    public void removeColumn(int columnIndex) {
        LOG.log(Level.INFO, "Removing column {0}", columnIndex);
        Column c = csv.getMetaData().getColumn(columnIndex);
        csv.removeColumn(c);
        table.tableChanged(new TableModelEvent(model, TableModelEvent.HEADER_ROW));
        tabbedPane.remove(columnIndex);
    }

    private void deleteColumnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteColumnMenuItemActionPerformed
        int min = table.getSelectedColumn();
        if (min > -1) {
            removeColumn(min);
        }
    }//GEN-LAST:event_deleteColumnMenuItemActionPerformed

    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed

        java.awt.FileDialog fd = new FileDialog(this, "Velg fil", FileDialog.SAVE);
        //fd.setFilenameFilter(new CSVFileFilter());
        fd.setVisible(true);

        String filename = fd.getFile();
        if (filename == null) {
        } else {
            try {
                File file = new File(fd.getDirectory(), filename);
                csv.write(new JsonWriter(new FileOutputStream(file)));
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save file!", "", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_exportMenuItemActionPerformed

    /**
     *
     * @param columnIndex
     */
    public void openColumnEditor(int columnIndex) {
        columnDialog = new ColumnEditorDialog(this, csv.getMetaData().getColumn(columnIndex));
    }
    ColumnEditorDialog columnDialog;

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
        }
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Viewer v = new Viewer();
                v.setSize(700, 400);
                v.setLocationRelativeTo(null);
                v.setVisible(true);

                if (args.length > 0) {
                    v.openFile(new File(args[0]));
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu charsetMenu;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteColumnMenuItem;
    private javax.swing.JMenuItem deleteRowMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem findMenuItem;
    private javax.swing.JMenuItem gotoMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuItem insertColumnMenuItem;
    private javax.swing.JMenuItem insertRowMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JMenu recentMenu;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem replaceMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable table;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem undoMenuItem;
    // End of variables declaration//GEN-END:variables

}
