package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.agent.agentMessages.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueKeeper {
    public final LinkedBlockingQueue<Message> timerQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> communicationQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> rmiQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> zmiKeeperQueue = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<Message> testerQueue = new LinkedBlockingQueue<>();
}
