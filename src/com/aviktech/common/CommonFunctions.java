package com.aviktech.common;

import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonFunctions 
{
	public static final String SIGNUPURL = "";
	private Context ctx;

	public CommonFunctions(Context ctx) 
	{
		this.ctx 			= ctx;
	}

	public String connectToServer(String url, HashMap<String, String> param) 
	{
		String str_Responce = null;
		if(isOnline())
		{
			RestClient client = new RestClient(url);
			System.out.println(url);
			for (String key: param.keySet()) 
			{
				System.out.println(key+"  "+ param.get(key));
				client.AddParam(key, param.get(key));
			}

			try
			{
				client.Execute(2);
				str_Responce = client.getResponse();
				System.out.println(str_Responce);
			} 
			catch (Exception e)
			{
				str_Responce = null;
			}
		}
		return str_Responce;
	}

	public boolean isOnline()
	{
		ConnectivityManager cm   = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo netinfo   = cm.getActiveNetworkInfo();
		if(netinfo      != null && netinfo.isConnectedOrConnecting())
		{
			return true;
		}
		return false;
	}
}