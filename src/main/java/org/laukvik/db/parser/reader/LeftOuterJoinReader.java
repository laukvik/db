package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.Column;
import org.laukvik.db.parser.ParseException;
import org.laukvik.db.parser.joins.LeftJoin;

public class LeftOuterJoinReader extends GroupReader {

    String table, left, right;

    public LeftOuterJoinReader() {
        super();
        addEither(new TextReader("LEFT OUTER JOIN"), new TextReader("LEFT JOIN"));
        addEmpty();
        add(new WordReader()).addReaderListener(new ReaderListener() {
            public void found(String values) {
                table = values;
            }
        });
        addEmpty();
        add(new TextReader("ON"));
        addEmpty();
        add(new ColumnReader()).addReaderListener(new ReaderListener() {
            public void found(String values) {
                left = values;
            }
        });
        add(new TextReader("="));
        add(new ColumnReader()).addReaderListener(new ReaderListener() {
            public void found(String values) {
                right = values;
            }
        });
    }

    public LeftJoin getJoin() throws ParseException {
        return new LeftJoin(Column.parse(left), Column.parse(right));
    }

}
