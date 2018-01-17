/*******************************************************************************
 * Copyright (c) 2009 Ferenc Hechler - ferenc_hechler@users.sourceforge.net
 * 
 * This file is part of AndroFish
 *
 * AndroFish is free software;
 *  
 *******************************************************************************/
package de.hechler.andfish;


import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class AndroFishHighscoreActivity extends Activity {

	
    static final int MAX_HIGHSCORE_ENTRIES = 10;

	private final static int MENU_CLEAR_HIGHSCORE  = 1;
	
	private TableLayout mHighscoreTable;
	TextView tvTitle;
	Button btOK;
	private String mLevelName;
	
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mLevelName = getIntent().getStringExtra(AndroFishMainActivity.INTENT_EXTRA_LEVEL_NAME);
		if (mLevelName == null)
			mLevelName = "easy";

		// create view from xml
        setContentView(R.layout.highscore);

        SimplePersistence persist = new SimplePersistence(this, "Scores-"+mLevelName);
//        String lastName = persist.getString("lastname", "");
        Highscore highscore = new Highscore(MAX_HIGHSCORE_ENTRIES);
        try {
			String hs = persist.getString("highscore", "");
			highscore.fromJSON(hs);
		} catch (JSONException ignore) {}

        // find buttons in view
        btOK = ((Button) findViewById(R.id.btOk));
        tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText("Highscore "+mLevelName);
        mHighscoreTable = ((TableLayout) findViewById(R.id.table));

        setHighscoreText(highscore);

        // set actions for buttons
        btOK.setOnClickListener(OkListener);
    }

	private void setHighscoreText(Highscore highscore) {
        for (int i = 0; i < highscore.getNumEntries(); i++) {
        	TableRow row = (TableRow)mHighscoreTable.getChildAt(i+1);
        	TextView tvName = (TextView)row.getChildAt(1);
        	tvName.setText(highscore.getEntry(i).name);
        	TextView tvLevel = (TextView)row.getChildAt(2);
        	tvLevel.setText(Integer.toString(highscore.getEntry(i).level));
        	TextView tvScore = (TextView)row.getChildAt(3);
        	tvScore.setText(Integer.toString(highscore.getEntry(i).score));
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_CLEAR_HIGHSCORE,  Menu.NONE, "Clear Highscore");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == MENU_CLEAR_HIGHSCORE) {
            SimplePersistence persist = new SimplePersistence(this, "Scores-"+mLevelName);
            persist.remove("highscore");
            persist.commit();
            setHighscoreText(new Highscore(MAX_HIGHSCORE_ENTRIES));
    	}
        return true;
    }
    
    
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener OkListener = new OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
	
}
