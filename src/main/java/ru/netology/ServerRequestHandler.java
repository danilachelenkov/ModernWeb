package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.net.URISyntaxException;

public class ServerRequestHandler implements Runnable {
    private final Socket socket;
    private final List<String> validPaths;

    public ServerRequestHandler(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {

        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream());) {

            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (checkRequestContext(parts)) {
                // just close socket
                return;
            }

            Request request = getRequest(requestLine);

            final var path = parts[1];

            if (!validPaths.contains(request.getHeaders())) {

                Handler handler = getMapHandler(request);

                if (handler != null) {
                    handler.handle(request, out);
                }

                sendWarningResponse(out);
                return;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();

                sendResponse(out, content, mimeType);

                return;
            }

            sendResponse(out, filePath);

        } catch (IOException | URISyntaxException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Request getRequest(String requestLine) throws URISyntaxException {
        String[] parts = requestLine.split(" ");
        return new RequestBuilder()
                .setMethod(getMethodRequestLine(parts[0]))
                .setHeaders(parts[1])
                .build();
    }

    private httpReqMethod getMethodRequestLine(String part) {
        return switch (part) {
            case "POST" -> httpReqMethod.POST;
            default -> httpReqMethod.GET;
        };
    }

    private Handler getMapHandler(Request request) {
        System.out.println(request.getMethod());
        System.out.println(request.getHeaders());
        synchronized (Server.mapHandlers.get(request.getMethod().toString())) {
            Map<String, Handler> mapPath = Server.mapHandlers.get(request.getMethod().toString());

            if (mapPath != null) {
                return mapPath.get(request.getHeaders());
            }
        }
        return null;
    }

    private void sendResponse(BufferedOutputStream out, byte[] content, String mimeType) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }

    private void sendResponse(BufferedOutputStream out, Path path) throws IOException {
        final var length = Files.size(path);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + path + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(path, out);
        out.flush();
    }

    private void sendWarningResponse(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private boolean checkRequestContext(String[] parts) {
        return parts.length != 3;
    }
}
