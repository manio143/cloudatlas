package pl.edu.mimuw.cloudatlas.agent.agentModules;

import pl.edu.mimuw.cloudatlas.agent.utility.*;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.*;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimerAddEvent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimerAddEventAck;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimerRemoveEvent;
import pl.edu.mimuw.cloudatlas.agent.agentMessages.timer.TimerRemoveEventAck;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static pl.edu.mimuw.cloudatlas.agent.utility.ModuleName.TIMER;

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

            Event toAdd = new Event(message.src, content.id, wakeUp, content.toRun);

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

            controller.removeEvent(message.src, content.id);

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
        private final ModuleName src;
        private final long id;
        private long wakeUp;
        private Runnable toRun;

        private Event (ModuleName src, long id, long wakeUp, Runnable toRun) {
            this.src = src;
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
        private final Set<PairModuleId> toRemove = new TreeSet<>();

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


        private void removeEvent(ModuleName src, long id) {
            toRemove.add(new PairModuleId(src, id));
        }

        private boolean isRemoved(Event event) {
            return toRemove.remove(new PairModuleId(event.src, event.id));
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

    private class PairModuleId implements Comparable<PairModuleId> {
        public ModuleName moduleName;
        public long id;

        private PairModuleId(ModuleName moduleName, long id) {
            this.moduleName = moduleName;
            this.id = id;
        }

        @Override
        public int compareTo(PairModuleId other) {
            int moduleCompare = moduleName.toString().compareTo(other.moduleName.toString());
            if (moduleCompare != 0) {
                return moduleCompare;
            }
            return Long.compare(id, other.id);
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
                        if (!controller.isRemoved(event)) {
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

        info.handler.addMessage(new Message(info.moduleName, TIMER, timerAddEvent));
    }

    public static class NotificationInfo {
        private final MessageHandler handler;
        private final Logger logger;
        private final MessageContent content;
        private final ModuleName moduleName;
        private final long id;
        private final long delay;

        public NotificationInfo(MessageHandler handler, Logger logger, MessageContent content,
                                ModuleName moduleName, long id, long delay) {
            this.handler = handler;
            this.logger = logger;
            this.content = content;
            this.moduleName = moduleName;
            this.id = id;
            this.delay = delay;
        }
    }

    private static class Announcer implements Runnable, Serializable {
        private final NotificationInfo info;
        private final long timestamp;

        private Announcer(NotificationInfo info, long timestamp) {
            this.info = info;
            this.timestamp = timestamp;
        }

        public void run() {
            try {
                MessageContent contentCopy = info.content.copy();

                info.handler.addMessage(new Message(TIMER, info.moduleName, contentCopy));

                long newTimestamp = timestamp + info.delay;

                Announcer announcer = new Announcer(info, newTimestamp);

                TimerAddEvent timerAddEvent = new TimerAddEvent(info.id, info.delay, newTimestamp, announcer);

                info.handler.addMessage(new Message(info.moduleName, TIMER, timerAddEvent));
            } catch (CopyNotImplementedException e) {
                info.logger.errLog(e.toString());
            }
        }
    }
}
