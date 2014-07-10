package com.aviktech.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class RestClient 
{
	private ArrayList <NameValuePair> params;
	private String url;
	private ArrayList<NameValuePair> headers;
	public String response;
	public String message;
	private int responseCode;
	public String getResponse() 
	{
		return response;
	}

	public String getErrorMessage() 
	{
		return message;
	}

	public RestClient(String url)
	{
		this.url = url;
		params = new ArrayList<NameValuePair>();
		Log.v("parameter--------",""+params);
		headers = new ArrayList<NameValuePair>();
		Log.v("headers--------",""+headers);
	}

	public void AddParam(String name, String value) 
	{
		params.add(new BasicNameValuePair(name, value));
	}
	
	public void AddHeader(String name, String value)
	{
		headers.add(new BasicNameValuePair(name, value));
	}
	
	public void Execute(int method) throws Exception
	{
		switch(method) 
		{
		case 1://for GET
		{
			//add parameters
			String combinedParams = "";
			if(!params.isEmpty())
			{
				combinedParams += "?";
				for(NameValuePair p : params)
				{
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
					if(combinedParams.length() > 1)
					{
						combinedParams  +=  "&" + paramString;
					}
					else
					{
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}
			executeRequest(request, url);
			break;
		}
		case 2://Post
		{
			HttpPost request = new HttpPost(url);
			//Log.v("Http Request--------",""+request);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
				//System.out.println(h);
			}

			if(!params.isEmpty())
			{
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			executeRequest(request, url);
			break;
		}
		}
	}

	private void executeRequest(HttpUriRequest request, String url) 
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;
		try 
		{
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.v(" Response Code--------",""+responseCode);
			message = httpResponse.getStatusLine().getReasonPhrase();
			//Log.v("Message--------",""+message);
			HttpEntity entity = httpResponse.getEntity();
			//Log.v("Http Entity--------",""+entity);
			if (entity != null) {
				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);
				// Closing the input stream will trigger connection release
				instream.close();
			}

		}
		catch (ClientProtocolException e)  
		{
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) 
	{

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try 
		{
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line + "\n");
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				is.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}