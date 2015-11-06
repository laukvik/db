package org.laukvik.db.csv.sql.parser;

import org.laukvik.db.csv.sql.ParseException;
import org.laukvik.db.csv.sql.Table;
import org.laukvik.db.csv.sql.joins.NaturalJoin;

public class NaturalJoinReader extends GroupReader {

    private String table;

    public NaturalJoinReader() {
        super();
        add(new TextReader("NATURAL JOIN"));
        addEmpty();
        add(new WordReader()).addReaderListener(new ReaderListener() {
            public void found(String values) {
                table = values;
            }
        }
        );
    }

    public NaturalJoin getJoin() throws ParseException {
        return new NaturalJoin(null, Table.parse(table));
    }

}
