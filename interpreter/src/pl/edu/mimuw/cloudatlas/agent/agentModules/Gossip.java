package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.*;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipContacts;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipSiblings;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipStrategyNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.GOSSIP;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.GOSSIP_STRATEGY;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.ZMI_KEEPER;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.ZMIKeeperFallbackContacts;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.ZMIKeeperSiblings;

import pl.edu.mimuw.cloudatlas.model.*;

import java.util.concurrent.LinkedBlockingQueue;

public class Gossip extends Module {
    String nodePath;
    Random rand = new Random();

    public Gossip(MessageHandler handler, LinkedBlockingQueue<Message> messages, String nodePath) {
        super(handler, messages);
        next();
    }

    private void next() {
        handler.addMessage(new Message(GOSSIP, GOSSIP_STRATEGY, new GossipStrategyNext()));
    }

    public void run() {
        while (true) {
            Message message;
            try {
                message = messages.take();
            } catch (InterruptedException iex) {
                continue;
            }
            System.out.println("Gossip received a message from: " + message.src);

            switch (message.content.operation) {
            case GOSSIP_NEXT:
                GossipNext gossipNext = (GossipNext) message.content;
                int level = gossipNext.level;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblings(level, nodePath)));
                break;
            case GOSSIP_SIBLINGS:
                GossipSiblings gossipSiblings = (GossipSiblings) message.content;
                List<List<ValueContact>> nonEmptyContacts = new ArrayList<>();
                for (Map.Entry<PathName, List<ValueContact>> pn : gossipSiblings.contacts.entrySet()) {
                    if (pn.getValue().size() > 0)
                        nonEmptyContacts.add(pn.getValue());
                }
                if (nonEmptyContacts.size() > 0) {
                    int idx = rand.nextInt(nonEmptyContacts.size());
                    handler.addMessage(new Message(GOSSIP, GOSSIP, new GossipContacts(nonEmptyContacts.get(idx))));
                } else {
                    handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperFallbackContactsGossip()));
                }
                break;
            case GOSSIP_CONTACTS:
                GossipContacts gossipContacts = (GossipContacts) message.content;
                int idx = rand.nextInt(gossipContacts.contacts.size());
                ValueContact chosen = gossipContacts.contacts.get(idx);
                // start communication with chosen contact
                // ???
                break;
            default:
                throw new IncorrectMessageContent(null, message.content.operation);
            }
        }
    }
}
