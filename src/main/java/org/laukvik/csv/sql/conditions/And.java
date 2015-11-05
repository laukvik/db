package org.laukvik.csv.sql.conditions;

import org.laukvik.csv.jdbc.data.ColumnData;
import org.laukvik.csv.sql.Condition;

public class And extends Condition {

    private final Condition conditionA;
    private final Condition conditionB;

    public And(Condition conditionA, Condition conditionB) {
        super();
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }

    public boolean accepts(ColumnData data, String[] values) {
        return false;
    }

    public String toString() {
        return "(" + conditionA + " AND " + conditionB + ")";
    }

}
