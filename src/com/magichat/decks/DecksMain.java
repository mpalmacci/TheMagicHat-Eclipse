package com.magichat.decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.magichat.MagicHatActivity;
import com.magichat.R;
import com.magichat.decks.DecksMainAdapter.DeckMainViewHolder;
import com.magichat.decks.db.MagicHatDb;
import com.magichat.players.Player;
import com.magichat.quickaction.ActionItem;
import com.magichat.quickaction.QuickAction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class DecksMain extends MagicHatActivity implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener, OnItemSelectedListener {
	private ListView lvDeckList;
	private EditText etDeckSearch;
	private Spinner sDeckOwner;
	private QuickAction mQuickAction;

	private List<Deck> allDecks = new ArrayList<Deck>();
	private DecksMainAdapter deckAdapter;

	private Deck selectedDeck;

	private static final String ALL_DECKS = "All Decks";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decks_main);

		initialize();

		new PopulateDeckList().execute();
		new PopulateOwnerSpinner().execute();

		setupQuickActions();
	}

	private class PopulateDeckList extends AsyncTask<Void, Void, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(Void... params) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			List<Deck> decks = mhDb.getAllDecks(false);
			mhDb.closeDB();

			return decks;
		}

		@Override
		protected void onPostExecute(List<Deck> decks) {
			super.onPostExecute(decks);
			allDecks = decks;

			deckAdapter = new DecksMainAdapter(DecksMain.this, allDecks);

			lvDeckList.setAdapter(deckAdapter);
		}
	}

	private class PopulateOwnerSpinner extends
			AsyncTask<String, Integer, List<Player>> {

		@Override
		protected List<Player> doInBackground(String... params) {
			List<Player> allOwners = new ArrayList<Player>();

			MagicHatDb mhDB = new MagicHatDb(DecksMain.this);
			mhDB.openReadableDB();
			allOwners = mhDB.getAllOwners();
			mhDB.closeDB();

			return allOwners;
		}

		@Override
		protected void onPostExecute(List<Player> allOwners) {
			super.onPostExecute(allOwners);
			if (allOwners.isEmpty()) {
				sDeckOwner.setVisibility(LinearLayout.GONE);
			} else {
				allOwners.add(0, new Player(ALL_DECKS, true));

				ArrayAdapter<Player> ownerAdapter = new ArrayAdapter<Player>(
						DecksMain.this, R.layout.mh_spinner, allOwners);
				ownerAdapter
						.setDropDownViewResource(R.layout.mh_spinner_dropdown);
				sDeckOwner.setAdapter(ownerAdapter);
			}
		}
	}

	private void setupQuickActions() {

		mQuickAction = new QuickAction(DecksMain.this);

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
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (pos == 0) {
							openDeckActivity(selectedDeck, "Edit");
						} else if (pos == 1) {
							startDeleteDeck(selectedDeck);
						}
					}
				});
	}

	private void openDeckActivity(Deck d, String type) {
		Bundle deckId = new Bundle();
		deckId.putInt("deckId", d.getId());

		Intent openDeckActivity = new Intent("");

		if (type.equals("Edit")) {
			openDeckActivity = new Intent("com.magichat.decks.DECKEDIT");
		} else if (type.equals("View")) {
			openDeckActivity = new Intent("com.magichat.decks.DECKVIEW");
		}

		openDeckActivity.putExtras(deckId);
		startActivity(openDeckActivity);
	}

	private void startDeleteDeck(Deck d) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage("Are you sure you want to delete " + d.toString() + "?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new deleteDeck().execute();
								dialog.dismiss();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		adb.setTitle("Deck Deletion");
		ad.show();
	}

	private class deleteDeck extends AsyncTask<String, Void, String> {
		Deck d;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			d = selectedDeck;
		}

		@Override
		protected String doInBackground(String... args) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openWritableDB();
			mhDb.deleteDeck(d);
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			allDecks.remove(selectedDeck);
			deckAdapter.notifyDataSetChanged();
			Toast.makeText(DecksMain.this, d.toString() + " has been deleted",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View clickedView, int pos,
			long id) {
		selectedDeck = (Deck) lvDeckList.getItemAtPosition(pos);

		openDeckActivity(selectedDeck, "View");
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View clickedView,
			int position, long id) {
		selectedDeck = (Deck) lvDeckList.getItemAtPosition(position);

		mQuickAction.show(clickedView);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Player p = (Player) sDeckOwner.getSelectedItem();
		if (p.getName() != ALL_DECKS) {
			new FilterDeckList().execute(p);
		} else {
			new PopulateDeckList().execute();
		}
	}

	private class FilterDeckList extends AsyncTask<Player, Void, List<Deck>> {

		@Override
		protected List<Deck> doInBackground(Player... owners) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			List<Deck> ownersDecks = mhDb.getDeckList(owners[0], false);
			mhDb.closeDB();

			return ownersDecks;
		}

		@Override
		protected void onPostExecute(List<Deck> ownersDecks) {
			super.onPostExecute(ownersDecks);
			allDecks = ownersDecks;

			deckAdapter = new DecksMainAdapter(DecksMain.this, allDecks);

			lvDeckList.setAdapter(deckAdapter);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ivDropdown:
			DeckMainViewHolder holder = (DeckMainViewHolder) ((View) v
					.getParent()).getTag();

			String[] deckAndOwnerNames = {
					holder.tvDeckName.getText().toString(),
					holder.tvOwnerName.getText().toString() };

			// TODO Create Common Code!
			new getSelectedDeck().execute(deckAndOwnerNames);

			// This shows the parent of the drop down button to match the
			// onItemLongClick
			mQuickAction.show((View) v.getParent());
			mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
			break;
		case R.id.bAdd:
			selectedDeck = new Deck();
			openDeckActivity(new Deck(), "Edit");
			break;
		default:
			break;
		}
	}

	private class getSelectedDeck extends AsyncTask<String, Void, Deck> {
		@Override
		protected Deck doInBackground(String... deckAndOwnerNames) {
			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			Deck d = mhDb.getDeck(deckAndOwnerNames[0], deckAndOwnerNames[1]);
			mhDb.closeDB();
			return d;
		}

		@Override
		protected void onPostExecute(Deck d) {
			super.onPostExecute(d);
			selectedDeck = d;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		new UpdateDeckList().execute(selectedDeck);
	}

	private class UpdateDeckList extends AsyncTask<Deck, Void, Deck> {

		@Override
		protected Deck doInBackground(Deck... decks) {
			Deck d = new Deck();

			MagicHatDb mhDb = new MagicHatDb(DecksMain.this);
			mhDb.openReadableDB();
			// This will return the Deck with the largest id if getId() == 0
			d = mhDb.getDeck(decks[0].getId());
			mhDb.closeDB();

			return d;
		}

		@Override
		protected void onPostExecute(Deck d) {
			super.onPostExecute(d);
			if (selectedDeck.getId() == 0) {
				// If the deck with the largest id is not already shown in the
				// list then add it
				// Explanation: If the deck with the largest id is already
				// shown, then no new deck was added
				if (!allDecks.contains(d)) {
					allDecks.add(d);
				}
			} else {
				allDecks.remove(selectedDeck);

				if (d.getId() != 0) {
					// This will only add the deck back to the list if the
					// selectedDeck is still found in the DB.
					// If the selectedDeck isn't found in the DB, then it was
					// deleted (from DeckView -> DeckEdit) and shouldn't be
					// added back
					allDecks.add(d);
				}
			}

			Collections.sort(allDecks);

			deckAdapter.notifyDataSetChanged();
		}
	}

	private void initialize() {
		lvDeckList = (ListView) findViewById(R.id.lvDeckList);
		etDeckSearch = (EditText) findViewById(R.id.etDeckSearch);
		sDeckOwner = (Spinner) findViewById(R.id.sDeckOwner);
		lvDeckList.setOnItemClickListener(this);
		lvDeckList.setOnItemLongClickListener(this);
		sDeckOwner.setOnItemSelectedListener(this);

		this.tvTitle.setText("Decks' List");

		this.bAdd.setVisibility(LinearLayout.VISIBLE);
		this.bCardSearch.setVisibility(LinearLayout.VISIBLE);
		this.bAdd.setOnClickListener(this);
	}
}
