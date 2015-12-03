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
package org.laukvik.db.csv.swing;

import java.util.ResourceBundle;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class AboutDialog extends javax.swing.JDialog {

    /**
     * Creates new form AboutDialog
     *
     * @param parent
     * @param modal
     * @param bundle
     */
    public AboutDialog(java.awt.Frame parent, boolean modal, ResourceBundle bundle) {
        super(parent, modal);
        initComponents();

        aboutLabel.setText(bundle.getString("about.title"));
        descLabel.setText(bundle.getString("about.description"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        aboutLabel = new javax.swing.JLabel();
        descLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setName("aboutDialog"); // NOI18N
        setResizable(false);

        jScrollPane1.setBorder(null);

        jPanel1.setLayout(null);

        aboutLabel.setText("About");
        aboutLabel.setEnabled(false);
        jPanel1.add(aboutLabel);
        aboutLabel.setBounds(20, 20, 300, 16);

        descLabel.setText("Written by Morten Laukvik");
        descLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(descLabel);
        descLabel.setBounds(20, 40, 290, 30);

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aboutLabel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
