package com.example.android.geofence;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GCMService extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		JSONObject jsonObj = new JSONObject();
		JSONArray array = new JSONArray();

		try {

			jsonObj.put("deviceId", params[0]);
			jsonObj.put("gcmRegId", params[0]);
			// jsonObj.put("version", android.os.);
			jsonObj.put("platform", "ANDROID");
			jsonObj.put("platformVersion", android.os.Build.VERSION.RELEASE);
			// jsonObj.put("uniqueDeviceId", );
			jsonObj.put("brand", android.os.Build.BRAND);
			jsonObj.put("model", android.os.Build.MODEL);
			jsonObj.put("location", array);

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://192.168.0.10:8080/dealsmessanger/device");
			// "http://127.0.0.1:8080/dealsmessanger/device");
			post.setHeader("content-type",
					"application/json; charset=UTF-8");

			StringEntity entity = new StringEntity(jsonObj.toString());

			post.setEntity(entity);
			httpClient.execute(post);

			Log.d("", "device data sent to server:  ");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

}
