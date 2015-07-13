package org.java.practice.net.server.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerConfiguration {

    @Value("#{server['server.host']}")
    private String host;
    @Value("#{server['server.port']}")
    private int port;
    @Value("#{server['channel.buffer.size']}")
    private int bufferSize;
    @Value("#{server['channel.buffer.direct']}")
    private boolean useDirectBuffer;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isUseDirectBuffer() {
        return useDirectBuffer;
    }

    public void setUseDirectBuffer(boolean useDirectBuffer) {
        this.useDirectBuffer = useDirectBuffer;
    }
}
