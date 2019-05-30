package com.snaporaz.snaporaz;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SnaporazRestClient {
    private static final String BASE_URL = "http://192.168.1.82/SnaporazSpring/";

    private static AsyncHttpClient client = new AsyncHttpClient(42729);

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void login(String token, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("idTokenString", token);
        client.post(getAbsoluteUrl("login"), params, responseHandler);
    }

    public static void listProjectsByTitle(String title, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("projects/" + title), responseHandler);
    }

    public static void getProjectById(int id, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("idTokenString", "");
        client.post(getAbsoluteUrl("project"), params, responseHandler);
    }

    public static void getProjectByOwner(String token, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl("projectsOwner/" + token), responseHandler);
    }

    public static void getProjectByCollab(String token, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl("projectsCollab/" + token), responseHandler);
    }

    public static void mainPage(AsyncHttpResponseHandler responseHandler){
        client.get(BASE_URL, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
