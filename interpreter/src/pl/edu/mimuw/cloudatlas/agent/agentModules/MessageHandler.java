package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {
    private LinkedBlockingQueue<Message> timerQueue;
    private LinkedBlockingQueue<Message> communicationQueue;
    private LinkedBlockingQueue<Message> rmiQueue;
    private LinkedBlockingQueue<Message> testerQueue;

    public MessageHandler(List<LinkedBlockingQueue<Message>> queues) {
        timerQueue = queues.get(0);
        communicationQueue = queues.get(1);
        rmiQueue = queues.get(2);
        testerQueue = queues.get(3);
    }

    public synchronized void addMessage(Message message) {
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
            case TESTER:
                testerQueue.add(message);
                break;
            default:
        }
    }
}
