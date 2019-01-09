package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageHandler {
    ConcurrentLinkedQueue<ModuleMessage> timerQueue = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<ModuleMessage> communicationQueue = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<ModuleMessage> rmiQueue = new ConcurrentLinkedQueue<>();

    public MessageHandler(List<ConcurrentLinkedQueue<ModuleMessage>> queues) {
        timerQueue = queues.get(0);
        communicationQueue = queues.get(1);
        rmiQueue = queues.get(2);
    }

    public synchronized void addMessage(ModuleMessage message) {
        switch (message.dest) {
            case TIMER:
                timerQueue.add(message);
                break;
            case COMMUNICATION:
                communicationQueue.add(message);
                break;
            case RMI:
                rmiQueue.add(message);
                break;
            default:
        }
    }
}
