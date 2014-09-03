/*
 * This activity allows the user to decide how much time to give for each word in a bee
 */

package com.mezcode.glass.speller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

/**
 * This Activity works like the activity to choose the game level
 */
public class ChooseTimeActivity extends Activity implements GestureDetector.BaseListener, OnInitListener {
    private AudioManager mAudioManager;
    private CardScrollView mView;
    private GestureDetector mDetector;
    private TextToSpeech mTTs;
    public static final String SPELLTIME = "spelltime";
    
    final int[] choices = { 30, 60, 90, 120, 180, 300 };

    /**
     * Initializes and sets the underlying adapter to be used by the {@link CardScrollView}
     * instantiated in {@link onCreate}, visible for testing.
     */
    //protected abstract void setAdapter(CardScrollView view);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mView = new CardScrollView(this) {
            @Override
            public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                if (mDetector.onMotionEvent(event)) {
                    return true;
                }
                return super.dispatchGenericFocusedEvent(event);
            }
        };
        mView.setHorizontalScrollBarEnabled(true);
        SelectValueScrollAdapter adapter = new SelectValueScrollAdapter(this, choices.length);
        mView.setAdapter(adapter);
        setContentView(mView);

        mDetector = new GestureDetector(this).setBaseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.activate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deactivate();
    }
    
    @Override
	protected void onDestroy() {
		// This is required cleanup
		super.onDestroy();
		if(mTTs != null) mTTs.shutdown();
	}

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mDetector.onMotionEvent(event);
    }

    /**
     * Plays a sound effect, overridable for testing.
     */
    protected void playSoundEffect(int soundId) {
        mAudioManager.playSoundEffect(soundId);
    }

    /**
     * Returns the {@link CardScrollView}, visible for testing.
     */
    CardScrollView getView() {
        return mView;
    }
    
    public class SelectValueScrollAdapter extends CardScrollAdapter {

        private final int mCount;

        public SelectValueScrollAdapter(Context context, int count) {
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ChooseTimeActivity.this).inflate(R.layout.card_select_value, parent);
            }

            final TextView view = (TextView) convertView.findViewById(R.id.value);
            switch(position) {
            case 0:
            	view.setText(R.string.time1);
            	break;
            case 1:
            	view.setText(R.string.time2);
            	break;
            case 2:
            	view.setText(R.string.time3);
            	break;
            case 3:
            	view.setText(R.string.time4);
            	break;
            case 4:
            	view.setText(R.string.time5);
            	break;
            case 5:
            	view.setText(R.string.time6);
            	break;
            }
            

            return convertView;
        }

        @Override
        public int getPosition(Object item) {
            if (item instanceof Integer) {
                int itemInt = (Integer) item;
                if (itemInt >= 0 && itemInt < mCount) {
                    return itemInt;
                }
            }
            return AdapterView.INVALID_POSITION;
        }
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        switch (gesture) {
            case TAP:
                final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(ChooseTimeActivity.this).edit();
                ed.putInt(SPELLTIME, choices[getView().getSelectedItemPosition()]);
                ed.commit();
                setResult(RESULT_OK);
                playSoundEffect(Sounds.TAP);
                mTTs = new TextToSpeech(this, this);
                finish();
                return true;
            case SWIPE_DOWN:
                setResult(RESULT_CANCELED);
                playSoundEffect(Sounds.DISMISSED);
                finish();
                return true;
            default:
                return false;
        }
    }

	@Override
	public void onInit(int status) {
		// This will play the selection out loud
		mTTs.speak(((TextView)findViewById(R.id.value)).getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
	}

}
