package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TimerModule implements Runnable {
    private final MessageHandler handler;
    private final LinkedBlockingQueue<ModuleMessage> messages;
    private final QueueController controller = new QueueController();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TimerModule(MessageHandler handler, LinkedBlockingQueue<ModuleMessage> messages) {
        this.handler = handler;
        this.messages = messages;
    }

    public void addEvent(ModuleMessage message) {
        final int MIN_LEN = 3 * 4;
        int len = message.contents.length;
        if (len < MIN_LEN) {
            System.out.println("addEvent message too short!");
            return;
        }

        ByteArrayInputStream byteStream = new ByteArrayInputStream(message.contents);

        try {
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);

            long id = objectStream.readLong();
            long delay = objectStream.readLong();
            long timestamp = objectStream.readLong();

            Runnable toRun = (Runnable)objectStream.readObject();

            long wakeUp = timestamp + delay;

            Event toAdd = new Event(id, wakeUp, toRun);

            controller.addEvent(toAdd);

        } catch (IOException e) {
            System.out.println("IOException while reading message!");
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        executor.execute(new Sleeper(controller));

        while (true) {
            try {
                ModuleMessage message = messages.take();
                switch (message.operation) {
                    case TIMER_SCHEDULE:
                        addEvent(message);
                        break;
                    default:
                        System.out.println("Incorrect message type for timer: " + message.operation);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class Event implements Comparable<Event> {
        public long id;
        public long wakeUp;
        public Runnable toRun;

        public Event (long id, long wakeUp, Runnable toRun) {
            this.id = id;
            this.wakeUp = wakeUp;
            this.toRun = toRun;
        }

        @Override
        public int compareTo(Event other) {
            return Long.compare(this.wakeUp, other.wakeUp);
        }
    }

    private class QueueController {
        private final PriorityBlockingQueue<Event> events = new PriorityBlockingQueue<>();

        public synchronized void addEvent(Event event) {
            events.add(event);

            notify();
        }

        public synchronized void issueWait(long time) {
            try {
                wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public Event takeEvent() {
            Event event = null;
            try {
                event = events.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return event;
        }

        public synchronized void checkNearest(Event event, long difference) {
            Event nearest = events.peek();
            if (nearest == null) {
                return;
            }
            if (nearest.wakeUp < event.wakeUp) {
                Long nWakeUp = nearest.wakeUp;
                Runnable nToRun = nearest.toRun;
                nearest.wakeUp = event.wakeUp;
                nearest.toRun = event.toRun;
                event.wakeUp = nWakeUp;
                event.toRun = nToRun;
            } else {
                issueWait(difference);
            }
        }
    }

    private class Sleeper implements Runnable {
        private final QueueController controller;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private Event event;

        public Sleeper(QueueController controller) {
            this.controller = controller;
        }

        @Override
        public void run() {
            while (true) {
                event = controller.takeEvent();

                while (true) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    long difference = event.wakeUp - timestamp.getTime();
                    if (difference <= 0) {
                        executor.execute(event.toRun);
                        break;
                    }
                    controller.checkNearest(event, difference);
                }
            }
        }
    }
}
