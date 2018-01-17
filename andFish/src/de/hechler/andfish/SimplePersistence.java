/*******************************************************************************
 * Copyright (c) 2009 Ferenc Hechler - ferenc_hechler@users.sourceforge.net
 * 
 * This file is part of AndroFish
 *
 * AndroFish is free software;
 *  
 *******************************************************************************/
package de.hechler.andfish;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SimplePersistence {

	private final static String TAG = "ANDMAZ.persist";
	
	private String storeName;
	private SharedPreferences sharedPrefs;
	SharedPreferences.Editor editor; 
	
	public SimplePersistence(Context ctx, String storeName) {
		this.storeName = storeName;
		this.sharedPrefs = ctx.getSharedPreferences(this.storeName, Context.MODE_PRIVATE);
		this.editor = null;
	}
	
	public String getString(String key, String defaultValue) {
		String value=sharedPrefs.getString(key, null);
		if (value== null)
			return defaultValue;
		return value;
	}
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean result = defaultValue;
		try {
			result = sharedPrefs.getBoolean(key, defaultValue);
		}
		catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
		return result;
	}
	public float getFloat(String key, float defaultValue) {
		float result = defaultValue;
		try {
			result = sharedPrefs.getFloat(key, defaultValue);
		}
		catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
		return result;
	}
	public int getInt(String key, int defaultValue) {
		int result = defaultValue;
		try {
			result = sharedPrefs.getInt(key, defaultValue);
		}
		catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
		return result;
	}
	public long getLong(String key, long defaultValue) {
		long result = defaultValue;
		try {
			result = sharedPrefs.getLong(key, defaultValue);
		}
		catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
		return result;
	}

	public void remove(String key) {
    	getEditor().remove(key);
	}
	
	public void putString(String key, String value) {
        if (value == null)
        	getEditor().remove(key);
        else
        	getEditor().putString(key, value);
	}
	public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value);
	}
	public void putFloat(String key, float value) {
        getEditor().putFloat(key, value);
	}
	public void putInt(String key, int value) {
        getEditor().putInt(key, value);
	}
	public void putLong(String key, long value) {
        getEditor().putLong(key, value);
	}
	
	public void commit() {
		Editor edt = editor;
		if (edt != null) {
			synchronized (edt) {
				edt.commit();
				if (edt == editor) {
					editor = null;
				}
			}
		}
	}

	private Editor getEditor() {
		Editor result = editor;
		if (result == null) {
			synchronized (this) {
				if (editor == null)
					editor = sharedPrefs.edit();
				result = editor;
			}
		}
		return result;
	}

}
