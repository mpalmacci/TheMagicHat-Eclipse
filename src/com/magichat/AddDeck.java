package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

		ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stAllOwners);

		sAllOwners.setAdapter(ownerAdapter);

		bAddDeck.setOnClickListener(this);
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

			int iActive;

			if (tbActiveDeck.isChecked()) {
				iActive = 1;
			} else {
				iActive = 0;
			}

			Deck d = new Deck(etDeckName.getText().toString(), new Player(
					sAllOwners.getSelectedItem().toString()),
					tbActiveDeck.isChecked(), 0);

			mhDB.openWritableDB();
			int ownerId = mhDB.getPlayerId(sAllOwners.getSelectedItem()
					.toString());
			Player p = mhDB.getOwner(ownerId);

			if (mhDB.deckExists(d)) {
				mhDB.closeDB();
				showDuplicateMessage();
			} else {
				mhDB.addDeck(etDeckName.getText().toString(), ownerId, iActive);
				mhDB.closeDB();
			}

			// TODO Enhance how to send information back and forth (Build a Server)
			Email eAddDeck = new Email(this);
			eAddDeck.addDeck(new Deck(etDeckName.getText().toString(), p,
					tbActiveDeck.isChecked()));

			Toast.makeText(
					AddDeck.this,
					sAllOwners.getSelectedItem().toString() + "'s "
							+ etDeckName.getText().toString()
							+ " Deck was added successfully.",
					Toast.LENGTH_LONG).show();
			/*
			 * REPLACED BY TOAST tvResults.setText("\n\n" +
			 * sAllOwners.getSelectedItem() .toString() + "'s " +
			 * etDeckName.getText().toString() +
			 * " Deck was added successfully.");
			 */

			etDeckName.setText("");

			// The next two lines of code hide the keyboard after addition
			InputMethodManager imm = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			break;
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
								// TODO Minimize Duplicate code
								int iActive;

								if (tbActiveDeck.isChecked()) {
									iActive = 1;
								} else {
									iActive = 0;
								}

								mhDB.openWritableDB();
								int ownerId = mhDB.getPlayerId(sAllOwners
										.getSelectedItem().toString());
								mhDB.addDeck(etDeckName.getText().toString(),
										ownerId, iActive);
								mhDB.closeDB();
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
	
	private void initialize() {
		etDeckName = (EditText) findViewById(R.id.etDeckName);
		sAllOwners = (Spinner) findViewById(R.id.sAllOwners);
		tbActiveDeck = (ToggleButton) findViewById(R.id.tbActiveDeck);
		bAddDeck = (Button) findViewById(R.id.bAddDeck);
	}
}
