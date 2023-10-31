package ru.netology;

public class Request {
    private httpReqMethod method;
    private String headers;
    private String body;

    public Request(httpReqMethod method, String headers, String body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public httpReqMethod getMethod() {
        return method;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}

