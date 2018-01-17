package de.hechler.andfish;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import de.hechler.andfish.Highscore.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidFishEatingFish extends Activity {
	

	final static int STATE_STOP = 0;
	final static int STATE_RUN  = 1;

	final static int DIR_LEFT  = 0;
	final static int DIR_RIGHT = 1;

	final static int SIZE_1 = 0;
	final static int SIZE_2 = 1;
	final static int SIZE_3 = 2;
	final static int SIZE_4 = 3;
	final static int SIZE_5 = 4;
	
	private final static int[] SPEED      = {400, 400, 300, 200, 200};
	private final static int[] FISHWIDTH  = {11, 18, 23, 35, 44};
	private final static int[] FISHHEIGHT = { 8, 15, 20, 25, 30};

	private final static int MAX_FOOD = 10000;

	private final static int[][] rndSizes = {
		{0,0,0,1,1,1,2,2,2,3},
		{0,0,0,1,1,1,2,2,3,3},
		{0,0,1,1,2,2,2,3,3,4},
		{0,0,1,1,2,2,3,3,4,4},
		{0,1,1,2,2,3,3,4,4,4},
		{0,1,2,2,3,3,4,4,4,4},
		{0,1,2,3,3,4,4,4,4,4},
		{0,1,2,3,4,4,4,4,4,4},
	};

	private boolean mShowFrames = false;
	

    static class VisibleObject {
    	VisibleObject(int posX, int posY, int dir, int size, int animStart) {
    		this.posX  = posX;
    		this.posY  = posY;
    		this.dir   = dir;
    		this.size  = size;
    		this.animStart = animStart;
    	}

        VisibleObject(Bundle b) {
            posX = b.getInt("posX");
            posY = b.getInt("posY");
            dir = b.getInt("dir");
            size = b.getInt("size");
            animStart = b.getInt("animStart");
        }

        Bundle onSaveInstanceState() {
            Bundle b = new Bundle(6);
            b.putInt("posX", posX);
            b.putInt("posY", posY);
            b.putInt("dir", dir);
            b.putInt("size", size);
            b.putInt("animStart", animStart);
            return b;
        }

    	int posX;
    	int posY;
    	int dir;
    	int size;
    	int animStart;
    }

    static class Fish extends VisibleObject{
    	Fish(int posX, int posY, int dir, int size, int animStart) {
    		super(posX, posY, dir, size, animStart);
    	}

        Fish(Bundle b) {
            super(b);
        }
    }

    
    
    private final static int GOODIE_NONE = 0;
    private final static int GOODIE_BIGMOUTH = 1;
    
    static class Goodie extends VisibleObject {
    	Goodie(int posX, int posY, int goodietype, int animStart) {
    		super(posX, posY, DIR_RIGHT, SIZE_3, animStart);
    		this.goodietype = goodietype;
    	}

        Goodie(Bundle b) {
            super(b);
            goodietype = b.getInt("goodietype");
        }

        Bundle onSaveInstanceState() {
            Bundle b = super.onSaveInstanceState();
            b.putInt("goodietype", goodietype);
            return b;
        }

    	int goodietype;
    }

    private Bitmap[] mBackground;
    private final static int[] mBackgroundId = {
    	R.drawable.fischback02
    };
    
    private Bitmap[] mComputerFish;
   	private final static int[] mComputerFishId = {
        R.drawable.comp1l,
        R.drawable.comp1r,
        R.drawable.comp2l,
        R.drawable.comp2r,
        R.drawable.comp3l,
        R.drawable.comp3r,
        R.drawable.comp4la,
        R.drawable.comp4lb,
        R.drawable.comp4lc,
        R.drawable.comp4ra,
        R.drawable.comp4rb,
        R.drawable.comp4rc,
        R.drawable.comp5la,
        R.drawable.comp5lb,
        R.drawable.comp5lc,
        R.drawable.comp5ra,
        R.drawable.comp5rb,
        R.drawable.comp5rc,
    };

    private Bitmap[] mPlayerFish;
   	private final static int[] mPlayerFishId = {
        R.drawable.player1l,
        R.drawable.player1r,
        R.drawable.player2l,
        R.drawable.player2r,
        R.drawable.player3l,
        R.drawable.player3r,
        R.drawable.player4l,
        R.drawable.player4r,
        R.drawable.player5l,
        R.drawable.player5r,
    };
   	
    private Bitmap[] mFishbone;
    private final static int[] mFishBoneId = {
        R.drawable.fishbone1l,
        R.drawable.fishbone1r,
        R.drawable.fishbone2l,
        R.drawable.fishbone2r,
        R.drawable.fishbone3l,
        R.drawable.fishbone3r,
        R.drawable.fishbone4l,
        R.drawable.fishbone4r,
        R.drawable.fishbone5l,
        R.drawable.fishbone5r,
    };
	
    private Bitmap[] mIndicator;
    private final static int[] mIndicatorId = {
        R.drawable.indicator,
        R.drawable.indicator_full,
    };

    private Bitmap[] mTargetMarker;
   	private final static int[] mTargetMarkerId = {
        R.drawable.target1,
        R.drawable.target2,
        R.drawable.target3,
        R.drawable.target4,
    };
   	
    private Bitmap[] mMouth;
   	private final static int[] mMouthId = {
        R.drawable.mouth1l,
        R.drawable.mouth2l,
        R.drawable.mouth3l,
        R.drawable.mouth4l,
        R.drawable.mouth3l,
        R.drawable.mouth2l,
        R.drawable.mouth1r,
        R.drawable.mouth2r,
        R.drawable.mouth3r,
        R.drawable.mouth4r,
        R.drawable.mouth3r,
        R.drawable.mouth2r,
    };
   	
    private Bitmap[] mBubble;
   	private final static int[] mBubbleId = {
        R.drawable.bubble,
    };
   	
    
    private void initBitmaps() {
    	if (mBackground == null) {
    		mBackground   = loadBitmapResources(mBackgroundId);
    		mComputerFish = loadBitmapResources(mComputerFishId);
    		mPlayerFish   = loadBitmapResources(mPlayerFishId);
    		mFishbone     = loadBitmapResources(mFishBoneId);
    		mIndicator    = loadBitmapResources(mIndicatorId);
    		mTargetMarker = loadBitmapResources(mTargetMarkerId);
    		mMouth        = loadBitmapResources(mMouthId);
    		mBubble       = loadBitmapResources(mBubbleId);
    	}
    }

    private void recycleBitmaps() {
        recycleBitmapResources(mBackground);
        recycleBitmapResources(mComputerFish);
        recycleBitmapResources(mPlayerFish);
        recycleBitmapResources(mFishbone);
        recycleBitmapResources(mIndicator);
        recycleBitmapResources(mTargetMarker);
        recycleBitmapResources(mMouth);
        recycleBitmapResources(mBubble);
        mBackground   = null;
        mComputerFish = null;
        mPlayerFish   = null;
        mFishbone     = null;
        mIndicator    = null;
        mTargetMarker = null;
        mMouth        = null;
        mBubble       = null;
   }

	private Bitmap[] loadBitmapResources(int[] resIDs) {
		Bitmap[] result = new Bitmap[resIDs.length];
		for (int i=0;i<resIDs.length;i++) {
			result[i] = BitmapFactory.decodeResource(getResources(), resIDs[i]);
		}
		return result;
	}

    private void recycleBitmapResources(Bitmap[] bmaps) {
        for (Bitmap b : bmaps)
            b.recycle();
    }

	private final static int MSG_PLAY_SOUND = 1;
	private final static int MSG_PLAY_MUSIC = 2;
	
    class SoundHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		switch (msg.what) {
    		case(MSG_PLAY_SOUND): {
    			int resId = msg.arg1;
    			playSound(resId);
    			break;
    		}
    		case(MSG_PLAY_MUSIC): {
    			startNextMusic();
    			break;
    		}
    		}
    	}
    	public void playDingSound() {
    		Message msg = Message.obtain(this, MSG_PLAY_SOUND, R.raw.ding, 0);
    		sendMessage(msg);
    	}
    	public void playEatenSound() {
    		Message msg = Message.obtain(this, MSG_PLAY_SOUND, R.raw.eaten, 0);
    		sendMessage(msg);
    	}
    	public void startMusic() {
    		Message msg = Message.obtain(this, MSG_PLAY_MUSIC);
    		sendMessage(msg);
    	}
    }    
	private SoundHandler soundHandler;
	
	private GraphView mGraphView;
    private String mLevelName = "";
    private boolean mPlayMusic;
    private boolean mPlaySound;
    
    private SimplePersistence persist;
    public Highscore highscore;
    private String mLastName = "";
    private int mHSPosition;
    
    private MediaPlayer mMPMusic; 
    private int mPlayList = 0;
    private int[] mSongs = {R.raw.sdbounce1, R.raw.forgan1};
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // thread for handling sound
        soundHandler = new SoundHandler();

        if (savedInstanceState != null &&
            savedInstanceState.containsKey("mShowFrames")) {
            mShowFrames = savedInstanceState.getBoolean("mShowFrames");
            mLevelName = savedInstanceState.getString("mLevelName");
            mPlayMusic = savedInstanceState.getBoolean("mPlayMusic");
            mPlaySound = savedInstanceState.getBoolean("mPlaySound");
            mPlayList = savedInstanceState.getInt("mPlayList");
        } else {
            mLevelName = getIntent().getStringExtra(AndroFishMainActivity.INTENT_EXTRA_LEVEL_NAME);
            mPlayMusic = getIntent().getBooleanExtra(AndroFishMainActivity.INTENT_EXTRA_PLAY_MUSIC, AndroFishMainActivity.DEFAULT_PLAY_MUSIC_VALUE);
            mPlaySound = getIntent().getBooleanExtra(AndroFishMainActivity.INTENT_EXTRA_PLAY_SOUND, AndroFishMainActivity.DEFAULT_PLAY_SOUND_VALUE);
        }
		if (mLevelName == null)
			mLevelName = "easy";
        initBitmaps();
        mGraphView = new GraphView(this);
        mGraphView.setId(123456789);
        mGraphView.setSaveEnabled(true);
		mGraphView.setLevel(mLevelName);
        setContentView(mGraphView);
		mGraphView.runProgram();
		readScores();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mShowFrames", mShowFrames);
        outState.putString("mLevelName", mLevelName);
        outState.putBoolean("mPlayMusic", mPlayMusic);
        outState.putBoolean("mPlaySound", mPlaySound);
        outState.putInt("mPlayList", mPlayList);
    }

	@Override
	protected void onResume() {
		super.onResume();
        soundHandler.startMusic();
	}

	@Override
	protected void onPause() {
		super.onPause();
        mGraphView.pauseGame();
		stopMusic();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleBitmaps();
    }

	private OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			soundHandler.startMusic();
		}
	};

    private OnCompletionListener soundCompletionListener =
        new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        };
 
	private void startNextMusic() {
		if (!mPlayMusic) {
			return;
		}
        if (mMPMusic != null)
            mMPMusic.release();
		mMPMusic = MediaPlayer.create(this, mSongs[mPlayList]);
		mPlayList = (mPlayList + 1) % mSongs.length;
		if (mMPMusic == null) {
			return;
		}
		mMPMusic.setOnCompletionListener(completionListener);
		mMPMusic.start();
	}

	private void stopMusic() {
		if (!mPlayMusic) {
			return;
		}
		if (mMPMusic != null) {
			mMPMusic.stop();
            mMPMusic.release();
            mMPMusic = null;
		}
	}


	private void playSound(int resID) {
		if (!mPlaySound) {
			return;
		}
		MediaPlayer mp = MediaPlayer.create(this, resID);
		if (mp != null) {
            mp.setOnCompletionListener(soundCompletionListener);
			mp.start();
		}
	}


	private void playDing() {
		soundHandler.playDingSound();
	}

	private void playEaten() {
		soundHandler.playEatenSound();
	}

	
	private void readScores() {
        persist = new SimplePersistence(this, "Scores-"+mLevelName);
        mLastName = persist.getString("lastname", mLastName);
    	highscore = new Highscore(AndroFishHighscoreActivity.MAX_HIGHSCORE_ENTRIES);
        try {
			String hs = persist.getString("highscore", "");
			highscore.fromJSON(hs);
		} catch (JSONException ignore) {}
	}

    private void writeScores() {
        persist = new SimplePersistence(this, "Scores-"+mLevelName);
        persist.putString("lastname", mLastName);
        try {
			String hs = highscore.toJSON(0);
			persist.putString("highscore", hs);
		} catch (JSONException ignore) {}
        persist.commit();
	}
    
    private final static int MENU_EXIT      = 1;
    private final static int MENU_RESTART   = 2;
    private final static int MENU_FRAMES    = 3;
    private final static int MENU_SPEEDUP   = 4;
    private final static int MENU_SPEEDDOWN = 5;
    	
    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_SPEEDDOWN, Menu.NONE, "Slower");
        menu.add(Menu.NONE, MENU_SPEEDUP,   Menu.NONE, "Faster");
        menu.add(Menu.NONE, MENU_FRAMES,    Menu.NONE, "Show Frames");
        menu.add(Menu.NONE, MENU_RESTART,   Menu.NONE, "Restart");
        menu.add(Menu.NONE, MENU_EXIT,      Menu.NONE, "Exit");
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        mGraphView.pauseGame();
    	return super.onMenuOpened(featureId, menu);
    }

    // not supported before 2.0
//    @Override
//    public void onBackPressed() {
//    	if (mGraphView.mMode != MODE_PAUSE) {
//    		mGraphView.pauseGame();
//    	}
//    	else {
//    		super.onBackPressed();
//    	}
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	if (mGraphView.mMode != MODE_PAUSE) {
	    		mGraphView.pauseGame();
	            return true;
	    	}
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	super.onOptionsMenuClosed(menu);
    	mGraphView.continueGame();
    }
    
    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == MENU_EXIT) {
    		mGraphView.mState = STATE_STOP;
    		finish();
    	}
    	else if (item.getItemId() == MENU_RESTART) {
    		mGraphView.init();
    	}
    	else if (item.getItemId() == MENU_FRAMES) {
    		mShowFrames = !mShowFrames;
    	}
    	else if (item.getItemId() == MENU_SPEEDUP) {
    		mGraphView.mSpeedFactor *= 1.5f;
    	}
    	else if (item.getItemId() == MENU_SPEEDDOWN) {
    		mGraphView.mSpeedFactor /= 1.5f;
    	}
        return true;
    }
    
    private final static int MODE_PAUSE = 0;
    private final static int MODE_PLAY  = 1;
    private final static int MODE_EATEN = 2;
    private final static int MODE_GAMEOVER = 3;
    
    

	private class GraphView extends View {
		
		private Fish   playerFish;
		private List<Fish> computerFishs;
		private List<Goodie> goodies;
		private int mMode;
		private int mOldMode;
		    
		private int mState;
		private int mSpeed;
		
    	private int mCount;
    	private int mAnimCount;
    	
    	private int mFood;
    	private int mLevel;
    	
    	int maxWidth  = 0;
    	int maxHeight = 0;
    	int mDensity = 160;
    	float mDensityFactor = 160;
    	
		Random rnd;
		
		float mSpeedFactor = 1.0f;
		float mLevelSpeed = 1.0f;
		
		int mScore = 0;
		
		int mActiveGoodie;
		int mActiveGoodieTime;
    	
    	public GraphView(Context context) {
            super(context);
            init();
        }


        @Override
        public Parcelable onSaveInstanceState() {
            Bundle b = new Bundle(17);
            b.putParcelable("superState", super.onSaveInstanceState());
            b.putBundle("playerFish", playerFish.onSaveInstanceState());
            ArrayList<Parcelable> fishParcels =
                new ArrayList<Parcelable>(computerFishs.size());
            for (Fish fish : computerFishs)
                fishParcels.add(fish.onSaveInstanceState());
            b.putParcelableArrayList("computerFishs", fishParcels);
            ArrayList<Parcelable> goodieParcels =
                new ArrayList<Parcelable>(goodies.size());
            for (Goodie goodie : goodies)
                goodieParcels.add(goodie.onSaveInstanceState());
            b.putParcelableArrayList("goodies", goodieParcels);
            b.putInt("mMode", mMode);
            b.putInt("mOldMode", mOldMode);
            b.putInt("mState", mState);
            b.putInt("mSpeed", mSpeed);
            b.putInt("mCount", mCount);
            b.putInt("mAnimCount", mAnimCount);
            b.putInt("mFood", mFood);
            b.putInt("mLevel", mLevel);
            b.putFloat("mSpeedFactor", mSpeedFactor);
            b.putFloat("mLevelSpeed", mLevelSpeed);
            b.putInt("mScore", mScore);
            b.putInt("mActiveGoodie", mActiveGoodie);
            b.putInt("mActiveGoodieTime", mActiveGoodieTime);
            return b;
        }

        @Override
        public void onRestoreInstanceState(Parcelable p) {
            Bundle b = (Bundle) p;

            super.onRestoreInstanceState(b.getParcelable("superState"));

            playerFish = new Fish(b.getBundle("playerFish"));

            ArrayList<Parcelable> fishParcels = b.getParcelableArrayList("computerFishs");
            computerFishs.clear();
            for (Parcelable fish : fishParcels)
                computerFishs.add(new Fish((Bundle)fish));

            ArrayList<Parcelable> goodieParcels = b.getParcelableArrayList("goodies");
            goodies.clear();
            for (Parcelable goodie : goodieParcels)
                goodies.add(new Goodie((Bundle)goodie));

            mMode = b.getInt("mMode");
            mOldMode = b.getInt("mOldMode");
            mState = b.getInt("mState");
            mSpeed = b.getInt("mSpeed");
            mCount = b.getInt("mCount");
            mAnimCount = b.getInt("mAnimCount");
            mFood = b.getInt("mFood");
            mLevel = b.getInt("mLevel");
            mSpeedFactor = b.getFloat("mSpeedFactor");
            mLevelSpeed = b.getFloat("mLevelSpeed");
            mScore = b.getInt("mScore");
            mActiveGoodie = b.getInt("mActiveGoodie");
            mActiveGoodieTime = b.getInt("mActiveGoodieTime");
        }


        public void pauseGame() {
			if (mMode != MODE_PAUSE) {
		        mOldMode = mMode; 
		        mMode = MODE_PAUSE;
	        }
		}

		public void continueGame() {
			if (mMode == MODE_PAUSE) {
		        mMode = mOldMode;
	        }
		}


		private void init() {
			maxWidth = 0;
			maxHeight = 0;
			playerFish = new Fish(0, 0, DIR_RIGHT, SIZE_3, mAnimCount);
			clearWaypoints();
			computerFishs = new ArrayList<Fish>();
			goodies = new ArrayList<Goodie>();
	    	mCount = 0;
	    	mSpeed = 50;
			mState = STATE_RUN;
			rnd = new Random(System.currentTimeMillis());
			mMode = MODE_PLAY;
			mFood = MAX_FOOD/2;
			mLevel = 1;
			mScore = 0;
			mActiveGoodie = GOODIE_NONE;
			mActiveGoodieTime = 0;
		}
        
		private void setLevel(String levelName) {
			if (levelName.equals("hard")) {
				mLevelSpeed = 1.0f;
			}
			else if (levelName.equals("medium")) {
				mLevelSpeed = 1.5f;
			}
			else if (levelName.equals("easy")) {
				mLevelSpeed = 2.0f;
			}
		}

        public void doStep() {
        	if (maxHeight == 0)
        		return;
        	if (mMode == MODE_PAUSE) {
        		return;
        	}
        	mCount += 1;
        	if (mCount%500==0) {
        		mLevel += 1;
        	}
        	timeoutActiveGoodie();
        	boolean playerFishHasMoved = movePlayerFish();
        	addNewComputerFish();
        	moveComputerFish();
        	removeInvisibleComputerFishs();
        	addNewGoodie();
        	moveGoodie();
        	removeInvisibleGoodies();
        	handleCollision(checkHit());
        	eatFood(playerFishHasMoved);
		}


		private void timeoutActiveGoodie() {
			if (mActiveGoodie == GOODIE_NONE) {
				return;
			}
			mActiveGoodieTime -= 1;
			if (mActiveGoodieTime <= 0) {
				mActiveGoodie = GOODIE_NONE;
			}
			
		}


		private void eatFood(boolean playerFishHasMoved) {
			if (mMode != MODE_PLAY) {
				return;
			}
        	int hunger = 2*playerFish.size+mLevel;
        	if (playerFish.size == SIZE_5) {
        		hunger += 30;
        	}
        	if (!playerFishHasMoved) {
        		hunger = hunger / 3;
        	}
        	mFood -= hunger;
        	if (mFood <= 0) {
        		shrinkPlayerFish();
        	}
		}


		private void handleCollision(VisibleObject collisionObject) {
			if (collisionObject == null) {
				return;
			}
			if (collisionObject instanceof Fish) {
				Fish collisionFish = (Fish) collisionObject;
				if ((mActiveGoodie == GOODIE_BIGMOUTH) || (collisionFish.size < playerFish.size)) {
					playDing();
					mScore += collisionFish.size+1;
					mFood = mFood + MAX_FOOD/8;
					if (mFood >= MAX_FOOD) {
						growPlayerFish();
					}
					computerFishs.remove(collisionFish);
				}
				else if (collisionFish.size == playerFish.size) {
					return;
				}
				else {
					mMode = MODE_EATEN;
					playEaten();
					clearWaypoints();
					addWaypoint(playerFish.posX/100, (int)(mDensityFactor*FISHHEIGHT[playerFish.size]/2.0f));
					checkFinished();
				}
			}
			else {
				Goodie goodie = (Goodie)collisionObject;
				if (goodie.goodietype == GOODIE_BIGMOUTH) {
					mActiveGoodie = GOODIE_BIGMOUTH;
					mActiveGoodieTime = 200;
				}
				goodies.remove(goodie);
			}
		}


		private VisibleObject checkHit() {
			if (mMode != MODE_PLAY) {
				return null;
			}
			for (Goodie goodie:goodies) {
				if (hits(playerFish, goodie)) {
					return goodie;
				}
			}
			for (Fish computerFish:computerFishs) {
				if (hits(playerFish, computerFish)) {
					return computerFish;
				}
			}
			return null;
		}
		

		private boolean hits(Fish fish, VisibleObject visibleObject) {
			int left1 = (int)displayPosX(fish);
			int top1 = (int)displayPosY(fish);
			int width1 = (int)(mDensityFactor*FISHWIDTH[fish.size]);
			int height1 = (int)(mDensityFactor*FISHHEIGHT[fish.size]);
			int delta1 = height1*2/10;
			Rect r1 = new Rect(left1+delta1, top1+delta1, left1+width1-delta1, top1+height1-delta1);
			int left2 = (int)displayPosX(visibleObject);
			int top2 = (int)displayPosY(visibleObject);
			int width2 = (int)(mDensityFactor*FISHWIDTH[visibleObject.size]);
			int height2 = (int)(mDensityFactor*FISHHEIGHT[visibleObject.size]);
			int delta2 = height2*2/10;
			Rect r2 = new Rect(left2+delta2, top2+delta2, left2+width2-delta2, top2+height2-delta2);
			if (r1.intersect(r2)) {
				return true;
			}
			return false;
		}


		private void moveComputerFish() {
			float computerSpeedFactor = mSpeedFactor*mDensityFactor*((10.0f+Math.min(10.0f,mLevel))/20.0f);
			for (Fish computerFish:computerFishs) {
				if (computerFish.dir == DIR_RIGHT) {
					computerFish.posX += computerSpeedFactor*SPEED[computerFish.size];
				}
				else {
					computerFish.posX -= computerSpeedFactor*SPEED[computerFish.size];
				}
			}
		}

		private void removeInvisibleComputerFishs() {
			List<Fish> visibleFishes = new ArrayList<Fish>(MAX_COMPUTERFISHS);
			for (Fish computerFish:computerFishs) {
				if ((computerFish.posX >=0) && (computerFish.posX<=maxWidth*100)) {
					visibleFishes.add(computerFish);
				}
			}
			computerFishs = visibleFishes;
		}

		/**
		 * 
		 * @return true if the playerfish has moved
		 */
		private boolean movePlayerFish() {
			if ((mTouchedWaypoints == null) || (mTouchedWaypoints.size()==0)) {
				return false;
			}
			float playerSpeedFactor = mLevelSpeed*mSpeedFactor*mDensityFactor*((20-Math.min(10,mLevel))/10);
    		float speed = playerSpeedFactor*SPEED[playerFish.size];
    		Point lastPoint = mTouchedWaypoints.get(mTouchedWaypoints.size()-1);
			removeReachedWaypoints(speed);
			Point target;
			if ((mTouchedWaypoints == null) || (mTouchedWaypoints.size()==0)) {
				target = lastPoint;
	        	if (mMode == MODE_EATEN) {
	        		mMode = MODE_PLAY;
	        		shrinkPlayerFish();
	        	}
			}
			else {
				target = mTouchedWaypoints.get(0);
			}
			int playerTargetX = 100*target.x;
			int playerTargetY = 100*target.y;
			float dirX = playerTargetX-playerFish.posX;
        	float dirY = playerTargetY-playerFish.posY;
        	double dist = Math.sqrt(dirX*dirX+dirY*dirY);
        	if (dist > 0) {
        		if (dist < speed) {
    	        	playerFish.posX = playerTargetX;
    	        	playerFish.posY = playerTargetY;
        		}
        		else {
        			playerFish.posX += dirX/dist*speed;
        			playerFish.posY += dirY/dist*speed;
        		}
	        	if (dirX > 0)
	        		playerFish.dir = DIR_RIGHT;
	        	else if (dirX < 0)
	        		playerFish.dir = DIR_LEFT;
        	}
        	return true;
		}

        private void removeReachedWaypoints(float speed) {
        	while (mTouchedWaypoints.size() > 0) {
        		Point point = mTouchedWaypoints.get(0);
    			float dirX = 100*point.x-playerFish.posX;
            	float dirY = 100*point.y-playerFish.posY;
            	double dist = Math.sqrt(dirX*dirX+dirY*dirY);
            	if (dist > speed) {
            		break;
            	}
        		mTouchedWaypoints.remove(0);
        	}
		}


		private void shrinkPlayerFish() {
        	if (checkFinished()) {
        		return;
        	}
        	playerFish.size -= 1;
        	mFood = MAX_FOOD/2;
		}

        private void growPlayerFish() {
        	if (playerFish.size == 4) {
        		mFood = MAX_FOOD;
        		return;
        	}
        	playerFish.size += 1;
        	mFood = MAX_FOOD/2;
		}

		private final static int MAX_COMPUTERFISHS = 20;
        
		private void addNewComputerFish() {
			int possibility = MAX_COMPUTERFISHS - computerFishs.size();
			
			if (possibility <= 0) {
				return;
			}
			if ((rnd.nextInt(100) < possibility) || (computerFishs.size() == 0)) {
				Fish compFish;
				int size = randomSize();
				int height = (int)(mDensityFactor*FISHHEIGHT[size]);
				if (rnd.nextInt(2) == 0) {
					compFish = new Fish(0, rnd.nextInt((maxHeight-height)*100)+100*height/2, DIR_RIGHT, size, mAnimCount);
				}
				else {
					compFish = new Fish(maxWidth*100, rnd.nextInt((maxHeight-height)*100)+100*height/2, DIR_LEFT, size, mAnimCount);
				}
				computerFishs.add(compFish);
			}			
		}

		private final static int MAX_GOODIES = 2;
		private void addNewGoodie() {
			int possibility = MAX_GOODIES - goodies.size();
			
			if (possibility <= 0) {
				return;
			}
			if (rnd.nextInt(500) < possibility) {
				int type = GOODIE_BIGMOUTH;
				int x = rnd.nextInt(maxWidth);
				Goodie goodie = new Goodie(x*100, maxHeight*100, type, mAnimCount);
				goodies.add(goodie);
			}			
		}

		private void moveGoodie() {
			float goodieSpeedFactor = mSpeedFactor*mDensityFactor*((10.0f+Math.min(10.0f,mLevel))/20.0f);
			for (Goodie goodie:goodies) {
				goodie.posY -= goodieSpeedFactor*SPEED[SIZE_3];
			}
		}

		private void removeInvisibleGoodies() {
			List<Goodie> visibleGoodies = new ArrayList<Goodie>(MAX_GOODIES);
			for (Goodie goodie:goodies) {
				if (goodie.posY > 0) {
					visibleGoodies.add(goodie);
				}
			}
			goodies = visibleGoodies;
		}


		private int randomSize() {
			int index = rnd.nextInt(10);
			int[] distribution;
			if (mLevel>7) {
				distribution = rndSizes[3];
			}
			else {
				distribution = rndSizes[mLevel/2];
			}
			return distribution[index];
		}


		private List<Point> mTouchedWaypoints;
		@Override
        public boolean onTouchEvent(MotionEvent event) {
        	super.onTouchEvent(event);
        	if (event.getAction() == MotionEvent.ACTION_DOWN) {
        		int x = (int)event.getX();
        		int y= (int)event.getY();
        		if (mMode == MODE_PLAY) {
        			clearWaypoints();
        			addWaypoint(x,y);
	    			mGraphView.invalidate();
        		}
        	}
        	else if (event.getAction() == MotionEvent.ACTION_MOVE) {
        		int x = (int)event.getX();
        		int y= (int)event.getY();
        		if (mMode == MODE_PLAY) {
        			addWaypoint(x,y);
	    			mGraphView.invalidate();
        		}
        	}
        	else if (event.getAction() == MotionEvent.ACTION_UP) {
        		int x = (int)event.getX();
        		int y= (int)event.getY();
        		if (mMode == MODE_PLAY) {
        			addWaypoint(x,y);
        			finishWaypoints();
	    			mGraphView.invalidate();
        		}
        		else if (mMode == MODE_GAMEOVER) {
        			init();
	    			mGraphView.invalidate();
        		}
        		else if (mMode == MODE_PAUSE) {
	    			continueGame();
        		}
        	}
        	return true;
        }
        
		private void finishWaypoints() {
			// TODO Auto-generated method stub
			
		}


		private void addWaypoint(int x, int y) {
			mTouchedWaypoints.add(new Point(x,y));
		}


		private void clearWaypoints() {
			mTouchedWaypoints = new ArrayList<Point>();
		}


		@Override protected void onDraw(Canvas canvas) {
			adjustDimensions(canvas);
			canvas.drawColor(Color.WHITE);
			Paint paint = new Paint();
            paint.setStrokeWidth(0);
            paint.setColor(Color.BLACK);
			drawBackground(canvas, paint);
			drawTargetMarker(canvas, paint);
			drawFood(canvas, paint);
			drawComputerFishs(canvas, paint, SIZE_1, playerFish.size);
			drawPlayerFish(canvas, paint);
			drawComputerFishs(canvas, paint, playerFish.size+1, SIZE_5);
			drawGoodies(canvas, paint);
			drawFrames(canvas, paint);
			drawWaypoints(canvas, paint, mTouchedWaypoints);
        }


		private void drawFrames(Canvas canvas, Paint paint) {
			if (!mShowFrames) {
				return;
			}
			Paint p = new Paint(paint);
			p.setStyle(Style.STROKE);
			p.setColor(Color.WHITE);
			drawFrame(canvas, p, playerFish);
			for (Fish computerFish:computerFishs) {
				drawFrame(canvas, p, computerFish);
			}
		}

		private void drawWaypoints(Canvas canvas, Paint paint, List<Point> waypoints) {
			if ((waypoints == null) || (waypoints.size()==0)) {
				return;
			}
			Paint p = new Paint(paint);
			p.setStyle(Style.STROKE);
			p.setColor(Color.RED);
			p.setStrokeWidth(2.0f);
			Path path = new Path();
			Point startPoint = waypoints.get(0);
			path.moveTo(startPoint.x, startPoint.y);
			for (int i=1; i<waypoints.size()-1; i++) {
				Point point = waypoints.get(i);
				path.lineTo(point.x, point.y);
			}
			Point endPoint = waypoints.get(waypoints.size()-1);
			path.setLastPoint(endPoint.x, endPoint.y);
			canvas.drawPath(path, p);
		}


		private void drawFrame(Canvas canvas, Paint paint, Fish fish) {
			int w = (int)(mDensityFactor*FISHWIDTH[fish.size]);
			int h = (int)(mDensityFactor*FISHHEIGHT[fish.size]);
			int x = (int)displayPosX(fish);
			int y = (int)displayPosY(fish);
			int d = h*2/10;
			Rect r = new Rect(x+d,y+d,x+w-d,y+h-d);
			canvas.drawRect(r, paint);
		}


		private void adjustDimensions(Canvas canvas) {
			if ((maxWidth != getWidth()) || (maxHeight != getHeight())) {
				clearWaypoints();
				maxWidth = getWidth();
				maxHeight = getHeight();
				mDensity = canvas.getDensity();
				mDensityFactor = mDensity/160.0f;
				if ((playerFish.posX > 100*maxWidth) 
					|| (playerFish.posY > 100*maxHeight)
					|| (playerFish.posX + playerFish.posY == 0)) {
					playerFish.posX =100*maxWidth/2;
					playerFish.posY =100*maxWidth/2;
				}
			}
		}


		private void drawBackground(Canvas canvas, Paint paint) {

			Rect src = new Rect(0,0, mBackground[0].getWidth(),mBackground[0].getHeight());  
			Rect dest = new Rect(0,0, maxWidth, maxHeight);
			canvas.drawBitmap(mBackground[0], src, dest, paint);
		}

		private void drawPlayerFish(Canvas canvas, Paint paint) {
			int animStep = (mAnimCount % 15)/5;
			canvas.drawBitmap(playerFishBitmap(playerFish.size, playerFish.dir, animStep), displayPosX(playerFish), displayPosY(playerFish), paint);
			float xOff = 100f*mDensityFactor*(FISHWIDTH[playerFish.size]-15f)/2f;
			if (playerFish.dir == DIR_LEFT) {
				xOff = -xOff;
			}
			if (mActiveGoodie == GOODIE_BIGMOUTH) {
				if ((mActiveGoodieTime>20) || ((mActiveGoodieTime%2)==0)) {
					drawMouth(canvas, paint, playerFish.posX+xOff, playerFish.posY, playerFish.dir, playerFish.animStart);
				}
			}
		}
		
		
		private void drawMouth(Canvas canvas, Paint paint, float posX, float posY, int dir, int animStart) {
			int animStep = ((mAnimCount-animStart) % 12)/2;
			float x = posX/100f-mDensityFactor*15f/2f;
			float y = posY/100f-mDensityFactor*19f/2f;
			if (dir == DIR_RIGHT) {
				animStep += 6;
			}
			canvas.drawBitmap(mMouth[animStep], x, y, paint);
		}

		private void drawBubble(Canvas canvas, Paint paint, float posX, float posY) {
			float x = posX/100f-mDensityFactor*25f/2f;
			float y = posY/100f-mDensityFactor*25f/2f;
			canvas.drawBitmap(mBubble[0], x, y, paint);
		}


		private float displayPosX(VisibleObject visibleObject) {
			return visibleObject.posX/100.0f-mDensityFactor*FISHWIDTH[visibleObject.size]/2.0f;
		}
		private float displayPosY(VisibleObject visibleObject) {
			return visibleObject.posY/100.0f-mDensityFactor*FISHHEIGHT[visibleObject.size]/2.0f;
		}


		private Bitmap playerFishBitmap(int size, int dir, int animStep) {
			if ((mMode == MODE_EATEN)||(mMode == MODE_GAMEOVER)) {
				return mFishbone[size*2+dir];
			}
			return mPlayerFish[size*2+dir];
		}

		private void drawComputerFishs(Canvas canvas, Paint paint, int minSize, int maxSize) {
			for (Fish computerFish:computerFishs) {
				if (minSize > computerFish.size ) {
					continue;
				}
				if (maxSize < computerFish.size) {
					continue;
				}
				int animStep = ((mAnimCount-computerFish.animStart) % 15)/5;
				canvas.drawBitmap(computerFishBitmap(computerFish.size, computerFish.dir, animStep), displayPosX(computerFish), displayPosY(computerFish), paint);
			}
		}
		private Bitmap computerFishBitmap(int size, int dir, int animStep) {
			int offset;
			if (size <= 2) {
				offset = size*2+dir; 
			}
			else {
				offset = 6+(size-3)*6+dir*3+animStep;
			}
			return mComputerFish[offset];
		}
 
		private void drawGoodies(Canvas canvas, Paint paint) {
			for (Goodie goodie:goodies) {
				if (goodie.goodietype == GOODIE_BIGMOUTH) {
					drawMouth(canvas, paint, goodie.posX, goodie.posY, DIR_RIGHT, goodie.animStart);
				}
				drawBubble(canvas, paint, goodie.posX, goodie.posY);
			}
		}

		private void drawTargetMarker(Canvas canvas, Paint paint) {
			if ((mMode != MODE_PLAY) || (mTouchedWaypoints == null) || (mTouchedWaypoints.size() == 0)) {
				return;
			}
			int animStep = mAnimCount%8/2;
			Point playerTarget = mTouchedWaypoints.get(mTouchedWaypoints.size()-1);
			canvas.drawBitmap(mTargetMarker[animStep], playerTarget.x-10, playerTarget.y-10, paint);
		}
		
		private void drawFood(Canvas canvas, Paint paint) {
            final float df = mDensityFactor;
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			if (mMode == MODE_PAUSE) {
				paint.setTextSize(28.0f * df);
				paint.setTextAlign(Align.CENTER);
				paint.setColor(0xFFFF4040);
				canvas.drawText("* PAUSED *", maxWidth/2, 160 * df, paint);
				paint.setTextSize(20.0f * df);
				canvas.drawText("Touch screen to continue", maxWidth/2, 200 * df, paint);
				canvas.drawText("Press back twice to exit", maxWidth/2, 230 * df, paint);
				return;
			}
			if (mMode == MODE_GAMEOVER) {
				paint.setTextSize(28.0f * df);
				paint.setTextAlign(Align.CENTER);
				paint.setColor(0xFFFF4040);
				if (mHSPosition != -1) {
					canvas.drawText("Congratulations "+mLastName+"!", maxWidth/2, 120 * df, paint);
					canvas.drawText("Highscore Rank "+(mHSPosition+1)+"", maxWidth/2, 160 * df, paint);
				}
				canvas.drawText("GAME OVER", maxWidth/2, 200 * df, paint);
				canvas.drawText("Level: "+mLevel, maxWidth/2, 240 * df, paint);
				canvas.drawText("Score: "+mScore, maxWidth/2, 280 * df, paint);
				
				return;
			}
			int w = (int)(60f*mDensityFactor);
			int h = (int)(18f*mDensityFactor);
			int left = maxWidth-10-w;
			paint.setTextSize(14.0f * df);
            final String meastxt = "Score: 99999";
            Rect txtbound = new Rect();
            paint.getTextBounds (meastxt, 0, meastxt.length(), txtbound);
            left = Math.min(left, maxWidth - txtbound.right);
			paint.setTextAlign(Align.LEFT);
			canvas.drawText("Level: "+mLevel, left, 20 * df, paint);
			canvas.drawText("Score: "+mScore, left, 40 * df, paint);
			int percent = w*mFood/MAX_FOOD;
			canvas.drawBitmap(mIndicator[0], left, 50 * df, paint);
			Rect src = new Rect(0,0,percent,h);
			Rect dest= new Rect(left +0, (int) (50 * df +0), left +percent, (int) (50 * df +h));
			canvas.drawBitmap(mIndicator[1], src, dest, paint);
		}
		
		
		private void runProgram() {
			mAnimCount += 1;
			doStep();
			mGraphView.invalidate();
			mGraphView.postDelayed(new Runnable() {
				@Override public void run() {
					if (mState == STATE_RUN)
						runProgram();				
					}
			}, mSpeed);
		}

		private boolean checkFinished() {
	    	if (playerFish.size > 1) {
	    		return false;
	    	}
			mMode = MODE_GAMEOVER;
			String infoText; 
			mHSPosition = highscore.insertEntry(mLastName, mLevel, mScore);
			if (mHSPosition != -1) {
				writeScores();
				infoText = "Congratulations!!! Position: "+(mHSPosition+1)+" , Level: "+ mLevelName+", Score: "+mScore+"";
			}
			else {
				infoText = mLevelName + ": Level: "+ mLevel + ", Score: "+mScore;;
			}
			if (mHSPosition >= 0)
				enterName();

			mGraphView.invalidate();
	        Toast.makeText(AndroidFishEatingFish.this, infoText, Toast.LENGTH_LONG).show();
			return true;
		}

    }

	private final static int DIALOG_ENTERNAME = 1;

	private void enterName() {
        showDialog(DIALOG_ENTERNAME);
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ENTERNAME:
	        LayoutInflater factory = LayoutInflater.from(this);
	        final View enternameView = factory.inflate(R.layout.dialog_entername, null);
			final EditText edName = (EditText)enternameView.findViewById(R.id.name_edit);
			edName.setText(mLastName);
	        return new AlertDialog.Builder(this)
	            .setIcon(R.drawable.icon)
	            .setTitle("New Highscore")
	            .setView(enternameView)
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	            		mLastName = edName.getText().toString();
	            		Entry entry = highscore.getEntry(mHSPosition);
	            		entry.name = mLastName;
	            		writeScores();
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            })
	            .create();
        }        
        return null;
    }
	
}
