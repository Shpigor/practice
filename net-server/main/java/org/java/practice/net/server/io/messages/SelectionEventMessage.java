package org.java.practice.net.server.io.messages;

import java.nio.channels.SelectionKey;

public class SelectionEventMessage {

    private SelectionKey key;

    public SelectionEventMessage(SelectionKey key) {
        this.key = key;
    }

    public SelectionKey getKey() {
        return key;
    }

}
