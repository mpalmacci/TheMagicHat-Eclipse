package com.magichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class GameStatsForPlayer extends Activity implements
		OnItemSelectedListener {
	float totalGamesWon, totalGamesLost = 0;
	List<Deck> allDecks = new ArrayList<Deck>();
	List<Player> allPlayers = new ArrayList<Player>();
	List<Deck> dOpponents = new ArrayList<Deck>();
	List<Deck> playersDecks = new ArrayList<Deck>();

	LinearLayout llByPlayers, llWonLostStats, llNonePlayed;
	Spinner sPlayerSelection;
	TextView tvTotalGames, tvGamesWon, tvGamesLost, tvEmptyList;
	ProgressBar pbTotalWonLost;

	Player currentPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_stats_for_player);

		initialize();

		new populatePlayerSpinner().execute();
	}

	private class populatePlayerSpinner extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			MagicHatDB mhDB = new MagicHatDB(GameStatsForPlayer.this);
			mhDB.openReadableDB();
			allPlayers = mhDB.getAllPlayers();
			mhDB.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (allPlayers.isEmpty()) {
				llByPlayers.setVisibility(LinearLayout.GONE);
				sPlayerSelection.setVisibility(LinearLayout.GONE);
				llNonePlayed.setVisibility(LinearLayout.VISIBLE);
				tvEmptyList.setText("There are No Players in the Database!");
			} else {
				ArrayAdapter<Player> ownerAdapter = new ArrayAdapter<Player>(
						GameStatsForPlayer.this,
						android.R.layout.simple_spinner_item, allPlayers);
				sPlayerSelection.setAdapter(ownerAdapter);

				currentPlayer = (Player) sPlayerSelection.getSelectedItem();
				new populateScreen().execute();
			}
		}
	}

	private class populateScreen extends AsyncTask<String, Integer, String> {
		List<Game> games = new ArrayList<Game>();

		@Override
		protected String doInBackground(String... params) {
			MagicHatDB mhDB = new MagicHatDB(GameStatsForPlayer.this);

			mhDB.openReadableDB();
			games = mhDB.getGames(currentPlayer);
			mhDB.closeDB();

			getStats(games);

			return null;
		}

		@Override
		protected void onPostExecute(String results) {
			super.onPostExecute(results);
			if (games.isEmpty()) {
				llByPlayers.setVisibility(LinearLayout.GONE);
				llNonePlayed.setVisibility(LinearLayout.VISIBLE);
			} else {
				tvTotalGames.setText(Integer.toString(games.size()));
				tvGamesWon.setText(Integer.toString(Math.round(totalGamesWon)));
				int iPercentage = Math
						.round(totalGamesWon / games.size() * 100);
				pbTotalWonLost.setProgress(iPercentage);
				tvGamesLost
						.setText(Integer.toString(Math.round(totalGamesLost)));

				// Get a list of games based on your opponents and populate the
				// Game Results in a separate method
				for (Deck dOpp : dOpponents) {
					List<Game> gameList = new ArrayList<Game>();
					for (Game g : games) {
						if (g.getDeck(1).equals(dOpp)
								|| g.getDeck(2).equals(dOpp)) {
							gameList.add(g);
						}
					}

					populateGamesForPlayers(gameList);
				}

				llByPlayers.setVisibility(LinearLayout.VISIBLE);
			}
		}
	}

	private void populateGamesForPlayers(List<Game> gameList) {
		List<Game> lGamesWon = new ArrayList<Game>();
		TextView tvTotalGamesAgainstOpp = new TextView(GameStatsForPlayer.this);
		ProgressBar pbGames = new ProgressBar(GameStatsForPlayer.this, null,
				android.R.attr.progressBarStyleHorizontal);
		TextView tvPercentWonAgainstOpp = new TextView(GameStatsForPlayer.this);
		Deck dOpp = new Deck();
		String sTotalGamesAgainstOpp, sPercentWonAgainstOpp;

		if (gameList.get(0).getPlayer(1).equals(currentPlayer)) {
			dOpp = gameList.get(0).getDeck(2);
		} else {
			dOpp = gameList.get(0).getDeck(1);
		}

		sTotalGamesAgainstOpp = dOpp.toString();

		for (Game g : gameList) {
			if (g.isWinner(currentPlayer)) {
				lGamesWon.add(g);
			}
		}

		float fGamesWon = lGamesWon.size();
		int iPercentage = Math.round(fGamesWon / gameList.size() * 100);

		pbGames.setMax(100);
		pbGames.setProgress(iPercentage);

		sPercentWonAgainstOpp = Integer.toString(iPercentage) + "% - Won "
				+ lGamesWon.size() + " out of " + gameList.size() + " games";

		tvTotalGamesAgainstOpp.setText(sTotalGamesAgainstOpp);
		tvPercentWonAgainstOpp.setText(sPercentWonAgainstOpp);
		llWonLostStats.addView(tvTotalGamesAgainstOpp);
		llWonLostStats.addView(pbGames);
		llWonLostStats.addView(tvPercentWonAgainstOpp);

	}

	private void getStats(List<Game> games) {
		List<Game> gamesWon = new ArrayList<Game>(), gamesLost = new ArrayList<Game>();

		for (Game g : games) {
			// Only add the Player if opponents doesn't consist of the Player
			// yet
			if (g.getPlayer(1).equals(currentPlayer)) {
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

			if (g.isWinner(currentPlayer)) {
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		llWonLostStats.removeAllViews();
		dOpponents = new ArrayList<Deck>();
		playersDecks = new ArrayList<Deck>();
		llNonePlayed.setVisibility(LinearLayout.GONE);

		currentPlayer = (Player) sPlayerSelection.getSelectedItem();
		new populateScreen().execute();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private void initialize() {
		llNonePlayed = (LinearLayout) findViewById(R.id.llNonePlayed);
		llByPlayers = (LinearLayout) findViewById(R.id.llByPlayers);
		sPlayerSelection = (Spinner) findViewById(R.id.sCurrentSelection);
		tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);

		llWonLostStats = (LinearLayout) findViewById(R.id.llWonLostStatsP);
		tvTotalGames = (TextView) findViewById(R.id.tvTotalGamesP);
		tvGamesWon = (TextView) findViewById(R.id.tvGamesWonP);
		tvGamesLost = (TextView) findViewById(R.id.tvGamesLostP);
		pbTotalWonLost = (ProgressBar) findViewById(R.id.pbTotalWonLostP);

		sPlayerSelection.setOnItemSelectedListener(this);
	}
}
