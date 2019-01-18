package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.agent.utility.Message;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.communication.CommunicationFlushOld;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.communication.CommunicationSend;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimedGossipMessage;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;
import static pl.edu.mimuw.cloudatlas.agent.utility.Message.Module.COMMUNICATION;

public class Communication extends Module {
    private final ExecutorService listener = Executors.newSingleThreadExecutor();
    private final SynchronizationController controller = new SynchronizationController();
    private final Map<Key, MapValue> received = new TreeMap<>();
    private DatagramSocket sendingSocket;
    private Long sendCount = 0L;

    private final long messageTimeout;
    private final long socketReviveDelay;

    private static final int UDP_PORT = 31337;

    private static final int UDP_PACKET_SIZE = 508;
    private static final int LONG_LENGTH = 8;
    private static final int INT_LENGTH = 4;
    private static final int BYTE_LENGTH = 1;
    private static final int UDP_METADATA = BYTE_LENGTH + INT_LENGTH + LONG_LENGTH;
    private static final int UDP_PACKET_SPACE = UDP_PACKET_SIZE - UDP_METADATA;


    public Communication(MessageHandler handler, LinkedBlockingQueue<Message> messages,
                         long messageTimeout, long socketReviveDelay) {
        super(handler, messages);
        this.messageTimeout = messageTimeout;
        this.socketReviveDelay = socketReviveDelay;

        this.logger = new Logger(COMMUNICATION);
    }

    private void sendMessage(Message message) {

        try {
            CommunicationSend content = (CommunicationSend) message.content;

            InetAddress ip = content.ip;

            logger.log("Sending message " + content.message.content.operation);

            if(content.message.content.isTimed()) {
                logger.log("Recorded timestamp before send");
                TimedGossipMessage tgm = (TimedGossipMessage) content.message.content;
                tgm.addTimestamp(Instant.now().toEpochMilli());

            }

            List<byte[]> fragments = fragmentate(content.message);

            for (byte[] fragment : fragments) {
                DatagramPacket packet = new DatagramPacket(
                        fragment, fragment.length, ip, UDP_PORT);

                sendingSocket.send(packet);
            }

            sendCount++;

        } catch (ClassCastException e) {
            logger.errLog("Cast exception!");
            e.printStackTrace();
        } catch (SocketException e) {
            logger.errLog("Sending socket went down!");
            startSendingSocket();
        } catch (IOException e) {
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
            logger.errLog("Fragments IOException!");
            e.printStackTrace();
        }

        return fragments;
    }

    private void flushOld() {
        logger.log("Flushing old messages");

        List<Key> toFlush = new LinkedList<>();

        controller.take();

        for (Map.Entry<Key, MapValue> entry : received.entrySet()) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (timestamp.getTime() - entry.getValue().timestamp > messageTimeout) {
                toFlush.add(entry.getKey());
            }
        }

        for (Key key : toFlush) {
            received.remove(key);
        }

        controller.release();
    }

    public void startSendingSocket() {
        while (true) {
            try {
                sendingSocket = new DatagramSocket();
                logger.log("Open the sending socket");
                break;
            } catch (SocketException e) {
                logger.errLog("Sending socket not opened!");
                e.printStackTrace();
                try {
                    sleep(socketReviveDelay);
                    logger.log("Woke up to open the sending socket");
                }  catch (InterruptedException ie) {

                }
            }
        }
    }

    @Override
    public void run() {
        startSendingSocket();

        Timer.NotificationInfo info =
                new Timer.NotificationInfo(handler, logger, new CommunicationFlushOld(), COMMUNICATION, 0, messageTimeout);

        Timer.scheduleNotification(info);

        listener.execute(new Listener(handler, controller));

        while (true) {
            try {
                Message message = messages.take();
                logger.log("New message to send from module " + message.src);
                switch (message.content.operation) {
                    case COMMUNICATION_SEND:
                        sendMessage(message);
                        break;
                    case COMMUNICATION_FLUSH_OLD:
                        flushOld();
                        break;
                    default:
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class Listener implements Runnable {
        private final ExecutorService collector = Executors.newSingleThreadExecutor();
        private final MessageHandler handler;
        private final SynchronizationController controller;
        private DatagramSocket listeningSocket;

        private Listener (MessageHandler handler, SynchronizationController controller) {
            this.handler = handler;
            this.controller = controller;
        }

        private void startListeningSocket() {
            while (true) {
                try {
                    listeningSocket = new DatagramSocket(UDP_PORT);
                    logger.log("Open the listening socket");
                    break;
                } catch (SocketException e) {
                    logger.errLog("Listening socket not opened!");
                    e.printStackTrace();
                    try {
                        sleep(socketReviveDelay);
                        logger.log("Woke up to open the listening socket");
                    }  catch (InterruptedException ie) {

                    }
                }
            }
        }

        public void run() {
            startListeningSocket();

            while (true) {
                try {
                    byte[] receiveData = new byte[UDP_PACKET_SIZE];

                    DatagramPacket receivePacket = new DatagramPacket(receiveData,
                            receiveData.length);

                    listeningSocket.receive(receivePacket);

                    logger.log("Listener received a message!");

                    collector.execute(new Collector(handler, controller, receiveData, receivePacket.getAddress()));

                } catch (SocketException e) {
                    logger.log("Listening socket went down!");
                    startListeningSocket();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private class Collector implements Runnable {
        private final MessageHandler handler;
        private final SynchronizationController controller;
        private byte[] data;
        private final InetAddress ip;

        private Collector(MessageHandler handler, SynchronizationController controller, byte[] data, InetAddress ip) {
            this.handler = handler;
            this.controller = controller;
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

                logger.log("Processing a message from " + ip.getHostAddress()
                        + ", id: " + id + ", part: " + (marker == 0 ? 0 : pieces));

                Key key = new Key(ip, id);

                byte [] content = new byte[UDP_PACKET_SPACE];
                System.arraycopy(data, UDP_METADATA, content, 0, UDP_PACKET_SPACE);

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                MapValue value = new MapValue(timestamp.getTime());

                controller.take();

                if (received.containsKey(key)) {
                    value = received.get(key);
                }

                controller.release();

                switch (marker) {
                    case 0:
                        value.pieces = pieces;
                        value.fragments.put(0, content);
                        break;
                    case 1:
                        value.fragments.put(pieces, content);
                        break;
                    default:
                        logger.errLog("Unknown marker!");
                }

                if (value.pieces == value.fragments.size()) {

                    int lastLen = value.fragments.get(value.pieces - 1).length;
                    int totalLen = UDP_PACKET_SPACE * (value.pieces - 1) + lastLen;
                    byte[] messageBytes = new byte[totalLen];

                    for (int i = 0; i < value.pieces; i++) {
                        System.arraycopy(value.fragments.get(i), 0, messageBytes,
                                UDP_PACKET_SPACE * i, value.fragments.get(i).length);
                    }

                    controller.take();

                    received.remove(key);

                    controller.release();

                    byteStream = new ByteArrayInputStream(messageBytes);

                    ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                    try {
                        Message message = (Message) objectStream.readObject();

                        if(message.content.isTimed()) {
                            TimedGossipMessage tgm = (TimedGossipMessage) message.content;
                            tgm.addTimestamp(Instant.now().toEpochMilli());
                            logger.log("Recorded timestamp after read");
                        }

                        handler.addMessage(message);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    received.put(key, value);
                }

            } catch (IOException e) {
                logger.errLog("Failed to read message");
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

    private class SynchronizationController {
        boolean taken = false;

        public synchronized void take() {
            try {
                while (taken) {
                    wait();
                }
                taken = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void release() {
            taken = false;
            notify();
        }
    }
}
