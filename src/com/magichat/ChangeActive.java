package com.magichat;

import java.util.ArrayList;
import java.util.List;

import com.magichat.decks.Deck;
import com.magichat.decks.db.MagicHatDB;
import com.magichat.players.Player;

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

	public final static String DEFAULT_DECK = "All Decks";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_active);

		initialize();

		new populateAllOwnersSpinner().execute();
	}

	// TODO Duplicate Code with UpdateDeck
	private class populateAllOwnersSpinner extends
			AsyncTask<String, Integer, ArrayAdapter<Player>> {

		@Override
		protected ArrayAdapter<Player> doInBackground(String... params) {
			List<Player> allOwners = new ArrayList<Player>();

			mhDb.openReadableDB();
			allOwners = mhDb.getAllOwners();
			mhDb.closeDB();

			if (allOwners.isEmpty()) {
				System.out.println("allOwners is empty!");
				finish();
			}

			ArrayAdapter<Player> ownerAdapter = new ArrayAdapter<Player>(
					ChangeActive.this, android.R.layout.simple_spinner_item,
					allOwners);

			return ownerAdapter;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Player> ownerAdapter) {
			super.onPostExecute(ownerAdapter);

			sAllOwners.setAdapter(ownerAdapter);
		}
	}

	// TODO Duplicate Code with UpdateDeck
	private class populateDecksSpinner extends
			AsyncTask<String, Integer, List<Deck>> {
		@Override
		protected List<Deck> doInBackground(String... args) {
			Player p = (Player) sAllOwners.getSelectedItem();

			mhDb.openReadableDB();
			deckList = mhDb.getDeckList(p);
			mhDb.closeDB();

			deckList.add(0, new Deck(DEFAULT_DECK, p));

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
				ArrayAdapter<Deck> deckAdapter = new ArrayAdapter<Deck>(
						ChangeActive.this,
						android.R.layout.simple_spinner_item, deckList);

				sOwnersDecks.setAdapter(deckAdapter);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		Player currentOwner = (Player) sAllOwners.getSelectedItem();
		Deck currentDeck = (Deck) sOwnersDecks.getSelectedItem();

		if (currentDeck.getName().equals(DEFAULT_DECK)) {

			new flipActiveStatusForPlayer().execute(currentOwner);

		} else {

			new flipActiveStatusForDeck().execute(currentDeck);
		}
	}

	private class flipActiveStatusForPlayer extends
			AsyncTask<Player, Integer, Player> {

		@Override
		protected Player doInBackground(Player... owners) {

			mhDb.openWritableDB();
			Player p = mhDb.flipActiveStatus(owners[0]);
			mhDb.closeDB();

			return p;
		}

		@Override
		protected void onPostExecute(Player p) {
			super.onPostExecute(p);

			String output = "";
			if (p.isActive()) {
				output = p.getName() + " is now active.";
			} else {
				output = p.getName() + " is now inactive.";
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
			AsyncTask<Deck, Integer, Deck> {

		@Override
		protected Deck doInBackground(Deck... decks) {

			mhDb.openWritableDB();
			Deck d = mhDb.flipActiveStatus(decks[0]);
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
			Deck currentDeck = (Deck) sOwnersDecks.getSelectedItem();
			if (!currentDeck.getName().equals(DEFAULT_DECK)) {
				tvActiveStatusChanged.setText("\n\n\n"
						+ sOwnersDecks.getSelectedItem().toString() + "\n\n\n");
			} else {
				tvActiveStatusChanged.setText("\n\n\n"
						+ sAllOwners.getSelectedItem().toString() + " has "
						+ deckList.size() + " decks.\n\n\n");
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
