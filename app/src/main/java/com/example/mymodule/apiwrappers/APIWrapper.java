package com.example.mymodule.apiwrappers;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.example.mymodule.mediawrappers.AbstractMediaWrapper;
import com.example.mymodule.mediawrappers.RemoteFileStreamingMediaWrapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by charlotte on 05.12.14.
 */




public class APIWrapper extends AsyncTask<String, Void, String> {


    private RemoteFileStreamingMediaWrapper parent;

    public APIWrapper(RemoteFileStreamingMediaWrapper parent) {
        this.parent = parent;
    }

    public static String encodeURL(String baseUrl, List<NameValuePair> params )
    {

        String paramString = URLEncodedUtils
                .format(params, "utf-8");
        baseUrl += "?" + paramString;
        return baseUrl;
    }




    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
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
            Log.d("", "onpostexecute");


            Log.d("","json array string: "+response);

            parent.processWebCallResult(response);



        }






        @Override
        protected String doInBackground(String... url) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            String response = null;




            HttpGet httpGet = new HttpGet(url[0]);
            HttpResponse httpResponse = null;
            httpGet.setHeader("Content-Type","application/json");


            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }


            httpEntity = httpResponse.getEntity();
            Log.d("", "length: "+httpEntity.getContentLength());
            Log.d("", "teeest: "+httpEntity.toString());
            try {
                response = EntityUtils.toString(httpEntity);


                Log.d("", "response: "+response);
            } catch (IOException e) {
                e.printStackTrace();
            }




            /*if (response!=null &&  (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK))
            {

                Log.d("", "create json obj");


            }*/

            return response;
        }




    }





