package pl.edu.mimuw.cloudatlas.client.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class IntervalHandler implements HttpHandler {
    Long interval;

    public IntervalHandler(Long interval) {
        this.interval = interval;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = interval.toString();

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}