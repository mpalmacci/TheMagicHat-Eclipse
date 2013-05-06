package com.magichat.decks.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
	protected static final String KEY_DECK_ROWID = "_id";
	protected static final String KEY_DECK_NAME = "deck_name";
	protected static final String KEY_DECK_OWNERID = "owner_id";
	protected static final String KEY_DECK_ACTIVE = "active_sts";

	protected static final String KEY_PLAYER_ROWID = "_id";
	protected static final String KEY_PLAYER_NAME = "player_name";
	protected static final String KEY_PLAYER_ACTIVE = "active_sts";
	protected static final String KEY_PLAYER_DCI = "dci";
	protected static final String KEY_PLAYER_SELF = "self";

	protected static final String KEY_GAME_ROWID = "_id";
	protected static final String KEY_GAME_PLAYER1 = "player_1";
	protected static final String KEY_GAME_PLAYER2 = "player_2";
	protected static final String KEY_GAME_DECK1 = "deck_1";
	protected static final String KEY_GAME_DECK2 = "deck_2";
	protected static final String KEY_GAME_WINNER = "winner";
	protected static final String KEY_GAME_DATE = "game_date";

	protected static final String DB_TABLE_ALLDECKS = "Decks";
	protected static final String DB_TABLE_ALLPLAYERS = "Players";
	protected static final String DB_TABLE_ALLGAMES = "Games";

	private Context mhContext;

	private boolean isUpgrade = false;

	public MagicHatDbHelper(Context mhContext, String dbName, int dbVersion) {
		super(mhContext, dbName, null, dbVersion);
		this.mhContext = mhContext;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

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

		// Only used when testing on a simulator
		if (MagicHatDb.DB_MOCKDATA) {
			setupMockData(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// super.onDowngrade(db, oldVersion, newVersion);
		onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.i("MagicHatDbHelper.onUpgrade",
				"Code Path for MagicHatDb onUpgrade was triggered.");

		isUpgrade = true;
		List<Player> allPlayers = new ArrayList<Player>();
		List<Deck> allDecks = new ArrayList<Deck>();
		List<Game> allGames = new ArrayList<Game>();

		// TODO This requires a certain db schema to be successful
		allPlayers = getAllPlayers(db);
		allDecks = getAllDecks(false, db);
		allGames = getAllGames(db);

		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLDECKS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLPLAYERS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLGAMES);

		onCreate(db);

		insertAllData(allPlayers, allDecks, allGames, db);

		Log.i("MagicHatDbHelper.populateAllGames", "Done Populating all Games.");
	}

	public boolean isUpgrade() {
		return this.isUpgrade;
	}

	// ///////////////////////////////////////////////////////////
	// INITIAL SETUP
	// ///////////////////////////////////////////////////////////

	private void insertAllData(List<Player> allOwners, List<Deck> allDecks,
			List<Game> allGames, SQLiteDatabase db) {

		// Players must be added before Decks
		for (Player p : allOwners) {
			p.setId(0);
			writePlayer(p, db);
		}
		Log.i("MagicHatDbHelper.setupAllData", "Done setting up Players");

		allOwners = getAllOwners(db);

		// This adds the previously existing decks prior to dropping tables
		for (Deck d : allDecks) {
			// Set the Id of the deck to zero since this is either an upgrade or
			// an install
			d.setId(0);
			d.getOwner().setId(0);
			writeDeck(d, db);
		}
		Log.i("MagicHatDbHelper.setupAllData", "Done setting up Decks");

		// Since this is only run from the onUpgrade task
		// The Deck and Player Ids might've changed since the deletion
		// and recreation of all of the data
		for (Game g : allGames) {
			g.setId(0);
			writeNewGame(g, db);
		}
		Log.i("MagicHatDbHelper.setupAllData", "Done setting up Games");
	}

	protected boolean backupDb(SQLiteDatabase db) {
		File dbBackupPath = new File(MagicHatDb.DB_BACKUP_PATH);
		File dbBackupFile = new File(MagicHatDb.DB_BACKUP_PATH
				+ MagicHatDb.DB_NAME);
		File dbFile = new File(MagicHatDb.DB_PATH + MagicHatDb.DB_NAME);

		try {
			if (!dbBackupPath.exists()) {
				dbBackupPath.mkdir();
			}

			// Open your local db as the input stream
			InputStream myInput = new FileInputStream(dbFile);

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(dbBackupFile);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[5700];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("MagicHatDb.backupDb", "Database has been backed up");

		return true;
	}

	protected void restoreDb(SQLiteDatabase db) {
		SQLiteDatabase backupMhDb = SQLiteDatabase.openDatabase(
				MagicHatDb.DB_BACKUP_PATH + MagicHatDb.DB_NAME, null,
				SQLiteDatabase.OPEN_READONLY);

		List<Player> allOwners = getAllOwners(backupMhDb);
		List<Deck> allDecks = getAllDecks(false, backupMhDb);
		List<Game> allGames = getAllGames(backupMhDb);

		backupMhDb.close();

		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLDECKS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLPLAYERS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALLGAMES);

		onCreate(db);

		insertAllData(allOwners, allDecks, allGames, db);

		Log.i("MagicHatDb.restoreDb", "Database has been restored");
	}

	private void setupMockData(SQLiteDatabase db) {
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
			cvp.put(KEY_PLAYER_NAME, p.getName());
			cvp.put(KEY_PLAYER_ACTIVE, iActive);
			cvp.put(KEY_PLAYER_SELF, 0);
			db.insert(DB_TABLE_ALLPLAYERS, null, cvp);
			Log.i("MagicHatDbHelper.setupMockData", p.toString()
					+ " Owner was added\n");
		}
		Log.i("MagicHatDbHelper.setupMockData", "Done setting up Players");

		iActive = 0;
		for (Deck d : allNewDecks) {
			if (!deckExists(d, db)) {
				iActive = d.isActive() ? 1 : 0;

				ContentValues cvd = new ContentValues();
				cvd.put(KEY_DECK_NAME, d.getName().toString());
				Player p = getPlayer(d.getOwner().getName(), db);
				cvd.put(KEY_DECK_OWNERID, p.getId());
				cvd.put(KEY_DECK_ACTIVE, iActive);
				db.insert(DB_TABLE_ALLDECKS, null, cvd);
				Log.i("MagicHatDbHelper.setupMockData", d.toString()
						+ " Deck was added\n");
			}
		}
		Log.i("MagicHatDbHelper.setupMockData", "Done setting up Decks");
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

		String[] deckColumns = new String[] { KEY_DECK_ROWID };
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
		Player p = d.getOwner();
		if (p.getId() == 0) {
			p = getPlayer(d.getOwner().getName(), db);
		}

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

		String query = "";
		if (deckId != 0) {
			query = KEY_DECK_ROWID + " = " + deckId;
		}

		Cursor dc = db.query(DB_TABLE_ALLDECKS, deckColumns, query, null, null,
				null, KEY_DECK_ROWID + " desc");

		if (dc.getCount() == 1 || deckId == 0) {
			int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
			int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
			int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
			int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

			dc.moveToFirst();

			deckId = dc.getInt(iDeckId);
			String deckName = dc.getString(iDeckName);
			int ownerId = dc.getInt(iDeckOwnerId);
			boolean active = (dc.getInt(iDeckActive) == 1);

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
		List<Deck> deckList = getDeckList(p, false, db);

		try {
			db.delete(DB_TABLE_ALLPLAYERS,
					KEY_PLAYER_ROWID + " = " + p.getId(), null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		// This will set all of that owner's decks to have no owner
		setDeckList(0, deckList, db);
	}

	protected Player getPlayer(int playerId, SQLiteDatabase db) {
		// return getOwner(playerId, db);
		Player p = new Player();
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_DCI, KEY_PLAYER_ACTIVE,
				KEY_PLAYER_SELF };

		String query = "";
		if (playerId != 0) {
			query = KEY_PLAYER_ROWID + " = " + playerId;
		}

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns, query, null,
				null, null, KEY_PLAYER_ROWID + " desc");

		if (pc.getCount() == 1 || playerId == 0) {
			int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
			int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
			int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
			int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
			int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

			pc.moveToFirst();

			playerId = pc.getInt(iOwnerId);
			String ownerName = pc.getString(iOwnerName);
			boolean active = (pc.getInt(iOwnerActive) == 1);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
			if (pc.isNull(iOwnerDci)) {
				p = new Player(playerId, ownerName, active, self);

			} else {
				int dci = pc.getInt(iOwnerDci);
				p = new Player(playerId, ownerName, dci, active, self);
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
				KEY_PLAYER_DCI, KEY_PLAYER_ACTIVE, KEY_PLAYER_SELF };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			boolean active = (pc.getInt(iOwnerActive) == 1);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
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
				KEY_PLAYER_NAME, KEY_PLAYER_DCI, KEY_PLAYER_SELF };

		// Add p to the list of Players so long as the deck is active,
		// p isn't a Wizards Decks
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, playerColumns,
				KEY_PLAYER_ACTIVE + " = 1 AND " + KEY_PLAYER_NAME
						+ " != 'Wizards of the Coast'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
			String ownerName = pc.getString(iOwner);

			Player p = new Player();
			if (pc.isNull(iOwnerDci)) {
				p = new Player(ownerId, ownerName, true, self);
			} else {
				p = new Player(ownerId, ownerName, pc.getInt(iOwnerDci), true,
						self);
			}

			// Add p to the list of Players so long as p isn't already in
			// the list of Players
			if (!allActivePlayers.contains(p)) {
				allActivePlayers.add(p);
			}
		}
		pc.close();

		Collections.sort(allActivePlayers);

		return allActivePlayers;
	}

	protected List<Player> getAllPlayers(SQLiteDatabase db) {
		List<Player> allPlayers = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_DCI, KEY_PLAYER_ACTIVE, KEY_PLAYER_SELF };

		// Add p to the list of Players so long as
		// p isn't the Wizards Decks
		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, KEY_PLAYER_NAME
				+ " != 'Wizards of the Coast'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			boolean active = (pc.getInt(iActive) == 1);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
			String ownerName = pc.getString(iOwnerName);

			Player p = new Player();
			if (pc.isNull(iOwnerDci)) {
				p = new Player(ownerId, ownerName, active, self);
			} else {
				p = new Player(ownerId, ownerName, pc.getInt(iOwnerDci),
						active, self);
			}

			// Add p to the list of Players so long as
			// p isn't already in the list of Players
			if (!allPlayers.contains(p)) {
				allPlayers.add(p);
			}
		}
		pc.close();

		Collections.sort(allPlayers);

		return allPlayers;
	}

	protected List<Player> getAllOwners(SQLiteDatabase db) {
		List<Player> allOwners = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_DCI, KEY_PLAYER_ACTIVE, KEY_PLAYER_SELF };

		Cursor pc = db.query(DB_TABLE_ALLPLAYERS, columns, null, null, null,
				null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerDci = pc.getColumnIndex(KEY_PLAYER_DCI);
		int iActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iOwnerSelf = pc.getColumnIndex(KEY_PLAYER_SELF);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			boolean active = (pc.getInt(iActive) == 1);
			boolean self = (pc.getInt(iOwnerSelf) == 1);
			String ownerName = pc.getString(iOwnerName);

			Player p = new Player();
			if (pc.isNull(iOwnerDci)) {
				p = new Player(ownerId, ownerName, active, self);
			} else {
				p = new Player(ownerId, ownerName, pc.getInt(iOwnerDci),
						active, self);
			}

			// Add p to the list of Players so long as p isn't already in
			// the list of Players
			if (!allOwners.contains(p)) {
				allOwners.add(p);
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
		Log.d("MagicHatDbHelper.populateAllGames",
				"Attempting to write Game Number " + g.getId() + ": "
						+ g.getDeck(0).toString() + " vs "
						+ g.getDeck(1).toString() + ".");

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

		Log.d("MagicHatDbHelper.populateAllGames", "Game Number " + g.getId()
				+ ": " + g.getDeck(0).toString() + " vs "
				+ g.getDeck(1).toString() + " was inserted.");
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