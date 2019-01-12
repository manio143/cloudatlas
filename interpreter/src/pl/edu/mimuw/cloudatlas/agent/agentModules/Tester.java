package pl.edu.mimuw.cloudatlas.agent.agentModules;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.COMMUNICATION;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.TESTER;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.TIMER;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.COMMUNICATION_SEND;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.TIMER_ADD_EVENT;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.TIMER_REMOVE_EVENT;

public class Tester extends Module {
    public Tester(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void run() {
        int [] a = {4000, 2000, 3000, 6000};

        int messagesCount = 5;

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

            {
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
            }

            {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream dataStream = new DataOutputStream(byteStream);
                try {
                    InetAddress ip = InetAddress.getByName("127.0.0.1");
                    byte[] bytes = ip.getAddress();
                    dataStream.write(bytes);

                    for (int i = 0; i < 16; i++) {
                        dataStream.writeByte(48);
                    }

                    Message message = new Message(
                            TESTER,
                            COMMUNICATION,
                            COMMUNICATION_SEND,
                            byteStream.toByteArray());

                    handler.addMessage(message);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }


            for (int i = 0; i < messagesCount; i++) {
                try {
                    Message received = messages.take();
                    String type = "";
                    long id = 0;
                    switch (received.operation) {
                        case TIMER_ADD_EVENT_ACK:
                            id = new BigInteger(received.contents).longValue();
                            type = "Added event " + id;
                            break;
                        case TIMER_REMOVE_EVENT_ACK:
                            id = new BigInteger(received.contents).longValue();
                            type = "Removed event " + id;
                            break;
                        default:
                            type = "Unknown";
                            break;
                    }
                    System.out.println("Received: " + type);
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
