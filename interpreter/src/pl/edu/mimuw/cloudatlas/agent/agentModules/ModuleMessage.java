package pl.edu.mimuw.cloudatlas.agent.agentModules;

public class ModuleMessage {
    public enum Module {
        TIMER, COMMUNICATION, RMI
    }

    public final Module src;
    public final Module dest;

    public ModuleMessage(Module src, Module dest) {
        this.src = src;
        this.dest = dest;
    }
}
