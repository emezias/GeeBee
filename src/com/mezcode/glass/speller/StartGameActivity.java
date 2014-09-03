/*
 * The charades sample was the start of this app
 * Built for the Hacking Glass for Education hackathon
 */

package com.mezcode.glass.speller;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * The initial Word Master splash screen activity 
 */
public class StartGameActivity extends Activity {

    /**
     * Handler used to post requests to start new activities so that the menu closing animation
     * works properly.
     */
    private final Handler mHandler = new Handler();

    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
                findViewById(R.id.game_name).setVisibility(View.INVISIBLE);
                findViewById(R.id.tip_tap_for_options).setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    };

    /** Audio manager used to play system sound effects. */
    private AudioManager mAudioManager;

    /** Gesture detector used to present the options menu. */
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
    }
    
    

    @Override
	protected void onResume() {
		//This will restore the stuff hidden when opening the menu
		super.onResume();
		if(findViewById(R.id.game_name).getVisibility() == View.INVISIBLE) {
			findViewById(R.id.game_name).setVisibility(View.VISIBLE);
		}
        if(findViewById(R.id.tip_tap_for_options).getVisibility() == View.INVISIBLE) {
        	findViewById(R.id.tip_tap_for_options).setVisibility(View.VISIBLE);
        }
	}



	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_game, menu);
        return true;
    }

    /**
     * The act of starting an activity here is wrapped inside a posted {@code Runnable} to avoid
     * animation problems between the closing menu and the new activity. The post ensures that the
     * menu gets the chance to slide down off the screen before the activity is started.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    	//Starts the main game activity
                    	startActivity(new Intent(StartGameActivity.this, LeadBeeActivity.class));
                    }
                });
                return true;

            case R.id.tutorial:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    	/**
                         * Starts the tutorial activity, but does not finish this activity so that the splash screen
                         * reappears when the tutorial is over.
                         */
                    	startActivity(new Intent(StartGameActivity.this, TutorialActivity.class));
                    }
                });
                return true;
            case R.id.wordlevel:
            	mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    	/**
                         * Starts the tutorial activity, but does not finish this activity so that the splash screen
                         * reappears when the tutorial is over.
                         */
                    	startActivity(new Intent(StartGameActivity.this, ChooseLevelActivity.class));
                    }
                });
            	return true;
            	
            case R.id.time:
            	mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    	/**
                         * Starts the tutorial activity, but does not finish this activity so that the splash screen
                         * reappears when the tutorial is over.
                         */
                    	startActivity(new Intent(StartGameActivity.this, ChooseTimeActivity.class));
                    }
                });
            	return true;	

            default:
                return false;
        }
    }

    
}
