package org.java.practice.net.server.io;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AkkaContext {

    public static final String IO = "IO";
    public static final String IO_DISPATCHER_NAME = "io-dispatcher";
    public static final String SERVER_DISPATCHER_NAME = "server-dispatcher";
    public static final String IO_ROUTER_ACTOR_NAME = "ioRouter";

    private static final String AKKA_CONF_FILE_NAME = "akka.conf";
    private static final String IO_WORKER_COUNT_PROP_NAME = "io_worker_count";
    private static final String CLIENT_ACTOR_TIMEOUT = "client_actor_timeout";

    private final Config config;
    @Autowired
    private ServerConfiguration configuration;

    public AkkaContext() {
        config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME)
                .withFallback(ConfigFactory.load());
    }

    public int getIOWorkerCount() {
        return config.getInt(IO_WORKER_COUNT_PROP_NAME);
    }

    public int getInactivityTimeout() {
        return config.getInt(CLIENT_ACTOR_TIMEOUT);
    }

    public Config getConfig() {
        return config;
    }

    public int getBufferSize() {
        return configuration.getBufferSize();
    }

    public boolean useDirectBuffer() {
        return configuration.isUseDirectBuffer();
    }

}
