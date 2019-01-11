package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.TESTER;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.TIMER;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.TIMER_ADD_EVENT;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.TIMER_REMOVE_EVENT;

public class Tester extends Module {
    public Tester(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void run() {
        int [] a = {4000, 2000, 3000, 6000};

        try {
            for (int i = 0; i < a.length; i++) {

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeLong(i);
                objectStream.writeLong(a[i]);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp);
                objectStream.writeLong(timestamp.getTime());
                objectStream.writeObject(new Test());

                Message message = new Message(
                        TESTER,
                        TIMER,
                        TIMER_ADD_EVENT,
                        byteStream.toByteArray());

                handler.addMessage(message);
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeLong(2);
            objectStream.writeObject(new Test());

            Message message = new Message(
                    TESTER,
                    TIMER,
                    TIMER_REMOVE_EVENT,
                    byteStream.toByteArray());

            handler.addMessage(message);

            while (true) {
                try {
                    Message received = messages.take();
                    long id = new BigInteger(received.contents).longValue();
                    String type = "";
                    switch (message.operation) {
                        case TIMER_ADD_EVENT_ACK:
                            type = "Added event ";
                            break;
                        case TIMER_REMOVE_EVENT_ACK:
                            type = "Removed event ";
                            break;
                        default:
                            type = "Unknown event ";
                            break;
                    }
                    System.out.println("Received: " + message.operation + id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Test implements Runnable, Serializable {
        @Override
        public void run() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Test " + timestamp);
        }
    }
}
