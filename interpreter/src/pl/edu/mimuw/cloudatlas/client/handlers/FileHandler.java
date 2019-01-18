package pl.edu.mimuw.cloudatlas.client.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler implements HttpHandler {
    String path;

    public FileHandler(String path) {
        this.path = path;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        byte[] response = Files.readAllBytes(Paths.get(path));

        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}