/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.glass.sample.charades;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.touchpad.Gesture;

/**
 * An implementation of the game that acts as a tutorial, restricting certain gestures to match
 * the instruction phrases on the screen.
 */
public class TutorialActivity extends BaseGameActivity implements OnInitListener {

    /** The number of phrases that will be selected for the game. */
    private static final int NUMBER_OF_PHRASES = 10;
    private static String[] mWords;
    private TextToSpeech mTts;
    boolean mSpeechReady = false;
    boolean mPhraseSpoken = false;
    TextView mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar in tutorial mode.
        mNext = (TextView) findViewById(R.id.game_state);
        mNext.setText("Tap to Continue"); 
        //model was created with call to super
        mWords = getCharadesModel().getWords();
        //additional array of just the word to spell and learn
        mTts = new TextToSpeech(this, this);
        //mTts.setOnUtteranceProgressListener(mSpListener);
    }

    //same as 
    @Override
    protected CharadesModel createCharadesModel() {
        List<String> allPhrases = Arrays.asList(getResources().getStringArray(
                R.array.definition));
        Collections.shuffle(allPhrases);
        return new CharadesModel(allPhrases.subList(0, NUMBER_OF_PHRASES));
    }

    /**
     * Overridden to only allow the tap gesture on the "Tap to score" screen and to only allow the
     * swipe gesture on the "Swipe to pass" screen. The game is also automatically ended when the
     * final card is either tapped or swiped.
     */
    @Override
    protected void handleGameGesture(Gesture gesture) {
        int phraseIndex = getCharadesModel().getCurrentPhraseIndex();
        switch (gesture) {
            case TAP:
                if (phraseIndex != NUMBER_OF_PHRASES) {
                    //score();
                	mTts.speak(getCharadesModel().getCurrentPhrase(), TextToSpeech.QUEUE_FLUSH, null);
                	//mPhraseSpoken = true;
                	getCurrentTextView().setText(mWords[getCharadesModel().mCurrentPhrase]);
                	mNext.setVisibility(View.GONE);
                }
                break;
            case SWIPE_RIGHT:
                if(phraseIndex != NUMBER_OF_PHRASES) {
                	
                	//mPhraseSpoken = false;
                	getCharadesModel().pass();
                	//advance to next
                	mTts.speak(mWords[getCharadesModel().mCurrentPhrase], TextToSpeech.QUEUE_FLUSH, null);
                    //playSoundEffect(Sounds.SELECTED);
                    getCurrentTextView().setText("");
                    mNext.setVisibility(View.VISIBLE);
                    
                }
                break;
        }

        // Finish the tutorial if we transitioned away from the final card.
        if (phraseIndex == getCharadesModel().getPhraseCount() - 1) {
            finish();
        }
    }

    //TTS prep complete
	@Override
	public void onInit(int arg0) {
		//what is this parameter?
		mSpeechReady = true;
		mTts.speak(mWords[0], TextToSpeech.QUEUE_FLUSH, null);
		getCurrentTextView().setText("");
	}
	
	 @Override
	protected void onDestroy() {
		// This is required cleanup
		super.onDestroy();
		mTts.shutdown();
	}
	
	/*private final UtteranceProgressListener mSpListener = new UtteranceProgressListener() {

		@Override
		public void onDone(String arg0) {
			// Cleared for the next word to play;
			if(mPhraseSpoken) return;
			TutorialActivity.this.getCurrentTextView().setText("Tap to continue");
		}

		@Override
		public void onError(String utteranceId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStart(String utteranceId) {
			// TODO Auto-generated method stub
			
		}
		
	};*/
	
}
