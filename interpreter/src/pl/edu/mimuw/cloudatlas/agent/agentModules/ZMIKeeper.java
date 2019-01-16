package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.*;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ContentNotInitialized;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.RMI;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.GOSSIP;

public class ZMIKeeper extends Module {
    private CloudAtlasAgent agent;

    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages, CloudAtlasAgent agent) {
        super(handler, messages);
        this.agent = agent;
        this.logger = new Logger(ZMI_KEEPER);
    }

    private void handleMessage(MessageContent content) {
        Message toRMI = new Message(ZMI_KEEPER, RMI, content);

        handler.addMessage(toRMI);
    }

    public void run() {
        try {
            while (true) {
                Message message = messages.take();

                logger.log("Received a message from: " + message.src);

                boolean correct = true;
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

                            Map<String, List<Attribute>> queries = agent.getQueries();

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

                        case ZMI_KEEPER_SIBLINGS_FOR_GOSSIP:

                            ZMIKeeperSiblingsForGossip zmiKeeperSiblingsForGossip = (ZMIKeeperSiblingsForGossip) message.content;
                            List<GossipInterFreshness.Node> myNodes = new ArrayList<>();
                            for (GossipInterFreshness.Node node : zmiKeeperSiblingsForGossip.msg.nodes) {
                                AttributesMap m = agent.getAttributes(node.pathName.toString());
                                myNodes.add(new GossipInterFreshness.Node(node.pathName, (ValueTime) m.get("timestamp")));
                            }
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP,
                                    new GossipSiblingsFreshness(zmiKeeperSiblingsForGossip.msg, myNodes)));
                            continue;

                        case ZMI_KEEPER_PROVIDE_DETAILS:

                            ZMIKeeperProvideDetails zmiKeeperProvideDetails = (ZMIKeeperProvideDetails) message.content;
                            Map<PathName, AttributesMap> details = new HashMap<>();
                            for (PathName pn : zmiKeeperProvideDetails.msg.nodes)
                                details.put(pn, agent.getAttributes(pn.toString()));
                            handler.addMessage(new Message(ZMI_KEEPER, GOSSIP,
                                    new GossipProvideDetails(zmiKeeperProvideDetails.msg, details)));
                            continue;

                        case ZMI_KEEPER_UPDATE_ZMI:

                            ZMIKeeperUpdateZMI zmiKeeperUpdateZMI = (ZMIKeeperUpdateZMI) message.content;
                            for (Map.Entry<PathName, AttributesMap> entry : zmiKeeperUpdateZMI.details.entrySet())
                                agent.setAttributes(entry.getKey().toString(), entry.getValue());
                            continue;

                        default:
                            logger.log("Incorrect message type: " + message.content.operation);
                            correct = false;
                    }

                } catch (AgentException e) {
                    content = new RMIError(e);
                }

                if (correct) {
                    handleMessage(content);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
