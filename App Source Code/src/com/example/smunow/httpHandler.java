package com.example.smunow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("deprecation")

//Class to handle all post and get calls to API
public class httpHandler {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	public httpHandler(){
		//doesn't do anything
	}
	public String makeRequest(String URL, String method, List<NameValuePair> params){
		try{

			//determines which method of obtaining should be used
			if(method == "POST"){
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(URL);

				//encodes the parameters and executes the call
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}else if(method == "GET"){
				DefaultHttpClient httpClient = new DefaultHttpClient();

				//formats the parameters into URL string, augments URL and executes call
				String paramString = URLEncodedUtils.format(params, "utf-8");
				URL += "?" + paramString;
				HttpGet httpGet = new HttpGet(URL);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}            
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {

			//creates a string out of the returned string from the method
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
				sb.append(line + "\n");
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		return json;
	}
}
