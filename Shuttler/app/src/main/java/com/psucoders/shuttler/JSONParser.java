package com.psucoders.shuttler;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Gaurav Jayasawal on 7/5/2016.
 */
public class JSONParser {

    InputStream is = null;
    String json = "";
    JSONObject jObj = null;
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params){

        try{
            if(method == "POST"){
                //request method is POST
                //defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //Log.i("line",line);
                sb.append(line + "\n");
                //Log.i("line",line);
            }
            is.close();
            json = sb.toString();
            Log.e("Testing", json);
        }
        catch (Exception e){
            Log.e("Buffer error", "Error converting result " + e.toString());
        }
        try{
            jObj = new JSONObject(json);

        }
        catch (JSONException e){
            Log.e("JSON PARSER","Error parsing data " +e.toString());
        }
        return jObj;
    }
}