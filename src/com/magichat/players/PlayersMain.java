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
	ListView lvPlayers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.players_main);

		initialize();

		new populatePlayersList().execute();
	}

	private class populatePlayersList extends
			AsyncTask<String, Integer, ArrayAdapter<Player>> {

		@Override
		protected ArrayAdapter<Player> doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayersMain.this);
			mhDb.openReadableDB();
			List<Player> allPlayers = mhDb.getAllPlayers();
			mhDb.closeDB();

			ArrayAdapter<Player> allPlayersAdapter = new ArrayAdapter<Player>(
					PlayersMain.this, android.R.layout.simple_list_item_1,
					allPlayers);

			return allPlayersAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Player> allPlayersAdapter) {
			super.onPostExecute(allPlayersAdapter);
			lvPlayers.setAdapter(allPlayersAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bAdd:
			Bundle emptyPlayerBundle = new Bundle();
			emptyPlayerBundle.putInt("playerId", 0);

			Intent openPlayerViewActivity = new Intent(
					"com.magichat.players.PLAYERVIEW");
			openPlayerViewActivity.putExtras(emptyPlayerBundle);
			startActivity(openPlayerViewActivity);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> playerList, View arg1, int pos,
			long arg3) {
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

		this.bAdd.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		this.bAdd.setOnClickListener(this);
	}
}
