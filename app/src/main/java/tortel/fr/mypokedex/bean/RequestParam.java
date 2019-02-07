package tortel.fr.mypokedex.bean;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class RequestParam {
    private Object callback;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters  = new HashMap<>();
    private String url;
    private RequestCategoriesEnum category;
    private Context context;

    public RequestParam(Object callback, Context context, String url, RequestCategoriesEnum category) {
        this.callback = callback;
        this.url = url;
        this.category = category;
        this.context = context;
    }

    public Object getCallback() {
        return callback;
    }

    public void setCallback(Object callback) {
        this.callback = callback;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RequestParam addHeaders(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public RequestParam addParameters(final String key, final String value) {
        this.parameters.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestCategoriesEnum getCategory() {
        return category;
    }

    public void setCategory(RequestCategoriesEnum category) {
        this.category = category;
    }

    public String getUri() {
        StringBuilder uri = new StringBuilder(getUrl());

        if (parameters.isEmpty()) {
            return uri.toString();
        }

        uri.append("?");

        for (Map.Entry entry : parameters.entrySet()) {
            uri.append(entry.getKey());
            uri.append("=");
            uri.append(entry.getValue());
            uri.append("&");
        }

        return uri.substring(0, uri.length() - 2);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
