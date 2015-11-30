package org.laukvik.db.parser.reader;

import org.laukvik.db.parser.Column;
import org.laukvik.db.parser.ParseException;
import org.laukvik.db.parser.joins.OuterJoin;

public class FullOuterJoinReader extends GroupReader {

    String table, left, right;

    public FullOuterJoinReader() {
        super();
        addEither(new TextReader("FULL OUTER JOIN"), new TextReader("OUTER JOIN"));
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

    public OuterJoin getJoin() throws ParseException {
        return new OuterJoin(Column.parse(left), Column.parse(right));
    }

}
