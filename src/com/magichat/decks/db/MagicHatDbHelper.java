package com.magichat.decks.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.magichat.decks.Deck;
import com.magichat.decks.games.Game;
import com.magichat.players.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MagicHatDbHelper extends SQLiteOpenHelper {
	private static final String KEY_DECK_ROWID = "_id";
	private static final String KEY_DECK_NAME = "deck_name";
	private static final String KEY_DECK_OWNERID = "owner_id";
	private static final String KEY_DECK_ACTIVE = "active_sts";

	private static final String KEY_PLAYER_ROWID = "_id";
	private static final String KEY_PLAYER_NAME = "player_name";
	private static final String KEY_PLAYER_ACTIVE = "active_sts";
	private static final String KEY_PLAYER_DCI = "dci";
	private static final String KEY_PLAYER_SELF = "self";

	private static final String KEY_GAME_ROWID = "_id";
	private static final String KEY_GAME_PLAYER1 = "player_1";
	private static final String KEY_GAME_PLAYER2 = "player_2";
	private static final String KEY_GAME_DECK1 = "deck_1";
	private static final String KEY_GAME_DECK2 = "deck_2";
	private static final String KEY_GAME_WINNER = "winner";
	private static final String KEY_GAME_DATE = "game_date";

	private static final String DB_TABLE_ALLDECKS = "Decks";
	private static final String DB_TABLE_ALLPLAYERS = "Players";
	private static final String DB_TABLE_ALLGAMES = "Games";

	private Context mhContext;

	private boolean isUpgrade = false;

	public MagicHatDbHelper(Context mhContext, String dbName, int dbVersion) {
		super(mhContext, dbName, null, dbVersion);
		this.mhContext = mhContext;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// if (dbName.equals(MagicHatDbHelper.MH_DB_NAME)) {
		Log.i("MagicHatDbHelper.onCreate",
				"Code Path for MagicHatDb OnCreate was triggered.");

		db.execSQL("CREATE TABLE " + DB_TABLE_ALLPLAYERS + " ("
				+ KEY_PLAYER_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_PLAYER_NAME + " TEXT NOT NULL, " + KEY_PLAYER_DCI
				+ " INTEGER, " + KEY_PLAYER_ACTIVE + " INTEGER NOT NULL, "
				+ KEY_PLAYER_SELF + " INTEGER NOT NULL);");
		db.execSQL("CREATE TABLE " + DB_TABLE_ALLDECKS + " (" + KEY_DECK_ROWID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DECK_NAME
				+ " TEXT NOT NULL, " + KEY_DECK_OWNERID + " INTEGER NOT NULL, "
				+ KEY_DECK_ACTIVE + " INTEGER NOT NULL, FOREIGN KEY("
				+ KEY_DECK_OWNERID + ") REFERENCES " + DB_TABLE_ALLPLAYERS
				+ "(" + KEY_PLAYER_ROWID + "));");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_ALLGAMES + " ("
				+ KEY_GAME_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_GAME_PLAYER1 + " INTEGER NOT NULL, " + KEY_GAME_PLAYER2
				+ " INTEGER NOT NULL, " + KEY_GAME_DECK1
				+ " INTEGER NOT NULL, " + KEY_GAME_DECK2
				+ " INTEGER NOT NULL, " + KEY_GAME_WINNER
				+ " INTEGER NOT NULL, " + KEY_GAME_DATE
				+ " INTEGER NOT NULL, FOREIGN KEY(" + KEY_GAME_PLAYER1
				+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
				+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_PLAYER2
				+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
				+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_DECK1
				+ ") REFERENCES " + DB_TABLE_ALLDECKS + "(" + KEY_DECK_ROWID
				+ "), FOREIGN KEY(" + KEY_GAME_DECK2 + ") REFERENCES "
				+ DB_TABLE_ALLDECKS + "(" + KEY_DECK_ROWID + "), FOREIGN KEY("
				+ KEY_GAME_WINNER + ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
				+ KEY_PLAYER_ROWID + "));");

		if (!isUpgrade) {
			setupPlayersAndDecks(new ArrayList<Deck>(), db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// super.onDowngrade(db, oldVersion, newVersion);
		onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// if (dbName.equals(MagicHatDbHelper.MH_DB_NAME)) {
		Log.i("MagicHatDbHelper.onUpgrade",
				"Code Path for MagicHatDb onUpgrade was triggered.");

		isUpgrade = true;
		List<Game> allGames = new ArrayList<Game>();
		List<Deck> allDecks = new ArrayList<Deck>();

		// TODO This requires a certain db schema to be successful
		allGames = getAllGames(db);
		allDecks = getAllDecks(false, db);

		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLDECKS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLPLAYERS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLGAMES);

		onCreate(db);

		setupPlayersAndDecks(allDecks, db);
		populateAllGames(allGames, db);

	}

	public boolean isUpgrade() {
		return this.isUpgrade;
	}

	// ///////////////////////////////////////////////////////////
	// INITIAL SETUP
	// ///////////////////////////////////////////////////////////

	private void setupPlayersAndDecks(List<Deck> allDecks, SQLiteDatabase db) {
		List<Deck> allNewDecks = new ArrayList<Deck>();
		List<Player> allPlayers = new ArrayList<Player>();

		SAXDataParser sdp = new SAXDataParser();
		try {
			sdp.parseDeckListXml(mhContext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		allPlayers = sdp.getAllOwners();
		allNewDecks = sdp.getAllDecks();

		// Players must be added before Decks
		for (Player p : allPlayers) {
			p.setId(0);
			writePlayer(p, db);
		}
		Log.i("MagicHatDbHelper.setupPlayersAndDecks",
				"Done setting up Players.");

		allPlayers = getAllPlayers(db);

		for (Deck d : allNewDecks) {
			// Need to set all ids of all deck owners to 0 since the id came
			// from XML originally
			d.getOwner().setId(0);
		}

		// This adds the previously existing decks prior to dropping tables
		for (Deck d : allDecks) {
			writeDeck(d, db);
		}
		Log.i("MagicHatDbHelper.setupPlayersAndDecks",
				"Done setting up original Decks.");

		for (Deck d : allNewDecks) {
			if (!deckExists(d, db)) {
				writeDeck(d, db);
			}
		}
		Log.i("MagicHatDbHelper.setupPlayersAndDecks", "Done setting up Decks.");
	}

	private void populateAllGames(List<Game> allGames, SQLiteDatabase db) {
		// Since this is only run from the onUpgrade task
		// The Deck and Player Ids might've changed since the deletion
		// and recreation of all of the data
		for (Game g : allGames) {
			writeNewGame(g, db);
		}
		Log.i("MagicHatDbHelper.populateAllGames", "Done Populating all Games.");
	}

	// //////////////////////////////////
	// DECKS
	// //////////////////////////////////

	protected void writeDeck(Deck d, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_NAME, d.getName());

		Player p = d.getOwner();
		if (p.getId() == 0) {
			p = getPlayer(p.getName(), db);
		}

		cv.put(KEY_DECK_OWNERID, p.getId());

		if (d.isActive()) {
			cv.put(KEY_DECK_ACTIVE, 1);
		} else {
			cv.put(KEY_DECK_ACTIVE, 0);
		}

		if (d.getId() == 0) {
			// This code is triggered when the deck is new
			try {
				db.insert(DB_TABLE_ALLDECKS, null, cv);
			} catch (SQLiteException exc) {
				exc.printStackTrace();
			} finally {
				Log.d("MagicHatDbHelper.writeDeck", d.toString()
						+ " was inserted.");
			}
		} else {
			try {
				db.update(DB_TABLE_ALLDECKS, cv,
						KEY_DECK_ROWID + " = " + d.getId(), null);
			} catch (SQLiteException exc) {
				exc.printStackTrace();
			} finally {
				Log.d("MagicHatDbHelper.writeDeck", d.toString()
						+ " was updated.");
			}
		}
	}

	protected void deleteDeck(Deck d, SQLiteDatabase db) {
		try {
			db.delete(DB_TABLE_ALLDECKS, KEY_DECK_ROWID + " = " + d.getId(),
					null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	protected int getDeckId(String sDeckName, String sOwnerName,
			SQLiteDatabase db) {
		int deckId = 0;

		Player owner = getPlayer(sOwnerName, db);

		String[] deckColumns = new String[] { KEY_DECK_NAME };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_NAME
				+ " = '" + sDeckName + "' AND " + KEY_DECK_OWNERID + " = "
				+ owner.getId(), null, null, null, null);
		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);

		if (dc.getCount() == 1) {
			dc.moveToFirst();
			deckId = dc.getInt(iDeckId);
		} else {
			Log.d("MagicHatDbHelper.getDeckId",
					"No unique deck was found with deck name: " + sDeckName
							+ " and Owner Id: " + owner.getId());
		}
		dc.close();

		return deckId;
	}

	protected boolean deckExists(Deck d, SQLiteDatabase db) {
		String[] deckColumns = new String[] { KEY_DECK_NAME, KEY_DECK_OWNERID };
		Player p = getPlayer(d.getOwner().getName(), db);
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_NAME
				+ " = '" + d.getName() + "' AND " + KEY_DECK_OWNERID + " = "
				+ p.getId(), null, null, null, null);

		if (dc.getCount() == 1) {
			dc.close();
			Log.d("MagicHatDb.deckExists", d + " does exist.");
			return true;
		}
		dc.close();

		Log.d("MagicHatDb.deckExists", d + " does not exist.");
		return false;
	}

	protected Deck getDeck(int deckId, SQLiteDatabase db) {
		Deck d = new Deck();
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE };

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_ROWID
				+ " = " + deckId, null, null, null, null);

		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		// int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		int ownerId = 0;
		boolean active = false;
		String deckName = "";
		if (dc.getCount() == 1) {
			dc.moveToFirst();
			deckName = dc.getString(iDeckName);
			ownerId = dc.getInt(iDeckOwnerId);
			active = (dc.getInt(iDeckActive) == 1);
			// manual = (dc.getInt(iDeckManual) == 1);

			Player owner = getPlayer(ownerId, db);

			d = new Deck(deckId, deckName, owner, active);
		} else {
			Log.d("MagicHatDbHelper.getDeck(id)",
					"No unique Deck was found for the deckId of " + deckId);
		}
		dc.close();

		return d;
	}

	protected Deck getDeck(String sDeckName, String sOwnerName,
			SQLiteDatabase db) {
		Deck d = new Deck();

		Player p = getPlayer(sOwnerName, db);

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_NAME + " = '" + sDeckName + "' AND "
						+ KEY_DECK_OWNERID + " = " + p.getId(), null, null,
				null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		// int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		boolean active = false;
		if (dc.getCount() == 1) {
			dc.moveToFirst();
			active = (dc.getInt(iDeckActive) == 1);
			// manual = (dc.getInt(iDeckManual) == 1);
			d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
					getPlayer(dc.getInt(iDeckOwnerId), db), active);
		} else {
			Log.d("MagicHatDbHelper.getDeck(String)",
					"No unique deck was found for deck name: " + sDeckName
							+ " and for Owner Id: " + p.getId());
		}
		dc.close();

		return d;
	}

	protected List<Deck> getAllDecks(boolean isActive, SQLiteDatabase db) {
		List<Deck> allDecks = new ArrayList<Deck>();

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE };

		String query = new String();
		if (isActive) {
			query = query.concat(KEY_DECK_ACTIVE + " = 1");
		}
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, query, null, null,
				null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			Player owner = getPlayer(dc.getInt(iDeckOwnerId), db);

			if (isActive) {
				d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
						owner, true);
			} else {
				boolean active = (dc.getInt(iDeckActive) == 1);

				d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
						owner, active);
			}
			allDecks.add(d);
		}

		dc.close();

		Collections.sort(allDecks);

		return allDecks;
	}

	protected List<Deck> getDeckList(Player owner, boolean isActive,
			SQLiteDatabase db) {
		List<Deck> deckList = new ArrayList<Deck>();

		if (owner.getId() == 0) {
			owner = getPlayer(owner.getName(), db);
		}

		String query = KEY_DECK_OWNERID + " = " + owner.getId();

		if (isActive) {
			query = query.concat(" AND " + KEY_DECK_ACTIVE + " = 1");
		}

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_ACTIVE };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, query, null, null,
				null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iName = dc.getColumnIndex(KEY_DECK_NAME);
		int iActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			if (isActive) {
				d = new Deck(dc.getInt(iDeckId), dc.getString(iName), owner,
						true);
			} else {
				boolean active = iActive == 1 ? true : false;

				d = new Deck(dc.getInt(iDeckId), dc.getString(iName), owner,
						active);
			}
			deckList.add(d);
		}
		dc.close();

		Collections.sort(deckList);

		return deckList;
	}

	protected void setDeckList(int playerId, List<Deck> deckList,
			SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_OWNERID, playerId);
		for (Deck d : deckList) {
			try {
				db.update(DB_TABLE_ALLDECKS, cv,
						KEY_DECK_ROWID + " = " + d.getId(), null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}
	}

	// /////////////////////////////
	// PLAYERS
	// /////////////////////////////
	// OWNERS
	// /////////////////////////////

	protected void writePlayer(Player p, SQLiteDatabase db) {
		int isActive = p.isActive() ? 1 : 0;
		int isSelf = p.isSelf() ? 1 : 0;

		ContentValues cv = new ContentValues();
		cv.put(KEY_PLAYER_NAME, p.getName());
		cv.put(KEY_PLAYER_ACTIVE, isActive);
		if (p.getDci() != 0) {
			cv.put(KEY_PLAYER_DCI, p.getDci());
		}
		cv.put(KEY_PLAYER_SELF, isSelf);

		if (p.getId() == 0) {
			// This code is triggered when the player is new
			try {
				db.insert(DB_TABLE_ALLPLAYERS, null, cv);
			} catch (SQLiteException exc) {
				exc.printStackTrace();
			} finally {
				Log.d("MagicHatDbHelper.writePlayer", "Player " + p.toString()
						+ " was inserted.");
			}
		} else {
			try {
				db.update(DB_TABLE_ALLPLAYERS, cv,
						KEY_PLAYER_ROWID + " = " + p.getId(), null);
			} catch (SQLiteException exc) {
				exc.printStackTrace();
			} finally {
				Log.d("MagicHatDbHelper.writePlayer", "Player " + p.toString()
						+ " was updated.");
			}
		}
	}

	protected void deletePlayer(Player p, SQLiteDatabase db) {
		try {
			db.delete(DB_TABLE_ALLPLAYERS,
					KEY_PLAYER_ROWID + " = " + p.getId(), null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	protected Player getPlayer(int playerId, SQLiteDatabase db) {
		// return getOwner(playerId, db);
		Player p = new Player();
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_DCI, KEY_PLAYER_ACTIVE,
				KEY_PLAYER_SELF };
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
				KEY_PLAYER_ROWID + " = " + playerId, null, null, null, null);

		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			boolean active = (pc.getInt(iOwnerActive) == 1);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
			if (pc.isNull(iOwnerDci)) {
				p = new Player(playerId, pc.getString(iOwnerName), active, self);
			} else {
				p = new Player(playerId, pc.getString(iOwnerName),
						pc.getInt(iOwnerDci), active, self);
			}
		} else {
			Log.d("MagicHatDbHelper.getPlayer(id)",
					"No unique owner was found for Owner Id: " + playerId);
		}
		pc.close();

		return p;
	}

	protected Player getPlayer(String name, SQLiteDatabase db) {
		// return getOwner(name, db);
		Player p = new Player();
		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE, KEY_PLAYER_DCI, KEY_PLAYER_SELF };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		boolean active = false, self = false;
		if (pc.getCount() == 1) {
			pc.moveToFirst();
			active = (pc.getInt(iOwnerActive) == 1);
			self = (pc.getInt(iOwnerSelf) == 1);
			if (pc.isNull(iOwnerDci)) {
				p = new Player(pc.getInt(iOwnerId), pc.getString(iOwnerName),
						active, self);
			} else {
				p = new Player(pc.getInt(iOwnerId), pc.getString(iOwnerName),
						pc.getInt(iOwnerDci), active, self);
			}
		} else {
			Log.d("MagicHatDbHelper.getPlayer(name)",
					"No unique player was found for Owner Name: " + name);
		}
		pc.close();

		return p;
	}

	protected int getPlayerId(String name, SQLiteDatabase db) {
		int playerId = 0;

		String[] columns = new String[] { KEY_PLAYER_ROWID };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			playerId = pc.getInt(iOwnerId);
		} else {
			Log.d("MagicHatDbHelper.getPlayerId",
					"No unique player was found for Owner Name: " + name
							+ "\nDuring Db Install or Upgrade this is normal.");
		}
		pc.close();

		return playerId;
	}

	protected List<Player> getActivePlayers(SQLiteDatabase db) {
		List<Player> allActivePlayers = new ArrayList<Player>();

		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		// Add p to the list of Players so long as the deck is active,
		// p isn't a Wizards Decks
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
				KEY_PLAYER_ACTIVE + " = 1 AND " + KEY_PLAYER_NAME
						+ " != 'Wizards of the Coast'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = pc.getColumnIndex(KEY_PLAYER_NAME);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			String sOwner = pc.getString(iOwner);
			Player owner = new Player(ownerId, sOwner, true);

			// Add p to the list of Players so long as p isn't already in
			// the list of Players
			if (!allActivePlayers.contains(owner)) {
				allActivePlayers.add(owner);
			}
		}
		pc.close();

		Collections.sort(allActivePlayers);

		return allActivePlayers;
	}

	protected List<Player> getAllPlayers(SQLiteDatabase db) {
		List<Player> allPlayers = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		// Add p to the list of Players so long as
		// p isn't the Wizards Decks
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " != 'Wizards of the Coast'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			boolean active = (pc.getInt(iActive) == 1);
			String sOwner = pc.getString(iOwner);
			Player owner = new Player(ownerId, sOwner, active);

			// Add p to the list of Players so long as
			// p isn't already in the list of Players
			if (!allPlayers.contains(owner)) {
				allPlayers.add(owner);
			}
		}
		pc.close();

		Collections.sort(allPlayers);

		return allPlayers;
	}

	protected List<Player> getAllOwners(SQLiteDatabase db) {
		List<Player> allOwners = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, null, null, null,
				null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			boolean active = (pc.getInt(iActive) == 1);
			String sOwner = pc.getString(iOwner);
			Player owner = new Player(ownerId, sOwner, active);

			// Add p to the list of Players so long as p isn't already in
			// the list of Players
			if (!allOwners.contains(owner)) {
				allOwners.add(owner);
			}
		}
		pc.close();

		Collections.sort(allOwners);

		return allOwners;
	}

	// /////////////////////////////
	// GAMES
	// /////////////////////////////

	protected void writeNewGame(Game g, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_GAME_PLAYER1, getPlayerId(g.getPlayer(0).getName(), db));
		cv.put(KEY_GAME_PLAYER2, getPlayerId(g.getPlayer(1).getName(), db));
		cv.put(KEY_GAME_DECK1,
				getDeckId(g.getDeck(0).getName(), g.getDeck(0).getOwner()
						.getName(), db));
		cv.put(KEY_GAME_DECK2,
				getDeckId(g.getDeck(1).getName(), g.getDeck(1).getOwner()
						.getName(), db));
		cv.put(KEY_GAME_WINNER, getPlayerId(g.getWinner().getName(), db));
		cv.put(KEY_GAME_DATE, g.getDate().getTime());

		try {
			db.insert(DB_TABLE_ALLGAMES, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		Log.d("MagicHatDbHelper.populateAllGames", "Game "
				+ g.getDeck(0).toString() + " vs " + g.getDeck(1).toString()
				+ " was inserted.");
	}

	/*
	 * protected void addGameResult(Map<Player, Deck> playersAndDecks, Player
	 * pWinner, Date gameDate, SQLiteDatabase db) { List<Player> player = new
	 * ArrayList<Player>(); player.addAll(playersAndDecks.keySet());
	 * 
	 * ContentValues cv = new ContentValues(); cv.put(KEY_GAME_PLAYER1,
	 * player.get(0).getId()); cv.put(KEY_GAME_PLAYER2, player.get(1).getId());
	 * cv.put(KEY_GAME_DECK1, playersAndDecks.get(player.get(0)).getId());
	 * cv.put(KEY_GAME_DECK2, playersAndDecks.get(player.get(1)).getId());
	 * cv.put(KEY_GAME_WINNER, pWinner.getId()); cv.put(KEY_GAME_DATE,
	 * gameDate.getTime()); try { db.insert(DB_TABLE_ALLGAMES, null, cv); }
	 * catch (SQLiteException e) { e.printStackTrace(); } }
	 */

	protected List<Game> getAllGames(SQLiteDatabase db) {
		List<Game> allGames = new ArrayList<Game>();

		String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER, KEY_GAME_DATE };

		Cursor gc = db.query(DB_TABLE_ALLGAMES, columns, null, null, null,
				null, null);

		int iGameId = gc.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = gc.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = gc.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = gc.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = gc.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = gc.getColumnIndex(KEY_GAME_WINNER);
		int iDate = gc.getColumnIndex(KEY_GAME_DATE);

		for (gc.moveToFirst(); !gc.isAfterLast(); gc.moveToNext()) {
			Map<Player, Deck> playersAndDecks = new HashMap<Player, Deck>();

			int gameId = gc.getInt(iGameId);
			int player1Id = gc.getInt(iPlayer1);
			int player2Id = gc.getInt(iPlayer2);
			int deck1Id = gc.getInt(iDeck1);
			int deck2Id = gc.getInt(iDeck2);
			int winnerId = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getPlayer(player1Id, db);
			Player p2 = getPlayer(player2Id, db);
			Deck d1 = getDeck(deck1Id, db);
			Deck d2 = getDeck(deck2Id, db);

			playersAndDecks.put(p1, d1);
			playersAndDecks.put(p2, d2);

			Player pW = getPlayer(winnerId, db);

			allGames.add(new Game(gameId, playersAndDecks, pW, date));
		}
		gc.close();

		return allGames;
	}

	protected List<Game> getGames(Player p, SQLiteDatabase db) {
		List<Game> games = new ArrayList<Game>();

		String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER, KEY_GAME_DATE };

		Cursor gc = db.query(DB_TABLE_ALLGAMES, columns,
				KEY_GAME_PLAYER1 + " = " + p.getId() + " OR "
						+ KEY_GAME_PLAYER2 + " = " + p.getId(), null, null,
				null, null);

		int iGameId = gc.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = gc.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = gc.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = gc.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = gc.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = gc.getColumnIndex(KEY_GAME_WINNER);
		int iDate = gc.getColumnIndex(KEY_GAME_DATE);

		for (gc.moveToFirst(); !gc.isAfterLast(); gc.moveToNext()) {
			Map<Player, Deck> playersAndDecks = new HashMap<Player, Deck>();

			int gameId = gc.getInt(iGameId);
			int player1Id = gc.getInt(iPlayer1);
			int player2Id = gc.getInt(iPlayer2);
			int deck1Id = gc.getInt(iDeck1);
			int deck2Id = gc.getInt(iDeck2);
			int winnerId = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getPlayer(player1Id, db);
			Player p2 = getPlayer(player2Id, db);
			Deck d1 = getDeck(deck1Id, db);
			Deck d2 = getDeck(deck2Id, db);

			playersAndDecks.put(p1, d1);
			playersAndDecks.put(p2, d2);

			Player pW = getPlayer(winnerId, db);

			games.add(new Game(gameId, playersAndDecks, pW, date));
		}
		gc.close();

		return games;
	}

	protected List<Game> getGames(Deck d, SQLiteDatabase db) {
		List<Game> games = new ArrayList<Game>();

		String[] gameColumns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER, KEY_GAME_DATE };

		Cursor gc = db.query(DB_TABLE_ALLGAMES, gameColumns,
				KEY_GAME_DECK1 + " = " + d.getId() + " OR " + KEY_GAME_DECK2
						+ " = " + d.getId(), null, null, null, null);

		int iGameId = gc.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = gc.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = gc.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = gc.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = gc.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = gc.getColumnIndex(KEY_GAME_WINNER);
		int iDate = gc.getColumnIndex(KEY_GAME_DATE);

		for (gc.moveToFirst(); !gc.isAfterLast(); gc.moveToNext()) {
			Map<Player, Deck> playersAndDecks = new HashMap<Player, Deck>();

			int gameId = gc.getInt(iGameId);
			int player1Id = gc.getInt(iPlayer1);
			int player2Id = gc.getInt(iPlayer2);
			int deck1Id = gc.getInt(iDeck1);
			int deck2Id = gc.getInt(iDeck2);
			int winnerId = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getPlayer(player1Id, db);
			Player p2 = getPlayer(player2Id, db);
			Deck d1 = getDeck(deck1Id, db);
			Deck d2 = getDeck(deck2Id, db);

			playersAndDecks.put(p1, d1);
			playersAndDecks.put(p2, d2);

			Player pW = getPlayer(winnerId, db);

			games.add(new Game(gameId, playersAndDecks, pW, date));
		}
		gc.close();

		return games;
	}
}