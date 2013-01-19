package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
	MagicHatDB mhDb = new MagicHatDB(this);
	List<Deck> deckList = new ArrayList<Deck>();

	Spinner sAllOwners, sOwnersDecks;
	Button bFlipActiveStatus;
	TextView tvActiveStatusChanged;

	String defaultDeck = "All Decks";
	String currentOwnerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_active);

		initialize();

		new populateAllOwnersSpinner().execute();
	}

	// TODO Duplicate Code with UpdateDeck
	private class populateAllOwnersSpinner extends
			AsyncTask<String, Integer, List<Player>> {

		@Override
		protected List<Player> doInBackground(String... params) {
			List<Player> allOwners = new ArrayList<Player>();

			mhDb.openReadableDB();
			allOwners = mhDb.getAllOwners();
			mhDb.closeDB();

			return allOwners;
		}

		@Override
		protected void onPostExecute(List<Player> allOwners) {
			super.onPostExecute(allOwners);

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

			ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(
					ChangeActive.this, android.R.layout.simple_spinner_item,
					stAllOwners);
			sAllOwners.setAdapter(ownerAdapter);
		}
	}

	// TODO Duplicate Code with UpdateDeck
	private class populateDecksSpinner extends
			AsyncTask<String, Integer, List<Deck>> {
		@Override
		protected List<Deck> doInBackground(String... args) {
			String sCurrentOwner = sAllOwners.getSelectedItem().toString();

			// The name of the owner could be populated with " (inactive)" at
			// the end
			if (sAllOwners.getSelectedItem().toString().endsWith(")")) {
				currentOwnerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf("(") - 1);
			} else {
				currentOwnerName = sCurrentOwner;
			}
			Player p = new Player(currentOwnerName);

			mhDb.openReadableDB();
			deckList = mhDb.getDeckList(p);
			mhDb.closeDB();

			return deckList;
		}

		@Override
		protected void onPostExecute(List<Deck> deckList) {
			super.onPostExecute(deckList);

			if (deckList.isEmpty()) {
				System.out.println("Deck List is empty!");
				String[] sEmpty = { "\n\nNo Decks\n\n" };

				ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(
						ChangeActive.this,
						android.R.layout.simple_spinner_item, sEmpty);

				sOwnersDecks.setAdapter(ownerAdapter);
			} else {
				tvActiveStatusChanged.setText("\n\n\n" + currentOwnerName
						+ " has " + deckList.size() + " decks.\n\n\n");

				String[] sDeckList = new String[deckList.size() + 1];

				sDeckList[0] = defaultDeck;

				String deckName = "";
				for (int i = 1; i < deckList.size() + 1; i++) {
					deckName = deckList.get(i - 1).toString();
					// This removes the name of the Owner from the deckName
					sDeckList[i] = deckName
							.substring(deckName.indexOf("'s ") + 3);
				}

				ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(
						ChangeActive.this,
						android.R.layout.simple_spinner_item, sDeckList);

				sOwnersDecks.setAdapter(ownerAdapter);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		String sCurrentOwner = sAllOwners.getSelectedItem().toString();
		String sCurrentDeck = sOwnersDecks.getSelectedItem().toString();


		if (sCurrentDeck.equals(defaultDeck)) {
			sCurrentOwner = sAllOwners.getSelectedItem().toString();
			String ownerName = "";

			// The name of the owner could be populated with " (inactive)" at
			// the end
			if (sAllOwners.getSelectedItem().toString().endsWith(")")) {
				ownerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf("(") - 1);
			} else {
				ownerName = sCurrentOwner;
			}

			new flipActiveStatusForPlayer().execute(ownerName);

		} else {
			sCurrentDeck = sOwnersDecks.getSelectedItem().toString();

			String deckName = sCurrentDeck.substring(0,
					sCurrentDeck.indexOf(" Deck"));
			String ownerName = "";

			if (sCurrentOwner.endsWith(")")) {
				ownerName = sCurrentOwner.substring(0,
						sCurrentOwner.indexOf(" ("));
			} else {
				ownerName = sCurrentOwner;
			}

			String[] arguments = { deckName, ownerName };

			new flipActiveStatusForDeck().execute(arguments);

		}
	}

	private class flipActiveStatusForPlayer extends
			AsyncTask<String, Integer, Player> {

		@Override
		protected Player doInBackground(String... ownerName) {
			Player p = new Player();

			mhDb.openWritableDB();
			p = mhDb.flipActiveStatus(new Player(ownerName[0]));
			mhDb.closeDB();

			return p;
		}

		@Override
		protected void onPostExecute(Player p) {
			super.onPostExecute(p);

			String output = "";
			if (p.isActive()) {
				output = p.toString() + " is now active.";
			} else {
				output = p.toString() + " is now inactive.";
			}

			AlertDialog.Builder adb = new AlertDialog.Builder(ChangeActive.this);
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
		}
	}

	private class flipActiveStatusForDeck extends
			AsyncTask<String, Integer, Deck> {

		@Override
		protected Deck doInBackground(String... args) {
			Deck d = new Deck();

			mhDb.openWritableDB();
			d = mhDb.flipActiveStatus(args[0], args[1]);
			mhDb.closeDB();

			return d;
		}

		@Override
		protected void onPostExecute(Deck d) {
			super.onPostExecute(d);

			AlertDialog.Builder adb = new AlertDialog.Builder(ChangeActive.this);
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch (arg0.getId()) {
		case R.id.sAllOwners:
			new populateDecksSpinner().execute();
			break;
		case R.id.sOwnersDecks:
			if (!sOwnersDecks.getSelectedItem().toString().equals(defaultDeck)) {
				tvActiveStatusChanged.setText("\n\n"
						+ sAllOwners.getSelectedItem().toString() + "'s "
						+ sOwnersDecks.getSelectedItem().toString() + "\n\n");
			}
			break;
		default:
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
