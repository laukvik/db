package org.laukvik.db.parser.conditions;

import org.laukvik.db.parser.Column;
import org.laukvik.db.parser.Condition;
import org.laukvik.db.parser.Value;
import org.laukvik.db.jdbc.data.ColumnData;

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