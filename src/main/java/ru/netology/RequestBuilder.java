package ru.netology;

public class RequestBuilder {
    private httpReqMethod method;
    private String headers;
    private String body;

    public RequestBuilder() {
    }

    public RequestBuilder setMethod(httpReqMethod method) {
        this.method = method;
        return this;
    }

    public RequestBuilder setHeaders(String headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public Request build() {
        if (headers == "" && method == null) {
            throw new IllegalStateException("Ошибка создания объекта. Объект не обладает основыми свойствами.");
        }
        return new Request(method, headers, body);
    }
}

