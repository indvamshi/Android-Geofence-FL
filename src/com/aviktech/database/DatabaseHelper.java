package com.aviktech.database;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private final static int DATABASE_VERSION 	= 1;
	private static final String DATABASE_NAME 	= "geofencing";
	private static final String TAG 			= "DatabaseHelper";
	private  final String TABLE_GEOFENCING 		= "geofencing_table";

	private  final String KEY_ID 		= "id";
	private  final String KEY_LATI 		= "lati";
	private  final String KEY_LONGI 	= "longi";
	private  final String KEY_RADIUS 	= "radius";
	private  final String KEY_MESSAGE 	= "message";
	private  final String KEY_EX_TIME 	= "ex_time";
	private  final String KEY_TIME 		= "time";
	private  final String KEY_TEST1		= "test1";
	private  final String KEY_TEST2		= "test2";

	// Table Create Statements
	// CATEGORY_PLACE_LIST table create statement
	private final String CREATE_TABLE_CATEGORY_PLACE_LIST= "CREATE TABLE "
			+ TABLE_GEOFENCING + 
			"("
			+ KEY_ID 					+ " INTEGER PRIMARY KEY," 
			+ KEY_LATI 					+ " TEXT," 
			+ KEY_LONGI 				+ " TEXT,"
			+ KEY_RADIUS 				+ " TEXT,"
			+ KEY_MESSAGE 				+ " TEXT,"
			+ KEY_EX_TIME 				+ " TEXT,"
			+ KEY_TIME 					+ " TEXT,"
			+ KEY_TEST1 				+ " TEXT,"
			+ KEY_TEST2 				+ " TEXT"
			+")";

	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// creating required tables
		db.execSQL(CREATE_TABLE_CATEGORY_PLACE_LIST);
		Log.e(TAG, "created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOFENCING);
		// create new tables
		onCreate(db);
	}

	// ------------------------ "CATEGORY_PLACE_LIST" table methods ----------------//

	/*
	 * Creating a category_place_list
	 */
	public String InsertValue( String LATI, String LONGI, String MESSAGE, 
			String RADIUS, String EX_TIME, String TIME, String TEST1, String TEST2) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_LATI, 				LATI);
		values.put(KEY_LONGI, 				LONGI);
		values.put(KEY_RADIUS, 				RADIUS);
		values.put(KEY_MESSAGE, 			MESSAGE);
		values.put(KEY_EX_TIME, 			EX_TIME);
		values.put(KEY_TIME, 				TIME);
		values.put(KEY_TEST1, 				TEST1);
		values.put(KEY_TEST2, 				TEST2);

		// Inserting Row

		long id = db.insert(TABLE_GEOFENCING, null, values);

		Log.d("DataInsertion: ","Insertion Succefully ..");
		db.close(); // Closing database connection
		return id+"";
	}

	/*
	 * Deleting a category_place_list
	 */
	public void deleteCategory_Place_List(long tado_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GEOFENCING, KEY_ID + " = ?",
				new String[] { String.valueOf(tado_id) });
		db.close();
	}

	/**
	 * getting all category_place_list
	 * @param latitude2 
	 * @param longitude2 
	 * @param SearchString 
	 * */
	public ArrayList<GeofenceDetail> getAllLists()//double latitude2, double longitude2) 
	{
		ArrayList<GeofenceDetail> catplclist = new ArrayList<GeofenceDetail>();
		String selectQuery = "";
		selectQuery = "SELECT  * FROM " + TABLE_GEOFENCING;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) 
		{
			do
			{
				float Radius = c.getFloat(c.getColumnIndex(KEY_RADIUS));
				double Lati = c.getDouble(c.getColumnIndex(KEY_LATI));
				double Longi = c.getDouble(c.getColumnIndex(KEY_LONGI));

				GeofenceDetail catlst = new GeofenceDetail();
				catlst.setID(c.getString(c.getColumnIndex(KEY_ID)));
				catlst.setLatitude(Lati);
				catlst.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
				catlst.setLongitude(Longi);
				catlst.setRadius(Radius);
				catlst.setExpirationDuration(c.getLong(c.getColumnIndex(KEY_EX_TIME)));
				catlst.setTransitionType(c.getInt(c.getColumnIndex(KEY_TIME)));

				catplclist.add(catlst);
			} 
			while (c.moveToNext());
		}
		Log.e("catplclist size", catplclist.size()+"");
		db.close();
		c.close();
		return catplclist;
	}

	public ArrayList<GeofenceDetail> getValue(String id) 
	{
		ArrayList<GeofenceDetail> catplclist = new ArrayList<GeofenceDetail>();
		String selectQuery = "";
		selectQuery = "SELECT * FROM " + TABLE_GEOFENCING +" where "+KEY_ID +" in ("+id+") order by id desc limit 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		System.out.println(selectQuery);
		if (c.moveToFirst()) 
		{
			do
			{
				float Radius = c.getFloat(c.getColumnIndex(KEY_RADIUS));
				double Lati = c.getDouble(c.getColumnIndex(KEY_LATI));
				double Longi = c.getDouble(c.getColumnIndex(KEY_LONGI));

				GeofenceDetail catlst = new GeofenceDetail();
				catlst.setID(c.getString(c.getColumnIndex(KEY_ID)));
				catlst.setLatitude(Lati);
				catlst.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
				catlst.setLongitude(Longi);
				catlst.setRadius(Radius);
				catlst.setExpirationDuration(c.getLong(c.getColumnIndex(KEY_EX_TIME)));
				catlst.setTransitionType(c.getInt(c.getColumnIndex(KEY_TIME)));
				catplclist.add(catlst);

			} 
			while (c.moveToNext());
		}
		Log.e("catplclist size", catplclist.size()+"");
		db.close();
		c.close();
		return catplclist;
	}

	public float distance(double latitude1,double longitude1 , double latitude2,double longitude2  )
	{
		DecimalFormat df = new DecimalFormat("##.######");
		try 
		{
			double Val = 3959 * Math.acos( Math.cos( Math.toRadians(latitude2) ) * Math.cos( Math.toRadians( latitude1 ) ) * 
					Math.cos( Math.toRadians( longitude1 ) - Math.toRadians(longitude2) ) + Math.sin( Math.toRadians(latitude2) ) 
					* Math.sin( Math.toRadians( latitude1))) * 5280;
			df.format(Val);
			return (float) Val;
		} 
		catch (Exception e) 
		{
			System.out.println(e.toString());
			return 0;
		}
	}
}