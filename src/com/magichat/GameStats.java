package com.magichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class GameStats extends Activity implements OnItemSelectedListener {

	float totalGames, totalGamesWon, totalGamesLost = 0;
	List<Deck> allDecks = new ArrayList<Deck>();
	List<Player> allPlayers = new ArrayList<Player>();
	List<Deck> dOpponents = new ArrayList<Deck>();
	List<Deck> playersDecks = new ArrayList<Deck>();

	LinearLayout llByDecks, llByPlayers, llWonLostStats, llNonePlayed;
	Spinner sCurrentSelection;
	TextView tvTotalGames, tvGamesWon, tvGamesLost;
	ProgressBar pbTotalWonLost;

	String currentDeck, currentPlayer, decksOrPlayersPref = "";

	MagicHatDB mhDB = new MagicHatDB(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_stats);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		decksOrPlayersPref = getPrefs
				.getString("viewByDecksOrPlayers", "Decks");
		
		initialize();

		if (decksOrPlayersPref.contentEquals("Decks")) {
			populateDeckSpinner();
			currentDeck = sCurrentSelection.getSelectedItem().toString();
			llByDecks.setVisibility(LinearLayout.VISIBLE);
			llByPlayers.setVisibility(LinearLayout.GONE);
		} else {
			populatePlayerSpinner();
			currentPlayer = sCurrentSelection.getSelectedItem().toString();
			llByDecks.setVisibility(LinearLayout.GONE);
			llByPlayers.setVisibility(LinearLayout.VISIBLE);
		}
	}

	private void populateScreen() {
		Deck d = new Deck();
		Player p = new Player();
		List<Game> games = new ArrayList<Game>();

		// Populate the proper information based on the preference
		if (decksOrPlayersPref.contentEquals("Decks")) {
			// Parse the Deck's Name for the owner and Deck Name separately
			String currentDeckName = currentDeck.substring(
					currentDeck.indexOf("'s ") + 3,
					currentDeck.indexOf(" Deck"));

			mhDB.openReadableDB();
			// TODO Just use Deck d = getDeck(deckName, ownerName)
			// Remove the middle man because the deck id can be grabbed by
			// d.getId()
			int dId = mhDB.getDeckId(currentDeckName,
					currentDeck.substring(0, currentDeck.indexOf("'s")));
			d = mhDB.getDeck(dId);
			games = mhDB.getGames(d);
			mhDB.closeDB();

			getStats(d, games);
		} else {
			mhDB.openReadableDB();
			int pId = mhDB.getPlayerId(currentPlayer);
			p = mhDB.getPlayer(pId);
			games = mhDB.getGames(p);
			mhDB.closeDB();

			getStats(p, games);
		}

		if (games.isEmpty()) {
			llByDecks.setVisibility(LinearLayout.GONE);
			llByPlayers.setVisibility(LinearLayout.GONE);
			llNonePlayed.setVisibility(LinearLayout.VISIBLE);
		} else {
			tvTotalGames.setText(Integer.toString(games.size()));
			tvGamesWon.setText(Integer.toString(Math.round(totalGamesWon)));
			int iPercentage = Math.round(totalGamesWon / games.size() * 100);
			pbTotalWonLost.setProgress(iPercentage);
			tvGamesLost.setText(Integer.toString(Math.round(totalGamesLost)));

			// Get a list of games based on your opponents and populate the Game
			// Set Results in a separate method
			for (Deck dOpp : dOpponents) {
				List<Game> gameList = new ArrayList<Game>();
				for (Game g : games) {
					if (g.getDeck(1).equals(dOpp) || g.getDeck(2).equals(dOpp)) {
						gameList.add(g);
					}
				}

				if (decksOrPlayersPref.contentEquals("Decks")) {
					populateGameSet(gameList, d);
				} else {
					populateGameSet(gameList, p);
				}
			}
		}
	}

	private void getStats(Deck d, List<Game> games) {
		Player p = new Player();
		List<Game> gamesWon = new ArrayList<Game>(), gamesLost = new ArrayList<Game>();

		for (Game g : games) {
			// Only add the Deck if opponents doesn't consist of the Deck yet
			if (g.getDeck(1).equals(d)) {
				// Deck owner != Player of that deck in all situations
				p = g.getPlayer(1);

				if (!dOpponents.contains(g.getDeck(2))) {
					dOpponents.add(g.getDeck(2));
				}
			} else if (!dOpponents.contains(g.getDeck(1))) {
				// Deck owner != Player of that deck in all situations
				p = g.getPlayer(2);

				dOpponents.add(g.getDeck(1));
			}

			if (g.isWinner(p)) {
				gamesWon.add(g);
			} else {
				gamesLost.add(g);
			}
		}
		Collections.sort(dOpponents);

		totalGamesWon = gamesWon.size();
		totalGamesLost = gamesLost.size();
	}

	private void getStats(Player p, List<Game> games) {
		List<Game> gamesWon = new ArrayList<Game>(), gamesLost = new ArrayList<Game>();

		for (Game g : games) {
			// Only add the Player if opponents doesn't consist of the Player
			// yet
			if (g.getPlayer(1).equals(p)) {
				if (!dOpponents.contains(g.getDeck(2))) {
					dOpponents.add(g.getDeck(2));
				}
				if (!playersDecks.contains(g.getDeck(1))) {
					playersDecks.add(g.getDeck(1));
				}
			} else {
				if (!dOpponents.contains(g.getDeck(1))) {
					dOpponents.add(g.getDeck(1));
				}
				if (!playersDecks.contains(g.getDeck(2))) {
					playersDecks.add(g.getDeck(2));
				}
			}

			if (g.isWinner(p)) {
				gamesWon.add(g);
			} else {
				gamesLost.add(g);
			}
		}
		Collections.sort(playersDecks);
		Collections.sort(dOpponents);

		totalGamesWon = gamesWon.size();
		totalGamesLost = gamesLost.size();
	}

	private void populateGameSet(List<Game> gameList, Deck d) {
		List<Game> lGamesWon = new ArrayList<Game>();
		TextView tvTotalGamesAgainstOpp = new TextView(this);
		ProgressBar pbGames = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);
		TextView tvPercentWonAgainstOpp = new TextView(this);
		Deck dOpp = new Deck();
		Player p = new Player();

		if (gameList.get(0).getDeck(1).equals(d)) {
			dOpp = gameList.get(0).getDeck(2);
			p = gameList.get(0).getPlayer(1);
		} else {
			dOpp = gameList.get(0).getDeck(1);
			p = gameList.get(0).getPlayer(2);
		}

		String sTotalGamesAgainstOpp = dOpp.toString();

		for (Game g : gameList) {
			if (g.isWinner(p)) {
				lGamesWon.add(g);
			}
		}

		float fGamesWon = lGamesWon.size();
		int iPercentage = Math.round(fGamesWon / gameList.size() * 100);

		pbGames.setMax(100);
		pbGames.setProgress(iPercentage);

		String sPercentWonAgainstOpp = Integer.toString(iPercentage)
				+ "% - Won " + lGamesWon.size() + " out of " + gameList.size()
				+ " games";

		tvTotalGamesAgainstOpp.setText(sTotalGamesAgainstOpp);
		tvPercentWonAgainstOpp.setText(sPercentWonAgainstOpp);
		llWonLostStats.addView(tvTotalGamesAgainstOpp);
		llWonLostStats.addView(pbGames);
		llWonLostStats.addView(tvPercentWonAgainstOpp);
	}

	private void populateGameSet(List<Game> gameList, Player p) {
		List<Game> lGamesWon = new ArrayList<Game>();
		TextView tvTotalGamesAgainstOpp = new TextView(this);
		ProgressBar pbGames = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);
		TextView tvPercentWonAgainstOpp = new TextView(this);
		Deck dOpp = new Deck();

		if (gameList.get(0).getPlayer(1).equals(p)) {
			dOpp = gameList.get(0).getDeck(2);
		} else {
			dOpp = gameList.get(0).getDeck(1);
		}

		String sTotalGamesAgainstOpp = dOpp.toString();

		for (Game g : gameList) {
			if (g.isWinner(p)) {
				lGamesWon.add(g);
			}
		}

		float fGamesWon = lGamesWon.size();
		int iPercentage = Math.round(fGamesWon / gameList.size() * 100);

		pbGames.setMax(100);
		pbGames.setProgress(iPercentage);

		String sPercentWonAgainstOpp = Integer.toString(iPercentage)
				+ "% - Won " + lGamesWon.size() + " out of " + gameList.size()
				+ " games";

		tvTotalGamesAgainstOpp.setText(sTotalGamesAgainstOpp);
		tvPercentWonAgainstOpp.setText(sPercentWonAgainstOpp);
		llWonLostStats.addView(tvTotalGamesAgainstOpp);
		llWonLostStats.addView(pbGames);
		llWonLostStats.addView(tvPercentWonAgainstOpp);
	}

	private void populateDeckSpinner() {
		mhDB.openReadableDB();
		allDecks = mhDB.getAllDecks();
		mhDB.closeDB();

		String[] stAllDecks = {};
		if (allDecks.isEmpty()) {
			stAllDecks[0] = "No Decks";
		} else {
			stAllDecks = new String[allDecks.size()];

			for (int i = 0; i < allDecks.size(); i++) {
				stAllDecks[i] = allDecks.get(i).toString();
			}
		}

		ArrayAdapter<String> deckAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stAllDecks);
		sCurrentSelection.setAdapter(deckAdapter);
	}

	private void populatePlayerSpinner() {
		mhDB.openReadableDB();
		allPlayers = mhDB.getAllPlayers();
		mhDB.closeDB();

		String[] stAllOwners = {};
		if (allPlayers.isEmpty()) {
			stAllOwners[0] = "No Players";
		} else {
			stAllOwners = new String[allPlayers.size()];

			for (int i = 0; i < allPlayers.size(); i++) {
				stAllOwners[i] = allPlayers.get(i).toString();
			}
		}

		ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stAllOwners);
		sCurrentSelection.setAdapter(ownerAdapter);
	}

	/*
	 * REMOVED FROM THIS SCREEN DUE TO REFRESH ISSUE WITH PREFERENCE CHANGES
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); MenuInflater mi = getMenuInflater();
	 * mi.inflate(R.menu.game_stats_menu, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.gameStatsPrefs: Intent gameStatsPrefs =
	 * new Intent("com.magichat.GAMESTATSPREFS"); startActivity(gameStatsPrefs);
	 * break; } return false; }
	 */

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		llWonLostStats.removeAllViews();
		dOpponents = new ArrayList<Deck>();
		playersDecks = new ArrayList<Deck>();
		llNonePlayed.setVisibility(LinearLayout.GONE);

		if (decksOrPlayersPref.contentEquals("Decks")) {
			currentDeck = sCurrentSelection.getSelectedItem().toString();
			llByDecks.setVisibility(LinearLayout.VISIBLE);
			llByPlayers.setVisibility(LinearLayout.GONE);
		} else {
			currentPlayer = sCurrentSelection.getSelectedItem().toString();
			llByDecks.setVisibility(LinearLayout.GONE);
			llByPlayers.setVisibility(LinearLayout.VISIBLE);
		}

		populateScreen();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private void initialize() {
		llNonePlayed = (LinearLayout) findViewById(R.id.llNonePlayed);
		llByDecks = (LinearLayout) findViewById(R.id.llByDecks);
		llByPlayers = (LinearLayout) findViewById(R.id.llByPlayers);
		sCurrentSelection = (Spinner) findViewById(R.id.sCurrentSelection);

		if (decksOrPlayersPref.contentEquals("Decks")) {
			llWonLostStats = (LinearLayout) findViewById(R.id.llWonLostStatsD);
			tvTotalGames = (TextView) findViewById(R.id.tvTotalGamesD);
			tvGamesWon = (TextView) findViewById(R.id.tvGamesWonD);
			tvGamesLost = (TextView) findViewById(R.id.tvGamesLostD);
			pbTotalWonLost = (ProgressBar) findViewById(R.id.pbTotalWonLostD);
		} else {
			llWonLostStats = (LinearLayout) findViewById(R.id.llWonLostStatsP);
			tvTotalGames = (TextView) findViewById(R.id.tvTotalGamesP);
			tvGamesWon = (TextView) findViewById(R.id.tvGamesWonP);
			tvGamesLost = (TextView) findViewById(R.id.tvGamesLostP);
			pbTotalWonLost = (ProgressBar) findViewById(R.id.pbTotalWonLostP);
		}

		sCurrentSelection.setOnItemSelectedListener(this);
	}
}
