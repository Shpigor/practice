package org.java.practice.net.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MultiClient.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        new MultiClient().start();
    }

    private void start() {
        for (int i = 0; i < 1000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try (SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 9090));) {
                        socket.configureBlocking(false);
                        String body = new UUID(System.currentTimeMillis(), RANDOM.nextLong()).toString();
                        socket.write(ByteBuffer.wrap(body.getBytes()));
                        LOG.debug("Conected number [{}]", "");
                    } catch (Exception e) {
                        LOG.error("Received exception during sending data", e);
                    }
                }
            });

        }
    }
}
