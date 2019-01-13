package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.agentExceptions.ContentNotInitialized;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.RMI;

public class ZMIKeeper extends Module {
    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
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

                switch(message.content.operation) {

                    case ZMI_KEEPER_ZONES:

                        List<String> zones = new LinkedList<>();

                        zones.add("/");
                        zones.add("/first");
                        zones.add("/second");

                        content = new RMIZones(zones);
                        break;

                    case ZMI_KEEPER_ATTRIBUTES:

                        AttributesMap map = new AttributesMap();

                        content = new RMIAttributes(map);
                        break;

                    case ZMI_KEEPER_QUERIES:

                        Map<String, List<Attribute>> queries = new HashMap<>();

                        content = new RMIQueries(queries);
                        break;

                    case ZMI_KEEPER_INSTALL_QUERY:

                        content = new RMIInstallQuery();
                        break;

                    case ZMI_KEEPER_REMOVE_QUERY:

                        content = new RMIRemoveQuery();
                        break;

                    case ZMI_KEEPER_SET_ATTRIBUTE:

                        content = new RMISetAttribute();
                        break;

                    case ZMI_KEEPER_FALLBACK_CONTACTS:

                        content = new RMIFallbackContacts();
                        break;

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
