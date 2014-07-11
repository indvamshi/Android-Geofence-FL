
package com.example.android.geofence;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.aviktech.common.Utils;
import com.example.android.geofence.GeofenceUtils.REMOVE_TYPE;
import com.example.android.geofence.GeofenceUtils.REQUEST_TYPE;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.location.Geofence;

public class MainActivity extends FragmentActivity {
		// Store the current request
	private REQUEST_TYPE mRequestType;

	// Store the current type of removal
	private REMOVE_TYPE mRemoveType;

	// Store a list of geofences to add
	List<Geofence> mCurrentGeofences;

	// Add geofences handler
	private GeofenceRequester mGeofenceRequester;
	// Remove geofences handler
	private GeofenceRemover mGeofenceRemover;

	/*
	 * An instance of an inner class that receives broadcasts from listeners and from the
	 * IntentService that receives geofence transition events
	 */
	
	// An intent filter for the broadcast receiver
	private IntentFilter mIntentFilter;

	// Store the list of geofences to remove
	private List<String> mGeofenceIdsToRemove;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	
		// Create an intent filter for the broadcast receiver
		mIntentFilter = new IntentFilter();

		// Action for broadcast Intents that report successful addition of geofences
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

		// Action for broadcast Intents that report successful removal of geofences
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

		// Action for broadcast Intents containing various types of geofencing errors
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

		// All Location Services sample apps use this category
		mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

		// Instantiate the current List of geofences
		mCurrentGeofences = new ArrayList<Geofence>();

		// Instantiate a Geofence requester
		mGeofenceRequester = new GeofenceRequester(this);

		// Instantiate a Geofence remover
		mGeofenceRemover = new GeofenceRemover(this);

		// Attach to the main UI
		setContentView(R.layout.activity_main);

		// Get handles to the Geofence editor fields in the UI

		final String regId 	= GCMRegistrar.getRegistrationId(this);
		if (regId.equals(""))
		{
			GCMRegistrar.register(this, Utils.GCMSenderId);
		}
		else 
		{
			Log.d("", "Already registered:  "+regId);
/*			new GCMService().execute(regId);
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","diamondsaurabh@gmail.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, regId);
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
*/		
			new GCMService().execute(regId);
		}
		
		TextView txt_label = (TextView)findViewById(R.id.label_geofence); 
		
		String Msg = getIntent().getStringExtra("msg");
		if(Msg != null)
			txt_label.setText(Msg.replace(", ", "\n"));
		
	}

	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed() in
	 * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
	 * start an Activity that handles Google Play services problems. The result of this
	 * call returns here, to onActivityResult.
	 * calls
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Choose what to do based on the request code
		System.out.println("onActivityResult");
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// If the request was to add geofences
				if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

					// Toggle the request flag and send a new request
					mGeofenceRequester.setInProgressFlag(false);

					// Restart the process of adding the current geofences
					mGeofenceRequester.addGeofences(mCurrentGeofences);

					// If the request was to remove geofences
				} else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

					// Toggle the removal flag and send a new removal request
					mGeofenceRemover.setInProgressFlag(false);

					// If the removal was by Intent
					if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

						// Restart the removal of all geofences for the PendingIntent
						mGeofenceRemover.removeGeofencesByIntent(
								mGeofenceRequester.getRequestPendingIntent());

						// If the removal was by a List of geofence IDs
					} else {

						// Restart the removal of the geofence list
						mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
					}
				}
				break;

				// If any other result was returned by Google Play services
			default:

				// Report that Google Play services was unable to resolve the problem.
				Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(GeofenceUtils.APPTAG,
					getString(R.string.unknown_activity_request_code, requestCode));

			break;
		}
	}



	

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 *
		 * @param dialog An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
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
}