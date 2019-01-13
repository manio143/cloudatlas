package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class ZoneNotFoundException extends AgentException {
    public ZoneNotFoundException(String pathName) {
        super("Zone " + pathName + " not found");
    }
}

