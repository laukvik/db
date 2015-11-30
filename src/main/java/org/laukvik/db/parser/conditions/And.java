package org.laukvik.db.parser.conditions;

import org.laukvik.db.jdbc.data.ColumnData;
import org.laukvik.db.parser.Condition;

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
