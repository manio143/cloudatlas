package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import pl.edu.mimuw.cloudatlas.agent.utility.*;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.IncorrectMessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip.*;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper.*;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.communication.CommunicationSend;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimerAddEvent;
import pl.edu.mimuw.cloudatlas.agent.gossipStrategies.GossipStrategy;
import pl.edu.mimuw.cloudatlas.model.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static pl.edu.mimuw.cloudatlas.agent.utility.ModuleName.*;

public class Gossip extends Module {
    private final String nodePath;
    private final InetAddress ip;
    private final ValueContact currentNode;
    private final GossipStrategy strategy;
    private final long gossipFrequency;

    private final Random rand = new Random();
    private int currentOutGossipLevel;
    private final Map<String, List<Node>> freshness = new HashMap<>();
    private final Map<String, Long> delay = new HashMap<>();

    private final GossipState[] gossips = new GossipState[100];
    private int gossipId;

    private final long repeatK;
    private final long repeatInterval;

    public Gossip(MessageHandler handler, LinkedBlockingQueue<Message> messages,
                  ValueContact currentNode, GossipStrategy strategy, long gossipFrequency, long repeatK, long repeatInterval) {
        super(handler, messages);
        this.currentNode = currentNode;
        this.nodePath = currentNode.getName().toString();
        this.ip = currentNode.getAddress();
        this.strategy = strategy;
        this.gossipFrequency = gossipFrequency;
        this.repeatK = repeatK;
        this.repeatInterval = repeatInterval;

        this.logger = new Logger(GOSSIP);
    }

    public void run() {

        Timer.NotificationInfo info =
                new Timer.NotificationInfo(handler, logger, new GossipNext(), GOSSIP, 0, gossipFrequency);

        Timer.scheduleNotification(info);

        while (true) {
            try {
                Message message = messages.take();

                logger.log("Received a message from: " + message.src);

                MessageContent content;
                Message forHandler;

                Message communicationInternal;
                CommunicationSend communicationSend;
                Message forCommunication;

                GossipInterFreshness gossipInterFreshness;
                GossipUpdate gossipUpdate;

                InetAddress address;
                String contactName;

                switch (message.content.operation) {

                    //local
                    case GOSSIP_NEXT:
                        currentOutGossipLevel = strategy.nextLevel();
                        logger.log("Initiating gossip at level:  " + currentOutGossipLevel);

                        content = new ZMIKeeperSiblings(currentOutGossipLevel, nodePath);
                        forHandler = new Message(GOSSIP, ZMI_KEEPER, content);
                        handler.addMessage(forHandler);
                        break;

                    //local
                    case GOSSIP_SIBLINGS:
                        GossipSiblings gossipSiblings = (GossipSiblings) message.content;

                        List<List<ValueContact>> nonEmptyContacts = new ArrayList<>();
                        for (Sibling sibling : gossipSiblings.siblings) {
                            if (sibling.contacts.size() > 0) {
                                nonEmptyContacts.add(sibling.contacts);
                            }
                        }

                        if (nonEmptyContacts.size() > 0) {
                            int idx = rand.nextInt(nonEmptyContacts.size());

                            List<ValueContact> contactsList = nonEmptyContacts.get(idx);

                            logger.log("Sibling with contacts " + contactsList);

                            content =  new GossipContacts(contactsList);
                            forHandler = new Message(GOSSIP, GOSSIP, content);
                            handler.addMessage(forHandler);

                        } else {
                            content = new ZMIKeeperFallbackContactsGossip();
                            forHandler = new Message(GOSSIP, ZMI_KEEPER, content);
                            handler.addMessage(forHandler);
                        }
                        break;

                    //local
                    case GOSSIP_CONTACTS:
                        GossipContacts gossipContacts = (GossipContacts) message.content;
                        try {
                            int range = gossipContacts.contacts.size();
                            if (range == 0) {
                                logger.errLog("No contacts found!");

                            } else {
                                int idx = rand.nextInt(range);
                                ValueContact chosen = gossipContacts.contacts.get(idx);
                                logger.log("Chosen " + chosen + " to gossip with.");

                                content = new ZMIKeeperFreshnessForContact(chosen);
                                forHandler = new Message(GOSSIP, ZMI_KEEPER, content);
                                handler.addMessage(forHandler);
                            }
                        } catch (IllegalArgumentException e) {
                            logger.errLog("No contacts found!");
                        }
                        break;

                    //local
                    case GOSSIP_FRESHNESS_TO_SEND:
                        GossipFreshnessToSend gossipFreshnessToSend = (GossipFreshnessToSend) message.content;
                        contactName = gossipFreshnessToSend.contact.getName().toString();

                        freshness.put(contactName, gossipFreshnessToSend.nodes);
                        int id = nextGossipId();

                        if (id >= 0) {
                            gossipInterFreshness =
                                    GossipInterFreshness.Start(gossipFreshnessToSend.nodes,
                                            currentNode,
                                            currentOutGossipLevel,
                                            id);

                            communicationInternal = new Message(GOSSIP, GOSSIP, gossipInterFreshness);

                            address = gossipFreshnessToSend.contact.getAddress();

                            communicationSend = new CommunicationSend(address, communicationInternal);

                            forCommunication = new Message(GOSSIP, COMMUNICATION, communicationSend);

                            handler.addMessage(forCommunication);

                            Repeat repeat = new Repeat(id, gossipFreshnessToSend);
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            content = new TimerAddEvent(id, repeatInterval, timestamp.getTime(), repeat);
                            forHandler = new Message(GOSSIP, TIMER, content);

                            handler.addMessage(forHandler);
                        }
                        break;

                    //foreign
                    case GOSSIP_INTER_FRESHNESS_START:
                        gossipInterFreshness = (GossipInterFreshness) message.content;
                        logger.log("Received gossip request from " + gossipInterFreshness.responseContact);

                        if(gossipInterFreshness.responseContact.getName().equals(currentNode.getName())) {
                            break; // do not exchange contacts with yourself
                        }
                        content = new ZMIKeeperSiblingsForGossip(gossipInterFreshness);
                        forHandler = new Message(GOSSIP, ZMI_KEEPER, content);
                        handler.addMessage(forHandler);

                        break;

                    //foreign
                    case GOSSIP_SIBLINGS_FRESHNESS:
                        GossipSiblingsFreshness gossipSiblingsFreshness = (GossipSiblingsFreshness) message.content;
                        gossipInterFreshness =
                                GossipInterFreshness.Response(gossipSiblingsFreshness.localData,
                                                            currentNode,
                                                            gossipSiblingsFreshness.sourceMsg.level,
                                                            gossipSiblingsFreshness.sourceMsg.id);

                        gossipInterFreshness.addTimestamps(gossipSiblingsFreshness.sourceMsg.timestamps);
                        communicationInternal = new Message(GOSSIP, GOSSIP, gossipInterFreshness);

                        address = gossipSiblingsFreshness.sourceMsg.responseContact.getAddress();

                        communicationSend = new CommunicationSend(address, communicationInternal);
                        forCommunication = new Message(GOSSIP, COMMUNICATION, communicationSend);

                        handler.addMessage(forCommunication);

                        compareFreshness(gossipSiblingsFreshness.localData, gossipSiblingsFreshness.sourceMsg.nodes, address);
                        break;

                    //local
                    case GOSSIP_INTER_FRESHNESS_RESPONSE:
                        gossipInterFreshness = (GossipInterFreshness) message.content;

                        gossips[gossipInterFreshness.id].set();

                        recordDelay(gossipInterFreshness.responseContact, gossipInterFreshness.timestamps);

                        address = gossipInterFreshness.responseContact.getAddress();

                        contactName = gossipInterFreshness.responseContact.getName().toString();

                        compareFreshness(freshness.get(contactName), gossipInterFreshness.nodes, address);
                        break;

                    //foreign
                    case GOSSIP_REQUEST_DETAILS:
                        GossipRequestDetails gossipRequestDetails = (GossipRequestDetails) message.content;

                        content = new ZMIKeeperProvideDetails(gossipRequestDetails);

                        forHandler = new Message(GOSSIP, ZMI_KEEPER, content);

                        handler.addMessage(forHandler);
                        break;

                    //foreign
                    case GOSSIP_PROVIDE_DETAILS:
                        GossipProvideDetails gossipProvideDetails = (GossipProvideDetails) message.content;
                        gossipUpdate = new GossipUpdate(gossipProvideDetails.details,
                                                        gossipProvideDetails.installedQueries,
                                                        gossipProvideDetails.uninstalledQueries,
                                                        currentNode);

                        gossipUpdate.addTimestamps(gossipProvideDetails.sourceMsg.timestamps);

                        communicationInternal = new Message(GOSSIP, GOSSIP, gossipUpdate);
                        address = gossipProvideDetails.sourceMsg.responseAddress;

                        communicationSend = new CommunicationSend(address, communicationInternal);

                        forCommunication = new Message(GOSSIP, COMMUNICATION, communicationSend);

                        handler.addMessage(forCommunication);
                        break;

                    //local
                    case GOSSIP_UPDATE:
                        gossipUpdate = (GossipUpdate) message.content;

                        recordDelay(gossipUpdate.responseContact, gossipUpdate.timestamps);

                        logger.log("Received " + gossipUpdate.details.size() + " updates.");

                        contactName = gossipUpdate.responseContact.getName().toString();

                        content = new ZMIKeeperUpdateZMI(gossipUpdate.details,
                                                        gossipUpdate.installedQueries,
                                                        gossipUpdate.uninstalledQueries,
                                                        delay.get(contactName));

                        forHandler = new Message(GOSSIP, ZMI_KEEPER, content);

                        handler.addMessage(forHandler);
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

    private void recordDelay(ValueContact contact, List<Long> timestamps) {
        if (timestamps.size() != 4) {
            logger.errLog("Invalid number of timestamps to record delay.");
            return;
        }
        long t1a = timestamps.get(0);
        long t1b = timestamps.get(1);
        long t2b = timestamps.get(2);
        long t2a = timestamps.get(3);

        long rtd = (t2a - t1a) - (t2b - t1b);

        long dT = t2b + (long)(0.5 * rtd) - t2a;

        delay.put(contact.getName().toString(), dT);

        logger.log("Recorded delay "+dT+"ms to node "+contact.getName());
    }

    private int nextGossipId() {
        int starting = gossipId - 1;
        while (starting != gossipId) {
            if (gossips[gossipId] == null || gossips[gossipId].isSet()) {
                gossips[gossipId] = new GossipState();
                int id = gossipId;
                gossipId = (gossipId + 1) % gossips.length;
                return id;
            } else {
                gossipId = (gossipId + 1) % gossips.length;
            }
        }

        logger.errLog("No more space for gossips");
        return -1;
    }

    private class GossipState {
        private boolean completed = false;
        private int tryNum = 0;

        public void set() { completed = true; }
        public boolean isSet() { return completed; }
        public boolean tryAgain() {
            tryNum++;
            return tryNum < repeatK;
        }
    }

    private void compareFreshness(List<Node> local, List<Node> remote, InetAddress target) {
        logger.log("Comparing freshness of local " + local.size()
                        + " nodes with remote " + remote.size() + " nodes.");
        List<PathName> updates = new ArrayList<>();
        for (Node fromRemote : remote) {
            boolean found = false;
            for (Node fromLocal : local) {
                if (fromRemote.pathName.equals(fromLocal.pathName)) {
                    if (fromRemote.freshness.getValue() > fromLocal.freshness.getValue()) {
                        updates.add(fromRemote.pathName);
                    }
                    found = true;
                }
            }
            if (!found) {
                updates.add(fromRemote.pathName);
            }
        }

        if (updates.size() == 0) {
            return; //do not send any requests
        }

        String updatesString = updates.stream().map(Object::toString).collect(Collectors.joining(", "));
        logger.log("Foreign node has updates for:" + updatesString);

        GossipRequestDetails gossipRequestDetails = new GossipRequestDetails(updates, ip);
        Message internal = new Message(GOSSIP, GOSSIP, gossipRequestDetails);
        CommunicationSend communicationSend = new CommunicationSend(target, internal);
        Message message = new Message(GOSSIP, COMMUNICATION, communicationSend);
        handler.addMessage(message);
    }

    public class Repeat implements Runnable, Serializable {
        private final int id;
        private final GossipFreshnessToSend toSend;

        public Repeat(int id, GossipFreshnessToSend toSend) {
            this.id = id;
            this.toSend = toSend;
        }

        public void run() {
            logger.log("Repeat: checking if gossip with id " + id + " has been completed.");

            if (!gossips[id].isSet() && gossips[id].tryAgain()) {

                String log = "Repeat: retrying (" + gossips[id].tryNum + "/" + repeatK + ")";

                logger.log(log);

                GossipInterFreshness gossipInterFreshness =
                        GossipInterFreshness.Start(toSend.nodes, currentNode, -1, id);
                Message communicationInternal = new Message(TIMER, GOSSIP, gossipInterFreshness);
                InetAddress address = toSend.contact.getAddress();
                CommunicationSend communicationSend = new CommunicationSend(address, communicationInternal);
                Message forCommunication = new Message(GOSSIP, COMMUNICATION, communicationSend);

                handler.addMessage(forCommunication);

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                TimerAddEvent timerAddEvent = new TimerAddEvent(id, repeatInterval, timestamp.getTime(), this);
                Message forTimer = new Message(GOSSIP, TIMER, timerAddEvent);

                handler.addMessage(forTimer);

            } else {
                logger.log("Repeat completed.");
            }
        }
    }
}
