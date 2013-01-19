package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddDeck extends Activity implements View.OnClickListener {
	List<Player> allOwners = new ArrayList<Player>();
	MagicHatDB mhDB = new MagicHatDB(this);

	EditText etDeckName;
	Spinner sAllOwners;
	ToggleButton tbActiveDeck;
	Button bAddDeck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_deck);
		initialize();

		new populateAllPlayers().execute();
	}

	private class populateAllPlayers extends
			AsyncTask<String, Integer, ArrayAdapter<String>> {

		@Override
		protected ArrayAdapter<String> doInBackground(String... params) {
			mhDB.openReadableDB();
			allOwners = mhDB.getAllPlayers();
			mhDB.closeDB();

			if (allOwners.isEmpty()) {
				System.out.println("allOwners is empty!");
				finish();
			}

			String[] stAllOwners = new String[allOwners.size()];

			for (int i = 0; i < allOwners.size(); i++) {
				stAllOwners[i] = allOwners.get(i).toString();
			}

			ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(
					AddDeck.this, android.R.layout.simple_spinner_item,
					stAllOwners);

			return ownerAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<String> ownerAdapter) {
			super.onPostExecute(ownerAdapter);
			sAllOwners.setAdapter(ownerAdapter);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bAddDeck:
			if (etDeckName.getText().toString().equals("")) {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setMessage(
						"Please enter a Deck Name in order to add the Deck.")
						.setNeutralButton("OK... Fine...",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog ad = adb.create();
				adb.setTitle("Deck Addition");
				ad.show();
				break;
			}

			new addDeck().execute();
			break;
		default:
			break;
		}
	}

	private class addDeck extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			int iActive = tbActiveDeck.isChecked() ? 1 : 0;
			Boolean isDup = false;

			Deck d = new Deck(etDeckName.getText().toString(), new Player(
					sAllOwners.getSelectedItem().toString()),
					tbActiveDeck.isChecked());

			mhDB.openWritableDB();
			int ownerId = mhDB.getPlayerId(sAllOwners.getSelectedItem()
					.toString());

			if (mhDB.deckExists(d)) {
				mhDB.closeDB();
				isDup = true;
			} else {
				mhDB.addDeck(etDeckName.getText().toString(), ownerId, iActive);
				mhDB.closeDB();
			}

			// TODO Enhance how to send information back and forth (Build a
			// Server)
			Email eAddDeck = new Email(AddDeck.this);
			eAddDeck.addDeck(d);

			return isDup;
		}

		@Override
		protected void onPostExecute(Boolean isDup) {
			super.onPostExecute(isDup);
			if (isDup) {
				showDuplicateMessage();
			} else {
				Toast.makeText(
						AddDeck.this,
						sAllOwners.getSelectedItem().toString() + "'s "
								+ etDeckName.getText().toString()
								+ " Deck was added successfully.",
						Toast.LENGTH_LONG).show();

				etDeckName.setText("");
			}

			// The next two lines of code hide the keyboard after addition
			InputMethodManager imm = (InputMethodManager) AddDeck.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(AddDeck.this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void showDuplicateMessage() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				"This deck is a duplicate of an already existing Deck.  "
						+ "Would you still like to add this deck?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new addDupDeck().execute();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		adb.setTitle("Deck Addition");
		ad.show();
	}

	private class addDupDeck extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			int iActive = tbActiveDeck.isChecked() ? 1 : 0;

			mhDB.openWritableDB();
			int ownerId = mhDB.getPlayerId(sAllOwners.getSelectedItem()
					.toString());
			mhDB.addDeck(etDeckName.getText().toString(), ownerId, iActive);
			mhDB.closeDB();

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(
					AddDeck.this,
					sAllOwners.getSelectedItem().toString() + "'s "
							+ etDeckName.getText().toString()
							+ " Deck was added successfully.",
					Toast.LENGTH_LONG).show();

			etDeckName.setText("");
		}

	}

	private void initialize() {
		etDeckName = (EditText) findViewById(R.id.etDeckName);
		sAllOwners = (Spinner) findViewById(R.id.sAllOwners);
		tbActiveDeck = (ToggleButton) findViewById(R.id.tbActiveDeck);
		bAddDeck = (Button) findViewById(R.id.bAddDeck);

		bAddDeck.setOnClickListener(this);
	}
}
