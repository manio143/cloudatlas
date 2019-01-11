package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {
    LinkedBlockingQueue<Message> timerQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Message> communicationQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Message> rmiQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Message> testerQueue = new LinkedBlockingQueue<>();

    public MessageHandler(List<LinkedBlockingQueue<Message>> queues) {
        timerQueue = queues.get(0);
        communicationQueue = queues.get(1);
        rmiQueue = queues.get(2);
        testerQueue = queues.get(3);
    }

    public synchronized void addMessage(Message message) {
        Message copy = new Message(message);
        switch (copy.dest) {
            case TIMER:
                timerQueue.add(copy);
                break;
            case COMMUNICATION:
                communicationQueue.add(copy);
                break;
            case RMI:
                rmiQueue.add(copy);
                break;
            case TESTER:
                testerQueue.add(copy);
                break;
            default:
        }
    }
}
