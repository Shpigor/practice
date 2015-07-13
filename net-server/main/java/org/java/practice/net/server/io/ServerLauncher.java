package org.java.practice.net.server.io;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerLauncher {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("server-context.xml");
        SimpleServer server = context.getBean(SimpleServer.class);
        server.start();
    }
}
