package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

	List<Player> allOwners = new ArrayList<Player>();
	List<Deck> deckList = new ArrayList<Deck>();
	MagicHatDB mhDb = new MagicHatDB(this);

	Deck originalDeck, updatedDeck = new Deck();
	// The Owner doesn't change over time
	String ownerName, selectedDeckName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_deck);
		
		initialize();

		sdUpdateDeckCriteria.open();
		sdUpdateDeckCriteria.lock();
		populateAllOwnersSpinner();
	}

	// TODO Duplicate Code with ChangeActive
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

	// TODO Duplicate Code with ChangeActive
	private void populateDecksSpinner(Player p) {
		mhDb.openReadableDB();
		deckList = mhDb.getDeckList(p);
		mhDb.closeDB();

		if (deckList.isEmpty()) {
			System.out.println("Deck List is empty!");
			String[] sEmpty = { "No Decks" };
			ArrayAdapter<String> ownerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, sEmpty);

			sOwnersDecks.setAdapter(ownerAdapter);
		} else {
			String[] sDeckList = new String[deckList.size() + 1];

			sDeckList[0] = "";

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bUpdateDeck:
			updatedDeck = new Deck(etDeckName.getText().toString(), new Player(
					ownerName), tbActiveDeck.isChecked());

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setMessage(
					"Are you sure you want to update\n"
							+ originalDeck.toString() + "\nto\n"
							+ updatedDeck.toString())
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mhDb.openWritableDB();
									mhDb.updateDeck(ownerName,
											selectedDeckName, etDeckName
													.getText().toString(),
											tbActiveDeck.isChecked());
									mhDb.closeDB();
									
									Toast.makeText(UpdateDeck.this, originalDeck.toString() + " has been updated to "
											+ updatedDeck.toString(), Toast.LENGTH_SHORT).show();

									UpdateDeck.this.finish();
									Intent openUpdateDeckActivity = new Intent(
											"com.magichat.UPDATEDECK");
									startActivity(openUpdateDeckActivity);
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
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		String selectedOwnerNameWithInactive = sAllOwners.getSelectedItem()
				.toString();

		// The name of the owner could be populated with " (inactive)"
		// at the end
		if (selectedOwnerNameWithInactive.endsWith(")")) {
			ownerName = selectedOwnerNameWithInactive.substring(0,
					selectedOwnerNameWithInactive.indexOf("(") - 1);
		} else {
			ownerName = selectedOwnerNameWithInactive;
		}

		switch (arg0.getId()) {
		case R.id.sAllOwners:
			Player p = new Player(ownerName);

			populateDecksSpinner(p);

			break;
		case R.id.sOwnersDecks:
			if (!sOwnersDecks.getSelectedItem().toString().equals("")) {
				String deckName = sOwnersDecks.getSelectedItem().toString();

				if (deckName.endsWith(")")) {
					tbActiveDeck.setChecked(false);
				} else {
					tbActiveDeck.setChecked(true);
				}

				// This removes the word Deck from the end of the deckName
				selectedDeckName = deckName.substring(0,
						deckName.indexOf(" Deck"));

				etDeckName.setText(selectedDeckName);

				sdUpdateDeckCriteria.unlock();
				sdUpdateDeckCriteria.close();
				llSelectDeck.setVisibility(LinearLayout.GONE);
				llUpdateDeckMain.setVisibility(LinearLayout.VISIBLE);

				originalDeck = new Deck(selectedDeckName,
						new Player(ownerName), tbActiveDeck.isChecked());

				tvUpdateDeckResults.setText("\n\nWould you like to update "
						+ originalDeck.toString() + "?");
			} else {
				selectedDeckName = "";
				sdUpdateDeckCriteria.open();
				sdUpdateDeckCriteria.lock();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) { }

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
