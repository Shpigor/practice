package org.java.practice.net.server.io;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.sysmsg.Terminate;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import org.java.practice.net.server.io.actors.IOActor;
import org.java.practice.net.server.io.actors.ServerActor;
import org.java.practice.net.server.io.messages.SelectionEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.channels.SelectionKey;

@Service
public class DefaultAkkaService implements AkkaService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAkkaService.class);

    /**
     * The akka.
     */
    private ActorSystem akka;

    /**
     * The server actor.
     */
    private ActorRef serverActor;

    /**
     * The io router.
     */
    private ActorRef ioRouter;

    /**
     * The akka service context.
     */
    @Autowired
    private AkkaContext context;

    @PostConstruct
    public void initActorSystem() {
        LOG.info("Initializing Akka system...");
        akka = ActorSystem.create(AkkaContext.IO, context.getConfig());
        LOG.info("Initializing Akka server actor...");
        serverActor = akka.actorOf(Props.create(new ServerActor.ActorCreator(context)).withDispatcher(AkkaContext.SERVER_DISPATCHER_NAME), AkkaContext.IO);
        LOG.info("Initializing Akka io router...");
        ioRouter = akka.actorOf(new RoundRobinPool(context.getIOWorkerCount()).props(Props.create(new IOActor.ActorCreator(serverActor, context)).withDispatcher(AkkaContext.IO_DISPATCHER_NAME)), AkkaContext.IO_ROUTER_ACTOR_NAME);
    }

    @PreDestroy
    public void preDestroy() {
        ioRouter.tell(new Broadcast(new Terminate()), ActorRef.noSender());
        serverActor.tell(new Broadcast(new Terminate()), ActorRef.noSender());
    }

    @Override
    public void processingData(SelectionKey ch) {
        ioRouter.tell(new SelectionEventMessage(ch), ActorRef.noSender());
    }
}
