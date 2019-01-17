package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.*;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ContentNotInitialized;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.NotSingletonZoneException;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.*;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.TIMER;

public class ZMIKeeper extends Module {
    private CloudAtlasAgent agent;
    private long computationInterval;
    private long cleanupFrequency;

    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages,
                     CloudAtlasAgent agent, long computationInterval, long cleanupFrequency) {
        super(handler, messages);
        this.agent = agent;
        this.computationInterval = computationInterval;
        this.cleanupFrequency = cleanupFrequency;
        this.logger = new Logger(ZMI_KEEPER);
    }

    private void handleMessage(MessageContent content) {
        Message toRMI = new Message(ZMI_KEEPER, RMI, content);

        handler.addMessage(toRMI);
    }

    private void scheduleQueryUpdates() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();

        QueriesUpdate updater = new QueriesUpdate(handler, time, computationInterval);

        TimerAddEvent timerAddEvent = new TimerAddEvent(0, computationInterval, time, updater);

        handler.addMessage(new Message(ZMI_KEEPER, TIMER, timerAddEvent));
    }

    private void scheduleCleanup() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();

        Cleanup cleanup = new Cleanup(handler, time, cleanupFrequency);

        TimerAddEvent timerAddEvent = new TimerAddEvent(0, cleanupFrequency, time, cleanup);

        handler.addMessage(new Message(ZMI_KEEPER, TIMER, timerAddEvent));
    }

    public void run() {
        scheduleQueryUpdates();
        scheduleCleanup();

        try {
            while (true) {
                Message message = messages.take();

                logger.log("Received a message from: " + message.src);

                boolean resend = true;
                MessageContent content = new RMIError(new ContentNotInitialized());

                try {

                    switch (message.content.operation) {

                        case ZMI_KEEPER_ZONES:

                            List<String> zones = agent.getZones();

                            content = new RMIZones(zones);
                            break;

                        case ZMI_KEEPER_ATTRIBUTES:

                            ZMIKeeperAttributesMap zmiKeeperAttributesMap = (ZMIKeeperAttributesMap) message.content;

                            AttributesMap map = agent.getAttributes(zmiKeeperAttributesMap.pathName);

                            content = new RMIAttributes(map);
                            break;

                        case ZMI_KEEPER_SIBLINGS:

                            ZMIKeeperSiblings zmiKeeperSiblings = (ZMIKeeperSiblings) message.content;

                            List<GossipSiblings.Sibling> data = agent.siblings(zmiKeeperSiblings.level,
                                    zmiKeeperSiblings.pathName);

                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP, new GossipSiblings(data)));
                            continue;

                        case ZMI_KEEPER_QUERIES:

                            Map<String, List<String>> queries = agent.getQueries();

                            content = new RMIQueries(queries);
                            break;

                        case ZMI_KEEPER_INSTALL_QUERY:

                            ZMIKeeperInstallQueries zmiKeeperInstallQueries = (ZMIKeeperInstallQueries) message.content;

                            agent.installQueries(zmiKeeperInstallQueries.query);

                            content = new RMIInstallQuery();
                            break;

                        case ZMI_KEEPER_REMOVE_QUERY:

                            ZMIKeeperRemoveQueries zmiKeeperRemoveQueries = (ZMIKeeperRemoveQueries) message.content;

                            agent.uninstallQuery(zmiKeeperRemoveQueries.query);

                            content = new RMIRemoveQuery();
                            break;

                        case ZMI_KEEPER_SET_ATTRIBUTE:

                            ZMIKeeperSetAttribute zmiKeeperSetAttribute = (ZMIKeeperSetAttribute) message.content;

                            agent.setAttribute(
                                    zmiKeeperSetAttribute.pathName,
                                    zmiKeeperSetAttribute.attribute,
                                    zmiKeeperSetAttribute.value
                            );

                            content = new RMISetAttribute();
                            break;

                        case ZMI_KEEPER_FALLBACK_CONTACTS:

                            ZMIKeeperFallbackContacts zmiKeeperFallbackContacts = (ZMIKeeperFallbackContacts) message.content;

                            agent.setFallbackContacts(zmiKeeperFallbackContacts.contacts);

                            content = new RMIFallbackContacts();
                            break;

                        case ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP:

                            ValueSet contacts = agent.getFallbackContacts();
                            List<ValueContact> lvc = new ArrayList<>();
                            for (Value v : contacts)
                                lvc.add((ValueContact) v);
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP, new GossipContacts(lvc)));
                            continue;

                        case ZMI_KEEPER_FRESHNESS_FOR_CONTACT:

                            ZMIKeeperFreshnessForContact zmiKeeperFreshnessForContact = (ZMIKeeperFreshnessForContact) message.content;
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP, new GossipFreshnessToSend(zmiKeeperFreshnessForContact.contact, agent.interestingNodes(zmiKeeperFreshnessForContact.contact))));
                            continue;

                        case ZMI_KEEPER_SIBLINGS_FOR_GOSSIP:

                            ZMIKeeperSiblingsForGossip zmiKeeperSiblingsForGossip = (ZMIKeeperSiblingsForGossip) message.content;
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP,
                                    new GossipSiblingsFreshness(zmiKeeperSiblingsForGossip.msg, agent.interestingNodes(zmiKeeperSiblingsForGossip.msg.responseContact))));
                            continue;

                        case ZMI_KEEPER_PROVIDE_DETAILS:

                            ZMIKeeperProvideDetails zmiKeeperProvideDetails = (ZMIKeeperProvideDetails) message.content;
                            Map<PathName, AttributesMap> details = new HashMap<>();
                            for (PathName pn : zmiKeeperProvideDetails.msg.nodes) {
                                details.put(pn, agent.getAttributes(pn.toString()));
                            }
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP,
                                    new GossipProvideDetails(zmiKeeperProvideDetails.msg, details, agent.getInstalledQueries())));
                            continue;

                        case ZMI_KEEPER_UPDATE_ZMI:

                            ZMIKeeperUpdateZMI zmiKeeperUpdateZMI = (ZMIKeeperUpdateZMI) message.content;
                            for (Map.Entry<PathName, AttributesMap> entry : zmiKeeperUpdateZMI.details.entrySet()) {
                                AttributesMap map1 = entry.getValue();
                                ValueTime timestamp = (ValueTime) map1.getOrNull("freshness");
                                if (timestamp == null)
                                    continue;
                                timestamp = new ValueTime(timestamp.getValue() + zmiKeeperUpdateZMI.delay);
                                map1.addOrChange("freshness", timestamp);
                                agent.setAttributes(entry.getKey().toString(), map1);
                            }
                            for (SignedQueryRequest sqr : zmiKeeperUpdateZMI.installedQueries) {
                                agent.installQueries(sqr);
                            }
                            continue;

                        case ZMI_KEEPER_RECOMPUTE_QUERIES:
                        try {
                            resend = false;
                                agent.recomputeQueries();
                            } catch (Exception e) {
                            e.printStackTrace();
                            }
                            break;

                        case TIMER_ADD_EVENT_ACK:
                            resend = false;
                            break;

                        case ZMI_KEEPER_CLEANUP:
                            resend = false;
                            agent.cleanUp(cleanupFrequency);
                            break;

                        default:
                            logger.errLog("Incorrect message type: " + message.content.operation);
                            resend = false;
                    }

                } catch (AgentException e) {
                    content = new RMIError(e);
                    e.printStackTrace();
                }

                if (resend) {
                    handleMessage(content);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class QueriesUpdate implements Runnable, Serializable {
        MessageHandler handler;
        long timestamp;
        long delay;

        public QueriesUpdate(MessageHandler handler, long timestamp, long delay) {
            this.handler = handler;
            this.timestamp = timestamp;
            this.delay = delay;
        }

        public void run() {
            handler.addMessage(new Message(TIMER, ZMI_KEEPER, new ZMIKeeperRecomputeQueries()));

            long newTimestamp = timestamp + delay;

            QueriesUpdate update = new QueriesUpdate(handler, newTimestamp, delay);
            TimerAddEvent timerAddEvent = new TimerAddEvent(0, delay, newTimestamp, update);

            handler.addMessage(new Message(ZMI_KEEPER, TIMER, timerAddEvent));
        }
    }

    public static class Cleanup implements Runnable, Serializable {
        MessageHandler handler;
        long timestamp;
        long delay;

        public Cleanup(MessageHandler handler, long timestamp, long delay) {
            this.handler = handler;
            this.timestamp = timestamp;
            this.delay = delay;
        }

        public void run() {
            handler.addMessage(new Message(TIMER, ZMI_KEEPER, new ZMIKeeperCleanup()));

            long newTimestamp = timestamp + delay;

            Cleanup cleanup = new Cleanup(handler, newTimestamp, delay);
            TimerAddEvent timerAddEvent = new TimerAddEvent(0, delay, newTimestamp, cleanup);

            handler.addMessage(new Message(ZMI_KEEPER, TIMER, timerAddEvent));
        }
    }
}
