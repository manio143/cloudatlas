package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.agent.utility.Message;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageHandler;

import java.util.concurrent.LinkedBlockingQueue;

abstract public class Module implements Runnable {
    protected final MessageHandler handler;
    protected final LinkedBlockingQueue<Message> messages;
    protected Logger logger;

    public Module(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        this.handler = handler;
        this.messages = messages;
    }
}
