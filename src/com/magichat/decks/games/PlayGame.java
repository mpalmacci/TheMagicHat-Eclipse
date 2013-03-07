package com.magichat.decks.games;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.Deck;
import com.magichat.decks.db.MagicHatDb;
import com.magichat.players.Player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class PlayGame extends MagicHatActivity implements
		OnItemSelectedListener {
	List<Deck> allActiveDecks = new ArrayList<Deck>();
	// List<Deck> gameDecks = new ArrayList<Deck>();
	List<Player> player = new ArrayList<Player>();
	Map<Player, Deck> playersAndDecks = new HashMap<Player, Deck>();

	int p1GameCount = 0, p2GameCount = 0;

	Button bPlayGame, bPlayer1, bPlayer2;
	LinearLayout llWinnerSection, llMatchupView;

	boolean ownDecks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_game);

		initialize();

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		ownDecks = getPrefs.getBoolean("ownersDecksOnly", false);

		// TODO Break this out into two separate code paths
		new getAllInfo().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean ownDecksNew = getPrefs.getBoolean("ownersDecksOnly", false);

		if (ownDecksNew != ownDecks) {
			ownDecks = ownDecksNew;

			// TODO Break this out into two separate code paths
			new getAllInfo().execute();
		}
	}

	private class getAllInfo extends AsyncTask<Boolean, Integer, String> {

		@Override
		protected String doInBackground(Boolean... prefs) {
			MagicHatDb getAllInfoDB = new MagicHatDb(PlayGame.this);
			getAllInfoDB.openReadableDB();
			player = getAllInfoDB.getActivePlayers();

			if (ownDecks) {
				// Here every player will play with their own decks
				for (Player p : player) {
					List<Deck> playersDecks = getAllInfoDB.getActiveDeckList(p);
					if (playersDecks.isEmpty()) {
						System.out.println("PlayGame.getAllInfo: "
								+ p.toString() + "'s deckList is empty!");
					}
					p.setDeckList(playersDecks);
				}
			} else {
				allActiveDecks = getAllInfoDB.getAllActiveDecks();
			}

			getAllInfoDB.closeDB();

			return null;
		}

		@Override
		protected void onPostExecute(String results) {
			super.onPostExecute(results);
			populateDeckSpinners();
			displayNewGame();
		}
	}

	private void populateDeckSpinners() {
		for (Player p : player) {
			List<Deck> deckList;

			TextView tvPlayer = new TextView(PlayGame.this);
			Spinner sPlayersDecks = new Spinner(PlayGame.this);

			tvPlayer.setText("\n" + p.toString() + " will play:");

			if (allActiveDecks.isEmpty()) {
				deckList = p.getDeckList();
			} else {
				deckList = allActiveDecks;
			}

			ArrayAdapter<Deck> deckAdapter = new ArrayAdapter<Deck>(
					PlayGame.this, android.R.layout.simple_spinner_item,
					deckList);
			sPlayersDecks.setAdapter(deckAdapter);

			tvPlayer.setTextSize(25);
			sPlayersDecks.setId(p.getId());
			sPlayersDecks.setOnItemSelectedListener(this);

			llMatchupView.addView(tvPlayer);
			llMatchupView.addView(sPlayersDecks);
		}
	}

	private void displayNewGame() {
		// TODO Add in handling in case the player doesn't have any active decks
		if (player.size() == 0) {
			bPlayGame.setEnabled(false);
			TextView tvErrorMessage = new TextView(PlayGame.this);
			tvErrorMessage.setText("\n\nNo active Players were found!\n\n");
			llMatchupView.addView(tvErrorMessage);
			return;
		}

		getNewRandomGame();
		bPlayGame.setEnabled(true);

		if (playersAndDecks.size() != player.size()) {
			bPlayGame.setEnabled(false);
			TextView tvErrorMessage = new TextView(PlayGame.this);
			tvErrorMessage
					.setText("Players and Decks are not of equal size\n\nPlayers size is "
							+ player.size()
							+ " and the Decks size is "
							+ playersAndDecks.size());
			llMatchupView.addView(tvErrorMessage);
			return;
		}

		if (player.size() == 2) {
			llWinnerSection.setVisibility(LinearLayout.VISIBLE);
			bPlayer1.setText(player.get(0).getName());
			bPlayer2.setText(player.get(1).getName());
		}

		if (allActiveDecks.isEmpty()) {
			for (Player p : player) {
				int pId = p.getId();
				Spinner sPlayersDeck = (Spinner) findViewById(pId);

				sPlayersDeck.setSelection(p.getDeckList().indexOf(
						playersAndDecks.get(p)));
			}
		} else {
			for (Player p : player) {
				int pId = p.getId();
				Spinner sPlayersDeck = (Spinner) findViewById(pId);

				sPlayersDeck.setSelection(allActiveDecks
						.indexOf(playersAndDecks.get(p)));
			}
		}
	}

	private void getNewRandomGame() {
		// Clear the cache of gameDecks, and start anew
		playersAndDecks = new HashMap<Player, Deck>();
		p1GameCount = 0;
		p2GameCount = 0;

		Random ran = new Random();

		int maxVal = 0;
		int r = 0;

		// TODO Allow user to choose who they are - always set them as Player 1
		if (allActiveDecks.isEmpty()) {
			for (Player p : player) {
				maxVal = p.getDeckList().size();
				r = ran.nextInt(maxVal);
				playersAndDecks.put(p, p.getDeckList().get(r));
			}
		} else {
			List<Integer> randomInts = new ArrayList<Integer>();
			maxVal = allActiveDecks.size();
			for (Player p : player) {
				r = ran.nextInt(maxVal);
				while (randomInts.contains(r)) {
					r = ran.nextInt(maxVal);
				}
				randomInts.add(r);
				playersAndDecks.put(p, allActiveDecks.get(r));
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bPlayGame:
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
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * for (int i = 0; i < gameDecks.size(); i++) { Spinner sDeck =
			 * (Spinner) findViewById(Players.get(i).getId()); gameDecks[i] =
			 * sDeck }
			 */
		}

		@Override
		protected Integer doInBackground(Integer... pNums) {
			MagicHatDb mhAddGameResult = new MagicHatDb(PlayGame.this);
			mhAddGameResult.openWritableDB();
			mhAddGameResult.addGameResult(playersAndDecks,
					player.get(pNums[0]), new Date());
			mhAddGameResult.closeDB();

			return pNums[0];
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		playersAndDecks.clear();

		Spinner sPlayersDeck;

		for (Player p : player) {
			sPlayersDeck = (Spinner) findViewById(p.getId());
			Deck d = (Deck) sPlayersDeck.getSelectedItem();
			playersAndDecks.put(p, d);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void initialize() {
		bPlayGame = (Button) findViewById(R.id.bPlayGame);
		bPlayer1 = (Button) findViewById(R.id.bPlayer1);
		bPlayer2 = (Button) findViewById(R.id.bPlayer2);
		llMatchupView = (LinearLayout) findViewById(R.id.llMatchupView);
		llWinnerSection = (LinearLayout) findViewById(R.id.llWinnerSection);

		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		
		this.tvTitle.setText("Play Games");

		bPlayGame.setOnClickListener(this);
		bPlayer1.setOnClickListener(this);
		bPlayer2.setOnClickListener(this);
	}
}
