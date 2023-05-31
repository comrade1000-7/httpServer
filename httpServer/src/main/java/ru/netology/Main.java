package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
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
        server.start(9999);
    }
}

