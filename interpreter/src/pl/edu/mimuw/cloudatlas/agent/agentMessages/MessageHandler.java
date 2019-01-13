package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.QueueKeeper;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {
    private final QueueKeeper keeper;

    public MessageHandler(QueueKeeper keeper) {
        this.keeper = keeper;
    }

    public synchronized void addMessage(Message message) {

        String log = "Handler has received message from " + message.src
                + " to " + message.dest
                + ", operation: " + message.content.operation;

        System.out.println(log);

        switch (message.dest) {
            case TIMER:
                keeper.timerQueue.add(message);
                break;
            case COMMUNICATION:
                keeper.communicationQueue.add(message);
                break;
            case RMI:
                keeper.rmiQueue.add(message);
                break;
            case ZMI_KEEPER:
                keeper.zmiKeeperQueue.add(message);
                break;
            case TESTER:
                keeper.testerQueue.add(message);
                break;
            default:
        }
    }
}
