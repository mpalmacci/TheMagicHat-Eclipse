package com.magichat.decks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

public class DeckView extends MagicHatActivity {
	TextView tvCardQuantity;

	Deck currentDeck;

	TabHost thDeckView;
	ListView lvDeckCardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_view);

		Bundle bName = getIntent().getExtras();
		int dId = bName.getInt("deckId");

		initialize();

		new getDeck().execute(dId);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new getDeck().execute(currentDeck.getId());
	}

	private class getDeck extends AsyncTask<Integer, Integer, Deck> {

		@Override
		protected Deck doInBackground(Integer... deckIds) {
			MagicHatDb mhDb = new MagicHatDb(DeckView.this);
			mhDb.openReadableDB();
			Deck resultDeck = mhDb.getDeck(deckIds[0]);
			mhDb.closeDB();

			return resultDeck;
		}

		@Override
		protected void onPostExecute(Deck resultDeck) {
			super.onPostExecute(resultDeck);
			currentDeck = resultDeck;

			DeckView.this.tvTitle.setText(currentDeck.toString());
			tvCardQuantity.setText("(0)");
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bEdit:
			Bundle deckId = new Bundle();
			deckId.putInt("deckId", currentDeck.getId());

			Intent openDeckEditActivity = new Intent(
					"com.magichat.decks.DECKEDIT");
			openDeckEditActivity.putExtras(deckId);
			startActivity(openDeckEditActivity);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		this.bEdit.setVisibility(LinearLayout.VISIBLE);
		this.bEdit.setOnClickListener(this);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		this.tvTitle.setText("Player's Deck");

		thDeckView = (TabHost) findViewById(R.id.thDeckView);
		thDeckView.setup();

		setupTabs();

		tvCardQuantity = (TextView) findViewById(R.id.tvCardQuantity);
		lvDeckCardList = (ListView) findViewById(R.id.lvDeckCardList);
	}

	private void setupTabs() {
		TabSpec tsCards = thDeckView.newTabSpec("tag1");
		tsCards.setContent(R.id.tabCards);
		View w = createTabView("Cards", tsCards);
		tsCards.setIndicator(w);
		thDeckView.addTab(tsCards);

		TabSpec tsStats = thDeckView.newTabSpec("tag2");
		tsStats.setContent(R.id.tabStats);
		View v = createTabView("Stats", tsStats);
		tsStats.setIndicator(v);
		thDeckView.addTab(tsStats);

		thDeckView.setCurrentTabByTag("tag1");
	}

	private View createTabView(String text, TabSpec tsTab) {
		View view = LayoutInflater.from(DeckView.this).inflate(
				R.layout.tab_label, null);
		TextView tvLabel = (TextView) view.findViewById(R.id.tvTabLabel);
		tvLabel.setText(text);
		return view;
	}
}