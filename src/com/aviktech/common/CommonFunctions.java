package com.aviktech.common;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommonFunctions {
	public static final String SIGNUPURL = "http://192.168.0.10:8080/dealsmessanger/device";
	private Context ctx;

	public CommonFunctions(Context ctx) {
		this.ctx = ctx;
	}

	public void connectToServer(String url , JSONObject jsonObject) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setHeader("content-type",
				"application/json; charset=UTF-8");

		StringEntity entity = new StringEntity(jsonObject.toString());

		post.setEntity(entity);
		httpClient.execute(post);

		Log.d("", "device data sent to server:  ");

	}
	
	public String connectToServer(String url, HashMap<String, String> param) {
		String str_Responce = null;
		if (isOnline()) {
			RestClient client = new RestClient(url);
			System.out.println(url);
			for (String key : param.keySet()) {
				System.out.println(key + "  " + param.get(key));
				client.AddParam(key, param.get(key));
			}

			try {
				client.Execute(2);
				str_Responce = client.getResponse();
				System.out.println(str_Responce);
			} catch (Exception e) {
				str_Responce = null;
			}
		}
		return str_Responce;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}