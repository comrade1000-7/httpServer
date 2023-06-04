package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/events.html", "/events.js");

    public static void main(String[] args) {
        Server server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            try {
                server.responseWithoutContent(responseStream, "404", "Not Found Dude");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                try {
                    server.responseWithoutContent(responseStream, "404", "Not Found");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        server.addHandler("GET", "/classic.html", (request, responseStream) -> {
            try {
                String template = Files.readString(Path.of("httpServer", "public", request.getPath()));
                byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();

                responseStream.write(request.getResponse(content).getBytes());
                responseStream.write(content);
                responseStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        handlersReg(server);
        server.start(9999);

    }
    private static void handlersReg(Server server){
        for (String path: validPaths){
            server.addHandler("GET", path, (request, responseStream) -> {
                try {
                    responseStream.write(request.getResponse().getBytes());
                    Files.copy(Path.of("httpServer", "public", request.getPath()), responseStream);
                    responseStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

