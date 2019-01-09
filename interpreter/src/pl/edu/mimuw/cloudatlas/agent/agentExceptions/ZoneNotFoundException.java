package pl.edu.mimuw.cloudatlas.agent.agentExceptions;

public class ZoneNotFoundException extends AgentException {
    protected ZoneNotFoundException(String pathName) {
        super("Zone " + pathName + " not found");
    }
}

