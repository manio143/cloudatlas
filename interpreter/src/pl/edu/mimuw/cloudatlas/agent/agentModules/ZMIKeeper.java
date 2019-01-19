package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.*;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ContentNotInitialized;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.gossip.*;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi.*;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.zmiKeeper.*;
import pl.edu.mimuw.cloudatlas.agent.utility.*;
import pl.edu.mimuw.cloudatlas.interpreter.InterpreterException;
import pl.edu.mimuw.cloudatlas.model.*;
import pl.edu.mimuw.cloudatlas.signer.SignedQueryRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.utility.ModuleName.*;

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

    private void handleMessage(MessageContent content, ModuleName destination) {
        Message toRMI = new Message(ZMI_KEEPER, destination, content);

        handler.addMessage(toRMI);
    }

    private void scheduleQueryUpdates() {
        Timer.NotificationInfo info =
                new Timer.NotificationInfo(handler, logger,
                        new ZMIKeeperRecomputeQueries(), ZMI_KEEPER, 0, computationInterval);

        Timer.scheduleNotification(info);
    }

    private void scheduleCleanup() {
        Timer.NotificationInfo info =
                new Timer.NotificationInfo(handler, logger,
                        new ZMIKeeperCleanup(), ZMI_KEEPER, 1, cleanupFrequency);

        Timer.scheduleNotification(info);
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

                List<Node> nodes;
                ValueContact contact;

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

                        case ZMI_KEEPER_QUERIES:

                            Map<String, List<String>> queries = agent.getQueries();

                            content = new RMIQueries(queries);
                            break;

                        case ZMI_KEEPER_TRY_INSTALL_QUERY:
                            ZMIKeeperTryInstallQuery zmiKeeperTryInstallQuery = (ZMIKeeperTryInstallQuery) message.content;

                            agent.tryInstallQuery(zmiKeeperTryInstallQuery.query);

                            content = new RMITryInstallQuery();
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

                            ZMIKeeperFallbackContacts zmiKeeperFallbackContacts =
                                    (ZMIKeeperFallbackContacts) message.content;

                            agent.setFallbackContacts(zmiKeeperFallbackContacts.contacts);

                            content = new RMIFallbackContacts();
                            break;

                        case ZMI_KEEPER_SIBLINGS:

                            ZMIKeeperSiblings zmiKeeperSiblings =
                                    (ZMIKeeperSiblings) message.content;

                            List<Sibling> siblingList =
                                    agent.siblings(zmiKeeperSiblings.level, zmiKeeperSiblings.pathName);

                            content = new GossipSiblings(siblingList);
                            break;

                        case ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP:

                            ValueSet contacts = agent.getFallbackContacts();
                            List<ValueContact> contactList = new ArrayList<>();
                            for (Value cont : contacts) {
                                contactList.add((ValueContact) cont);
                            }

                            content = new GossipContacts(contactList);
                            break;

                        case ZMI_KEEPER_FRESHNESS_FOR_CONTACT:

                            ZMIKeeperFreshnessForContact zmiKeeperFreshnessForContact =
                                    (ZMIKeeperFreshnessForContact) message.content;

                            contact = zmiKeeperFreshnessForContact.contact;

                            nodes = agent.interestingNodes(contact);

                            content = new GossipFreshnessToSend(contact, nodes);
                            break;

                        case ZMI_KEEPER_SIBLINGS_FOR_GOSSIP:

                            ZMIKeeperSiblingsForGossip zmiKeeperSiblingsForGossip =
                                    (ZMIKeeperSiblingsForGossip) message.content;

                            contact = zmiKeeperSiblingsForGossip.msg.responseContact;

                            nodes = agent.interestingNodes(contact);

                            content = new GossipSiblingsFreshness(zmiKeeperSiblingsForGossip.msg, nodes);
                            break;

                        case ZMI_KEEPER_PROVIDE_DETAILS:

                            ZMIKeeperProvideDetails zmiKeeperProvideDetails =
                                    (ZMIKeeperProvideDetails) message.content;

                            Map<PathName, AttributesMap> details = new HashMap<>();

                            for (PathName path : zmiKeeperProvideDetails.msg.nodes) {
                                details.put(path, agent.getAttributes(path.toString()));
                            }

                            content = new GossipProvideDetails(zmiKeeperProvideDetails.msg,
                                                                details,
                                                                agent.getInstalledQueries(),
                                                                agent.getUninstalledQueries());
                            break;

                        case ZMI_KEEPER_UPDATE_ZMI:

                            ZMIKeeperUpdateZMI zmiKeeperUpdateZMI = (ZMIKeeperUpdateZMI) message.content;

                            for (Map.Entry<PathName, AttributesMap> entry : zmiKeeperUpdateZMI.details.entrySet()) {
                                AttributesMap map1 = entry.getValue();
                                ValueTime timestamp = (ValueTime) map1.getOrNull("freshness");

                                logger.log("ZMIKeeper received update for " + entry.getKey() + "  " + timestamp);

                                if (timestamp == null) {
                                    continue;
                                }

                                timestamp = new ValueTime(timestamp.getValue() + zmiKeeperUpdateZMI.delay);
                                map1.addOrChange("freshness", timestamp);
                                agent.setAttributes(entry.getKey().toString(), map1);
                            }

                            for (SignedQueryRequest sqr : zmiKeeperUpdateZMI.installedQueries) {
                                agent.installQueries(sqr);
                            }

                            for (Long queryId : zmiKeeperUpdateZMI.uninstalledQueries) {
                                agent.safeUninstallQueryById(queryId);
                            }
                            continue;

                        case ZMI_KEEPER_RECOMPUTE_QUERIES:
                            agent.recomputeQueries();
                            break;

                        case ZMI_KEEPER_CLEANUP:
                            agent.cleanUp(cleanupFrequency);
                            break;

                        case TIMER_ADD_EVENT_ACK:
                            break;

                        default:
                            logger.errLog("Incorrect message type: " + message.content.operation);
                            resend = false;
                    }

                } catch (AgentException e) {
                    content = new RMIError(e);
                    e.printStackTrace();
                }

                ModuleName dest = RMI;

                switch(message.content.operation) {
                    case ZMI_KEEPER_ZONES:
                    case ZMI_KEEPER_ATTRIBUTES:
                    case ZMI_KEEPER_QUERIES:
                    case ZMI_KEEPER_TRY_INSTALL_QUERY:
                    case ZMI_KEEPER_INSTALL_QUERY:
                    case ZMI_KEEPER_REMOVE_QUERY:
                    case ZMI_KEEPER_SET_ATTRIBUTE:
                    case ZMI_KEEPER_FALLBACK_CONTACTS:
                        dest = RMI;
                        break;

                    case ZMI_KEEPER_SIBLINGS:
                    case ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP:
                    case ZMI_KEEPER_FRESHNESS_FOR_CONTACT:
                    case ZMI_KEEPER_SIBLINGS_FOR_GOSSIP:
                    case ZMI_KEEPER_PROVIDE_DETAILS:
                        dest = GOSSIP;
                        break;

                    default:
                        resend = false;
                }

                if (resend) {
                    handleMessage(content, dest);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
