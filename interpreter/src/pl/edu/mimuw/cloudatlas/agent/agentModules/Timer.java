package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.Message;
import pl.edu.mimuw.cloudatlas.agent.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.Message.Module.TIMER;

public class Timer extends Module {
    private final QueueController controller = new QueueController();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Timer(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);
    }

    public void addEvent(Message message) {
        try {
            TimerAddEvent content = (TimerAddEvent) message.content;

            long wakeUp = content.timestamp + content.delay;

            Event toAdd = new Event(content.id, wakeUp, content.toRun);

            controller.addEvent(toAdd);

            TimerAddEventAck ackContent = new TimerAddEventAck(content.id);

            Message ack = new Message(TIMER, message.src, ackContent);
            handler.addMessage(ack);

        } catch (ClassCastException e) {
            System.out.println("Invalid cast to TimerAddEvent!");
            e.printStackTrace();
        }
    }


    public void removeEvent(Message message) {
        try {
            TimerRemoveEvent content = (TimerRemoveEvent) message.content;

            controller.removeEvent(content.id);

            TimerRemoveEventAck ackContent = new TimerRemoveEventAck(content.id);

            Message ack = new Message(TIMER, message.src, ackContent);

            handler.addMessage(ack);

        } catch (ClassCastException e) {
            System.out.println("Invalid cast to TimerAddEvent!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        executor.execute(new Sleeper(controller));

        while (true) {
            try {
                Message message = messages.take();
                switch (message.content.operation) {
                    case TIMER_ADD_EVENT:
                        addEvent(message);
                        break;
                    case TIMER_REMOVE_EVENT:
                        removeEvent(message);
                        break;
                    default:
                        System.out.println("Incorrect message type for timer: " + message.content.operation);
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
        private final Set<Long> toRemove = new HashSet<>();

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


        public void removeEvent(long id) {
            toRemove.add(id);
        }

        public boolean isIdRemoved(long id) {
            return toRemove.remove(id);
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
                        if (!controller.isIdRemoved(event.id)) {
                            executor.execute(event.toRun);
                        }
                        break;
                    }
                    controller.checkNearest(event, difference);
                }
            }
        }
    }
}
