package pl.edu.mimuw.cloudatlas.agent.agentMessages.rmi;

import pl.edu.mimuw.cloudatlas.agent.utility.MessageContent;
import pl.edu.mimuw.cloudatlas.agent.agentExceptions.AgentException;

import static pl.edu.mimuw.cloudatlas.agent.utility.Operation.RMI_ERROR;

public class RMIError extends MessageContent {
    public AgentException error;

    public RMIError(AgentException error) {
        this.error = error;

        operation = RMI_ERROR;
    }
}
