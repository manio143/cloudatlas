package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Communication extends Module {
    private final ExecutorService listener = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;

    private static final int UDP_PORT = 31337;
    private static final int UDP_PACKET_SIZE = 508;
    private static final int UDP_METADATA = 5;
    private static final int UDP_PACKET_SPACE = UDP_PACKET_SIZE - UDP_METADATA;
    private static final int MESSAGE_METADATA = 4;

    public Communication(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    private void sendMessage(Message message) {

        byte [] ipBytes = new byte[4];

        System.arraycopy(message.contents, 0, ipBytes, 0, 4);

        try {
            InetAddress address = InetAddress.getByAddress(ipBytes);

            System.out.println(address);

            byte[][] fragments = fragmentate(message.contents);

            for (byte[] fragment : fragments) {
                DatagramPacket packet = new DatagramPacket(
                        fragment, message.contents.length, address, 5757);

                socket.send(packet);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[][] fragmentate(byte[] contents) {
        int fragmentsCount = (contents.length + UDP_PACKET_SPACE - 1 - MESSAGE_METADATA) / UDP_PACKET_SPACE;

        byte[][] fragments = new byte[fragmentsCount][UDP_PACKET_SIZE];

        fragments[0][0] = 0;

        fragments[0][1] = (byte)(fragmentsCount >> 24);
        fragments[0][2] = (byte)(fragmentsCount >> 16);
        fragments[0][3] = (byte)(fragmentsCount >> 8);
        fragments[0][4] = (byte)(fragmentsCount);

        int len = Integer.min(contents.length - MESSAGE_METADATA, UDP_PACKET_SPACE);

        System.arraycopy(contents, MESSAGE_METADATA, fragments[0], UDP_METADATA, len);

        for (int i = 1; i < fragmentsCount; i++) {
            System.out.println("a!");

            fragments[i][0] = 1;
            fragments[i][1] = (byte)(i >> 24);
            fragments[i][2] = (byte)(i >> 16);
            fragments[i][3] = (byte)(i >> 8);
            fragments[i][4] = (byte)(i);

            int srcStartPos = MESSAGE_METADATA + i * UDP_PACKET_SPACE;

            len = Integer.min(contents.length - srcStartPos, UDP_PACKET_SPACE);

            System.arraycopy(contents, srcStartPos, fragments[i], UDP_METADATA, len);
        }

        return fragments;
    }

    @Override
    public void run() {
        listener.execute(new Listener());

        try {
            socket = new DatagramSocket();

            while (true) {
                try {
                    Message message = messages.take();
                    System.out.println("New message to send from module " + message.src);
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

        private Listener () {

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
                        e.printStackTrace();
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
        };
    }
}
