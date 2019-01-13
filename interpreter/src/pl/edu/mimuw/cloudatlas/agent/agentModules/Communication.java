package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.CommunicationSend;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Communication extends Module {
    private final ExecutorService listener = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;
    private Long sendCount = 0L;

    private final Map<Key, MapValue> received = new TreeMap<>();

    private static final int UDP_PORT = 31337;
    private static final int UDP_PACKET_SIZE = 508;
    private static final int LONG_LENGTH = 8;
    private static final int INT_LENGTH = 4;
    private static final int BYTE_LENGTH = 1;
    private static final int UDP_METADATA = BYTE_LENGTH + INT_LENGTH + LONG_LENGTH;
    private static final int UDP_PACKET_SPACE = UDP_PACKET_SIZE - UDP_METADATA;
    private static final int TIMEOUT = 5 * 1000;

    public Communication(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    private void sendMessage(Message message) {

        try {
            CommunicationSend content = (CommunicationSend) message.content;

            InetAddress ip = content.ip;

            List<byte[]> fragments = fragmentate(content.message);

            for (byte[] fragment : fragments) {
                DatagramPacket packet = new DatagramPacket(
                        fragment, fragment.length, ip, UDP_PORT);

                socket.send(packet);
            }

            sendCount++;

        } catch (ClassCastException e) {
            System.out.println("Cast exception in Communication!");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO - check socket
            e.printStackTrace();
        }
    }

    private List<byte[]> fragmentate(Message message) {
        List<byte[]> fragments = new LinkedList<>();

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(message);

            byte[] contentBytes = byteStream.toByteArray();

            int contentLen = contentBytes.length;

            int fragmentsCount = (contentLen + (UDP_PACKET_SPACE - 1)) / UDP_PACKET_SPACE;

            int len = Integer.min(contentLen + UDP_METADATA, UDP_PACKET_SPACE);

            byte [] fragment = new byte[len + UDP_METADATA];

            byteStream = new ByteArrayOutputStream();

            DataOutputStream dataStream = new DataOutputStream(byteStream);

            dataStream.writeByte(0);
            dataStream.writeInt(fragmentsCount);
            dataStream.writeLong(sendCount);

            System.arraycopy(byteStream.toByteArray(), 0, fragment, 0, UDP_METADATA);
            System.arraycopy(contentBytes, 0, fragment, UDP_METADATA, len);

            fragments.add(fragment);

            for (int j = 1; j < fragmentsCount; j++) {

                len = Integer.min(contentLen - j * UDP_PACKET_SPACE, UDP_PACKET_SPACE);

                fragment = new byte[len + UDP_METADATA];

                byteStream = new ByteArrayOutputStream();

                dataStream = new DataOutputStream(byteStream);

                dataStream.writeByte(1);
                dataStream.writeInt(j);
                dataStream.writeLong(sendCount);

                System.arraycopy(byteStream.toByteArray(), 0, fragment, 0, UDP_METADATA);
                System.arraycopy(contentBytes, j * UDP_PACKET_SPACE, fragment, UDP_METADATA, len);

                fragments.add(fragment);
            }

        } catch (IOException e) {
            System.out.println("Fragments IOException!");
        }

        return fragments;
    }

    @Override
    public void run() {
        listener.execute(new Listener(handler));

        try {
            socket = new DatagramSocket();

            while (true) {
                try {
                    Message message = messages.take();
                    System.out.println("New message to send from module " + message.src);
                    switch (message.content.operation) {
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
        private final ExecutorService collector = Executors.newSingleThreadExecutor();
        private MessageHandler handler;
        private DatagramSocket listeningSocket;

        private Listener (MessageHandler handler) {
            this.handler = handler;
        }

        public void run() {
            try {
                listeningSocket = new DatagramSocket(UDP_PORT);

                while (true) {
                    try {
                        byte[] receiveData = new byte[UDP_PACKET_SIZE];

                        System.out.println("Received a message!");

                        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                                receiveData.length);

                        listeningSocket.receive(receivePacket);

                        collector.execute(new Collector(handler, receiveData, receivePacket.getAddress()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
        };
    }

    private class Collector implements Runnable {
        private final MessageHandler handler;
        private byte[] data;
        private final InetAddress ip;

        private Collector(MessageHandler handler, byte[] data, InetAddress ip) {
            this.handler = handler;
            this.data = data;
            this.ip = ip;
        }

        public void run() {
            try {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

                DataInputStream dataStream = new DataInputStream(byteStream);

                byte marker;
                int pieces;
                long id;

                marker = dataStream.readByte();
                pieces = dataStream.readInt();
                id = dataStream.readLong();

                System.out.println("Processing a message from " + ip.getHostAddress()
                        + ", id: " + id + ", part: " + (marker == 0 ? 0 : pieces));

                Key key = new Key(ip, id);

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                MapValue value = new MapValue(timestamp.getTime());

                if (received.containsKey(key)) {
                    value = received.get(key);
                }

                // TODO: schedule regular cleaning

                if (timestamp.getTime() - value.timestamp > TIMEOUT) {
                    return;
                }

                byte [] content = new byte[UDP_PACKET_SPACE];

                System.arraycopy(data, UDP_METADATA, content, 0, UDP_PACKET_SPACE);

                switch (marker) {
                    case 0:
                        value.pieces = pieces;
                        value.fragments.put(0, content);
                        break;
                    case 1:
                        value.fragments.put(pieces, content);
                        break;
                    default:
                        System.out.println("Unknown marker!");
                }

                if (value.pieces == value.fragments.size()) {

                    int lastLen = value.fragments.get(value.pieces - 1).length;
                    int totalLen = UDP_PACKET_SPACE * (value.pieces - 1) + lastLen;
                    byte[] messageBytes = new byte[totalLen];

                    for (int i = 0; i < value.pieces; i++) {
                        System.arraycopy(value.fragments.get(i), 0, messageBytes,
                                UDP_PACKET_SPACE * i, value.fragments.get(i).length);
                    }

                    received.remove(key);

                    byteStream = new ByteArrayInputStream(messageBytes);

                    ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                    try {
                        Message message = (Message) objectStream.readObject();

                        handler.addMessage(message);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    received.put(key, value);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Key implements Comparable<Key> {
        public InetAddress ip;
        public long id;

        private Key(InetAddress ip, long id) {
            this.ip = ip;
            this.id = id;
        }

        @Override
        public int compareTo(Key other) {
            int ipCompare = ip.toString().compareTo(other.ip.toString());
            if (ipCompare != 0) {
                return ipCompare;
            }
            return Long.compare(id, other.id);
        }
    }

    private class MapValue {
        long timestamp;
        Map<Integer, byte[]> fragments = new TreeMap<>();
        int pieces = -1;

        private MapValue(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}