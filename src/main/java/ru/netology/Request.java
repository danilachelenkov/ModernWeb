package ru.netology;

import org.apache.http.NameValuePair;

import java.util.*;
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

    public List<String> getPostParam(String name) {
        return getPostParams().get(name);

    }

    public Map<String, List<String>> getPostParams() {
        Map<String, List<String>> map = new HashMap<>();

        if (body.trim().length() > 0) {
            String[] postParams = body.split("&");
            for (int i = 0; i < postParams.length; i++) {
                if (postParams[i].split("=").length == 2) {
                    if (map.containsKey(postParams[i].split("=")[0])) {
                        map.get(postParams[i].split("=")[0]).add(postParams[i].split("=")[1]);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(postParams[i].split("=")[1]);
                        map.put(postParams[i].split("=")[0], list);
                    }
                }
            }
        }
        return map;
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

