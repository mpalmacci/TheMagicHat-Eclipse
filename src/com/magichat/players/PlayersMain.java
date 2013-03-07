package com.magichat.players;

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
	List<Player> allPlayers;

	ListView lvPlayers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.players_main);

		initialize();

		new populatePlayersList().execute();
	}

	private class populatePlayersList extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayersMain.this);
			mhDb.openReadableDB();
			allPlayers = mhDb.getAllPlayers();
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ArrayAdapter<Player> allPlayersAdapter = new ArrayAdapter<Player>(
					PlayersMain.this, android.R.layout.simple_list_item_1,
					allPlayers);
			lvPlayers.setAdapter(allPlayersAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> playerList, View arg1, int pos, long arg3) {
		Player p = (Player) playerList.getItemAtPosition(pos);
		
		Bundle playerBundle = new Bundle();
		playerBundle.putInt("playerId", p.getId());
		
		Intent openPlayerViewActivity = new Intent(
				"com.magichat.players.PLAYERVIEW");
		openPlayerViewActivity.putExtras(playerBundle);
		startActivity(openPlayerViewActivity);
	}

	private void initialize() {
		lvPlayers = (ListView) findViewById(R.id.lvPlayers);
		lvPlayers.setOnItemClickListener(this);

		this.bAddPlayer.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
	}
}
