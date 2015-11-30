package org.laukvik.db.parser.reader;

import java.util.ArrayList;
import java.util.List;
import org.laukvik.db.parser.Join;

public class JoinReader extends GroupReader {

    private final List<JoinReaderListener> joinListeners;

    public JoinReader() {
        super();
        joinListeners = new ArrayList<>();
    }

    public void addJoinReaderListener(JoinReaderListener listener) {
        joinListeners.add(listener);
    }

    public void removeJoinReaderListener(JoinReaderListener listener) {
        joinListeners.remove(listener);
    }

    public void fireJoinFound(Join join) {
        for (JoinReaderListener l : joinListeners) {
            l.found(join);
        }
    }

}
