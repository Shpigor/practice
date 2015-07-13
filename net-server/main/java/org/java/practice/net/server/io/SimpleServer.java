package org.java.practice.net.server.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimpleServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleServer.class);
    private final AtomicBoolean isRunning;

    @Autowired
    private ServerConfiguration configuration;
    @Autowired
    private DefaultAkkaService akkaService;

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private SelectionKey serverKey;

    public SimpleServer() {
        isRunning = new AtomicBoolean();
    }

    @PostConstruct
    private void init() throws IOException {
        LOG.info("Initialization server...");
        selector = Selector.open();
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(configuration.getHost(), configuration.getPort()));
            serverSocket.configureBlocking(false);
            serverKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            LOG.error("Cant start server", e);
        }
    }

    @Override
    public void start() {
        LOG.info("Starting server...");
        isRunning.set(true);
        try {
            while (isRunning.get()) {
                int count = selector.select();
                if (count == 0) {
                    continue;
                }

                Iterator<SelectionKey> clientKeys = selector.selectedKeys().iterator();
                while (clientKeys.hasNext()) {
                    SelectionKey key = clientKeys.next();
                    clientKeys.remove();
                    if (!key.equals(serverKey)) {
                        LOG.trace("Received message from {}", key.attachment());
                        preProcessingData(key);
                    } else {
                        if (key.isAcceptable()) {
                            SocketChannel acceptSocket = serverSocket.accept();
                            acceptSocket.configureBlocking(false);
                            acceptSocket.register(selector, SelectionKey.OP_READ);
                            LOG.debug("Registered key for operation OP_READ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Got error during running server", e);
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        isRunning.set(false);
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void preProcessingData(SelectionKey key) {
        try {
            LOG.info("Processing data...");
            akkaService.processingData(key);
        } catch (Exception e) {
            LOG.error("Got error during processing data from socket", e);
        }
    }
}
