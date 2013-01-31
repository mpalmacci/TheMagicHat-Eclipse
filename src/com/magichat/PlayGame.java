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
import android.os.AsyncTask;
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

	TextView tvPlayer1Deck, tvWinner;
	UpsideDownTextView tvPlayer2Deck;
	Button bPlayGame, bPlayer1, bPlayer2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_game);

		initialize();

		new getAllInfo().execute();
	}

	private class getAllInfo extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			MagicHatDB getAllInfoDB = new MagicHatDB(PlayGame.this);
			getAllInfoDB.openReadableDB();
			allActiveDecks = getAllInfoDB.getAllActiveDecks();
			Players = getAllInfoDB.getActivePlayers();
			getAllInfoDB.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			displayNewGame();
		}
	}

	private void displayNewGame() {
		// TODO Add in handling in case the player doesn't have any active decks
		if (Players.size() == 0) {
			bPlayGame.setEnabled(false);
			tvPlayer1Deck.setText("\n\nYou don't have any active Players.");
		} else if (Players.size() == 1) {
			getNewRandomGame();
			tvPlayer1Deck.setText(printGame(gameDecks.get(0), Players.get(0)));
		} else if (Players.size() == 2) {
			bPlayGame.setEnabled(true);
			tvWinner.setVisibility(LinearLayout.VISIBLE);
			bPlayer1.setText(Players.get(0).getName());
			bPlayer1.setVisibility(LinearLayout.VISIBLE);
			bPlayer2.setText(Players.get(1).getName());
			bPlayer2.setVisibility(LinearLayout.VISIBLE);

			getNewRandomGame();
			tvPlayer1Deck.setText(printGame(gameDecks.get(1), Players.get(1)));
			tvPlayer2Deck.setText(printGame(gameDecks.get(0), Players.get(0)));
		} else {
			bPlayGame.setEnabled(true);

			getNewRandomGame();
			tvPlayer1Deck.setText(printGame(gameDecks, Players));
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

			displayNewGame();
			break;
		case R.id.bPlayer1:
			++p1GameCount;
			new addGameResult().execute(0);
			break;
		case R.id.bPlayer2:
			++p2GameCount;
			new addGameResult().execute(1);
			break;
		default:
			break;
		}
	}

	private class addGameResult extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			MagicHatDB mhAddGameResult = new MagicHatDB(PlayGame.this);
			mhAddGameResult.openWritableDB();
			mhAddGameResult.addGameResult(Players, gameDecks,
					Players.get(params[0]), new Date());
			mhAddGameResult.closeDB();
			return params[0];
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 0) {
				if (p1GameCount + p2GameCount > 1) {
					if (p1GameCount == 2) {
						showCompletedDialog();
					}
				} else {
					showWinnerDialog();
				}
			} else if (result == 1) {
				if (p1GameCount + p2GameCount > 1) {
					if (p2GameCount == 2) {
						showCompletedDialog();
					}
				} else {
					showWinnerDialog();
				}
			}
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

						displayNewGame();
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

								displayNewGame();
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

	private void getNewRandomGame() {
		Random ran = new Random();

		int maxVal = allActiveDecks.size();
		int r = 0;

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean ownDecks = getPrefs.getBoolean("ownersDecksOnly", false);

		// Iterates through the total number of players
		// to find that many random decks
		for (Player p : Players) {
			// TODO Allow user to choose who they are - set them as Player 1
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
				// See if the deck is unique to the decks already selected to be
				// played
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

	private String printGame(Deck d, Player p) {
		String output = "\n";

		// Decks and Players should have the same number of items in it
		if (gameDecks.size() != Players.size()) {
			bPlayGame.setEnabled(false);
			output = "Players and Decks are not of equal size\n\n";
			output = output.concat("Players size is " + Players.size()).concat(
					" and the Decks size is " + gameDecks.size());
		} else if (Players.size() == 0) {
			bPlayGame.setEnabled(false);
			output = "No active players were found!\n\n";
		} else {
			output = output.concat(p.toString() + " will play " + d.toString())
					.concat("\n");
		}

		return output;
	}

	private String printGame(List<Deck> gameDecks, List<Player> Players) {
		String output = "\n\n";

		// Decks and Players should have the same number of items in it
		if (gameDecks.size() != Players.size()) {
			bPlayGame.setEnabled(false);
			output = "Players and Decks are not of equal size\n";
			output = output.concat("Players size is ")
					.concat(Integer.toString(Players.size()))
					.concat(" and the Decks size is ")
					.concat(Integer.toString(gameDecks.size()));
		} else if (Players.size() == 0) {
			bPlayGame.setEnabled(false);
			output = "No active players were found!\n\n";
		} else {
			for (int i = 0; i < Players.size() - 1; i++) {
				output = output.concat(Players.get(i).toString()
						.concat(" will play ")
						.concat(gameDecks.get(i).toString()).concat("\nand\n"));
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
		tvPlayer1Deck = (TextView) findViewById(R.id.tvPlayer1Deck);
		tvPlayer2Deck = (UpsideDownTextView) findViewById(R.id.tvPlayer2Deck);
		bPlayer1 = (Button) findViewById(R.id.bPlayer1);
		bPlayer2 = (Button) findViewById(R.id.bPlayer2);
		tvWinner = (TextView) findViewById(R.id.tvWinner);

		bPlayGame.setOnClickListener(this);
		bPlayer1.setOnClickListener(this);
		bPlayer2.setOnClickListener(this);
	}
}
