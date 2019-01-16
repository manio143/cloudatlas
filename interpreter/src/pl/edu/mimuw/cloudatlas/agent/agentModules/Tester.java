package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.Logger;
import pl.edu.mimuw.cloudatlas.agent.Message;
import pl.edu.mimuw.cloudatlas.agent.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.*;

public class Tester extends Module {
    public Tester(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
        this.logger = new Logger(TESTER);
    }

    public void run() {
        test();
    }

    public void test() {
        int [] delays = {4000, 2000, 3000, 6000};

        int messagesCount = 6;

        for (int i = 0; i < delays.length; i++) {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.log("Message timestamp: " + timestamp);
            TimerAddEvent content = new TimerAddEvent(i, delays[i], timestamp.getTime(), new Test());

            Message message = new Message(TESTER, TIMER, content);

            handler.addMessage(message);
        }

        {
            TimerRemoveEvent content = new TimerRemoveEvent(2);

            Message message = new Message(TESTER, TIMER, content);

            handler.addMessage(message);
        }

        {
            TimerRemoveEvent content = new TimerRemoveEvent(3);

            Message message = new Message(TESTER, TIMER, content);

            try {

                InetAddress ip = InetAddress.getByName("127.0.0.1");

                CommunicationSend sendContent = new CommunicationSend(ip, message);

                Message toSend = new Message(TESTER, COMMUNICATION, sendContent);

                handler.addMessage(toSend);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < messagesCount; i++) {
            try {
                Message message = messages.take();
                String type = "";
                long id = 0;
                switch (message.content.operation) {
                    case TIMER_ADD_EVENT_ACK:
                        try {
                            type = "Added event " + ((TimerAddEventAck) message.content).id;
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                    case TIMER_REMOVE_EVENT_ACK:
                        try {
                            type = "Remove event " + ((TimerRemoveEventAck) message.content).id;
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        type = "Unknown";
                        break;
                }
                logger.log("Received: " + type);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
