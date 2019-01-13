package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class ZMIKeeper extends Module {
    public ZMIKeeper(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void run() {
        try {
            while (true) {
                Message message = messages.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
