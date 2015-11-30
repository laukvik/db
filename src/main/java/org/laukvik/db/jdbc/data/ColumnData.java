package org.laukvik.db.jdbc.data;

import org.laukvik.db.parser.Column;

public interface ColumnData {

	Class<?> getColumnClass(int columnIndex);
	int indexOf(Column column);
	Column [] listColumns();
	Column getColumn(int columnIndex);
	String [] getRow(int rowIndex);
	int getRowCount();
	int getColumnCount();
	String getValue(int columnIndex, int rowIndex);
	String getValue(Column column, int rowIndex);
	
}