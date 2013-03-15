package com.magichat.decks;

import java.util.List;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;
import com.magichat.quickaction.ActionItem;
import com.magichat.quickaction.QuickAction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DecksMain extends MagicHatActivity implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener {
	ListView lvDeckList;
	EditText etDeckSearch;

	QuickAction mQuickAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decks_main);

		mQuickAction = new QuickAction(this);

		initialize();

		new populateDeckList().execute();
	}

	private class populateDeckList extends
			AsyncTask<String, Integer, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			List<Deck> allDecks = mhDb.getAllDecks(false);
			mhDb.closeDB();

			return allDecks;
		}

		@Override
		protected void onPostExecute(List<Deck> allDecks) {
			super.onPostExecute(allDecks);

			// Getting adapter by passing deckList
			DecksMainAdapter deckAdapter = new DecksMainAdapter(DecksMain.this,
					allDecks);
			lvDeckList.setAdapter(deckAdapter);

			setupQuickActions();
		}
	}

	private void setupQuickActions() {
		// Add action item
		ActionItem editAction = new ActionItem();

		editAction.setTitle("Edit");
		editAction.setIcon(getResources().getDrawable(R.drawable.qa_edit));

		// Accept action item
		ActionItem deleteAction = new ActionItem();

		deleteAction.setTitle("Delete");
		deleteAction.setIcon(getResources().getDrawable(R.drawable.qa_trash));

		mQuickAction.addActionItem(editAction);
		mQuickAction.addActionItem(deleteAction);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(int pos) {
						if (pos == 0) {
							// Edit item selected

						} else if (pos == 1) {
							// Delete item selected

						}
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View clickedView,
			int position, long id) {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View clickedView,
			int position, long id) {
		mQuickAction.show(clickedView);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		return false;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ivDropdown:
			// This shows the parent of the dropdown button to match the
			// onItemLongClick
			mQuickAction.show((View) v.getParent());
			mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
			break;
		case R.id.bAdd:

		}
	}

	private void initialize() {
		lvDeckList = (ListView) findViewById(R.id.lvDeckList);
		etDeckSearch = (EditText) findViewById(R.id.etDeckSearch);
		lvDeckList.setOnItemClickListener(this);
		lvDeckList.setOnItemLongClickListener(this);

		this.bAdd.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		this.bAdd.setOnClickListener(this);
	}
}
