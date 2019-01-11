package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.concurrent.LinkedBlockingQueue;

abstract public class Module implements Runnable {
    protected final MessageHandler handler;
    protected final LinkedBlockingQueue<Message> messages;

    public Module(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        this.handler = handler;
        this.messages = messages;
    }
}
