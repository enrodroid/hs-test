package org.hyperskill.hstest.v6.mocks.web.request;

import org.apache.http.entity.ContentType;
import org.hyperskill.hstest.v6.mocks.web.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    String method = "";
    String uri = "";
    String version = "";

    Map<String, String> getParams = new HashMap<>();
    Map<String, String> headers = new HashMap<>();

    String content = "";
    int contentLength;

    public HttpRequest() { }

    public HttpRequest(String method) {
        this.method = method;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getGetParams() {
        return getParams;
    }

    public HttpRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public HttpRequest setGetParam(String getParam, String value) {
        getParams.put(getParam, value);
        return this;
    }

    public HttpRequest addHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public HttpRequest setContent(String content) {
        this.content = content;
        contentLength = content.length();
        return this;
    }

    public HttpRequest setContentType(ContentType type) {
        return addHeader("Content-Type", type.getMimeType());
    }

    public HttpResponse send() {
        return HttpRequestExecutor.send(this);
    }
}
