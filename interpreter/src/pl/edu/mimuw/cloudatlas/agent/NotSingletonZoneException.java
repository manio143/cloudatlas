package pl.edu.mimuw.cloudatlas.agent;

public class NotSingletonZoneException extends AgentException {
	protected NotSingletonZoneException(String pathName) {
        super("Zone " + pathName + " is not a singleton zone");
    }
}
