package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class RMIInterrupted extends AgentException {
    public RMIInterrupted() {
        super("RMI thread interrupted!");
    }
}
