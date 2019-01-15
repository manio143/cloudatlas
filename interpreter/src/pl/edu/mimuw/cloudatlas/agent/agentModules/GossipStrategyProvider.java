package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.mimuw.cloudatlas.agent.gossipStrategies.*;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageSource;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageContent;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.GOSSIP;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.GOSSIP_STRATEGY;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.model.PathName;

public class GossipStrategyProvider extends Module {
    GossipStrategy strategy;

    public GossipStrategyProvider(MessageHandler handler, LinkedBlockingQueue<Message> messages,
            GossipStrategy strategy) {
        super(handler, messages);
        this.strategy = strategy;
    }

    public void setGossipStrategy(GossipStrategy strategy) {
        this.strategy = strategy;
    }

    public void run() {
        while (true) {
            try {
                Message message = messages.take();

                System.out.println("GossipSP received a message from: " + message.src);

                if (message.src != GOSSIP)
                    throw new IncorrectMessageSource(GOSSIP, message.src);

                switch (message.content.operation) {
                case GOSSIP_STRATEGY_NEXT:
                    int level = this.strategy.nextLevel();
                    MessageContent content = new GossipNext(level);
                    handler.addMessage(new Message(GOSSIP_STRATEGY, GOSSIP, content));
                    break;
                }
            } catch (InterruptedException iex) {
                continue;
            } catch (IncorrectMessageSource ims) {
                ims.printStackTrace();
                continue;
            }
        }
    }
}
