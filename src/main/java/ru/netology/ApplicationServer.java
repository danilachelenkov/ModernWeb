package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ApplicationServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.addHandler("GET", "/messages.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {

                final var filePath = Path.of(".", "public", request.getHeaders());
                final var length = Files.size(filePath);

                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + filePath + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());

                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });


        server.start();
    }
}
