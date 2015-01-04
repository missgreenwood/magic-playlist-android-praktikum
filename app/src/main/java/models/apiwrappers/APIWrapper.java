package models.apiwrappers;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by charlotte on 05.12.14.
 */


public class APIWrapper extends AsyncTask<String, Void, String> {


    public static final String GET_METHOD = "get_method";
    public static final String POST_METHOD = "post_method";
    private static final String TAG = "main.java.models.apiwrappers.APIWrapper";
    private CallbackInterface parent;
    private String callback;
    private String method;

    public APIWrapper(CallbackInterface parent, String callback, String method) {
        this.parent = parent;
        this.callback = callback;
        this.method = method;
    }

    public static String encodeURL(String baseUrl, List<NameValuePair> params) {

        String paramString = URLEncodedUtils
                .format(params, "utf-8");
        baseUrl += "?" + paramString;
        return baseUrl;
    }


    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    @Override
    protected void onPostExecute(String response) {
        Log.d(TAG, "onpostexecute");
        Log.d(TAG, "json array string: " + response);

        //   parent.processWebCallResult(response, true);

        /*
        Method method=null;
        try {
            method=parent.getClass().getMethod(callback, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        try {
            if (method != null) {
                method.invoke(parent, response);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/


        parent.processWebCallResult(response, callback);

    }


    @Override
    protected String doInBackground(String... url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = null;
        String response = null;
        HttpResponse httpResponse = null;
        HttpRequestBase httpMessage = null;


        if (method.equals(GET_METHOD)) {

            httpMessage = new HttpGet(url[0]);

        } else if (method.equals(POST_METHOD)) {

            httpMessage = new HttpPost(url[0]);

        }
        // http://developer.android.com/training/basics/fragments/index.html


        if (httpMessage != null) {
            httpMessage.setHeader("Content-Type", "application/json");
        }


        try {
            httpResponse = httpClient.execute(httpMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (httpResponse != null) {
            httpEntity = httpResponse.getEntity();
            Log.d(TAG, "length: " + httpEntity.getContentLength());
            Log.d(TAG, "teeest: " + httpEntity.toString());


            try {
                response = EntityUtils.toString(httpEntity);


                Log.d(TAG, "response: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }









            /*if (response!=null &&  (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK))
            {

                Log.d("", "create json obj");


            }*/

        return response;
    }


}





