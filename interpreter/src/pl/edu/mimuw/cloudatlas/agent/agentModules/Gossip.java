package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;

import pl.edu.mimuw.cloudatlas.agent.Logger;
import pl.edu.mimuw.cloudatlas.agent.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipContacts;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.GossipSiblings;
import pl.edu.mimuw.cloudatlas.agent.Message;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.ZMIKeeperSiblings;

import pl.edu.mimuw.cloudatlas.agent.gossipStrategies.GossipStrategy;
import pl.edu.mimuw.cloudatlas.model.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.*;

public class Gossip extends Module {
    private final String nodePath;
    private final InetAddress ip;
    private final ValueContact currentNode;
    private final GossipStrategy strategy;
    private final long gossipFrequency;

    private final Random rand = new Random();
    private int currentOutGossipLevel;
    private final Map<String, List<GossipInterFreshness.Node>> freshness = new HashMap<>();

    public Gossip(MessageHandler handler, LinkedBlockingQueue<Message> messages,
                  ValueContact currentNode, GossipStrategy strategy, long gossipFrequency) {
        super(handler, messages);
        this.currentNode = currentNode;
        this.nodePath = currentNode.getName().toString();
        this.ip = currentNode.getAddress();
        this.strategy = strategy;
        this.gossipFrequency = gossipFrequency;

        this.logger = new Logger(GOSSIP);
    }

    public void run() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();

        Announcer announcer = new Announcer(handler, time, gossipFrequency);

        TimerAddEvent timerAddEvent = new TimerAddEvent(0, gossipFrequency, time, announcer);

        handler.addMessage(new Message(GOSSIP, TIMER, timerAddEvent));

        while (true) {
            try {
                Message message = messages.take();

                logger.log("Received a message from: " + message.src);

                switch (message.content.operation) {

                    //local
                    case GOSSIP_NEXT:
                        currentOutGossipLevel = strategy.nextLevel();
                        logger.log("Initiating gossip at level:  " + currentOutGossipLevel);
                        handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblings(currentOutGossipLevel, nodePath)));
                        break;

                    //local
                    case GOSSIP_SIBLINGS:
                        GossipSiblings gossipSiblings = (GossipSiblings) message.content;
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
                        try {
                            int idx = rand.nextInt(gossipContacts.contacts.size());
                            ValueContact chosen = gossipContacts.contacts.get(idx);
                            List<GossipInterFreshness.Node> freshnessNodes = new ArrayList<>();
                            // get ZMI's interesting for chosen
                            handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperFreshnessForContact(chosen)));
                        } catch (IllegalArgumentException e) {
                            logger.errLog("Gossip: No contacts found!");
                        }
                        break;

                    //local
                    case GOSSIP_FRESHNESS_TO_SEND:
                        GossipFreshnessToSend toSend = (GossipFreshnessToSend) message.content;
                        freshness.put(toSend.contact.getName().toString(), toSend.nodes);
                        message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Start(toSend.nodes, currentNode, currentOutGossipLevel));
                        handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(toSend.contact.getAddress(), message)));
                        break;

                    //foreign
                    case GOSSIP_INTER_FRESHNESS_START:
                        GossipInterFreshness gifs = (GossipInterFreshness) message.content;
                        logger.log("Received gossip request from "+gifs.responseContact);
                        if(gifs.responseContact.getName().equals(currentNode.getName()))
                            break; // do not exchange contacts with yourself
                        handler.addMessage(new Message(GOSSIP, ZMI_KEEPER, new ZMIKeeperSiblingsForGossip(gifs)));
                        break;

                    //foreign
                    case GOSSIP_SIBLINGS_FRESHNESS:
                        GossipSiblingsFreshness gossipSiblingsFreshness = (GossipSiblingsFreshness) message.content;
                        message = new Message(GOSSIP, GOSSIP, GossipInterFreshness.Response(gossipSiblingsFreshness.localData, currentNode, gossipSiblingsFreshness.sourceMsg.level));
                        handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(gossipSiblingsFreshness.sourceMsg.responseContact.getAddress(), message)));
                        compareFreshness(gossipSiblingsFreshness.localData, gossipSiblingsFreshness.sourceMsg.nodes, gossipSiblingsFreshness.sourceMsg.responseContact.getAddress());
                        break;

                    //local
                    case GOSSIP_INTER_FRESHNESS_RESPONSE:
                        GossipInterFreshness gifr = (GossipInterFreshness) message.content;
                        compareFreshness(freshness.get(gifr.responseContact.getName().toString()), gifr.nodes, gifr.responseContact.getAddress());
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
                        break;

                    case TIMER_ADD_EVENT_ACK:
                        break;

                    default:
                        throw new IncorrectMessageContent(null, message.content.operation);
                }
            } catch (InterruptedException iex) {
                logger.errLog("Interrupted exception!");
            }
        }
    }

    private void compareFreshness(List<GossipInterFreshness.Node> local, List<GossipInterFreshness.Node> remote, InetAddress target) {
        logger.log("Comparing freshness of local "+local.size()+" nodes with remote "+remote.size()+" nodes.");
        List<PathName> myUpdates = new ArrayList<>();
        for (GossipInterFreshness.Node n : remote) {
            boolean found = false;
            for (GossipInterFreshness.Node s : local)
                if (n.pathName.equals(s.pathName)) {
                    if (n.freshness.getValue() > s.freshness.getValue())
                        myUpdates.add(n.pathName);
                    found = true;
                }
            if (!found)
                myUpdates.add(n.pathName);
        }
        String updates = myUpdates.stream().map(Object::toString).collect(Collectors.joining(", "));
        logger.log("Foreign node has updates for:" + updates);
        Message message = new Message(GOSSIP, GOSSIP, new GossipRequestDetails(myUpdates, ip));
        handler.addMessage(new Message(GOSSIP, COMMUNICATION, new CommunicationSend(target, message)));
    }

    public static class Announcer implements Runnable, Serializable {
        MessageHandler handler;
        long timestamp;
        long delay;

        public Announcer(MessageHandler handler, long timestamp, long delay) {
            this.handler = handler;
            this.timestamp = timestamp;
            this.delay = delay;
        }

        public void run() {
            handler.addMessage(new Message(TIMER, GOSSIP, new GossipNext()));

            long newTimestamp = timestamp + delay;

            Announcer announcer = new Announcer(handler, newTimestamp, delay);
            TimerAddEvent timerAddEvent = new TimerAddEvent(0, delay, newTimestamp, announcer);

            handler.addMessage(new Message(GOSSIP, TIMER, timerAddEvent));
        }
    }
}
