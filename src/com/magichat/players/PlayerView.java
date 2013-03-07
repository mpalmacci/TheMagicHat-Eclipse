package com.magichat.players;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.Deck;
import com.magichat.decks.db.MagicHatDb;

public class PlayerView extends MagicHatActivity {
	EditText etPlayerName, etDci;
	CheckBox cbSelf, cbActive;

	TabHost thPlayer;
	ListView lvDeckList;

	boolean isSave;
	int playerId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_view);

		isSave = true;

		initialize();

		Bundle playerBundle = getIntent().getExtras();
		int playerId = playerBundle.getInt("playerId");

		// It equals zero when we are creating a new player
		if (playerId != 0) {
			new populatePlayerInfo().execute(playerId);
		}
		new populateDeckList().execute();
	}

	private class populatePlayerInfo extends
			AsyncTask<Integer, Integer, Player> {

		@Override
		protected Player doInBackground(Integer... playerIds) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openReadableDB();
			Player p = mhDb.getPlayer(playerIds[0]);
			mhDb.closeDB();
			return p;
		}

		@Override
		protected void onPostExecute(Player p) {
			super.onPostExecute(p);
			playerId = p.getId();

			etPlayerName.setText(p.getName());
			if (!Integer.toString(p.getDci()).equals("0")) {
				etDci.setText(Integer.toString(p.getDci()));
			}
			cbSelf.setChecked(p.isSelf());
			cbActive.setChecked(p.isActive());
		}
	}

	private class populateDeckList extends
			AsyncTask<String, Integer, ArrayAdapter<Deck>> {

		@Override
		protected ArrayAdapter<Deck> doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openReadableDB();
			List<Deck> allDecks = mhDb.getAllDecks();
			mhDb.closeDB();

			ArrayAdapter<Deck> allDecksAdapter = new ArrayAdapter<Deck>(
					PlayerView.this, android.R.layout.simple_list_item_multiple_choice,
					allDecks);
			return allDecksAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Deck> allDecksAdapter) {
			super.onPostExecute(allDecksAdapter);
			lvDeckList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			lvDeckList.setAdapter(allDecksAdapter);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isSave && !etPlayerName.getText().toString().isEmpty()) {
			new savePlayer().execute();
		}
		finish();
	}

	private class savePlayer extends AsyncTask<String, Integer, String> {
		Player p;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			String playerName = etPlayerName.getText().toString();
			boolean active = cbActive.isChecked();
			boolean self = cbSelf.isChecked();
			int dci = 0;
			if (!etDci.getText().toString().isEmpty()) {
				dci = Integer.parseInt(etDci.getText().toString());
			}

			p = new Player(playerName, dci, active, self);
		}

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openWritableDB();
			mhDb.writePlayer(p);
			mhDb.closeDB();
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		isSave = false;
		finish();
	}

	private void initialize() {
		etPlayerName = (EditText) findViewById(R.id.etPlayerName);
		etDci = (EditText) findViewById(R.id.etDci);
		cbActive = (CheckBox) findViewById(R.id.cbActive);
		cbSelf = (CheckBox) findViewById(R.id.cbSelf);

		this.bDelete.setVisibility(LinearLayout.VISIBLE);
		bDelete.setOnClickListener(this);
		lvDeckList = (ListView) findViewById(R.id.lvDeckList);

		thPlayer = (TabHost) findViewById(R.id.thPlayers);
		thPlayer.setup();
		TabSpec tsPlayer = thPlayer.newTabSpec("tag1");
		tsPlayer.setContent(R.id.tabPlayerDetails);
		tsPlayer.setIndicator("Player Details");
		thPlayer.addTab(tsPlayer);

		TabSpec tsPlayerDecks = thPlayer.newTabSpec("tag2");
		tsPlayerDecks.setContent(R.id.tabPlayerDecks);
		tsPlayerDecks.setIndicator("Player's Decks");
		thPlayer.addTab(tsPlayerDecks);
	}
}
