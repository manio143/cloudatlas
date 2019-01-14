package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.CloudAtlasAgent;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ContentNotInitialized;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.RMI;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.GOSSIP;

public class ZMIKeeper extends Module {
    private CloudAtlasAgent agent;

    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages, CloudAtlasAgent agent) {
        super(handler, messages);
        this.agent = agent;
    }

    private void handleMessage(MessageContent content) {
        Message toRMI = new Message(ZMI_KEEPER, RMI, content);

        handler.addMessage(toRMI);
    }

    public void run() {
        try {
            while (true) {
                Message message = messages.take();

                System.out.println("Keeper received a message from: " + message.src);

                boolean correct = true;
                MessageContent content = new RMIError(new ContentNotInitialized());

                // TODO - add error detection

                switch(message.content.operation) {

                    case ZMI_KEEPER_ZONES:

                        List<String> zones = agent.getZones();

                        content = new RMIZones(zones);
                        break;

                    case ZMI_KEEPER_ATTRIBUTES:

                        ZMIKeeperAttributesMap zmiKeeperAttributesMap = (ZMIKeeperAttributesMap)message.content;

                        AttributesMap map = agent.getAttributes(zmiKeeperAttributesMap.pathName);

                        content = new RMIAttributes(map);
                        break;

                    case ZMI_KEEPER_SIBLINGS:

                        ZMIKeeperSiblings zmiKeeperSiblings = (ZMIKeeperSiblings) message.content;
                            
                        Map<PathName, List<ValueContact>> data = agent.siblings(zmiKeeperSiblings.level,
                                zmiKeeperSiblings.pathName);

                        handler.addMessage(new Message(ZMI_KEEPER, GOSSIP, new GossipSiblings(data)));
                        continue;

                    case ZMI_KEEPER_QUERIES:

                        Map<String, List<Attribute>> queries = agent.getQueries();

                        content = new RMIQueries(queries);
                        break;

                    case ZMI_KEEPER_INSTALL_QUERY:

                        ZMIKeeperInstallQueries zmiKeeperInstallQueries = (ZMIKeeperInstallQueries)message.content;

                        agent.installQueries(zmiKeeperInstallQueries.query);

                        content = new RMIInstallQuery();
                        break;

                    case ZMI_KEEPER_REMOVE_QUERY:

                        ZMIKeeperRemoveQueries zmiKeeperRemoveQueries = (ZMIKeeperRemoveQueries)message.content;

                        agent.uninstallQuery(zmiKeeperRemoveQueries.query);

                        content = new RMIRemoveQuery();
                        break;

                    case ZMI_KEEPER_SET_ATTRIBUTE:

                        ZMIKeeperSetAttribute zmiKeeperSetAttribute = (ZMIKeeperSetAttribute)message.content;

                        agent.setAttribute(
                                zmiKeeperSetAttribute.pathName,
                                zmiKeeperSetAttribute.attribute,
                                zmiKeeperSetAttribute.value
                                );

                        content = new RMISetAttribute();
                        break;

                    case ZMI_KEEPER_FALLBACK_CONTACTS:

                        ZMIKeeperFallbackContacts zmiKeeperFallbackContacts = (ZMIKeeperFallbackContacts)message.content;

                        agent.setFallbackContacts(zmiKeeperFallbackContacts.contacts);

                        content = new RMIFallbackContacts();
                        break;

                    case ZMI_KEEPER_FALLBACK_CONTACTS_GOSSIP:
                    
                        ValueSet contacts = agent.getFallbackContacts();
                        List<ValueContact> lvc = new ArrayList<>();
                        for(Value v : contacts)
                            lvc.add((ValueContact) v);
                        handler.addMessage(new Message(ZMI_KEEPER, GOSSIP, new GossipContacts(lvc)));
                        continue;

                    default:
                        System.out.println("Incorrect message type in ZMI Keeper: " + message.content.operation);
                        correct = false;
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
