package com.magichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MagicHatDbHelper extends SQLiteOpenHelper {
	public static final String KEY_DECK_ROWID = "_id";
	public static final String KEY_DECK_NAME = "deck_name";
	public static final String KEY_DECK_OWNERID = "owner_id";
	public static final String KEY_DECK_ACTIVE = "active_sts";
	public static final String KEY_DECK_MANUAL = "man_created";

	public static final String KEY_PLAYER_ROWID = "_id";
	public static final String KEY_PLAYER_NAME = "player_name";
	public static final String KEY_PLAYER_ACTIVE = "active_sts";

	public static final String KEY_GAME_ROWID = "_id";
	public static final String KEY_GAME_PLAYER1 = "player_1";
	public static final String KEY_GAME_PLAYER2 = "player_2";
	public static final String KEY_GAME_DECK1 = "deck_1";
	public static final String KEY_GAME_DECK2 = "deck_2";
	public static final String KEY_GAME_WINNER = "winner";
	public static final String KEY_GAME_DATE = "game_date";

	public static final String KEY_EXPANSION_ROWID = "_id";
	public static final String KEY_EXPANSION_NAME = "exp_name";
	public static final String KEY_EXPANSION_SHORTNAME = "exp_shortname";

	public static final String KEY_CARD_ROWID = "_id";
	public static final String KEY_CARD_NAME = "card_name";
	public static final String KEY_CARD_ISBLUE = "card_isblue";
	public static final String KEY_CARD_ISBLACK = "card_isblack";
	public static final String KEY_CARD_ISWHITE = "card_iswhite";
	public static final String KEY_CARD_ISGREEN = "card_isgreen";
	public static final String KEY_CARD_ISRED = "card_isred";
	public static final String KEY_CARD_MANACOST = "card_manacost";
	public static final String KEY_CARD_CMC = "card_cmc";
	public static final String KEY_CARD_TYPE = "card_type";
	public static final String KEY_CARD_SUBTYPES = "card_subtypes";
	public static final String KEY_CARD_POWER = "card_power";
	public static final String KEY_CARD_TOUGHNESS = "card_toughness";
	public static final String KEY_CARD_RARITY = "card_rarity";
	public static final String KEY_CARD_TEXT = "card_text";

	public static final String KEY_REL_CARD_EXP_ROWID = "_id";
	public static final String KEY_REL_CARD_ID = "card_id";
	public static final String KEY_REL_EXP_ID = "exp_id";
	public static final String KEY_REL_PIC_URL = "pic_url";

	private static final String DB_TABLE_ALLDECKS = "Decks";
	private static final String DB_TABLE_ALLPLAYERS = "Players";
	private static final String DB_TABLE_ALLGAMES = "Games";
	public static final String DB_TABLE_ALLEXPANSIONS = "Expansions";
	public static final String DB_TABLE_ALLCARDS = "Cards";
	public static final String DB_TABLE_REL_CARD_EXP = "Rel_CardExp";

	private Context mhContext;
	private String dbName = "";

	private boolean isUpgrade = false;

	public MagicHatDbHelper(Context mhContext, String dbName, int dbVersion) {
		super(mhContext, dbName, null, dbVersion);
		this.mhContext = mhContext;
		this.dbName = dbName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (!isUpgrade) {
			Toast.makeText(mhContext,
					"Initializing database... Please wait...",
					Toast.LENGTH_SHORT).show();
		}

		if (dbName.equals(MagicHatDB.MH_DB_NAME)) {
			System.out
					.println("Code Path for MagicHatDb OnCreate was triggered.");

			db.execSQL("CREATE TABLE " + DB_TABLE_ALLPLAYERS + " ("
					+ KEY_PLAYER_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_PLAYER_NAME + " TEXT NOT NULL, " + KEY_PLAYER_ACTIVE
					+ " INTEGER NOT NULL);");
			db.execSQL("CREATE TABLE " + DB_TABLE_ALLDECKS + " ("
					+ KEY_DECK_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_DECK_NAME + " TEXT NOT NULL, " + KEY_DECK_OWNERID
					+ " INTEGER NOT NULL, " + KEY_DECK_ACTIVE
					+ " INTEGER NOT NULL, " + KEY_DECK_MANUAL
					+ " INTEGER NOT NULL, FOREIGN KEY(" + KEY_DECK_OWNERID
					+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "));");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_ALLGAMES + " ("
					+ KEY_GAME_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_GAME_PLAYER1 + " INTEGER NOT NULL, "
					+ KEY_GAME_PLAYER2 + " INTEGER NOT NULL, " + KEY_GAME_DECK1
					+ " INTEGER NOT NULL, " + KEY_GAME_DECK2
					+ " INTEGER NOT NULL, " + KEY_GAME_WINNER
					+ " INTEGER NOT NULL, " + KEY_GAME_DATE
					+ " INTEGER NOT NULL, FOREIGN KEY(" + KEY_GAME_PLAYER1
					+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_PLAYER2
					+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_DECK1
					+ ") REFERENCES " + DB_TABLE_ALLDECKS + "("
					+ KEY_DECK_ROWID + "), FOREIGN KEY(" + KEY_GAME_DECK2
					+ ") REFERENCES " + DB_TABLE_ALLDECKS + "("
					+ KEY_DECK_ROWID + "), FOREIGN KEY(" + KEY_GAME_WINNER
					+ ") REFERENCES " + DB_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "));");

			if (!isUpgrade) {
				setupPlayersAndDecks(new ArrayList<Deck>(), db);
			}
		} else {
			// This code path is triggered for CardDb
			System.out.println("Code Path for CardDb onCreate was triggered.");

		}
		if (!isUpgrade) {
			Toast.makeText(mhContext, "Database Initialization Complete.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Toast.makeText(mhContext, "Upgrading database... Please wait...",
				Toast.LENGTH_SHORT).show();

		if (dbName.equals(MagicHatDB.MH_DB_NAME)) {
			System.out
					.println("Code Path for MagicHatDb onUpgrade was triggered.");

			isUpgrade = true;
			List<Game> allGames = new ArrayList<Game>();
			List<Deck> allDecks = new ArrayList<Deck>();

			// TODO This requires a certain db schema to be successful
			allGames = getAllGames(db);
			allDecks = getAllDecks(db);

			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLDECKS);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLPLAYERS);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLGAMES);

			onCreate(db);

			setupPlayersAndDecks(allDecks, db);
			populateAllGames(allGames, db);
		} else {
			// This code path is triggered for CardDb
			System.out.println("Code Path for CardDb onUpgrade was triggered.");
		}
		Toast.makeText(mhContext, "Database Upgrade Complete.",
				Toast.LENGTH_SHORT).show();
	}

	// ///////////////////////////////////////////////////////////
	// INITIAL SETUP
	// ///////////////////////////////////////////////////////////

	protected void setupPlayersAndDecks(List<Deck> allDecks, SQLiteDatabase db) {
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
		int iActive = 0;
		for (Player p : allPlayers) {
			iActive = p.isActive() ? 1 : 0;

			ContentValues cvp = new ContentValues();
			cvp.put(KEY_PLAYER_NAME, p.getName().toString());
			cvp.put(KEY_PLAYER_ACTIVE, iActive);
			db.insert(DB_TABLE_ALLPLAYERS, null, cvp);
			System.out.println(p.getName().toString() + " Owner was added\n");
		}
		System.out.println("Done setting up Players.");

		// This adds the previously existing decks prior to dropping tables
		iActive = 0;
		int iManual = 0;
		for (Deck d : allDecks) {
			iActive = d.isActive() ? 1 : 0;
			iManual = d.isManual() ? 1 : 0;

			ContentValues cvd = new ContentValues();
			cvd.put(KEY_DECK_NAME, d.getName().toString());
			Player p = getPlayer(d.getOwner().toString(), db);
			cvd.put(KEY_DECK_OWNERID, p.getId());
			cvd.put(KEY_DECK_ACTIVE, iActive);
			cvd.put(KEY_DECK_MANUAL, iManual);
			db.insert(DB_TABLE_ALLDECKS, null, cvd);
			System.out.println(d.getName().toString() + " Deck was added for "
					+ p.toString() + "\n");
		}
		System.out.println("Done setting up original Decks.");

		iActive = 0;
		for (Deck d : allNewDecks) {
			if (!deckExists(d, db)) {
				iActive = d.isActive() ? 1 : 0;

				ContentValues cvd = new ContentValues();
				cvd.put(KEY_DECK_NAME, d.getName().toString());
				Player p = getPlayer(d.getOwner().toString(), db);
				cvd.put(KEY_DECK_OWNERID, p.getId());
				cvd.put(KEY_DECK_ACTIVE, iActive);
				cvd.put(KEY_DECK_MANUAL, 0);
				db.insert(DB_TABLE_ALLDECKS, null, cvd);
				System.out.println(d.getName().toString()
						+ " Deck was added for " + p.toString() + "\n");
			}
		}
		System.out.println("Done setting up Decks.");
	}

	protected void populateAllGames(List<Game> allGames, SQLiteDatabase db) {
		for (Game g : allGames) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_GAME_PLAYER1, getOwner(g.getPlayer(1).toString(), db)
					.getId());
			cv.put(KEY_GAME_PLAYER2, getOwner(g.getPlayer(2).toString(), db)
					.getId());
			cv.put(KEY_GAME_DECK1,
					getDeckId(g.getDeck(1).getName(), g.getDeck(1).getOwner()
							.toString(), db));
			cv.put(KEY_GAME_DECK2,
					getDeckId(g.getDeck(2).getName(), g.getDeck(2).getOwner()
							.toString(), db));
			cv.put(KEY_GAME_WINNER, getOwner(g.getWinner().toString(), db)
					.getId());
			cv.put(KEY_GAME_DATE, g.getDate().getTime());

			db.insert(DB_TABLE_ALLGAMES, null, cv);
			System.out.println("Game " + g.getDeck(1).toString() + " vs "
					+ g.getDeck(2).toString() + " was added.");
		}
		System.out.println("Done Populating all Games.");
	}

	// //////////////////////////////////
	// DECKS
	// //////////////////////////////////

	protected void addDeck(String name, int OwnerId, Integer active,
			SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_NAME, name);
		cv.put(KEY_DECK_OWNERID, OwnerId);
		cv.put(KEY_DECK_ACTIVE, active);
		cv.put(KEY_DECK_MANUAL, 1);
		try {
			db.insert(DB_TABLE_ALLDECKS, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	protected void updateDeck(String owner, String oldDeckName,
			String newDeckName, boolean newActive, SQLiteDatabase db) {

		int deckId = getDeckId(oldDeckName, owner, db);

		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_NAME, newDeckName);
		if (newActive) {
			cv.put(KEY_DECK_ACTIVE, 1);
		} else {
			cv.put(KEY_DECK_ACTIVE, 0);
		}

		try {
			db.update(DB_TABLE_ALLDECKS, cv, KEY_DECK_ROWID + " = " + deckId,
					null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	protected void deleteDecks(int[] id, SQLiteDatabase db) {
		for (int dId : id) {
			try {
				db.delete(DB_TABLE_ALLDECKS, KEY_DECK_ROWID + " = " + dId, null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}
	}

	protected Deck getDeck(int deckId, SQLiteDatabase db) {
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_ROWID
				+ " = " + deckId, null, null, null, null);

		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		int ownerId = 0;
		boolean active = false, manual = false;
		String deckName = "";
		if (dc.getCount() == 1) {
			dc.moveToFirst();
			deckName = dc.getString(iDeckName);
			ownerId = dc.getInt(iDeckOwnerId);
			active = (dc.getInt(iDeckActive) == 1);
			manual = (dc.getInt(iDeckManual) == 1);
		} else {
			System.out
					.println("MagicHatDB.getDeck(id): No unique Deck was found for the deckId of "
							+ deckId);
		}
		dc.close();

		Player owner = getOwner(ownerId, db);

		return new Deck(deckId, deckName, owner, active, manual);
	}

	protected Deck getDeck(String sDeckName, String sOwnerName,
			SQLiteDatabase db) {
		Deck d = new Deck();

		Player p = getPlayer(sOwnerName, db);

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_NAME + " = '" + sDeckName + "' AND "
						+ KEY_DECK_OWNERID + " = " + p.getId(), null, null,
				null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		boolean active = false, manual = false;
		if (dc.getCount() == 1) {
			dc.moveToFirst();
			active = (dc.getInt(iDeckActive) == 1);
			manual = (dc.getInt(iDeckManual) == 1);
			d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName), getOwner(
					dc.getInt(iDeckOwnerId), db), active, manual);
		} else {
			System.out
					.println("No unique deck was found in MagicHatDB.getDeck(deckName).");
		}
		dc.close();

		return d;
	}

	protected List<Deck> getAllDecks(SQLiteDatabase db) {
		List<Deck> allDecks = new ArrayList<Deck>();

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, null, null, null,
				null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
					KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId), null,
					null, null, null);

			int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
			int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
			int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

			if (pc.getCount() == 1) {
				pc.moveToFirst();
				boolean active = (dc.getInt(iDeckActive) == 1);
				boolean manual = (dc.getInt(iDeckManual) == 1);
				boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
				Player owner = new Player(pc.getInt(iOwnerId),
						pc.getString(iOwnerName), ownerActive);
				d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
						owner, active, manual);
				allDecks.add(d);
			} else {
				System.out
						.println("MagicHatDB.getAllDecks: Deck was not found.");
			}
			pc.close();
		}

		dc.close();

		Collections.sort(allDecks);

		return allDecks;
	}

	protected List<Deck> getAllActiveDecks(SQLiteDatabase db) {
		List<Deck> allActiveDecks = new ArrayList<Deck>();

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_ACTIVE
				+ " = 1", null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
					KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId), null,
					null, null, null);

			int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
			int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
			int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

			if (pc.getCount() == 1) {
				pc.moveToFirst();
				boolean manual = (dc.getInt(iDeckManual) == 1);
				boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
				Player owner = new Player(pc.getInt(iOwnerId),
						pc.getString(iOwnerName), ownerActive);
				d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
						owner, true, manual);
				allActiveDecks.add(d);
			} else {
				System.out
						.println("MagicHatDB.getAllDecks: Deck was not found.");
			}
			pc.close();
		}

		dc.close();

		Collections.sort(allActiveDecks);

		return allActiveDecks;
	}

	protected List<Deck> getAllManualDecks(SQLiteDatabase db) {
		List<Deck> allManDecks = new ArrayList<Deck>();
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_MANUAL
				+ " = 1", null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
					KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId), null,
					null, null, null);

			int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
			int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
			int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

			if (pc.getCount() == 1) {
				pc.moveToFirst();
				int deckId = dc.getInt(iDeckId);
				boolean active = (dc.getInt(iDeckActive) == 1);
				boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
				Player owner = new Player(pc.getInt(iOwnerId),
						pc.getString(iOwnerName), ownerActive);
				d = new Deck(deckId, dc.getString(iDeckName), owner, active,
						true);
				allManDecks.add(d);
			} else {
				System.out
						.println("MagicHatDB.getAllManualDecks: There are no manually created decks to delete.");
			}
			pc.close();
		}
		dc.close();

		Collections.sort(allManDecks);

		return allManDecks;
	}

	protected List<Deck> getDeckList(Player p, SQLiteDatabase db) {
		List<Deck> deckList = new ArrayList<Deck>();
		Player pReal = getPlayer(p.toString(), db);

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_OWNERID
				+ " = " + pReal.getId(), null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iName = dc.getColumnIndex(KEY_DECK_NAME);
		int iActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
		int iManual = dc.getColumnIndex(KEY_DECK_MANUAL);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			int deckId = dc.getInt(iDeckId);
			boolean active = (dc.getInt(iActive) == 1);
			boolean manual = (dc.getInt(iManual) == 1);
			d = new Deck(deckId, dc.getString(iName), pReal, active, manual);
			deckList.add(d);
		}
		dc.close();

		Collections.sort(deckList);

		return deckList;
	}

	protected int getDeckId(String sDeckName, String sOwnerName,
			SQLiteDatabase db) {
		int deckId = 0;

		Player p = getPlayer(sOwnerName, db);

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_NAME + " = '" + sDeckName + "' AND "
						+ KEY_DECK_OWNERID + " = " + p.getId(), null, null,
				null, null);
		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);

		if (dc.getCount() == 1) {
			dc.moveToFirst();
			deckId = dc.getInt(iDeckId);
		} else {
			System.out
					.println("MagicHatDB.getDeckId: No unique deck was found.");
		}
		dc.close();

		return deckId;
	}

	protected boolean deckExists(Deck d, SQLiteDatabase db) {
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Player p = getPlayer(d.getOwner().toString(), db);
		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, KEY_DECK_NAME
				+ " = '" + d.getName().toString() + "' AND " + KEY_DECK_OWNERID
				+ " = " + p.getId(), null, null, null, null);

		if (dc.getCount() == 1) {
			dc.close();
			return true;
		}
		dc.close();

		return false;
	}

	// /////////////////////////////
	// PLAYERS
	// /////////////////////////////
	// OWNERS
	// /////////////////////////////

	protected Player getOwner(int ownerId, SQLiteDatabase db) {
		Player p = new Player();
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
				KEY_PLAYER_ROWID + " = " + ownerId, null, null, null, null);

		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
			p = new Player(ownerId, pc.getString(iOwnerName), ownerActive);
		} else {
			System.out
					.println("MagicHatDB.getOwner(id): No unique owner was found.");
		}
		pc.close();

		return p;
	}

	protected Player getPlayer(int playerId, SQLiteDatabase db) {
		return getOwner(playerId, db);
	}

	protected Player getOwner(String name, SQLiteDatabase db) {
		Player p = new Player();
		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

		boolean active = false;
		if (pc.getCount() == 1) {
			pc.moveToFirst();
			active = (pc.getInt(iOwnerActive) == 1);
			p = new Player(pc.getInt(iOwnerId), pc.getString(iOwnerName),
					active);
		} else {
			System.out
					.println("MagicHatDB.getOwner(name): No unique player was found.");
		}
		pc.close();

		return p;
	}

	protected Player getPlayer(String name, SQLiteDatabase db) {
		return getOwner(name, db);
	}

	protected int getPlayerId(String name, SQLiteDatabase db) {
		int playerId = 0;

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			playerId = pc.getInt(iOwnerId);
		} else {
			System.out
					.println("MagicHatDB.getPlayerId: No unique player was found.");
		}
		pc.close();

		return playerId;
	}

	protected List<Player> getActivePlayers(SQLiteDatabase db) {
		List<Player> allActivePlayers = new ArrayList<Player>();

		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		// Add p to the list of Players so long as the deck is active,
		// p isn't the Wizards Decks
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

	protected Player flipActiveStatus(Player pFake, SQLiteDatabase db) {
		Player pReal = getOwner(pFake.toString(), db);

		ContentValues cv = new ContentValues();
		if (pReal.isActive()) {
			cv.put(KEY_PLAYER_ACTIVE, 0);
		} else {
			cv.put(KEY_PLAYER_ACTIVE, 1);
		}
		try {
			db.update(DB_TABLE_ALLPLAYERS, cv,
					KEY_PLAYER_ROWID + " = " + pReal.getId(), null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		return getOwner(pReal.toString(), db);
	}

	protected Deck flipActiveStatus(String deckName, String ownerName,
			SQLiteDatabase db) {
		int deckId = getDeckId(deckName, ownerName, db);

		Deck d = getDeck(deckId, db);

		ContentValues cv = new ContentValues();
		if (d.isActive()) {
			cv.put(KEY_DECK_ACTIVE, 0);
		} else {
			cv.put(KEY_DECK_ACTIVE, 1);
		}

		try {
			db.update(DB_TABLE_ALLDECKS, cv,
					KEY_DECK_ROWID + " = " + d.getId(), null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		return getDeck(deckId, db);
	}

	// /////////////////////////////
	// GAMES
	// /////////////////////////////

	protected void addGameResult(List<Player> Players, List<Deck> gameDecks,
			Player pWinner, Date gameDate, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_GAME_PLAYER1, Players.get(0).getId());
		cv.put(KEY_GAME_PLAYER2, Players.get(1).getId());
		cv.put(KEY_GAME_DECK1, gameDecks.get(0).getId());
		cv.put(KEY_GAME_DECK2, gameDecks.get(1).getId());
		cv.put(KEY_GAME_WINNER, pWinner.getId());
		cv.put(KEY_GAME_DATE, gameDate.getTime());
		try {
			db.insert(DB_TABLE_ALLGAMES, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

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
			int gameId = gc.getInt(iGameId);
			int player1Id = gc.getInt(iPlayer1);
			int player2Id = gc.getInt(iPlayer2);
			int deck1Id = gc.getInt(iDeck1);
			int deck2Id = gc.getInt(iDeck2);
			int winnerId = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getOwner(player1Id, db);
			Player p2 = getOwner(player2Id, db);
			Deck d1 = getDeck(deck1Id, db);
			Deck d2 = getDeck(deck2Id, db);
			Player pW = getOwner(winnerId, db);

			allGames.add(new Game(gameId, p1, p2, d1, d2, pW, date));
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
			int gameId = gc.getInt(iGameId);
			int player1 = gc.getInt(iPlayer1);
			int player2 = gc.getInt(iPlayer2);
			int deck1 = gc.getInt(iDeck1);
			int deck2 = gc.getInt(iDeck2);
			int winner = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getPlayer(player1, db);
			Player p2 = getPlayer(player2, db);
			Deck d1 = getDeck(deck1, db);
			Deck d2 = getDeck(deck2, db);
			Player pW = getPlayer(winner, db);

			games.add(new Game(gameId, p1, p2, d1, d2, pW, date));
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
			int gameId = gc.getInt(iGameId);
			int player1 = gc.getInt(iPlayer1);
			int player2 = gc.getInt(iPlayer2);
			int deck1 = gc.getInt(iDeck1);
			int deck2 = gc.getInt(iDeck2);
			int winner = gc.getInt(iGameWinner);
			Date date = new Date(gc.getLong(iDate));

			Player p1 = getPlayer(player1, db);
			Player p2 = getPlayer(player2, db);
			Deck d1 = getDeck(deck1, db);
			Deck d2 = getDeck(deck2, db);
			Player pW = getPlayer(winner, db);

			games.add(new Game(gameId, p1, p2, d1, d2, pW, date));
		}
		gc.close();

		return games;
	}

	// //////////////////////////////////
	// EXPANSIONS
	// //////////////////////////////////

	protected int getExpansionId(String shortName, SQLiteDatabase db) {
		int cardSetId = 0;

		String[] columns = new String[] { KEY_EXPANSION_ROWID,
				KEY_EXPANSION_NAME, KEY_EXPANSION_SHORTNAME };

		Cursor expC = db.query(DB_TABLE_ALLEXPANSIONS, columns,
				KEY_EXPANSION_SHORTNAME + " = '" + shortName + "'", null, null,
				null, null);

		int iExpansionId = expC.getColumnIndex(KEY_EXPANSION_ROWID);

		if (expC.getCount() == 1) {
			expC.moveToFirst();
			cardSetId = expC.getInt(iExpansionId);
		} else {
			System.out
					.println("No unique expansion was found in MagicHatDB.getExpansionId.");
		}
		expC.close();

		return cardSetId;
	}

	protected List<Expansion> getAllExpansions(SQLiteDatabase db) {
		List<Expansion> allExpansions = new ArrayList<Expansion>();
		String[] expansionColumns = new String[] { KEY_EXPANSION_ROWID,
				KEY_EXPANSION_NAME, KEY_EXPANSION_SHORTNAME };

		Cursor expC = db.query(DB_TABLE_ALLEXPANSIONS, expansionColumns, null,
				null, null, null, null);

		int iExpansionId = expC.getColumnIndex(KEY_EXPANSION_ROWID);
		int iExpansionName = expC.getColumnIndex(KEY_EXPANSION_NAME);
		int iExpansionShortName = expC.getColumnIndex(KEY_EXPANSION_SHORTNAME);

		Expansion exp;
		for (expC.moveToFirst(); !expC.isAfterLast(); expC.moveToNext()) {
			exp = new Expansion(expC.getInt(iExpansionId),
					expC.getString(iExpansionName),
					expC.getString(iExpansionShortName));

			allExpansions.add(exp);
		}
		expC.close();

		Collections.sort(allExpansions);

		return allExpansions;
	}

	// //////////////////////////////
	// CARDS
	// //////////////////////////////

	protected List<Card> getAllCardIds(SQLiteDatabase db) {
		List<Card> allCards = new ArrayList<Card>();
		String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_NAME };

		Cursor cc = db.query(DB_TABLE_ALLCARDS, cardColumns, null, null, null,
				null, null);

		int iCardId = cc.getColumnIndex(KEY_CARD_ROWID);
		int iCardName = cc.getColumnIndex(KEY_CARD_NAME);

		Card c;
		for (cc.moveToFirst(); !cc.isAfterLast(); cc.moveToNext()) {
			c = new Card(cc.getInt(iCardId), cc.getString(iCardName));

			allCards.add(c);
		}
		cc.close();

		Collections.sort(allCards);

		return allCards;
	}
}