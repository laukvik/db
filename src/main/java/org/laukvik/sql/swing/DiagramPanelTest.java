package org.laukvik.sql.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.laukvik.csv.columns.Column;
import org.laukvik.csv.columns.ForeignKey;
import org.laukvik.csv.columns.IntegerColumn;
import org.laukvik.csv.columns.StringColumn;
import org.laukvik.csv.columns.Table;

/**
 * Created by morten on 25.10.2015.
 */
public class DiagramPanelTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Employee
        Table employee = new Table("Employee");

        Column eID = new IntegerColumn("employeeID");
        eID.setPrimaryKey(true);
        eID.setAllowNulls(true);
        employee.addColumn(eID);

        employee.addColumn(new StringColumn("firstName"));
        employee.addColumn(new StringColumn("lastName"));
        employee.addColumn(new StringColumn("email"));

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
        company.addColumn(new StringColumn("name"));

        // Department
        Table department = new Table("Department");
        Column dID = new IntegerColumn("departmentID");
        dID.setPrimaryKey(true);
        dID.setAllowNulls(false);
        department.addColumn(dID);
        department.addColumn(new StringColumn("name"));
        department.addColumn(new StringColumn("contact"));

        IntegerColumn companyID = new IntegerColumn("companyID");
        ForeignKey contactFK = new ForeignKey("Company", "companyID");
        companyID.setForeignKey(contactFK);

        DiagramPanel panel = new DiagramPanel();
        panel.addTable(employee);
        panel.addTable(company);
        panel.addTable(department);

        panel.setTableLocation(new Point(50, 50), employee);
        panel.setTableLocation(new Point(300, 50), company);
        panel.setTableLocation(new Point(300, 300), department);

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(panel), BorderLayout.CENTER);
        frame.setSize(500, 500);
        frame.setVisible(true);

        panel.repaint();

    }

}
