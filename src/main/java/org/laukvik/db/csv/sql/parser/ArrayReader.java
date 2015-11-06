package org.laukvik.db.csv.sql.parser;

/**
 *
 *
 * @author morten
 *
 */
public class ArrayReader extends GroupReader {

    public ArrayReader(String... items) {
        super();
        for (String s : items) {
            addOptional(new TextReader(s));
        }
    }

}
