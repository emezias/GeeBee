/*
 * App built for the Hacking Glass for Education hackathon
 * built from the Charades sample
 */

package com.mezcode.glass.speller;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.touchpad.Gesture;

/**
 * This screen is where the user goes to study words
 * The word is read out loud and when they are ready, the spelling is shown and a definition is read.
 */
public class TutorialActivity extends BaseGameActivity implements OnInitListener {

    /** The number of phrases that will be selected for the game. */
    private static String[] mWords;
    private TextToSpeech mTts;
    TextView mNext;
    Drawable mBee;
    boolean resizeOnce = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar in tutorial mode.
        mNext = (TextView) findViewById(R.id.game_state);
        mBee = getResources().getDrawable(R.drawable.ic_game_50);
        mBee.setBounds(0, 0, mBee.getIntrinsicWidth(), mBee.getIntrinsicHeight());
        mNext.setCompoundDrawables(null, null, null, mBee);
        mNext.setText(R.string.tutor); 
        //model was created with call to super
        mWords = getCharadesModel().getWords();
        //additional array of just the word to spell and learn
        mTts = new TextToSpeech(this, this);
        //mTts.setOnUtteranceProgressListener(mSpListener);
    }

    /**
     * Overridden to handle the tap gesture, tap is when the user gets to see the word
     * swipe gesture is also allowed, swipe takes the user to the next word
     */
    @Override
    protected void handleGameGesture(Gesture gesture) {

        switch (gesture) {
            case TAP:
            	//this case plays the sentence audio and displays the word
            	mTts.speak(getCharadesModel().getCurrentPhrase(), TextToSpeech.QUEUE_FLUSH, null);
            	getCurrentTextView().setText(mWords[getCharadesModel().mCurrentPhrase]);
            	mNext.setVisibility(View.GONE);
            	if(!resizeOnce) {
            		mBee.setBounds(0, 0, mBee.getIntrinsicWidth()*2, mBee.getIntrinsicHeight()*2);
            		resizeOnce = true;
            	}
                break;
            case SWIPE_LEFT:
                // Swipe left (backward) is handled here and allows two way navigation through the words
            	if(getCharadesModel().mCurrentPhrase > 2) {
            		getCharadesModel().mCurrentPhrase = getCharadesModel().mCurrentPhrase -2;
                	getCharadesModel().pass();
            	} else {
            		tugPhrase();
            	}
            	
            	//advance to next
            	mTts.speak(mWords[getCharadesModel().mCurrentPhrase], TextToSpeech.QUEUE_FLUSH, null);
                //playSoundEffect(Sounds.SELECTED);
                getCurrentTextView().setText("");
                mNext.setCompoundDrawables(null, mBee, null, null);
                mNext.setVisibility(View.VISIBLE);
                break;
            case SWIPE_RIGHT:
            	if(getCharadesModel().getCurrentPhraseIndex() == getCharadesModel().mTotal-1) {
            		//finished last word already
            		getCurrentTextView().setText(R.string.endgame);
            		return;
            	}
            	//this case speaks the word and does not display any more to the speller
            	getCharadesModel().pass();
            	//advance to next
            	mTts.speak(mWords[getCharadesModel().mCurrentPhrase], TextToSpeech.QUEUE_FLUSH, null);
                //playSoundEffect(Sounds.SELECTED);
                getCurrentTextView().setText("");
                mNext.setCompoundDrawables(null, mBee, null, null);
                mNext.setVisibility(View.VISIBLE);
                break;
        }
    }

    //TTS prep complete
	@Override
	public void onInit(int arg0) {
		//what is this parameter?
		mTts.speak(mWords[0], TextToSpeech.QUEUE_FLUSH, null);
    	//Beginner at level 0 is the default
    	getCurrentTextView().setText(getLevelText());
	}
	
	 @Override
	protected void onDestroy() {
		// This is required cleanup
		super.onDestroy();
		mTts.shutdown();
	}
	
}
