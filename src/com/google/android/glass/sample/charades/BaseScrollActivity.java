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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Base {@link Activity} for {@link CardScrollView} based Activities.
 */
public class BaseScrollActivity extends Activity implements GestureDetector.BaseListener {
	public static final String LEVEL = "level";
    private AudioManager mAudioManager;
    private CardScrollView mView;
    private GestureDetector mDetector;
    

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
        SelectValueScrollAdapter adapter = new SelectValueScrollAdapter(this, 3);
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
                convertView = LayoutInflater.from(BaseScrollActivity.this).inflate(R.layout.card_select_value, parent);
            }

            final TextView view = (TextView) convertView.findViewById(R.id.value);
            switch(position) {
            case 0:
            	view.setText("Beginner");
            	break;
            case 1:
            	view.setText("Intermediate");
            	break;
            case 2:
            	view.setText("Advanced");
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
                final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(BaseScrollActivity.this).edit();
                ed.putInt(LEVEL, getView().getSelectedItemPosition());
                ed.commit();
                setResult(RESULT_OK);
                playSoundEffect(Sounds.TAP);
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

}
