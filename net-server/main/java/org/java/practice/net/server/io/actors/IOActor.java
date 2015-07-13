package org.java.practice.net.server.io.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import org.java.practice.net.server.io.AkkaContext;
import org.java.practice.net.server.io.messages.SelectionEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class IOActor extends UntypedActor {

    private static final Logger LOG = LoggerFactory.getLogger(IOActor.class);

    private ActorRef serverActor;
    private AkkaContext context;
    private final ByteBuffer bb;

    public IOActor(ActorRef serverActor, AkkaContext context) {
        this.serverActor = serverActor;
        this.context = context;
        if (context.useDirectBuffer()) {
            this.bb = ByteBuffer.allocateDirect(context.getBufferSize());
        } else {
            this.bb = ByteBuffer.allocate(context.getBufferSize());
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOG.trace("Received message {} in IO actor ", message);
        if (message instanceof SelectionEventMessage) {
            processRawMessage((SelectionEventMessage) message);
        }
    }

    private void processRawMessage(SelectionEventMessage message) {
        try {
            SocketChannel channel = (SocketChannel) message.getKey().channel();
            LOG.trace("Processing data...");
            if (channel != null) {
                channel.configureBlocking(false);
                while ((channel.read(bb)) > 0) {
                    bb.flip();
                    byte[] array;
                    if (bb.hasArray()) {
                        array = bb.array();
                    } else {
                        array = new byte[bb.remaining()];
                        bb.get(array);
                    }
                    bb.clear();
                    processBytes(array);
                }
                LOG.trace("Data processed");
            } else {
                LOG.warn("SocketChannel is null");
            }
        } catch (Exception e) {
            LOG.error("Got error during processing data from socket", e);
        }
    }

    private void processBytes(byte[] bytes) {
        LOG.info("---> {}", new String(bytes));
    }

    public static class ActorCreator implements Creator<IOActor> {

        /**
         * The Constant serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The server actor.
         */
        private final ActorRef serverActor;

        /**
         * The Akka service context
         */
        private final AkkaContext context;

        /**
         * Instantiates a new actor creator.
         *
         * @param serverActor the server actor
         * @param context     the akka context
         */
        public ActorCreator(ActorRef serverActor, AkkaContext context) {
            super();
            this.serverActor = serverActor;
            this.context = context;
        }

        @Override
        public IOActor create() throws Exception {
            return new IOActor(serverActor, context);
        }
    }

    @Override
    public void preStart() {
        LOG.info("Starting " + this);
    }

    @Override
    public void postStop() {
        LOG.info("Stopped " + this);
    }

}
