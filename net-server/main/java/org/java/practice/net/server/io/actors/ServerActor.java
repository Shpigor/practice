package org.java.practice.net.server.io.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import org.java.practice.net.server.io.AkkaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerActor extends UntypedActor {

    private static final Logger LOG = LoggerFactory.getLogger(ServerActor.class);

    private AkkaContext context;
    private final Map<String, ActorRef> sessions;

    public ServerActor(AkkaContext context) {
        this.context = context;
        this.sessions = new HashMap<>();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOG.trace("Received message {} in server actor", message);
    }

    public static class ActorCreator implements Creator<ServerActor> {

        private static final long serialVersionUID = -7605965778911662207L;

        private AkkaContext context;

        public ActorCreator(AkkaContext context) {
            this.context = context;
        }

        @Override
        public ServerActor create() throws Exception {
            return new ServerActor(context);
        }

    }

    @Override
    public void preStart() {
        LOG.info("Starting server actor {}", this);
    }

    @Override
    public void postStop() {
        LOG.info("Stopped server actor {}", this);
    }

    private ActorRef getOrCreateClientActor(String sessionId) {
        ActorRef client = sessions.get(sessionId);
        if (client == null) {
            try {
                client = new ClientActor.ActorCreator(self(), context).create().getSelf();
                sessions.put(getClientId(), client);
                context().watch(client);
            } catch (Exception e) {
                LOG.error("Can't create client actor.", e);
            }
        }
        return client;
    }

    private String getClientId() {
        return UUID.randomUUID().toString();
    }
}
