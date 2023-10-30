package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Integer serverPort;
    private static Integer serverMaxPool;
    private boolean isBadConfig = false;
    private ExecutorService executorService;

    public Server() {
        loadServerSettings();
        executorService = Executors.newFixedThreadPool(serverMaxPool);
    }

    private void loadServerSettings() {

        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("server.properties");

        try {

            properties.load(inputStream);

            if (properties.getProperty("server.port").equals("")) {
                System.out.println("Порт не может быть пустым. Серевер не инициализирован.");
                isBadConfig = true;
            } else {
                serverPort = Integer.parseInt(properties.getProperty("server.port"));
            }

            if (properties.getProperty("server.thread.maxPool").equals("")) {
                serverMaxPool = 64;

                System.out.println(String.format(
                        "Количество потоков обработки запросов от клиента не задано. Установлено значение по умолчанию = %s",
                        serverMaxPool)
                );

            } else {
                serverMaxPool = Integer.parseInt(properties.getProperty("server.thread.maxPool"));
            }


        } catch (IOException ex) {
            System.out.println("Ошибка инициализации параметров сервера:\n" + ex.getMessage());
        }
    }

    public void start() {

        if (isBadConfig) {
            System.out.println("Ошибка инициализации сервера.");
            return;
        }

        final var validPaths = List.of(
                "/index.html",
                "/spring.svg",
                "/spring.png",
                "/resources.html",
                "/styles.css",
                "/app.js",
                "/links.html",
                "/forms.html",
                "/classic.html",
                "/events.html",
                "/events.js"
        );

        try (final var serverSocket = new ServerSocket(serverPort)) {

            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept();
                    executorService.execute(new ServerRequestHandler(socket,validPaths));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}




