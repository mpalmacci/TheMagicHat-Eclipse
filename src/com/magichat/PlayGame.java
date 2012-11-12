package com.magichat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayGame extends Activity implements View.OnClickListener {
	List<Deck> allActiveDecks = new ArrayList<Deck>();
	List<Deck> gameDecks = new ArrayList<Deck>();
	List<Player> Players = new ArrayList<Player>();

	int p1GameCount = 0, p2GameCount = 0;

	TextView tvDisplayGames, tvWinner;
	Button bPlayGame, bPlayer1, bPlayer2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_game);

		initialize();

		MagicHatDB getAllInfoDB = new MagicHatDB(this);
		getAllInfoDB.openReadableDB();
		allActiveDecks = getAllInfoDB.getAllActiveDecks();
		Players = getAllInfoDB.getActivePlayers();
		getAllInfoDB.closeDB();

		// TODO Add in handling in case the player doesn't have any active decks
		if (Players.size() == 0) {
			bPlayGame.setEnabled(false);
			tvDisplayGames.setText("\n\nYou don't have any active Players.");
		} else if (Players.size() == 1) {
			getNewGame();
			tvDisplayGames.setText(printNewGame(gameDecks, Players));
		} else if (Players.size() == 2) {
			bPlayGame.setEnabled(true);
			tvWinner.setVisibility(LinearLayout.VISIBLE);
			bPlayer1.setText(Players.get(0).getName());
			bPlayer1.setVisibility(LinearLayout.VISIBLE);
			bPlayer2.setText(Players.get(1).getName());
			bPlayer2.setVisibility(LinearLayout.VISIBLE);

			getNewGame();
			tvDisplayGames.setText(printNewGame(gameDecks, Players));
		} else {
			bPlayGame.setEnabled(true);

			getNewGame();
			tvDisplayGames.setText(printNewGame(gameDecks, Players));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bPlayGame:
			// Clear the cache of gameDecks, and start anew
			gameDecks = new ArrayList<Deck>();
			p1GameCount = 0;
			p2GameCount = 0;

			getNewGame();
			tvDisplayGames.setText(printNewGame(gameDecks, Players));
			break;
		case R.id.bPlayer1:
			++p1GameCount;
			MagicHatDB addGameResult1 = new MagicHatDB(this);
			addGameResult1.openWritableDB();
			addGameResult1.addGameResult(Players, gameDecks, Players.get(0),
					new Date());
			addGameResult1.closeDB();
			if (p1GameCount + p2GameCount > 1) {
				if (p1GameCount == 2) {
					showCompletedDialog();
				}
			} else {
				showWinnerDialog();
			}
			break;
		case R.id.bPlayer2:
			++p2GameCount;
			MagicHatDB addGameResult2 = new MagicHatDB(this);
			addGameResult2.openWritableDB();
			addGameResult2.addGameResult(Players, gameDecks, Players.get(1),
					new Date());
			addGameResult2.closeDB();
			if (p1GameCount + p2GameCount > 1) {
				if (p2GameCount == 2) {
					showCompletedDialog();
				}
			} else {
				showWinnerDialog();
			}
			break;
		}
	}

	private void showWinnerDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				"Congrats to the Winner!\nWould you like to play for best of 3?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Clear the cache of gameDecks, and start anew
						gameDecks = new ArrayList<Deck>();
						p1GameCount = 0;
						p2GameCount = 0;
						getNewGame();
						tvDisplayGames
								.setText(printNewGame(gameDecks, Players));
						dialog.dismiss();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	private void showCompletedDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				"Congrats to the Winner!\nWould you like to play more games?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Clear the cache of gameDecks, and start anew
								gameDecks = new ArrayList<Deck>();
								p1GameCount = 0;
								p2GameCount = 0;
								getNewGame();
								tvDisplayGames.setText(printNewGame(gameDecks,
										Players));
								dialog.dismiss();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	private void getNewGame() {
		Random ran = new Random();

		int maxVal = allActiveDecks.size();
		int r = 0;

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean ownDecks = getPrefs.getBoolean("ownersDecksOnly", false);

		// Iterates through the total number of players
		// to find that many random decks
		for (Player p : Players) {
			r = ran.nextInt(maxVal);
			Deck d = allActiveDecks.get(r);

			if (ownDecks) {
				// See if the deck is unique to the decks already selected to be
				// played and since the Preference to only play with your own
				// decks is on check to see if the current player is the same as
				// the deck's owner
				while (gameDecks.contains(d) || !d.getOwner().equals(p)) {
					// try the next random integer that is smaller than the
					// total number of decks
					r = ran.nextInt(maxVal);
					d = allActiveDecks.get(r);
				}
			} else {
				// See if the deck is unique to the decks already selected to be played
				while (gameDecks.contains(d)) {
					// try the next random integer that is smaller than the
					// total number of decks
					r = ran.nextInt(maxVal);
					d = allActiveDecks.get(r);
				}
			}
			gameDecks.add(d);
		}
	}

	private String printNewGame(List<Deck> gameDecks, List<Player> Players) {
		String output = "\n\n";

		// Decks and Players should have the same number of items in it
		if (gameDecks.size() != Players.size()) {
			output = "Players and Decks are not equal\n\n";
			output = output.concat("Players size is " + Players.size()).concat(
					" and the Decks size is " + gameDecks.size());
		} else if (Players.size() == 0) {
			output = "No active players were found!\n\n";
		} else {
			for (int i = 0; i < Players.size() - 1; i++) {
				output = output.concat(Players.get(i).toString()
						+ " will play " + gameDecks.get(i).toString()
						+ "\nand\n");
			}

			output = output.concat(Players.get(Players.size() - 1).toString()
					.concat(" will play ")
					.concat(gameDecks.get(Players.size() - 1).toString())
					.concat(".\n\n"));
		}

		return output;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.play_game_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.playGamePrefs:
			Intent playGamePrefs = new Intent("com.magichat.PLAYGAMEPREFS");
			startActivity(playGamePrefs);
			break;
		}
		return false;
	}

	private void initialize() {
		bPlayGame = (Button) findViewById(R.id.bPlayGame);
		tvDisplayGames = (TextView) findViewById(R.id.tvDisplayGames);
		bPlayer1 = (Button) findViewById(R.id.bPlayer1);
		bPlayer2 = (Button) findViewById(R.id.bPlayer2);
		tvWinner = (TextView) findViewById(R.id.tvWinner);

		bPlayGame.setOnClickListener(this);
		bPlayer1.setOnClickListener(this);
		bPlayer2.setOnClickListener(this);
	}
}
