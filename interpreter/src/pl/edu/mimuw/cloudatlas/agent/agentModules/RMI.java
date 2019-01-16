package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.CloudAtlasRMI;
import pl.edu.mimuw.cloudatlas.agent.Message;
import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.MessageHandler;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.ZMI_KEEPER;

public class RMI extends Module {
    private SynchronousQueue<MessageContent> rmi = new SynchronousQueue<>();
    private RMIController controller = new RMIController();

    public RMI(MessageHandler handler, LinkedBlockingQueue<Message> messages)
    {
        
        super(handler, messages);
    }

    public void run() {
        try {
            CloudAtlasRMI object = new CloudAtlasRMI(handler, rmi, controller);
            CloudAtlasAPI stub =
                    (CloudAtlasAPI) UnicastRemoteObject.exportObject(object, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("CloudAtlasAPI", stub);

            while (true) {
                Message message = messages.take();

                if (message.src == ZMI_KEEPER && controller.waiting) {
                    rmi.put(message.content);
                } else if (message.src != ZMI_KEEPER) {
                    System.out.println("Message not from ZMI Keeper, but from: " + message.src);
                } else {
                    System.out.println("No RMI function invoked!");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public class RMIController {
        public boolean waiting;
    }
}
