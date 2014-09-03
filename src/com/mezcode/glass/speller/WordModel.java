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

package com.mezcode.glass.speller;

import java.io.Serializable;
import java.util.List;

/**
 * Represents the state of the game: the phrases that were randomly selected, which of those
 * phrases have been guessed correctly, and which phrase the players are currently on. This
 * class is serializable so that it can be stored in an {@code Intent} and passed from the
 * game activity to the results activity.
 */
public class WordModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The phrases in this instance of the game. */
    final String[] mPhrases;

    /**
     * Values that indicate whether or not the corresponding item in {@code mPhrases} has been
     * guessed correctly.
     */
    private final boolean[] mGuessedPhrases;

    /**
     * Keeps track of the total number of guessed phrases, so that this value can be retrieved
     * without iterating over the array.
     */
    int mScore;

    /** The index of the phrase that the player is currently on in the game. */
    int mCurrentPhrase;
    
    int mTotal;

    /** Constructs a new model with the specified list of phrases. */
    public WordModel(List<String> phrases) {
    	mTotal = phrases.size();
        mPhrases = phrases.toArray(new String[mTotal]);
        for(int dex = 0; dex < mTotal; dex++) {
        	mPhrases[dex] = setWordToCaps(mPhrases[dex]);
        }
        mGuessedPhrases = new boolean[mTotal];
        mScore = 0;
        mCurrentPhrase = 0;
    }

    private String setWordToCaps(String phrase) {
    	final int dex = phrase.indexOf(" ");
    	//end of first word
    	String word = phrase.substring(0, dex);
    	return word.toUpperCase() + phrase.substring(dex);
    }
    /** Returns the number of phrases in the model. */
    public int getPhraseCount() {
        return mPhrases.length;
    }

    /** Returns the number of phrases that have been guessed correctly. */
    public int getScore() {
        return mScore;
    }

    /** Returns true if all of the phrases have been guessed correctly. */
    public boolean areAllPhrasesGuessedCorrectly() {
        return getScore() == mPhrases.length;
    }

    /** Returns the phrase that the players are currently on in the game. */
    public String getCurrentPhrase() {
        return getPhrase(mCurrentPhrase);
    }

    /** Returns the index of the phrase that the players are currently on in the game. */
    public int getCurrentPhraseIndex() {
        return mCurrentPhrase;
    }

    /** Returns the phrase at the specified index in the game. */
    public String getPhrase(int index) {
        return mPhrases[index];
    }

    /** Returns true if the phrase at the specified index has been guessed correctly. */
    public boolean isPhraseGuessedCorrectly(int index) {
        return mGuessedPhrases[index];
    }

    /**
     * Marks the current phrase as guessed correctly and then advances to the next phrase in the
     * game.
     *
     * @return true if all of the phrases have been guessed correctly after this one
     */
    public boolean markGuessed() {
        mGuessedPhrases[mCurrentPhrase] = true;
        mScore++;
        return advance();
    }

    /**
     * Passes on the current phrase and advances to the next phrase in the game.
     *
     * @return always false, because being able to pass means that not all phrase have been
     *     guessed correctly
     */
    public boolean pass() {
    	mGuessedPhrases[mCurrentPhrase] = true;
        return advance();
    }

    /**
     * Attempts to advance the current phrase of the game to the next unguessed phrase
     *
     * @return true if all of the phrases have been guessed and no advancing could be
     *     done, or false if there are still unguessed phrases
     */
    private boolean advance() {
        if(!areAllPhrasesGuessedCorrectly() && !isOver()) {
            do {
                mCurrentPhrase = (mCurrentPhrase + 1) % mPhrases.length;
            } while (mGuessedPhrases[mCurrentPhrase]);
            return false;
        }
        return true;
    }

	public String[] getWords() {
		final String[] onlyWord = new String[mTotal];
		for(int dex = 0; dex < mTotal; dex++) {
			onlyWord[dex] = new String(mPhrases[dex].substring(0, mPhrases[dex].indexOf(" ")));
        }
		return onlyWord;
	}
	
	public boolean isOver() {
		for(boolean b: mGuessedPhrases) {
			if(!b) return false;
		} //found a phrase that's not been guessed
		
		return true;
	}
}
