package com.magichat.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

public class PlayersMain extends MagicHatActivity implements
		OnItemClickListener {
	ListView lvPlayers;

	ArrayAdapter<Player> playerAdapter;
	List<Player> allPlayers = new ArrayList<Player>();
	Player selectedPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.players_main);

		initialize();

		new PopulatePlayersList().execute();
	}

	private class PopulatePlayersList extends
			AsyncTask<Void, Void, List<Player>> {

		@Override
		protected List<Player> doInBackground(Void... params) {
			MagicHatDb mhDb = new MagicHatDb(PlayersMain.this);
			mhDb.openReadableDB();
			List<Player> players = mhDb.getAllPlayers();
			mhDb.closeDB();

			return players;
		}

		@Override
		protected void onPostExecute(List<Player> players) {
			super.onPostExecute(players);
			allPlayers = players;

			playerAdapter = new ArrayAdapter<Player>(PlayersMain.this,
					android.R.layout.simple_list_item_1, allPlayers);
			lvPlayers.setAdapter(playerAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bAdd:
			selectedPlayer = new Player();

			openPlayerView();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> playerList, View view, int pos,
			long id) {
		selectedPlayer = (Player) playerList.getItemAtPosition(pos);

		openPlayerView();
	}

	private void openPlayerView() {
		Bundle playerBundle = new Bundle();
		playerBundle.putInt("playerId", selectedPlayer.getId());

		Intent openPlayerViewActivity = new Intent(
				"com.magichat.players.PLAYERVIEW");
		openPlayerViewActivity.putExtras(playerBundle);
		startActivity(openPlayerViewActivity);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new UpdatePlayersList().execute(selectedPlayer);
	}

	private class UpdatePlayersList extends AsyncTask<Player, Void, Player> {

		@Override
		protected Player doInBackground(Player... players) {
			Player p = new Player();

			MagicHatDb mhDb = new MagicHatDb(PlayersMain.this);
			mhDb.openReadableDB();
			// This will return the Player with the largest id if getId() == 0
			p = mhDb.getPlayer(players[0].getId());
			mhDb.closeDB();

			return p;
		}

		@Override
		protected void onPostExecute(Player p) {
			super.onPostExecute(p);

			// With selectedPlayer.getID() == 0 that implies that the
			// mhDb.getPlayer() will return the player with the largest id in
			// the DB
			if (selectedPlayer.getId() == 0) {
				// If the player with the largest id is not already shown in the
				// list then add it
				// Explanation: If the player with the largest id is already
				// shown, then no new player was added
				if (!allPlayers.contains(p)) {
					allPlayers.add(p);
				}
			} else {
				allPlayers.remove(selectedPlayer);
				if (p.getId() != 0) {
					// This will only add the player back to the list if the
					// selectedPlayer is still found in the DB.
					// If the selectedPlayer isn't found in the DB, then it was
					// deleted (from PlayerView) and shouldn't be added back
					allPlayers.add(p);
				}
			}

			Collections.sort(allPlayers);

			playerAdapter.notifyDataSetChanged();
		}

	}

	private void initialize() {
		lvPlayers = (ListView) findViewById(R.id.lvPlayers);
		lvPlayers.setOnItemClickListener(this);

		this.tvTitle.setText("Players' List");

		this.bAdd.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		this.bAdd.setOnClickListener(this);
	}
}
