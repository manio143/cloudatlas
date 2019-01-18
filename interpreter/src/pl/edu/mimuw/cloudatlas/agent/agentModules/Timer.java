package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.Logger;
import pl.edu.mimuw.cloudatlas.agent.Message;
import pl.edu.mimuw.cloudatlas.agent.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.MessageHandler;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.sleep;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.GOSSIP;
import static pl.edu.mimuw.cloudatlas.agent.Message.Module.TIMER;

public class Timer extends Module {
    private final QueueController controller = new QueueController();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Timer(MessageHandler handler, LinkedBlockingQueue<Message> messages) {
        super(handler, messages);

        this.logger = new Logger(TIMER);
    }

    private void addEvent(Message message) {
        try {
            TimerAddEvent content = (TimerAddEvent) message.content;

            long wakeUp = content.timestamp + content.delay;

            Event toAdd = new Event(content.id, wakeUp, content.toRun);

            controller.addEvent(toAdd);

            TimerAddEventAck ackContent = new TimerAddEventAck(content.id);

            Message ack = new Message(TIMER, message.src, ackContent);
            handler.addMessage(ack);

        } catch (ClassCastException e) {
            logger.errLog("Invalid cast to TimerAddEvent!");
        }
    }


    private void removeEvent(Message message) {
        try {
            TimerRemoveEvent content = (TimerRemoveEvent) message.content;

            controller.removeEvent(content.id);

            TimerRemoveEventAck ackContent = new TimerRemoveEventAck(content.id);

            Message ack = new Message(TIMER, message.src, ackContent);

            handler.addMessage(ack);

        } catch (ClassCastException e) {
            logger.errLog("Invalid cast to TimerAddEvent!");
        }
    }

    @Override
    public void run() {
        executor.execute(new Sleeper(controller));

        try {
            while(true) {
                Message message = messages.take();
                switch (message.content.operation) {
                    case TIMER_ADD_EVENT:
                        addEvent(message);
                        break;
                    case TIMER_REMOVE_EVENT:
                        removeEvent(message);
                        break;
                    default:
                        logger.errLog("Incorrect message type: " + message.content.operation);
                }
            }
        } catch (InterruptedException e) {
            logger.errLog("Interrupted exception");
        }
    }

    private class Event implements Comparable<Event> {
        private long id;
        private long wakeUp;
        private Runnable toRun;

        private Event (long id, long wakeUp, Runnable toRun) {
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

        private synchronized void addEvent(Event event) {
            events.add(event);

            notify();
        }

        private synchronized void issueWait(long time) {
            try {
                wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private Event takeEvent() {
            Event event = null;
            try {
                event = events.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return event;
        }


        private void removeEvent(long id) {
            toRemove.add(id);
        }

        private boolean isIdRemoved(long id) {
            return toRemove.remove(id);
        }

        private synchronized void checkNearest(Event event, long difference) {
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

        private Sleeper(QueueController controller) {
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

    public static void scheduleNotification(NotificationInfo info) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();

        Announcer announcer = new Announcer(info, time);

        TimerAddEvent timerAddEvent = new TimerAddEvent(info.id, info.delay, time, announcer);

        info.handler.addMessage(new Message(info.module, TIMER, timerAddEvent));
    }

    public static class NotificationInfo {
        public final MessageHandler handler;
        public final MessageContent content;
        public final Message.Module module;
        public final long id;
        public final long delay;

        public NotificationInfo(MessageHandler handler, Logger logger, MessageContent content,
                                Message.Module module, long id, long delay) {
            this.handler = handler;
            this.content = content;
            this.module = module;
            this.id = id;
            this.delay = delay;
        }
    }

    public static class Announcer implements Runnable, Serializable {
        private final NotificationInfo info;
        private final long timestamp;

        public Announcer(NotificationInfo info, long timestamp) {
            this.info = info;
            this.timestamp = timestamp;
        }

        public void run() {
            // TODO: send a copy of info.content
            info.handler.addMessage(new Message(TIMER, info.module, info.content));

            long newTimestamp = timestamp + info.delay;

            Announcer announcer = new Announcer(info, newTimestamp);

            TimerAddEvent timerAddEvent = new TimerAddEvent(info.id, info.delay, newTimestamp, announcer);

            info.handler.addMessage(new Message(info.module, TIMER, timerAddEvent));
        }
    }
}
