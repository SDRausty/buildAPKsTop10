/*******************************************************************************
 * Copyright (c) 2009 Ferenc Hechler - ferenc_hechler@users.sourceforge.net
 * 
 * This file is part of AndroFish
 *
 * AndroFish is free software;
 *  
 *******************************************************************************/
package de.hechler.andfish;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Highscore {

	public class Entry {
		public int score;
		public int level;
		public String name;
		public Entry() {
			this(0,0,"");
		}
		public Entry(int score, int level, String name) {
			this.score = score;
			this.level = level;
			this.name = name;
		}
		public JSONObject toJSONObject() throws JSONException {
			JSONObject jo = new JSONObject();
			jo.put("score", score);
			jo.put("level", level);
			jo.put("name", name);
			return jo;
		}
		public Entry fromJSON(JSONObject jo) throws JSONException {
			score = jo.optInt("score");
			level = jo.optInt("level");
			name= jo.optString("name");
			return this;
		}
	}

	private int mMaxSize;
	private List<Entry> entries;

	public Highscore(int maxSize) {
		mMaxSize = maxSize;
		entries = new ArrayList<Entry>();
	}
	
	public String toJSON(int indent) throws JSONException {
		JSONArray ja = new JSONArray();
		for (Entry entry:entries) {
			JSONObject jo = entry.toJSONObject();
			ja.put(jo);
		}
		return ja.toString(indent);
	}

	public void fromJSON(String jsonText) throws JSONException {
		if (jsonText == null)
			return;
		JSONArray ja = new JSONArray(jsonText);
		entries.clear();
		for (int i=0; i<ja.length(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			entries.add(new Entry().fromJSON(jo));
		}
	}

	public int getNumEntries() {
		return entries.size();
	}

	public Entry getEntry(int i) {
		if ((i>=0) && (i<getNumEntries()))
				return entries.get(i);
		return new Entry();
	}

	public void setEntry(int i, String name, int level, int score) {
		while (entries.size() <= i)
			entries.add(new Entry());
		entries.get(i).name = name;
		entries.get(i).level = level;
		entries.get(i).score = score;
	}

	public int insertEntry(String name, int level, int score) {
		int size = getNumEntries();
		for (int i=0;i<size;i++) {
			if (entries.get(i).score < score) {
				entries.add(i, new Entry(score, level, name));
				return i;
			}
		}
		if (size >= mMaxSize) {
			return -1;
		}
		entries.add(new Entry(score, level, name));
		return size;
	}

	
	
}
