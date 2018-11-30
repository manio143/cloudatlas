package pl.edu.mimuw.cloudatlas.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        String file = args[0];
        server.createContext("/test", new MyHandler(args[0]));
        server.createContext("/test2", new MyHandler2());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        private String file;

        public MyHandler(String file) {
            this.file = file;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
//            String response = "<p>This is the response</p>";
            List<String> lines = Files.readAllLines(Paths.get(file));
            String response = "";
            for (String line : lines) {
                response += line + "\n";
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MyHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "  <title>Bootstrap Website Example</title>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                    "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
                    "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>\n" +
                    "  <style>\n" +
                    "  .fakeimg {\n" +
                    "      height: 200px;\n" +
                    "      background: #aaa;\n" +
                    "  }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div class=\"jumbotron text-center\" style=\"margin-bottom:0\">\n" +
                    "  <h1>My First Bootstrap Page</h1>\n" +
                    "  <p>Resize this responsive page to see the effect!</p> \n" +
                    "</div>\n" +
                    "\n" +
                    "<nav class=\"navbar navbar-inverse\">\n" +
                    "  <div class=\"container-fluid\">\n" +
                    "    <div class=\"navbar-header\">\n" +
                    "      <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#myNavbar\">\n" +
                    "        <span class=\"icon-bar\"></span>\n" +
                    "        <span class=\"icon-bar\"></span>\n" +
                    "        <span class=\"icon-bar\"></span>                        \n" +
                    "      </button>\n" +
                    "      <a class=\"navbar-brand\" href=\"#\">WebSiteName</a>\n" +
                    "    </div>\n" +
                    "    <div class=\"collapse navbar-collapse\" id=\"myNavbar\">\n" +
                    "      <ul class=\"nav navbar-nav\">\n" +
                    "        <li class=\"active\"><a href=\"#\">Home</a></li>\n" +
                    "        <li><a href=\"#\">Page 2</a></li>\n" +
                    "        <li><a href=\"#\">Page 3</a></li>\n" +
                    "      </ul>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</nav>\n" +
                    "\n" +
                    "<a href=\"#\">woow</a>\n" +
                    "\n" +
                    "<!-- -->\n" +
                    "<script>\n" +
                    "function loadDoc() {\n" +
                    "  alert(\"A\");\n" +
                    "  var req = new XMLHttpRequest();\n" +
                    "    req.open(\"GET\", \"http://localhost:8000/register/\";\n" +
                    "    req.addEventListener(\"error\", function () {\n" +
                    "        alert(\"Error: \" + this.responseText);\n" +
                    "    });\n" +
                    "    req.addEventListener(\"load\", function () {\n" +
                    "        alert(this.responseText);\n" +
                    "    });\n" +
                    "    req.send();\n" +
                    "}\n" +
                    "</script>\n" +
                    "<!-- -->\n" +
                    "\n" +
                    "<a href=\"#\" onclick=\"loadDoc()\">Page 3</a>\n" +
                    "\n" +
                    "<button type=\"button\" href=\"#\">Request data</button>\n" +
                    "\n" +
                    "<p id=\"demo\"></p>\n" +
                    "\n" +
                    "\n" +
                    "<div class=\"container\">\n" +
                    "  <div class=\"row\">\n" +
                    "    <div class=\"col-sm-4\">\n" +
                    "      <h2>About Me</h2>\n" +
                    "      <h5>Photo of me:</h5>\n" +
                    "      <div class=\"fakeimg\">Fake Image</div>\n" +
                    "      <p>Some text about me in culpa qui officia deserunt mollit anim..</p>\n" +
                    "      <h3>Some Links</h3>\n" +
                    "      <p>Lorem ipsum dolor sit ame.</p>\n" +
                    "      <ul class=\"nav nav-pills nav-stacked\">\n" +
                    "        <li class=\"active\"><a href=\"#\">Link 1</a></li>\n" +
                    "        <li><a href=\"#\">Link 2</a></li>\n" +
                    "        <li><a href=\"#\">Link 3</a></li>\n" +
                    "      </ul>\n" +
                    "      <hr class=\"hidden-sm hidden-md hidden-lg\">\n" +
                    "    </div>\n" +
                    "    <div class=\"col-sm-8\">\n" +
                    "      <h2>TITLE HEADING</h2>\n" +
                    "      <h5>Title description, Dec 7, 2017</h5>\n" +
                    "      <div class=\"fakeimg\">Fake Image</div>\n" +
                    "      <p>Some text..</p>\n" +
                    "      <p>Sunt in culpa qui officia deserunt mollit anim id est laborum consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.</p>\n" +
                    "      <br>\n" +
                    "      <h2>TITLE HEADING</h2>\n" +
                    "      <h5>Title description, Sep 2, 2017</h5>\n" +
                    "      <div class=\"fakeimg\">Fake Image</div>\n" +
                    "      <p>Some text..</p>\n" +
                    "      <p>Sunt in culpa qui officia deserunt mollit anim id est laborum consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.</p>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>\n" +
                    "\n" +
                    "<div class=\"jumbotron text-center\" style=\"margin-bottom:0\">\n" +
                    "  <p>Footer</p>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n" +
                    "\"";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}