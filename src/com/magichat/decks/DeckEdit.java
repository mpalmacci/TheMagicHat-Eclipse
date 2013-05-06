package com.magichat.decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.cards.Card;
import com.magichat.decks.db.MagicHatDb;
import com.magichat.players.Player;

public class DeckEdit extends MagicHatActivity implements OnClickListener {
	EditText etDeckName;
	TextView tvCardQuantity;
	ListView lvDeckDetails;
	Spinner spDeckOwner;
	CheckBox cbActive;

	Deck currentDeck;

	boolean isSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_edit);

		Bundle bName = getIntent().getExtras();
		int dId = bName.getInt("deckId");

		isSave = true;

		initialize();

		if (dId == 0) {
			DeckEdit.this.tvTitle.setText("Create a New Deck");
			currentDeck = new Deck();
			new PopulateOwnerSpinner().execute();
		} else {
			// This will only flicker quickly while the background task runs
			this.tvTitle.setText("Edit Deck");
			new PopulateDeck().execute(dId);
		}
	}

	private class PopulateDeck extends AsyncTask<Integer, Integer, Deck> {

		@Override
		protected Deck doInBackground(Integer... deckIds) {
			MagicHatDb mhDb = new MagicHatDb(DeckEdit.this);
			mhDb.openReadableDB();
			Deck resultDeck = mhDb.getDeck(deckIds[0]);
			mhDb.closeDB();

			return resultDeck;
		}

		@Override
		protected void onPostExecute(Deck resultDeck) {
			super.onPostExecute(resultDeck);
			currentDeck = resultDeck;

			new PopulateOwnerSpinner().execute();

			DeckEdit.this.tvTitle.setText("Edit " + resultDeck.toString());

			etDeckName.setText(resultDeck.getName());
			cbActive.setChecked(resultDeck.isActive());
			tvCardQuantity.setText("(0)");
		}
	}

	private class PopulateOwnerSpinner extends
			AsyncTask<String, Integer, ArrayAdapter<Player>> {

		@Override
		protected ArrayAdapter<Player> doInBackground(String... params) {
			List<Player> allPlayers = new ArrayList<Player>();

			MagicHatDb mhDb = new MagicHatDb(DeckEdit.this);
			mhDb.openReadableDB();
			allPlayers = mhDb.getAllOwners();
			mhDb.closeDB();

			allPlayers.add(new Player());
			Collections.sort(allPlayers);

			ArrayAdapter<Player> playerAdapter = new ArrayAdapter<Player>(
					DeckEdit.this, R.layout.mh_spinner, allPlayers);
			return playerAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Player> playerAdapter) {
			super.onPostExecute(playerAdapter);
			playerAdapter.setDropDownViewResource(R.layout.mh_spinner_dropdown);
			// playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spDeckOwner.setAdapter(playerAdapter);

			if (currentDeck.getId() != 0) {
				spDeckOwner.setSelection(playerAdapter.getPosition(currentDeck
						.getOwner()));
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		if (!etDeckName.getText().toString().isEmpty() && isSave) {
			// The next two lines of code hide the keyboard after addition
			InputMethodManager imm = (InputMethodManager) DeckEdit.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(DeckEdit.this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			AsyncTask<String, Integer, String> dTask = new SaveDeck().execute();
			try {
				dTask.get(3000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		} else if (currentDeck.getId() != 0
				&& etDeckName.getText().toString().isEmpty()) {
			// TODO If the deck already exists and the name is empty then warn
			// the user of what they're doing
		}
	}

	private class SaveDeck extends AsyncTask<String, Integer, String> {
		List<Card> deckAllCards;
		Deck d;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			d = currentDeck;
			d.setName(etDeckName.getText().toString());
			d.setOwner((Player) spDeckOwner.getSelectedItem());
			d.setActive(cbActive.isChecked());
		}

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(DeckEdit.this);
			mhDb.openWritableDB();
			mhDb.writeDeck(d);
			mhDb.closeDB();
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bCheck:
			isSave = true;
			DeckEdit.this.finish();
			break;
		case R.id.bDelete:
			if (currentDeck.getId() == 0) {
				isSave = false;
				DeckEdit.this.finish();
			} else {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setMessage(
						"Are you sure you want to delete "
								+ currentDeck.toString() + "?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										new DeleteDeck().execute();
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

	private class DeleteDeck extends AsyncTask<Void, Void, Void> {
		Deck d;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			d = currentDeck;
		}

		@Override
		protected Void doInBackground(Void... params) {
			MagicHatDb mhDb = new MagicHatDb(DeckEdit.this);
			mhDb.openWritableDB();
			mhDb.deleteDeck(d);
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			isSave = false;
			// TODO This needs to send the user back to the DeckMain
			// because DeckView is null
			DeckEdit.this.finish();
		}
	}

	private void initialize() {
		etDeckName = (EditText) findViewById(R.id.etDeckName);
		tvCardQuantity = (TextView) findViewById(R.id.tvCardQuantity);
		lvDeckDetails = (ListView) findViewById(R.id.lvDeckDetails);
		spDeckOwner = (Spinner) findViewById(R.id.spDeckOwner);
		cbActive = (CheckBox) findViewById(R.id.cbActive);

		this.bCheck.setVisibility(LinearLayout.VISIBLE);
		this.bDelete.setVisibility(LinearLayout.VISIBLE);

		this.bCheck.setOnClickListener(this);
		this.bDelete.setOnClickListener(this);
	}
}
