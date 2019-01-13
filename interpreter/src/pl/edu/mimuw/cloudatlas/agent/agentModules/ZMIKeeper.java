package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.RMIZones;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.agentMessages.Message.Module.RMI;

public class ZMIKeeper extends Module {
    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void run() {
        try {
            while (true) {
                Message message = messages.take();

                System.out.println("Keeper received a message from: " + message.src);

                switch(message.content.operation) {
                    case ZMI_KEEPER_ZONES:

                        List<String> zones = new LinkedList<>();

                        zones.add("/");
                        zones.add("/first");
                        zones.add("/second");

                        RMIZones content = new RMIZones(zones);

                        Message toRMI = new Message(ZMI_KEEPER, RMI, content);

                        handler.addMessage(toRMI);

                        break;

                    default:
                        System.out.println("Incorrect message type in ZMI Keeper: " + message.content.operation);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
