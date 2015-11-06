package org.laukvik.db.csv.sql.conditions;

import org.laukvik.db.csv.sql.Column;
import org.laukvik.db.csv.sql.Condition;
import org.laukvik.db.csv.sql.Value;
import org.laukvik.db.csv.jdbc.data.ColumnData;

public class Greater extends Condition{
	
	Column column; 
	Value value;
	
	public Greater( Column column, Value value ){
		super();
		this.column = column;
		this.value = value;
	}
	
	public String toString(){
		return column + " > " + value;
	}

	public boolean accepts( ColumnData data, String [] values ) {
		return false;
	}

}