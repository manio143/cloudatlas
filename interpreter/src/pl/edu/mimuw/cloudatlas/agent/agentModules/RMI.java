package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.*;
import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.agent.utility.Message;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageHandler;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.CloudAtlasAPI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static pl.edu.mimuw.cloudatlas.agent.utility.Message.Module.ZMI_KEEPER;
import static pl.edu.mimuw.cloudatlas.agent.utility.Message.Module.RMI;

public class RMI extends Module {
    private SynchronousQueue<MessageContent> rmi = new SynchronousQueue<>();
    private RMIController controller = new RMIController();

    public RMI(MessageHandler handler, LinkedBlockingQueue<Message> messages)
    {
        super(handler, messages);
        this.logger = new Logger(RMI);
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

                if (controller.waiting && message.src == ZMI_KEEPER) {
                    rmi.put(message.content);
                } else if (message.src != ZMI_KEEPER) {
                    logger.errLog("Message not from ZMI Keeper, but from: " + message.src);
                } else {
                    logger.errLog("No RMI function invoked, RMI was not awaiting a message!");
                }
            }
        } catch (InterruptedException e) {
            logger.errLog("Interrupted exception!");
        } catch (RemoteException e) {
            logger.errLog("RemoteException exception!");
            e.printStackTrace();
        }

    }

    public class RMIController {
        public boolean waiting;
    }
}
