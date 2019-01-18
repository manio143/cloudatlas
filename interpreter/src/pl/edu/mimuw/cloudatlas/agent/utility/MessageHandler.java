package pl.edu.mimuw.cloudatlas.agent.utility;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.CONTENT_PLACEHOLDER;

public class MessageHandler {
    private final QueueKeeper keeper;
    private final Logger logger = new Logger("HANDLER");

    public MessageHandler(QueueKeeper keeper) {
        this.keeper = keeper;
    }

    public synchronized void addMessage(Message message) {

        if (message.content.operation == CONTENT_PLACEHOLDER) {
            logger.errLog("Message sent without proper content set!");

            return;
        }

        logger.log("Message from " + message.src
                + " to " + message.dest
                + ", operation: " + message.content.operation);

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
            case GOSSIP:
                keeper.gossipQueue.add(message);
                break;
            case GOSSIP_STRATEGY:
                keeper.gossipStrategyQueue.add(message);
                break;
            default:
                logger.errLog("Unknown destination: " + message.dest);
        }
    }
}
