package com.appli.ilink;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import java.util.Map;
import org.json.JSONArray;

public class CustomRequest extends Request<JSONArray> {
    private Listener<JSONArray> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params, Listener<JSONArray> reponseListener, ErrorListener errorListener) {
        super(0, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomRequest(int method, String url, Map<String, String> params, Listener<JSONArray> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return this.params;
    }

    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new JSONArray(new String(response.data, HttpHeaderParser.parseCharset(response.headers))), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Throwable e) {
            return Response.error(new ParseError(e));
        }
    }

    protected void deliverResponse(JSONArray response) {
        this.listener.onResponse(response);
    }
}
