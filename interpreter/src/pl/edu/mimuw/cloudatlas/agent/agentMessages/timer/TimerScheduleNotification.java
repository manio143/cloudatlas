package pl.edu.mimuw.cloudatlas.agent.agentMessages.timer;

import pl.edu.mimuw.cloudatlas.agent.utility.Message;
import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;

public class TimerScheduleNotification extends MessageContent {
    public Operation operationToSchedule;
    public long timestamp;
    public long delay;
    public long id;
    public Message.Module module;

    TimerScheduleNotification(long id, Operation operationToSchedule,
                              long delay, long timestamp, Message.Module module) {
        this.id = id;
        this.operationToSchedule = operationToSchedule;
        this.timestamp = timestamp;
        this.delay = delay;
        this.module = module;
    }
}
