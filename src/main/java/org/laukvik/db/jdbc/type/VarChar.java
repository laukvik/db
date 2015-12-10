package org.laukvik.db.jdbc.type;

import org.laukvik.db.parser.Value;

public class VarChar extends Value {

    String value;

    public VarChar(String value) {
        super(value);
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public boolean equals(Value obj) {
        if (obj instanceof VarChar) {
            VarChar v = (VarChar) obj;
            return this.value.equalsIgnoreCase(v.value);
        } else {
            return false;
        }
    }

    public boolean greater(Value obj) {
        return false;
    }

    public boolean less(Value value) {
        return false;
    }

    public boolean like(VarChar value) {
        return this.value.contains(value.value);
    }

}
