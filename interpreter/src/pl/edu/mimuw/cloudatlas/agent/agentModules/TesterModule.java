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
        int [] a = {4000, 2000, 3000, 6000};

        try {
            for (int i = 0; i < a.length; i++) {

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeLong(i);
                objectStream.writeLong(a[i]);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp);
                objectStream.writeLong(timestamp.getTime());
                objectStream.writeObject(new Test());

                ModuleMessage message = new ModuleMessage(
                        ModuleMessage.Module.TESTER,
                        ModuleMessage.Module.TIMER,
                        ModuleMessage.Operation.TIMER_ADD_EVENT,
                        byteStream.toByteArray());

                handler.addMessage(message);
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeLong(2);
            objectStream.writeObject(new Test());

            ModuleMessage message = new ModuleMessage(
                    ModuleMessage.Module.TESTER,
                    ModuleMessage.Module.TIMER,
                    ModuleMessage.Operation.TIMER_REMOVE_EVENT,
                    byteStream.toByteArray());

            handler.addMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
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
