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
import java.util.stream.Collectors;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.*;

public class Gossip extends Module {
    private final String nodePath;
    private final InetAddress ip;

    private final Random rand = new Random();
    private int currentOutGossipLevel;
    private final Map<Integer, List<GossipSiblings.Sibling>> siblings = new HashMap<>();

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

            //local
            case GOSSIP_NEXT:
                GossipNext gossipNext = (GossipNext) message.content;
                currentOutGossipLevel = gossipNext.level;
                System.out.println("Initiating gossip at level:  "+currentOutGossipLevel);
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblings(currentOutGossipLevel, nodePath)));
                break;

            //local
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

            //local
            case GOSSIP_CONTACTS:
                GossipContacts gossipContacts = (GossipContacts) message.content;
                int idx = rand.nextInt(gossipContacts.contacts.size());
                ValueContact chosen = gossipContacts.contacts.get(idx);
                List<GossipInterFreshness.Node> freshnessNodes = new ArrayList<>();
                for(GossipSiblings.Sibling s : siblings.get(currentOutGossipLevel))
                    freshnessNodes.add(new GossipInterFreshness.Node(s.pathName,s.timestamp));
                message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Start(freshnessNodes, ip, currentOutGossipLevel));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(chosen.getAddress(), message)));
                break;

            //foreign
            case GOSSIP_INTER_FRESHNESS_START:
                GossipInterFreshness gifs = (GossipInterFreshness) message.content;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblingsForGossip(gifs)));
                break;

            //foreign
            case GOSSIP_SIBLINGS_FRESHNESS:
                GossipSiblingsFreshness gossipSiblingsFreshness = (GossipSiblingsFreshness) message.content;
                message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Response(gossipSiblingsFreshness.localData, ip, gossipSiblingsFreshness.sourceMsg.level));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(gossipSiblingsFreshness.sourceMsg.responseAddress, message)));
                break;

            //local
            case GOSSIP_INTER_FRESHNESS_RESPONSE:
                GossipInterFreshness gifr = (GossipInterFreshness) message.content;
                List<GossipSiblings.Sibling> lsiblings = siblings.get(gifr.level);
                List<PathName> myUpdates = new ArrayList<>();
                for(GossipInterFreshness.Node n : gifr.nodes) {
                    boolean found = false;
                    for (GossipSiblings.Sibling s : lsiblings)
                        if (n.pathName.equals(s.pathName)) {
                            if (n.freshness.getValue() > s.timestamp.getValue())
                                myUpdates.add(n.pathName);
                            found = true;
                        }
                    if(!found)
                        myUpdates.add(n.pathName);
                }
                System.out.println("Foreign node has updates for:" + myUpdates.stream().map(Object::toString).collect(Collectors.joining(", ")));
                message = new Message(GOSSIP, GOSSIP, new GossipRequestDetails(myUpdates, ip));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(gifr.responseAddress, message)));
                break;

            //foreign
            case GOSSIP_REQUEST_DETAILS:
                GossipRequestDetails grd = (GossipRequestDetails) message.content;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperProvideDetails(grd)));
                break;

            //foreign
            case GOSSIP_PROVIDE_DETAILS:
                GossipProvideDetails gpd = (GossipProvideDetails) message.content;
                message = new Message(GOSSIP, GOSSIP, new GossipUpdate(gpd.details));
                handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(gpd.sourceMsg.responseAddress, message)));
                break;

            //local
            case GOSSIP_UPDATE:
                GossipUpdate update = (GossipUpdate) message.content;
                handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperUpdateZMI(update.details)));
                next();
                break;

            default:
                throw new IncorrectMessageContent(null, message.content.operation);
            }
        }
    }
}
