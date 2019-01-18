package pl.edu.mimuw.cloudatlas.agent.utility;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueKeeper {
    public final LinkedBlockingQueue<Message> timerQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> communicationQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> rmiQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> zmiKeeperQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> testerQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> gossipQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> gossipStrategyQueue = new LinkedBlockingQueue<>();
}
