/*
 * app was built from the charades sample for the 
 *   Hacking Glass for Education hackathon
 */

package com.mezcode.glass.speller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;

/**
 * The activity selected when the user wants to "lead a bee"
 */
public class LeadBeeActivity extends BaseGameActivity {
    private static final String TAG = "LeadBeeActivity";
    /** The maximum duration of the game. */
    private static int GAME_TIME_SECONDS = 30; //(int) TimeUnit.MINUTES.toSeconds(1);

    /** Handler used to keep the game ticking once per second. */
    private final Handler mHandler = new Handler();

    /**
     * Runner that is called once per second during the game to advance the state or end the game
     * when time runs out.
     */
    private final Runnable mTick = new Runnable() {
        @Override
        public void run() {
            mSecondsRemaining--;
            updateTimer();
            
            if (mSecondsRemaining <= 0) {
                endGame();
            } else {
                nextTick();
            }
        }
    };

    /** Keeps track of the amount of time remaining in the game. */
    private int mSecondsRemaining;

    /** TextView that displays the amount of time remaining in the game. */
    private TextView mTimer;
    private Drawable mTimerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimer = (TextView) findViewById(R.id.timer);
        mTimerIcon = getResources().getDrawable(R.drawable.ic_game_50);
        mTimerIcon.setBounds(0, 0, mTimerIcon.getIntrinsicWidth(), mTimerIcon.getIntrinsicHeight());
    }

    
    @Override
	protected void onResume() {
		// This is to set the timeout chosen by the user 
		super.onResume();
		if(PreferenceManager.getDefaultSharedPreferences(this).contains(ChooseTimeActivity.SPELLTIME)) {
			GAME_TIME_SECONDS = PreferenceManager.getDefaultSharedPreferences(this)
					.getInt(ChooseTimeActivity.SPELLTIME, GAME_TIME_SECONDS);
			mSecondsRemaining = GAME_TIME_SECONDS;
		}
		//get the party started, show the first word and start the timer
		newWordReset();
	}

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mTick);
    }

    @Override
    protected void handleGameGesture(Gesture gesture) {
    	getCurrentTextView().setCompoundDrawables(null, null, null, null);
        switch (gesture) {
            case TAP:
            	//correct answer
                if(!score() || getCharadesModel().areAllPhrasesGuessedCorrectly()) {
                	//game over
                	endGame();
                	return;
                }
                newWordReset();
                break;
            case SWIPE_RIGHT:
            	if(!pass()) {
            		//gave over
            		endGame();
            		return;
            	}
            	newWordReset();                
                break;
            case SWIPE_LEFT:
                // Swipe left (backward) is always handled here to provide a brief
                // "disallowed" tug animation.
            	//There's no going backward in a bee
                tugPhrase();
                break;
		default:
			Log.i(TAG, "unsupported gesture");
			break;
                
        }
    }
    
    void newWordReset() {
    	//this timer setup is in 3 places
    	mSecondsRemaining = GAME_TIME_SECONDS;
    	mTimer.setTextColor(Color.WHITE);
    	updateTimer();
        nextTick();
    }

    /** Enqueues the next timer tick into the message queue after one second. */
    private void nextTick() {
        mHandler.postDelayed(mTick, 1000);
    }

    /** Updates the timer display with the current number of seconds remaining. */
    private void updateTimer() {
        // The code point U+EE01 in Roboto is the vertically centered colon used in the clock on
        // the Glass home screen.
        String timeString = String.format(
            "%d\uee01%02d", mSecondsRemaining / 60, mSecondsRemaining % 60);
        mTimer.setText(timeString);
        if(mSecondsRemaining == 15) {
        	mTimer.setTextColor(Color.RED);
        }
    }

    /**
     * Called either when the last phrase is guessed correctly or time has run out to finish the
     * game play activity and display the game results screen.
     * 
     * TODO verify end of game condition
     */
    private void endGame() {
    	if(getCharadesModel().isOver()) {
    		getCurrentTextView().setText(R.string.endgame);
    		mTimer.setText("");
    		mGameState.setText("");
    		mHandler.removeCallbacks(mTick);
        } else {
        	//0 seconds remaining, used to auto advance
        	//handleGameGesture(Gesture.SWIPE_RIGHT);
        	playSoundEffect(Sounds.SELECTED);
        	getCurrentTextView().setText(R.string.nextword);
        	getCurrentTextView().setCompoundDrawables(null, null, null, mTimerIcon);
        	mTimer.setText("");
    		mGameState.setText("");
    		mHandler.removeCallbacks(mTick);
        	
        }
        
    }
}
