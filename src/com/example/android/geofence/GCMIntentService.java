package com.example.android.geofence;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.aviktech.common.CommonFunctions;
import com.aviktech.common.Utils;
import com.aviktech.database.DatabaseHelper;
import com.aviktech.database.GeofenceDetail;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.location.Geofence;

public class GCMIntentService extends GCMBaseIntentService
{
	private GeofenceRequester mGeofenceRequester;
	private ArrayList<Geofence> mCurrentGeofences;

	public GCMIntentService()
	{
		super(Utils.GCMSenderId);
	}

	@Override
	protected void onError(Context context, String regId) 
	{
		Log.e("", "error registration id : "+regId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) 
	{
		handleMessage(context, intent);
	}

	@Override
	protected void onRegistered(Context context, String regId) 
	{
		handleRegistration(context, regId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) 
	{

	}

	private void handleMessage(Context context, Intent intent) 
	{
		try 
		{
			Utils.notiMsg 				= intent.getStringExtra("msg");
			Utils.notiLati 				= intent.getStringExtra("lati");
			Utils.notiLongi 			= intent.getStringExtra("longi");
			Utils.notiRadius 			= intent.getStringExtra("radius");
			Utils.notiExp 				= intent.getStringExtra("exp");
			Utils.notificationReceived	= true;
			System.out.println(Utils.notificationReceived);
			
			handleDialog(context);

			try 
			{
				PowerManager pm 			= (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl 				= pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			 NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher) // notification icon
			.setContentTitle(Utils.notiMsg) // title for notification
			.setContentText("Message "+Utils.notiMsg) // message for notification
			.setAutoCancel(true); // clear notification after click

			PendingIntent pi = PendingIntent.getActivity(this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
			mBuilder.setContentIntent(pi);
			//mNotificationManager.notify(0, mBuilder.build());

			DatabaseHelper database = new DatabaseHelper(context);
			database.InsertValue(Utils.notiLati, Utils.notiLongi, Utils.notiMsg, Utils.notiRadius, Utils.notiExp, "", "", "");

			GeofenceSampleReceiver mBroadcastReceiver = new GeofenceSampleReceiver();
			IntentFilter mIntentFilter = new IntentFilter();

			// Action for broadcast Intents that report successful addition of geofences
			mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

			// Action for broadcast Intents that report successful removal of geofences
			mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

			// Action for broadcast Intents containing various types of geofencing errors
			mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

			// All Location Services sample apps use this category
			mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
		
			ArrayList<GeofenceDetail> arrDetail = database.getAllLists();
			if(mCurrentGeofences == null)
				mCurrentGeofences = new ArrayList<Geofence>();
			if(mGeofenceRequester == null)
				mGeofenceRequester = new GeofenceRequester(context);
			long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1 * DateUtils.HOUR_IN_MILLIS;
			LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, mIntentFilter);

			for (int i = 0; i < arrDetail.size(); i++) 
			{
				String geofenceId 	= arrDetail.get(i).getID();
				double Lati 		= arrDetail.get(i).getLatitude();
				double Longi 		= arrDetail.get(i).getLongitudee();
				float radius 		= arrDetail.get(i).getRadius();
				long expiration 	= arrDetail.get(i).getExpirationDuration() * GEOFENCE_EXPIRATION_IN_MILLISECONDS;//Long.parseLong(Utils.notiExp);
				int transition 		= Geofence.GEOFENCE_TRANSITION_ENTER;

				SimpleGeofence mGeofence = new SimpleGeofence(geofenceId, Lati, Longi, radius, expiration, transition );
				mCurrentGeofences.add(mGeofence.toGeofence());
			}
			try 
			{
				// Try to add geofences
				mGeofenceRequester.addGeofences(mCurrentGeofences);
			} catch (Exception e)
			{
				e.printStackTrace();
				// Notify user that previous request hasn't finished.
				Toast.makeText(this, R.string.add_geofences_already_requested_error,
						Toast.LENGTH_LONG).show();
			}

			System.out.println("geofenceId  "+mGeofenceRequester.getInProgressFlag());
		}
		catch (Exception e) 
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	private void handleDialog(final Context context) 
	{
		Handler h = new Handler(Looper.getMainLooper());
		h.post(new Runnable()
		{
			public void run()
			{

			}         
		});
	}

	private void handleRegistration(final Context context,final String regId) 
	{
		Utils.registrationId = regId;
		Log.e("", "registration id : "+regId);
		Handler h = new Handler(Looper.getMainLooper());
		final CommonFunctions cmF 		= new CommonFunctions(context);
		h.post(new Runnable()
		{
			public void run() 
			{
				System.out.println("Registration Successful");
				Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show();
				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						HashMap<String, String> param		= new HashMap<String, String>();

						TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
						String IMEI = telephonyManager.getDeviceId();
						if (String.valueOf(IMEI).toString().trim().equals("null") )
						{
							WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
							WifiInfo wifiInf = wifiMan.getConnectionInfo();
							String macAddr = wifiInf.getMacAddress();
							IMEI = macAddr;
						}
						else if (String.valueOf(IMEI).toString().trim().equals("") )
						{
							WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
							WifiInfo wifiInf = wifiMan.getConnectionInfo();
							String macAddr = wifiInf.getMacAddress();
							IMEI = macAddr;
						}

						param.put("devicename", getDeviceName());
						param.put("imei", String.valueOf(IMEI.replace(":", "")));
						param.put("tocken", regId);
						param.put("apptype", "Android");
						String res = cmF.connectToServer(CommonFunctions.SIGNUPURL.concat("adddata.php"), param);
						System.out.println(res);
					}
				});
				//t.start();
			}         
		});
	}

	public String getDeviceName() 
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) 
		{
			return capitalize(model);
		} 
		else 
		{
			return capitalize(manufacturer) + " " + model;
		}
	}


	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}



	/**
	 * Define a Broadcast receiver that receives updates from connection listeners and
	 * the geofence transition service.
	 */
	public class GeofenceSampleReceiver extends BroadcastReceiver {
		/*
		 * Define the required method for broadcast receivers
		 * This method is invoked when a broadcast Intent triggers the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("GeofenceSampleReceiver");
			// Check the action code and determine what to do
			String action = intent.getAction();

			// Intent contains information about errors in adding or removing geofences
			if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

				handleGeofenceError(context, intent);

				// Intent contains information about successful addition or removal of geofences
			} else if (
					TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
					||
					TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

				handleGeofenceStatus(context, intent);

				// Intent contains information about a geofence transition
			} else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

				handleGeofenceTransition(context, intent);

				// The Intent contained an invalid action
			} else {
				Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
				Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * If you want to display a UI message about adding or removing geofences, put it here.
		 *
		 * @param context A Context for this component
		 * @param intent The received broadcast Intent
		 */
		private void handleGeofenceStatus(Context context, Intent intent) {

		}

		/**
		 * Report geofence transitions to the UI
		 *
		 * @param context A Context for this component
		 * @param intent The Intent containing the transition
		 */
		private void handleGeofenceTransition(Context context, Intent intent) {
			/*
			 * If you want to change the UI when a transition occurs, put the code
			 * here. The current design of the app uses a notification to inform the
			 * user that a transition has occurred.
			 */
		}

		/**
		 * Report addition or removal errors to the UI, using a Toast
		 *
		 * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
		 */
		private void handleGeofenceError(Context context, Intent intent) 
		{
			String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
			Log.e(GeofenceUtils.APPTAG, msg);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}
}
