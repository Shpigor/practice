package org.java.practice.net.server.io;

import java.nio.channels.SelectionKey;

public interface AkkaService {

    void processingData(SelectionKey ch);
}
