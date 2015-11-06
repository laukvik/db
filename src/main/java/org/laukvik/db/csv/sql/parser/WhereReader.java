package org.laukvik.db.csv.sql.parser;

public class WhereReader extends GroupReader {

    public WhereReader() {
        super();
        add(new TextReader("WHERE"));
        addEmpty();
        ConditionReader wr = new ConditionReader();
        add(new ListReader(wr, new ConditionSeperator()));
    }

}
