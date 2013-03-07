package com.magichat.decks;

import java.util.List;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;

public class DecksMain extends MagicHatActivity implements
		OnItemSelectedListener {
	ListView lvDeckList;
	EditText etDeckSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decks_main);

		initialize();

		new populateDeckList().execute();
	}

	private class populateDeckList extends
			AsyncTask<String, Integer, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			List<Deck> allDecks = mhDb.getAllDecks();
			mhDb.closeDB();
			return allDecks;
		}

		@Override
		protected void onPostExecute(List<Deck> allDecks) {
			super.onPostExecute(allDecks);
			for (Deck d : allDecks) {

			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private void initialize() {
		lvDeckList = (ListView) findViewById(R.id.lvDeckList);
		etDeckSearch = (EditText) findViewById(R.id.etDeckSearch);
	}
}
