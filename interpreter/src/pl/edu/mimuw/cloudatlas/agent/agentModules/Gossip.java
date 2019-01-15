package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.net.InetAddress;
import java.util.*;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipContacts;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipSiblings;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipStrategyNext;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.ZMIKeeperSiblings;

import pl.edu.mimuw.cloudatlas.model.*;

import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.*;

public class Gossip extends Module {
    String nodePath;
    InetAddress ip;

    Random rand = new Random();
    int currentOutGossipLevel;
    Map<Integer, List<GossipSiblings.Sibling>> siblings;

    public Gossip(MessageHandler handler, LinkedBlockingQueue<Message> messages, ValueContact currentNode) {
        super(handler, messages);
        nodePath = currentNode.getName().toString();
        ip = currentNode.getAddress();
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
                currentOutGossipLevel = gossipNext.level;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblings(currentOutGossipLevel, nodePath)));
                break;
            case GOSSIP_SIBLINGS:
                GossipSiblings gossipSiblings = (GossipSiblings) message.content;
                siblings.put(currentOutGossipLevel, gossipSiblings.siblings);
                List<List<ValueContact>> nonEmptyContacts = new ArrayList<>();
                for (GossipSiblings.Sibling sibling : gossipSiblings.siblings) {
                    if (sibling.contacts.size() > 0)
                        nonEmptyContacts.add(sibling.contacts);
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
                List<GossipInterFreshness.Node> freshnessNodes = new ArrayList<>();
                for(GossipSiblings.Sibling s : siblings.get(currentOutGossipLevel))
                    freshnessNodes.add(new GossipInterFreshness.Node(s.pathName,s.timestamp));
                message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Start(freshnessNodes, ip));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(chosen.getAddress(), message)));
                break;

            case GOSSIP_INTER_FRESHNESS_START:
                GossipInterFreshness gifs = (GossipInterFreshness) message.content;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblingsForGossip(gifs)));
                break;

            case GOSSIP_SIBLINGS_FRESHNESS:
                GossipSiblingsFreshness gossipSiblingsFreshness = (GossipSiblingsFreshness) message.content;
                message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Response(gossipSiblingsFreshness.localData, ip));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(gossipSiblingsFreshness.sourceMsg.responseAddress, message)));
                break;

            case GOSSIP_INTER_FRESHNESS_RESPONSE:
                GossipInterFreshness gifr = (GossipInterFreshness) message.content;
                System.out.println("Comparing contacts");
                break;

            default:
                throw new IncorrectMessageContent(null, message.content.operation);
            }
        }
    }
}
