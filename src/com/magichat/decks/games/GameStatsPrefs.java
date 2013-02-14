package com.magichat.decks.games;

import com.magichat.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GameStatsPrefs extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.game_stats_prefs);
	}
}
