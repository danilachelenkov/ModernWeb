package ru.netology;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Request {
    private httpReqMethod method;
    private String headers;
    private String body;

    private List<NameValuePair> listQueryParams;

    public Request(httpReqMethod method, String headers, String body, List<NameValuePair> listQueryParams) {
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.listQueryParams = listQueryParams;
    }

    public Map<String, String> getQueryParams() {
        return listQueryParams.stream()
                .collect(Collectors.toMap(a -> a.getName(), a -> a.getValue()));
    }

    public Optional<String> getQueryParam(String name) {
        return listQueryParams.stream()
                .filter(a -> a.getName().equals(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList()).stream().findFirst();
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

