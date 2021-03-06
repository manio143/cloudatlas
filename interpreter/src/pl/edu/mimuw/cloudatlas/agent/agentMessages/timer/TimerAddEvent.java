package pl.edu.mimuw.cloudatlas.agent.agentMessages.timer;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.TIMER_ADD_EVENT;

public class TimerAddEvent extends MessageContent {
    public long id;
    public long delay;
    public long timestamp;

    public Runnable toRun;

    public TimerAddEvent(long id, long delay, long timestamp, Runnable toRun) {
        this.id = id;
        this.delay = delay;
        this.timestamp = timestamp;
        this.toRun = toRun;

        operation = TIMER_ADD_EVENT;
    }
}
