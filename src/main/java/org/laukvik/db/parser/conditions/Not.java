package org.laukvik.db.parser.conditions;

import org.laukvik.db.parser.Condition;
import org.laukvik.db.jdbc.data.ColumnData;

public class Not extends Condition {

	Condition conditionA;
	
	public Not(Condition conditionA ) {
		this.conditionA = conditionA;
	}
	
	public boolean accepts(  ColumnData data, String [] values ) {
		return false;
	}
	
	public String toString(){
		return "NOT (" + conditionA + ")";
	}

}
