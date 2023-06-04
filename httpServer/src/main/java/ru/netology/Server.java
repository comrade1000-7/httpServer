package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private ConcurrentHashMap<String, Map<String, Handler>> handlers;
    final static int coresCount = 64;
    private final ExecutorService executorService;
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");


    public Server() {
        executorService = Executors.newFixedThreadPool(coresCount);
        handlers = new ConcurrentHashMap<>();
    }

    //start the connection
    public void start(int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> connect(socket));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // handle the connection
    public void connect(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            System.out.println("New connection");
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();

            if (requestLine == null) {
                return;
            }

            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            String method = parts[0];
            String pathWithParams = parts[1];

            Request request = new Request(method, pathWithParams);

            if (request == null || !handlers.containsKey(request.getMethod())) {
                responseWithoutContent(out, "404", "Not Found");
            }

            if (handlers.get(request.getMethod()).containsKey(request.getPath())) {
                Handler handler = handlers.get(request.getMethod()).get(request.getPath());
                handler.handle(request, out);
            } else {
                if (!handlers.get(request.getMethod()).containsKey(request.getPath())) {
                    responseWithoutContent(out, "404", "Not Found");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new HashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    protected void responseWithoutContent(BufferedOutputStream out, String responseCode, String responseStatus) throws IOException {
        out.write((
                "HTTP/1.1 " + responseCode + " " + responseStatus + "\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection close\r\n" +
                    "\r\n"
                ).getBytes());
        out.flush();
    }
}
