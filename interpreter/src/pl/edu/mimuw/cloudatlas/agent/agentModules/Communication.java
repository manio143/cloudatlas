package pl.edu.mimuw.cloudatlas.agent.agentModules;

import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Module.TIMER;
import static pl.edu.mimuw.cloudatlas.agent.agentModules.Message.Operation.TIMER_ADD_EVENT_ACK;

public class Communication extends Module {
    private final ExecutorService listener = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;

    public static final int UDP_PORT = 31337;
    public static final int UDP_PACKET_SIZE = 508;

    public Communication(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void sendMessage(Message message) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(message.contents);

        try {
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);

            byte [] ipBytes = new byte[4];

            for (int i = 0; i < ipBytes.length; i++) {
                ipBytes[i] = objectStream.readByte();
            }

            InetAddress address = InetAddress.getByAddress(ipBytes);

            DatagramPacket packet = new DatagramPacket(
                    message.contents, message.contents.length, address, UDP_PORT);

            socket.send(packet);


        } catch (IOException e) {
            System.out.println("IOException while reading message!");
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        listener.execute(new Listener());

        try {
            socket = new DatagramSocket();

            while (true) {
                try {
                    Message message = messages.take();
                    switch (message.operation) {
                        case COMMUNICATION_SEND:
                            sendMessage(message);
                            break;
                        default:
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private class Listener implements Runnable {
        private DatagramSocket listeningSocket;

        public Listener () {

        }


        public void run() {
            try {
                listeningSocket = new DatagramSocket(UDP_PORT);

                byte[] receiveData = new byte[UDP_PACKET_SIZE];

                DatagramPacket receivePacket = new DatagramPacket(receiveData,
                        receiveData.length);

                while (true) {
                    try {
                        listeningSocket.receive(receivePacket);

                        ByteArrayInputStream byteStream = new ByteArrayInputStream(receivePacket.getData());

                        try {
                            ObjectInputStream objectStream = new ObjectInputStream(byteStream);


                        } catch (IOException e) {
                            System.out.println("IOException while reading message!");
                            e.printStackTrace();
                            return;
                        }


                    } catch (IOException e) {

                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
        };
    }
}
