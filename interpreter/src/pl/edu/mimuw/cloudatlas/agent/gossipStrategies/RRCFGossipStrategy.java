package pl.edu.mimuw.cloudatlas.agent.gossipStrategies;

import java.util.*;
import java.time.Instant;

import pl.edu.mimuw.cloudatlas.model.PathName;

// Round Robin Constant Frequency
public class RRCFGossipStrategy extends GossipStrategy {
    private final int currentLevel;
    private final int frequency; // milliseconds

    private class LevelWithTimestamp {
        public final int level;
        public final long timestamp;

        public LevelWithTimestamp(int level, long timestamp) {
            this.level = level;
            this.timestamp = timestamp;
        }
    }

    private Deque<LevelWithTimestamp> queue = new ArrayDeque<>();

    public RRCFGossipStrategy(String nodePath, int frequency) {
        currentLevel = new PathName(nodePath).getComponents().size() - 1;
        this.frequency = frequency;
        long now = Instant.now().toEpochMilli();
        for (int i = 1; i < currentLevel; i++)
            queue.addFirst(new LevelWithTimestamp(i, now));
    }

    public int nextLevel() throws InterruptedException {
        LevelWithTimestamp first = queue.removeFirst();
        long smallest = first.timestamp;
        long now = Instant.now().toEpochMilli();
        while (smallest + frequency > now) { // not yet
            Thread.sleep(smallest + frequency - now);
            now = Instant.now().toEpochMilli();
        }
        int level = first.level;
        LevelWithTimestamp last = new LevelWithTimestamp(first.level, now);
        queue.addLast(last);
        return level;
    }

}