package pl.edu.mimuw.cloudatlas.agent.agentModules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;

public class TesterModule implements Runnable {
    private final MessageHandler handler;

    public TesterModule(MessageHandler handler) {
        this.handler = handler;
    }

    public void run() {
        int [] a = {4000, 2000};

        for (int i = 0; i < a.length; i++) {

            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeLong(0);
                objectStream.writeLong(a[i]);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp);
                objectStream.writeLong(timestamp.getTime());
                objectStream.writeObject(new Test());

                ModuleMessage message = new ModuleMessage(
                        ModuleMessage.Module.TIMER,
                        ModuleMessage.Module.TIMER,
                        ModuleMessage.Operation.TIMER_SCHEDULE,
                        byteStream.toByteArray());

                handler.addMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Test implements Runnable, Serializable {
        @Override
        public void run() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Test " + timestamp);
        }
    }
}
