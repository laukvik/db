package org.laukvik.db.parser.reader;

public class CommaReader extends GroupReader {

    public CommaReader() {
        super();
        EmptyReader empty = new EmptyReader();
        empty.setRequired(false);
        addOptional(empty);
        add(",");
        addOptional(empty);
    }

}
