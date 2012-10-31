package com.magichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAllDecks extends Activity {

	List<Deck> allDecks = new ArrayList<Deck>();
	String sAllDecks = "";

	TextView tvDisplayAllDecks, tvDeckListEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_all_decks);
		tvDisplayAllDecks = (TextView) findViewById(R.id.tvDisplayAllDecks);
		tvDeckListEnd = (TextView) findViewById(R.id.tvDeckListEnd);

		MagicHatDB getAllDecksDB = new MagicHatDB(this);
		getAllDecksDB.openReadableDB();
		allDecks = getAllDecksDB.getAllDecks();
		getAllDecksDB.closeDB();

		if (allDecks.isEmpty()) {
			sAllDecks = "You have no decks in your Database!";
		} else {
			// Order the Decks before displaying them
			Collections.sort(allDecks);

			for (Deck d : allDecks) {
				sAllDecks = sAllDecks.concat(d.toString()).concat("\n");
			}
			tvDeckListEnd.setText("There are a total of " + allDecks.size()
					+ " Decks in the list.");
		}

		tvDisplayAllDecks.setText(sAllDecks);
	}
}
