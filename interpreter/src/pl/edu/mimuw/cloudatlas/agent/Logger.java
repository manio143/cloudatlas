package pl.edu.mimuw.cloudatlas.agent;

import java.sql.Timestamp;

public class Logger {
    private final String source;

    public Logger(Message.Module module) {
        this.source = module.toString();
    }

    public Logger(String source) {
        this.source = source;
    }

    public void log(String toLog) {
        log(toLog, true);
    }

    public void errLog(String toLog) {
        errLog(toLog, true);
    }

    public void log(String toLog, boolean verbose) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (verbose) {
            System.out.println("LOG " + source + " " + timestamp + " : " + toLog);
        }
    }

    public void errLog(String toLog, boolean verbose) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (verbose) {
            System.out.println("ERROR " + source + " " + timestamp + " : " + toLog);
        }
    }
}
