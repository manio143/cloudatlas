package pl.edu.mimuw.cloudatlas.fetcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FetcherServer {

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(args[0]));
            Long interval = Long.parseLong(prop.getProperty("fetchingInterval"));
            Fetcher fetcher = new Fetcher(prop);
            ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(fetcher, 0L, interval, TimeUnit.SECONDS);

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + args[0]);
        } catch (IOException e) {
            System.out.println("IOException.");
        }
    }
}
