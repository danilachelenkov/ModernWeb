package ru.netology;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
public class RequestBuilder {
    private httpReqMethod method;
    private String headers;
    private String body;
    private List<NameValuePair> listQueryParams;

    public RequestBuilder() {
    }

    public RequestBuilder setMethod(httpReqMethod method) {
        this.method = method;
        return this;
    }

    public RequestBuilder setHeaders(String headers) throws URISyntaxException {
        URI uri = new URI(headers);

        if (uri.getQuery() != "" && uri.getQuery() != null) {
            listQueryParams = URLEncodedUtils.parse(uri, HTTP.UTF_8);
        }

        this.headers = uri.getPath();
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
        return new Request(method, headers, body, listQueryParams);
    }
}

