package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TimerModule implements Runnable {
    private final MessageHandler handler;
    private final ConcurrentLinkedQueue<ModuleMessage> messages;
    private final PriorityQueue<Event> events = new PriorityQueue<>();

    public TimerModule(MessageHandler handler, ConcurrentLinkedQueue<ModuleMessage> messages) {
        this.handler = handler;
        this.messages = messages;
    }

    public void addEvent(Long id, Long delay, Long timestamp, Long callback) {

    }

    public void run() {
        while (true) {
            message = messages.take();
        }
    }

    private class Event {
        Long timestamp;
        Long callback;
    }
}
