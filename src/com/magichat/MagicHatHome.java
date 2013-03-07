package com.magichat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MagicHatHome extends MagicHatActivity {

	protected Button bPlayGame, bViewGameStats, bDecks, bAddDeck, bUpdateDeck,
			bChangeActive, bDeleteDeck, bDisplayAllDecks;

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
		case R.id.bEnterGame:
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
		case R.id.bAddDeck:
			Intent openAddDeckActivity = new Intent(
					"com.magichat.decks.ADDDECK");
			startActivity(openAddDeckActivity);
			break;
		case R.id.bUpdateDeck:
			Intent openUpdateDeckActivity = new Intent(
					"com.magichat.decks.UPDATEDECK");
			startActivity(openUpdateDeckActivity);
			break;
		case R.id.bChangeActive:
			Intent openChangeActiveActivity = new Intent(
					"com.magichat.CHANGEACTIVE");
			startActivity(openChangeActiveActivity);
			break;
		case R.id.bDeleteDeck:
			Intent openDeleteDeckActivity = new Intent(
					"com.magichat.decks.DELETEDECK");
			startActivity(openDeleteDeckActivity);
			break;
		case R.id.bDisplayAllDecks:
			Intent openDisplayAllDecksActivity = new Intent(
					"com.magichat.decks.DISPLAYALLDECKS");
			startActivity(openDisplayAllDecksActivity);
			break;
		case R.id.bPlayers:
			Intent openPlayersActivity = new Intent(
					"com.magichat.players.PLAYERSMAIN");
			startActivity(openPlayersActivity);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		bPlayGame = (Button) findViewById(R.id.bEnterGame);
		bViewGameStats = (Button) findViewById(R.id.bViewGameStats);
		bDecks = (Button) findViewById(R.id.bDecks);
		bAddDeck = (Button) findViewById(R.id.bAddDeck);
		bUpdateDeck = (Button) findViewById(R.id.bUpdateDeck);
		bDeleteDeck = (Button) findViewById(R.id.bDeleteDeck);
		bChangeActive = (Button) findViewById(R.id.bChangeActive);
		bDisplayAllDecks = (Button) findViewById(R.id.bDisplayAllDecks);
		
		this.bPrefs.setVisibility(LinearLayout.VISIBLE);
		this.bPlayers.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		
		bPlayGame.setOnClickListener(this);
		bViewGameStats.setOnClickListener(this);
		bDecks.setOnClickListener(this);
		bAddDeck.setOnClickListener(this);
		bUpdateDeck.setOnClickListener(this);
		bChangeActive.setOnClickListener(this);
		bDeleteDeck.setOnClickListener(this);
		bDisplayAllDecks.setOnClickListener(this);
	}
}