package com.magichat.decks;

import java.util.ArrayList;
import java.util.List;

import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;
import com.magichat.players.Player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class UpdateDeck extends Activity implements OnClickListener,
		OnItemSelectedListener, OnDrawerOpenListener, OnDrawerCloseListener {

	Button bUpdateDeck;
	EditText etDeckName;
	ToggleButton tbActiveDeck;
	TextView tvUpdateDeckResults;
	SlidingDrawer sdUpdateDeckCriteria;
	Spinner sAllOwners, sOwnersDecks;
	LinearLayout llSelectDeck, llUpdateDeckMain;

	Deck originalDeck, updatedDeck = new Deck();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_deck);

		initialize();

		sdUpdateDeckCriteria.open();
		sdUpdateDeckCriteria.lock();

		new populateAllOwnersSpinner().execute();
	}

	// TODO Duplicate Code with ChangeActive
	private class populateAllOwnersSpinner extends
			AsyncTask<String, Integer, ArrayAdapter<Player>> {

		@Override
		protected ArrayAdapter<Player> doInBackground(String... params) {
			List<Player> allOwners = new ArrayList<Player>();

			MagicHatDb mhDb = new MagicHatDb(UpdateDeck.this);
			mhDb.openReadableDB();
			allOwners = mhDb.getAllOwners();
			mhDb.closeDB();
			if (allOwners.isEmpty()) {
				System.out.println("allOwners is empty!");
				finish();
			}

			ArrayAdapter<Player> ownerAdapter = new ArrayAdapter<Player>(
					UpdateDeck.this, android.R.layout.simple_spinner_item,
					allOwners);

			return ownerAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Player> ownerAdapter) {
			super.onPostExecute(ownerAdapter);

			sAllOwners.setAdapter(ownerAdapter);
		}

	}

	// TODO Duplicate Code with ChangeActive
	private class populateDecksSpinner extends
			AsyncTask<Player, Integer, ArrayAdapter<Deck>> {

		@Override
		protected ArrayAdapter<Deck> doInBackground(Player... p) {
			List<Deck> deckList = new ArrayList<Deck>();

			MagicHatDb mhDb = new MagicHatDb(UpdateDeck.this);
			mhDb.openReadableDB();
			// TODO Defect found here where the deck-list isn't showing correct
			// information
			deckList = mhDb.getDeckList(p[0]);
			mhDb.closeDB();

			if (deckList.isEmpty()) {
				System.out.println("Deck List is empty!");
				String sEmpty = "No Decks";

				deckList.add(new Deck(sEmpty, p[0], true));
			} else {
				deckList.add(0, new Deck());
			}
			ArrayAdapter<Deck> deckAdapter = new ArrayAdapter<Deck>(UpdateDeck.this,
					android.R.layout.simple_spinner_item, deckList);

			return deckAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Deck> deckAdapter) {
			super.onPostExecute(deckAdapter);

			sOwnersDecks.setAdapter(deckAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bUpdateDeck:
			updatedDeck = new Deck(originalDeck.getId(), etDeckName.getText()
					.toString(), originalDeck.getOwner(),
					tbActiveDeck.isChecked());

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setMessage(
					"Are you sure you want to update\n"
							+ originalDeck.toString() + "\nto\n"
							+ updatedDeck.toString())
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new updateDeck().execute();
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
			adb.setTitle("Deck Updation");
			ad.show();
			break;
		default:
			break;
		}
	}

	private class updateDeck extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			MagicHatDb mhDb = new MagicHatDb(UpdateDeck.this);
			mhDb.openWritableDB();
			mhDb.writeDeck(updatedDeck);
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String results) {
			super.onPostExecute(results);
			Toast.makeText(
					UpdateDeck.this,
					originalDeck.toString() + " has been updated to "
							+ updatedDeck.toString(), Toast.LENGTH_SHORT)
					.show();

			UpdateDeck.this.finish();
			Intent openUpdateDeckActivity = new Intent(
					"com.magichat.UPDATEDECK");
			startActivity(openUpdateDeckActivity);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Player selectedOwner = (Player) sAllOwners.getSelectedItem();

		switch (arg0.getId()) {
		case R.id.sAllOwners:

			new populateDecksSpinner().execute(selectedOwner);

			break;
		case R.id.sOwnersDecks:
			originalDeck = (Deck) sOwnersDecks.getSelectedItem();

			if (!originalDeck.getName().isEmpty()) {

				if (originalDeck.isActive()) {
					tbActiveDeck.setChecked(true);
				} else {
					tbActiveDeck.setChecked(false);
				}

				etDeckName.setText(originalDeck.getName());

				sdUpdateDeckCriteria.unlock();
				sdUpdateDeckCriteria.close();
				llSelectDeck.setVisibility(LinearLayout.GONE);
				llUpdateDeckMain.setVisibility(LinearLayout.VISIBLE);

				tvUpdateDeckResults.setText("\n\nWould you like to update "
						+ originalDeck.toString() + "?");
			} else {
				sdUpdateDeckCriteria.open();
				sdUpdateDeckCriteria.lock();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onDrawerClosed() {
		llUpdateDeckMain.setVisibility(LinearLayout.VISIBLE);
	}

	@Override
	public void onDrawerOpened() {
		llUpdateDeckMain.setVisibility(LinearLayout.INVISIBLE);
	}

	private void initialize() {
		bUpdateDeck = (Button) findViewById(R.id.bUpdateDeck);
		etDeckName = (EditText) findViewById(R.id.etDeckName);
		tbActiveDeck = (ToggleButton) findViewById(R.id.tbActiveDeck);
		tvUpdateDeckResults = (TextView) findViewById(R.id.tvUpdateDeckResults);
		sdUpdateDeckCriteria = (SlidingDrawer) findViewById(R.id.sdUpdateDeckCriteria);
		sAllOwners = (Spinner) findViewById(R.id.sAllOwners);
		sOwnersDecks = (Spinner) findViewById(R.id.sOwnersDecks);
		llSelectDeck = (LinearLayout) findViewById(R.id.llSelectDeck);
		llUpdateDeckMain = (LinearLayout) findViewById(R.id.llUpdateDeckMain);

		sAllOwners.setOnItemSelectedListener(this);
		sOwnersDecks.setOnItemSelectedListener(this);

		sdUpdateDeckCriteria.setOnDrawerOpenListener(this);
		sdUpdateDeckCriteria.setOnDrawerCloseListener(this);

		bUpdateDeck.setOnClickListener(this);
	}
}
