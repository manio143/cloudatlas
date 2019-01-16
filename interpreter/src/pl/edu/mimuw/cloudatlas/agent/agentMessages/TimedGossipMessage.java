package pl.edu.mimuw.cloudatlas.agent.agentMessages;

import pl.edu.mimuw.cloudatlas.agent.MessageContent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimedGossipMessage extends MessageContent {
    @Override
    public boolean isTimed() {
        return true;
    }

    public final List<Long> timestamps = new ArrayList<>();

    public void addTimestamp(long t) {timestamps.add(t);}
    public void addTimestamps(Collection<Long> ts) {timestamps.addAll(ts);}
}
