package com.magichat.players;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.Deck;
import com.magichat.decks.db.MagicHatDb;

public class PlayerView extends MagicHatActivity implements OnItemClickListener {
	EditText etPlayerName, etDci;
	CheckBox cbSelf, cbActive;
	PlayerViewAdapter allDecksAdapter;

	TabHost thPlayer;
	ListView lvDeckList;

	boolean isSave;
	Player currentPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_view);

		isSave = true;

		Bundle playerBundle = getIntent().getExtras();
		int playerId = playerBundle.getInt("playerId");

		initialize();

		// It equals zero when we are creating a new player
		if (playerId != 0) {
			new PopulatePlayerInfo().execute(playerId);
			currentPlayer = new Player();
			this.tvTitle.setText("Player's Details");
		} else {
			currentPlayer = new Player();
			this.tvTitle.setText("Create a New Player");
		}
		new PopulateDeckList().execute();
	}

	private class PopulatePlayerInfo extends
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
			currentPlayer = p;

			etPlayerName.setText(p.getName());
			if (p.getDci() != 0) {
				etDci.setText(Integer.toString(p.getDci()));
			}
			cbSelf.setChecked(p.isSelf());
			cbActive.setChecked(p.isActive());
		}
	}

	private class PopulateDeckList extends
			AsyncTask<String, Integer, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openReadableDB();
			List<Deck> allDecks = mhDb.getAllDecks(false);
			mhDb.closeDB();

			return allDecks;
		}

		@Override
		protected void onPostExecute(List<Deck> allDecks) {
			super.onPostExecute(allDecks);
			allDecksAdapter = new PlayerViewAdapter(PlayerView.this, allDecks,
					currentPlayer);
			lvDeckList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			lvDeckList.isClickable();
			lvDeckList.setAdapter(allDecksAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bCheck:
			isSave = true;
			PlayerView.this.finish();
			break;
		case R.id.bDelete:
			if (currentPlayer.getId() == 0) {
				isSave = false;
				PlayerView.this.finish();
			} else {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setMessage(
						"Are you sure you want to delete "
								+ currentPlayer.getName() + "?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										new DeletePlayer().execute();
										dialog.dismiss();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog ad = adb.create();
				adb.setTitle("Player Deletion");
				ad.show();
			}
			break;
		default:
			break;
		}
	}

	private class DeletePlayer extends AsyncTask<String, Integer, String> {
		Player p;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			p = currentPlayer;
		}

		@Override
		protected String doInBackground(String... args) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openWritableDB();
			mhDb.deletePlayer(p);
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			isSave = false;
			PlayerView.this.finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> deckList, View view, int pos, long id) {
		CheckBox cbOwner = (CheckBox) deckList.findViewWithTag(view.getTag());
		cbOwner.setChecked(!cbOwner.isChecked());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isSave && !etPlayerName.getText().toString().isEmpty()) {
			AsyncTask<String, Integer, String> pTask = new SavePlayer()
					.execute();
			try {
				// This will wait for the SavePlayer task to complete
				pTask.get(3000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
	}

	private class SavePlayer extends AsyncTask<String, Integer, String> {
		Player p;
		List<Deck> deckList;
		List<Deck> removeDeckList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (currentPlayer.getId() == 0) {
				String playerName = etPlayerName.getText().toString();
				boolean active = cbActive.isChecked();
				boolean self = cbSelf.isChecked();
				int dci = 0;
				if (!etDci.getText().toString().isEmpty()) {
					dci = Integer.parseInt(etDci.getText().toString());
				}

				p = new Player(playerName, dci, active, self);
			} else {
				currentPlayer.setName(etPlayerName.getText().toString());
				currentPlayer.setActive(cbActive.isChecked());
				currentPlayer.setSelf(cbSelf.isChecked());
				if (!etDci.getText().toString().isEmpty()) {
					currentPlayer.setDci(Integer.parseInt(etDci.getText()
							.toString()));
				}

				p = currentPlayer;
			}

			deckList = new ArrayList<Deck>();
			removeDeckList = new ArrayList<Deck>();
			int len = allDecksAdapter.getCount();
			SparseBooleanArray sba = allDecksAdapter.getItemsChecked();
			for (int i = 0; i < len; i++) {
				Deck d = (Deck) allDecksAdapter.getItem(i);

				if (sba.get(i) && !d.getOwner().equals(currentPlayer)) {
					// If the checkbox is checked, and the owner isn't the
					// current player
					deckList.add(d);
				} else if (!sba.get(i) && d.getOwner().equals(currentPlayer)) {
					// Else if the checkbox is not checked, and the owner is the
					// current player
					removeDeckList.add(d);
				}
			}
		}

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(PlayerView.this);
			mhDb.openWritableDB();
			mhDb.writePlayer(p);
			if (!deckList.isEmpty()) {
				mhDb.setDeckList(p.getId(), deckList);
			}

			if (!removeDeckList.isEmpty()) {
				mhDb.setDeckList(0, removeDeckList);
			}

			mhDb.closeDB();
			return null;
		}
	}

	private void initialize() {
		etPlayerName = (EditText) findViewById(R.id.etPlayerName);
		etDci = (EditText) findViewById(R.id.etDci);
		cbActive = (CheckBox) findViewById(R.id.cbActive);
		cbSelf = (CheckBox) findViewById(R.id.cbSelf);
		lvDeckList = (ListView) findViewById(R.id.lvDeckList);

		lvDeckList.setOnItemClickListener(this);
		this.bCheck.setVisibility(LinearLayout.VISIBLE);
		this.bCheck.setOnClickListener(this);
		this.bDelete.setVisibility(LinearLayout.VISIBLE);
		this.bDelete.setOnClickListener(this);

		thPlayer = (TabHost) findViewById(R.id.thPlayers);
		thPlayer.setup();

		setupTabs();
	}

	private void setupTabs() {
		TabSpec tsPlayerInfo = thPlayer.newTabSpec("tag1");
		tsPlayerInfo.setContent(R.id.tabPlayerDetails);
		View w = createTabView("Info", tsPlayerInfo);
		tsPlayerInfo.setIndicator(w);
		thPlayer.addTab(tsPlayerInfo);

		TabSpec tsPlayerDecks = thPlayer.newTabSpec("tag2");
		tsPlayerDecks.setContent(R.id.tabPlayerDecks);
		View v = createTabView("Decks", tsPlayerDecks);
		tsPlayerDecks.setIndicator(v);
		thPlayer.addTab(tsPlayerDecks);

		thPlayer.setCurrentTabByTag("tag1");
	}

	private View createTabView(String text, TabSpec tsTab) {
		View view = LayoutInflater.from(PlayerView.this).inflate(
				R.layout.tab_label, null);
		TextView tvLabel = (TextView) view.findViewById(R.id.tvTabLabel);
		tvLabel.setText(text);
		return view;
	}
}
