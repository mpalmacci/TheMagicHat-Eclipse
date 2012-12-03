package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteDeck extends Activity implements View.OnClickListener {
	List<Deck> allDecks = new ArrayList<Deck>();

	List<Deck> deleteDecks = new ArrayList<Deck>();
	String sDeleteDecks = "";

	Button bDeleteDeck;
	TableLayout tlDeckList;
	LinearLayout llDeckList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_deck);

		tlDeckList = (TableLayout) findViewById(R.id.tlDeckList);
		bDeleteDeck = (Button) findViewById(R.id.bDeleteDecks);
		llDeckList = (LinearLayout) findViewById(R.id.llDeckList);

		bDeleteDeck.setOnClickListener(this);
		bDeleteDeck.setEnabled(true);

		MagicHatDB getAllInfoDB = new MagicHatDB(this);
		getAllInfoDB.openReadableDB();
		allDecks = getAllInfoDB.getAllManualDecks();
		getAllInfoDB.closeDB();

		if (allDecks.isEmpty()) {
			bDeleteDeck.setEnabled(false);
			TableRow trEmpty = new TableRow(this);
			TextView tvEmpty = new TextView(this);
			tvEmpty.setText("Dude...\nYou have no manually created decks to delete.");
			trEmpty.addView(tvEmpty);
			tlDeckList.addView(trEmpty);
		} else {
			for (Deck d : allDecks) {
				TableRow trDeck = new TableRow(this);
				String sDeckName = d.toString();
				CheckBox cbDeck = new CheckBox(this);
				cbDeck.setId(d.getId());
				cbDeck.setText(sDeckName);
				trDeck.addView(cbDeck);
				tlDeckList.addView(trDeck);
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bDeleteDecks:

			for (Deck d : allDecks) {
				CheckBox cb = (CheckBox) findViewById(d.getId());
				if (cb.isChecked()) {
					deleteDecks.add(d);
					sDeleteDecks = sDeleteDecks.concat("\n: " + d.toString());
				}
			}

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setMessage(
					"Are you sure you want to delete these Decks?"
							+ sDeleteDecks)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									int[] dId = new int[deleteDecks.size()];
									MagicHatDB deleteDecksDB = new MagicHatDB(
											DeleteDeck.this);
									deleteDecksDB.openWritableDB();
									for (int i = 0; i < deleteDecks.size(); i++) {
										dId[i] = deleteDecks.get(i).getId();
									}
									deleteDecksDB.deleteDecks(dId);
									deleteDecksDB.closeDB();

									Toast.makeText(
											DeleteDeck.this,
											sDeleteDecks
													+ "\nHave been deleted.",
											Toast.LENGTH_LONG).show();

									DeleteDeck.this.finish();
									Intent openDeleteDeckActivity = new Intent(
											"com.magichat.DELETEDECK");
									startActivity(openDeleteDeckActivity);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									sDeleteDecks = "";
									dialog.cancel();
								}
							});
			AlertDialog ad = adb.create();
			ad.show();
			break;
		default:
			break;
		}
	}
}
