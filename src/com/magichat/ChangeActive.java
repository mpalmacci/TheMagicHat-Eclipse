package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ChangeActive extends Activity implements View.OnClickListener,
		OnItemSelectedListener {
	// TODO Allow changing multiple Decks at the same time
	List<Player> allOwners = new ArrayList<Player>();
	List<Deck> deckList = new ArrayList<Deck>();
	MagicHatDB mhDb = new MagicHatDB(this);

	Spinner sAllOwners, sOwnersDecks;
	Button bFlipActiveStatus;
	TextView tvActiveStatusChanged;
	String sCurrentDeck, sCurrentOwner = "";

	String defaultDeck = "All Decks";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_active);
		initialize();
		populateAllOwnersSpinner();
	}

	@Override
	public void onClick(View arg0) {
		sCurrentOwner = sAllOwners.getSelectedItem().toString();
		sCurrentDeck = sOwnersDecks.getSelectedItem().toString();

		Deck d = new Deck();
		Player p = new Player();
		if (sCurrentDeck.equals(defaultDeck)) {
			String ownerName = "";

			// The name of the owner could be populated with " (inactive)" at
			// the end
			if (sAllOwners.getSelectedItem().toString().endsWith(")")) {
				ownerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf("(") - 1);
			} else {
				ownerName = sCurrentOwner;
			}

			mhDb.openWritableDB();
			p = mhDb.flipActiveStatus(new Player(ownerName));
			mhDb.closeDB();

			String output;
			if (p.isActive()) {
				output = p.toString() + " is now active.";
			} else {
				output = p.toString() + " is now inactive.";
			}

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setMessage(output).setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();

							ChangeActive.this.finish();
							Intent openChangeActiveStatusActivity = new Intent(
									"com.magichat.CHANGEACTIVE");
							startActivity(openChangeActiveStatusActivity);
						}
					});
			adb.setTitle("Player's Active Status");
			AlertDialog ad = adb.create();
			ad.show();
		} else {
			String sCurrentDeckName = sCurrentDeck.substring(0,
					sCurrentDeck.indexOf(" Deck"));
			String sCurrentOwnerName = "";

			if (sCurrentOwner.endsWith(")")) {
				sCurrentOwnerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf(" ("));
			} else {
				sCurrentOwnerName = sCurrentOwner;
			}

			mhDb.openWritableDB();
			d = mhDb.flipActiveStatus(sCurrentDeckName, sCurrentOwnerName);
			mhDb.closeDB();

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setMessage("The Deck is now\n" + d.toString())
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();

									ChangeActive.this.finish();
									Intent openChangeActiveStatusActivity = new Intent(
											"com.magichat.CHANGEACTIVE");
									startActivity(openChangeActiveStatusActivity);
								}
							});
			adb.setTitle("Deck's Active Status");
			AlertDialog ad = adb.create();
			ad.show();
		}
	}

	// TODO Duplicate Code with UpdateDeck
	private void populateAllOwnersSpinner() {
		mhDb.openReadableDB();
		allOwners = mhDb.getAllOwners();
		mhDb.closeDB();

		if (allOwners.isEmpty()) {
			System.out.println("allOwners is empty!");
			finish();
		}

		String[] stAllOwners = new String[allOwners.size()];

		for (int i = 0; i < allOwners.size(); i++) {
			stAllOwners[i] = allOwners.get(i).toString();
			if (!allOwners.get(i).isActive()) {
				stAllOwners[i] = stAllOwners[i].concat(" (inactive)");
			}
		}

		ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stAllOwners);

		sAllOwners.setAdapter(ownerAdapter);
	}

	// TODO Duplicate Code with UpdateDeck
	private void populateDecksSpinner(Player p) {
		mhDb.openReadableDB();
		deckList = mhDb.getDeckList(p);
		mhDb.closeDB();

		if (deckList.isEmpty()) {
			System.out.println("Deck List is empty!");
			String[] sEmpty = { "\n\nNo Decks\n\n" };
			ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, sEmpty);

			sOwnersDecks.setAdapter(ownerAdapter);
		} else {
			String[] sDeckList = new String[deckList.size() + 1];

			sDeckList[0] = defaultDeck;

			String deckName = "";
			for (int i = 1; i < deckList.size() + 1; i++) {
				deckName = deckList.get(i - 1).toString();
				// This removes the name of the Owner from the deckName
				sDeckList[i] = deckName.substring(deckName.indexOf("'s ") + 3);
			}

			ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, sDeckList);

			sOwnersDecks.setAdapter(ownerAdapter);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch (arg0.getId()) {
		case R.id.sAllOwners:
			String sCurrentOwner = sAllOwners.getSelectedItem().toString();
			String ownerName = "";

			// The name of the owner could be populated with " (inactive)" at
			// the end
			if (sAllOwners.getSelectedItem().toString().endsWith(")")) {
				ownerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf("(") - 1);
			} else {
				ownerName = sCurrentOwner;
			}
			Player p = new Player(ownerName);
			// TODO Do I need to set this to happen here???
			populateDecksSpinner(p);
			tvActiveStatusChanged.setText("\n\n\n" + ownerName + " has "
					+ deckList.size() + " decks.\n\n\n");
			break;
		case R.id.sOwnersDecks:
			if (!sOwnersDecks.getSelectedItem().toString().equals(defaultDeck)) {
				tvActiveStatusChanged.setText("\n\n"
						+ sAllOwners.getSelectedItem().toString() + "'s "
						+ sOwnersDecks.getSelectedItem().toString() + "\n\n");
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void initialize() {
		sAllOwners = (Spinner) findViewById(R.id.sAllOwners);
		sOwnersDecks = (Spinner) findViewById(R.id.sOwnersDecks);
		bFlipActiveStatus = (Button) findViewById(R.id.bFlipActiveStatus);
		tvActiveStatusChanged = (TextView) findViewById(R.id.tvActiveStatusChanged);

		sAllOwners.setOnItemSelectedListener(this);
		sOwnersDecks.setOnItemSelectedListener(this);

		bFlipActiveStatus.setOnClickListener(this);
	}
}
