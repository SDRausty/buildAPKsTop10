/*******************************************************************************
 * Copyright (c) 2009 Ferenc Hechler - ferenc_hechler@users.sourceforge.net
 * 
 * This file is part of AndroFish
 *
 * AndroFish is free software;
 *  
 *******************************************************************************/
package de.hechler.andfish;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class AndroFishHelpActivity extends Activity {

	Button btBack;
	Button btReleaseNotes;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// create view from xml
        setContentView(R.layout.help);
    }
	
}
