package com.magichat.decks;

import java.util.ArrayList;
import java.util.List;

import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

	List<Deck> decksToDelete = new ArrayList<Deck>();
	String sDeleteDecks = "";

	Button bDeleteDeck;
	TableLayout tlDeckList;
	LinearLayout llDeckList;
	Boolean flag = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_deck);
		initialize();

		new populateDecksToDelete().execute();

	}

	private class populateDecksToDelete extends
			AsyncTask<String, Integer, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(String... params) {
			MagicHatDb getAllInfoDB = new MagicHatDb(DeleteDeck.this);
			getAllInfoDB.openReadableDB();
			allDecks = getAllInfoDB.getAllManualDecks();
			getAllInfoDB.closeDB();

			return allDecks;
		}

		@Override
		protected void onPostExecute(List<Deck> result) {
			super.onPostExecute(result);

			if (allDecks.isEmpty()) {
				bDeleteDeck.setEnabled(false);
				TableRow trEmpty = new TableRow(DeleteDeck.this);
				TextView tvEmpty = new TextView(DeleteDeck.this);
				tvEmpty.setText("Dude...\nYou have no manually created decks to delete.");
				trEmpty.addView(tvEmpty);
				tlDeckList.addView(trEmpty);
			} else {
				for (Deck d : allDecks) {
					TableRow trDeck = new TableRow(DeleteDeck.this);
					String sDeckName = d.toString();
					CheckBox cbDeck = new CheckBox(DeleteDeck.this);
					cbDeck.setId(d.getId());
					cbDeck.setText(sDeckName);
					trDeck.addView(cbDeck);
					tlDeckList.addView(trDeck);
				}
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
					decksToDelete.add(d);
					sDeleteDecks = sDeleteDecks.concat("\n: " + d.toString());
				}
			}

			AlertDialog.Builder adb = new AlertDialog.Builder(DeleteDeck.this);
			adb.setMessage(
					"Are you sure you want to delete these Decks?"
							+ sDeleteDecks)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new deleteDecks().execute();
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

	private class deleteDecks extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			int[] dId = new int[decksToDelete.size()];

			MagicHatDb deleteDecksDB = new MagicHatDb(DeleteDeck.this);
			deleteDecksDB.openWritableDB();
			for (int i = 0; i < decksToDelete.size(); i++) {
				dId[i] = decksToDelete.get(i).getId();
			}
			deleteDecksDB.deleteDecks(dId);
			deleteDecksDB.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(DeleteDeck.this,
					sDeleteDecks + "\nHave been deleted.", Toast.LENGTH_LONG)
					.show();
			
			DeleteDeck.this.finish();
			Intent openDeleteDeckActivity = new Intent(
					"com.magichat.DELETEDECK");
			startActivity(openDeleteDeckActivity);
		}
	}

	private void initialize() {
		tlDeckList = (TableLayout) findViewById(R.id.tlDeckList);
		bDeleteDeck = (Button) findViewById(R.id.bDeleteDecks);
		llDeckList = (LinearLayout) findViewById(R.id.llDeckList);

		bDeleteDeck.setOnClickListener(this);
		bDeleteDeck.setEnabled(true);
	}
}
