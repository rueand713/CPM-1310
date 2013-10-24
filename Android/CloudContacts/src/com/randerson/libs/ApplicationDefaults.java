package com.randerson.libs;

import android.content.Context;
import android.content.SharedPreferences;

public final class ApplicationDefaults {
	
	SharedPreferences preferences = null;
	
	// shared preferences constructor for single pref file
	public ApplicationDefaults(Context context) {
		
		preferences = context.getSharedPreferences("defaults", 0);
	}
	
	// shared preferences constructor for multiple pref files
	public ApplicationDefaults(Context context, String Filename)
	{
		preferences = context.getSharedPreferences(Filename, 0);
	}
	
	// methods for setting of the primitive data ***********
	public void setBool(String key, boolean value)
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(key, value);
		editor.apply();
	}
	
	public void setInt(String key, int value)
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putInt(key, value);
		editor.apply();
	}
	
	public void setFloat(String key, float value)
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putFloat(key, value);
		editor.apply();
	}
	
	public void setLong(String key, long value)
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putLong(key, value);
		editor.apply();
	}
	
	public void setString(String key, String value)
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putString(key, value);
		editor.apply();
	}
	
	// ****************************** end setting methods
	
	// creates and returns the preferences editor object
	private SharedPreferences.Editor getEditor()
	{
		return preferences.edit();
	}
	
	// method returns the preferences data for getting of the primitive types stored
	public SharedPreferences getData()
	{
		return preferences;
	}
}
