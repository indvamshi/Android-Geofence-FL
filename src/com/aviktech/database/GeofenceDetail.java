package com.aviktech.database;

public class GeofenceDetail 
{
	private String Id				= "";
	private String Message			= "";
	private double Latitude			= 0;
	private double Longitude		= 0;
	private float Radius			= 0;
	private long ExpirationDuration= 0;
	private int TransitionType		= 0;
	
	public String getID()
	{
		return Id;
	}
	
	public void setID(String ID) 
	{
		this.Id = ID;
	}
	
	public double getLatitude() {
		return Latitude;
	}
	
	public void setLatitude(double Latitude) 
	{
		this.Latitude = Latitude;
	}
	
	public double getLongitudee() {
		return Longitude;
	}
	
	public void setLongitude(double Longitude) 
	{
		this.Longitude = Longitude;
	}

	public float getRadius() {
		return Radius;
	}

	public void setRadius(float radius) {
		Radius = radius;
	}

	/**
	 * @return the mExpirationDuration
	 */
	public long getExpirationDuration() {
		return ExpirationDuration;
	}

	/**
	 * @param mExpirationDuration the mExpirationDuration to set
	 */
	public void setExpirationDuration(long ExpirationDuration) {
		this.ExpirationDuration = ExpirationDuration;
	}

	/**
	 * @return the transitionType
	 */
	public int getTransitionType() {
		return TransitionType;
	}

	/**
	 * @param transitionType the transitionType to set
	 */
	public void setTransitionType(int transitionType) {
		TransitionType = transitionType;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		Message = message;
	}
}
