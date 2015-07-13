package org.java.practice.net.server.io.actors;

import org.java.practice.net.server.io.AkkaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.nio.channels.SocketChannel;

public class ClientActor extends UntypedActor {

    private static final Logger LOG = LoggerFactory.getLogger(ClientActor.class);

    private final ActorRef serverActor;
    private final AkkaContext context;
    private SocketChannel channel;

    public ClientActor(ActorRef serverActor, AkkaContext context) {
        this.serverActor = serverActor;
        this.context = context;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOG.trace("Received message {} in client actor", message);
    }

    @Override
    public void preStart() {
        LOG.info("Starting client actor {}", this);
    }

    @Override
    public void postStop() {
        LOG.info("Stopped client actor {}", this);
    }

    public static class ActorCreator implements Creator<ClientActor> {

        private static final long serialVersionUID = 8120698819564082372L;

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
         */
        public ActorCreator(ActorRef serverActor, AkkaContext context) {
            super();
            this.serverActor = serverActor;
            this.context = context;
        }

        @Override
        public ClientActor create() throws Exception {
            return new ClientActor(serverActor, context);
        }
    }
}
