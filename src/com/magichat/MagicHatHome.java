package com.magichat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MagicHatHome extends MagicHatActivity {

	protected Button bPlayGame, bViewGameStats, bDecks;

	protected String decksOrPlayersPref = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.magic_hat_main);

		initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		decksOrPlayersPref = getPrefs
				.getString("viewByDecksOrPlayers", "Decks");
	}

	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bPlayGame:
			Intent openGameActivity = new Intent(
					"com.magichat.decks.games.PLAYGAME");
			startActivity(openGameActivity);
			break;
		case R.id.bViewGameStats:
			if (decksOrPlayersPref.contentEquals("Decks")) {
				Intent openGameStatsActivity = new Intent(
						"com.magichat.decks.games.GAMESTATSFORDECK");
				startActivity(openGameStatsActivity);
			} else {
				Intent openGameStatsActivity = new Intent(
						"com.magichat.decks.games.GAMESTATSFORPLAYER");
				startActivity(openGameStatsActivity);
			}
			break;
		case R.id.bDecks:
			Intent openDeckActivity = new Intent(
					"com.magichat.decks.DECKSMAIN");
			startActivity(openDeckActivity);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		bPlayGame = (Button) findViewById(R.id.bPlayGame);
		bViewGameStats = (Button) findViewById(R.id.bViewGameStats);
		bDecks = (Button) findViewById(R.id.bDecks);
		
		this.bPrefs.setVisibility(LinearLayout.VISIBLE);
		this.bPlayers.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		
		bPlayGame.setOnClickListener(this);
		bViewGameStats.setOnClickListener(this);
		bDecks.setOnClickListener(this);
	}
}