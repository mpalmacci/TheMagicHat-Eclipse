package com.magichat;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MagicHatDB {

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

	public static final String KEY_CARDSET_ROWID = "_id";
	public static final String KEY_CARDSET_NAME = "card_set_name";
	public static final String KEY_CARDSET_SHORTNAME = "card_set_shortname";

	public static final String KEY_CARD_ROWID = "_id";
	public static final String KEY_CARD_NAME = "card_name";
	public static final String KEY_CARD_DEFAULT_CARDSET = "card_def_set";
	public static final String KEY_CARD_DEFAULT_PICURL = "card_def_picurl";
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
	public static final String KEY_CARD_TEXT = "card_text";

	public static final String KEY_CARDSET_PIC_ROWID = "_id";
	public static final String KEY_CARDSET_PIC_CARD_ID = "cardset_pic_card_id";
	public static final String KEY_CARDSET_PIC_CARDSET_ID = "cardset_pic_set_id";
	public static final String KEY_CARDSET_PIC_PICURL = "cardset_pic_picurl";

	private static final String DATABASE_NAME = "MagicHatDB";
	// private static final String DATABASE_PATH =
	// "/data/data/com.magichat/databases/";
	private static final String DATABASE_TABLE_ALLDECKS = "Decks";
	private static final String DATABASE_TABLE_ALLPLAYERS = "Players";
	private static final String DATABASE_TABLE_ALLGAMES = "Games";
	private static final String DATABASE_TABLE_ALLCARDSETS = "CardSets";
	private static final String DATABASE_TABLE_ALLCARDS = "Cards";
	private static final String DATABASE_TABLE_CARDSET_PIC = "CardSetPics";

	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {
		private Context context;
		private boolean isUpgrade = false;

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (!isUpgrade) {
				// TODO This is not displaying
				Toast.makeText(context,
						"Initializing database... Please wait...",
						Toast.LENGTH_LONG).show();
			}

			db.execSQL("CREATE TABLE " + DATABASE_TABLE_ALLPLAYERS + " ("
					+ KEY_PLAYER_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_PLAYER_NAME + " TEXT NOT NULL, " + KEY_PLAYER_ACTIVE
					+ " INTEGER NOT NULL);");
			db.execSQL("CREATE TABLE " + DATABASE_TABLE_ALLDECKS + " ("
					+ KEY_DECK_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_DECK_NAME + " TEXT NOT NULL, " + KEY_DECK_OWNERID
					+ " INTEGER NOT NULL, " + KEY_DECK_ACTIVE
					+ " INTEGER NOT NULL, " + KEY_DECK_MANUAL
					+ " INTEGER NOT NULL, FOREIGN KEY(" + KEY_DECK_OWNERID
					+ ") REFERENCES " + DATABASE_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "));");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_ALLGAMES
					+ " (" + KEY_GAME_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_GAME_PLAYER1
					+ " INTEGER NOT NULL, " + KEY_GAME_PLAYER2
					+ " INTEGER NOT NULL, " + KEY_GAME_DECK1
					+ " INTEGER NOT NULL, " + KEY_GAME_DECK2
					+ " INTEGER NOT NULL, " + KEY_GAME_WINNER
					+ " INTEGER NOT NULL, " + KEY_GAME_DATE
					+ " INTEGER NOT NULL, FOREIGN KEY(" + KEY_GAME_PLAYER1
					+ ") REFERENCES " + DATABASE_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_PLAYER2
					+ ") REFERENCES " + DATABASE_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "), FOREIGN KEY(" + KEY_GAME_DECK1
					+ ") REFERENCES " + DATABASE_TABLE_ALLDECKS + "("
					+ KEY_DECK_ROWID + "), FOREIGN KEY(" + KEY_GAME_DECK2
					+ ") REFERENCES " + DATABASE_TABLE_ALLDECKS + "("
					+ KEY_DECK_ROWID + "), FOREIGN KEY(" + KEY_GAME_WINNER
					+ ") REFERENCES " + DATABASE_TABLE_ALLPLAYERS + "("
					+ KEY_PLAYER_ROWID + "));");
			db.execSQL("CREATE TABLE " + DATABASE_TABLE_ALLCARDSETS + " ("
					+ KEY_CARDSET_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CARDSET_NAME
					+ " TEXT NOT NULL, " + KEY_CARDSET_SHORTNAME
					+ " TEXT NOT NULL);");
			db.execSQL("CREATE TABLE " + DATABASE_TABLE_ALLCARDS + " ("
					+ KEY_CARD_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_CARD_NAME + " TEXT NOT NULL, "
					+ KEY_CARD_DEFAULT_CARDSET + " INTEGER NOT NULL, "
					+ KEY_CARD_DEFAULT_PICURL + " TEXT NOT NULL, "
					+ KEY_CARD_ISBLUE + " INTEGER NOT NULL, "
					+ KEY_CARD_ISBLACK + " INTEGER NOT NULL, "
					+ KEY_CARD_ISWHITE + " INTEGER NOT NULL, "
					+ KEY_CARD_ISGREEN + " INTEGER NOT NULL, " + KEY_CARD_ISRED
					+ " INTEGER NOT NULL, " + KEY_CARD_MANACOST + " TEXT, "
					+ KEY_CARD_CMC + " INTEGER NOT NULL, " + KEY_CARD_TYPE
					+ " TEXT NOT NULL, " + KEY_CARD_SUBTYPES + " TEXT, "
					+ KEY_CARD_POWER + " INTEGER, " + KEY_CARD_TOUGHNESS
					+ " INTEGER, " + KEY_CARD_TEXT + " TEXT, FOREIGN KEY("
					+ KEY_CARD_DEFAULT_CARDSET + ") REFERENCES "
					+ DATABASE_TABLE_ALLCARDSETS + "(" + KEY_CARDSET_ROWID
					+ "));");
			db.execSQL("CREATE TABLE " + DATABASE_TABLE_CARDSET_PIC + " ("
					+ KEY_CARDSET_PIC_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_CARDSET_PIC_CARD_ID + " INTEGER NOT NULL, "
					+ KEY_CARDSET_PIC_CARDSET_ID + " INTEGER NOT NULL, "
					+ KEY_CARDSET_PIC_PICURL + " TEXT NOT NULL, FOREIGN KEY("
					+ KEY_CARDSET_PIC_CARD_ID + ") REFERENCES "
					+ DATABASE_TABLE_ALLCARDS + "(" + KEY_CARD_ROWID
					+ "), FOREIGN KEY(" + KEY_CARDSET_PIC_CARDSET_ID
					+ ") REFERENCES " + DATABASE_TABLE_ALLCARDSETS + "("
					+ KEY_CARDSET_ROWID + "));");

			if (!isUpgrade) {
				setupPlayersAndDecks(db, new ArrayList<Deck>());
				setupCards(db);
				Toast.makeText(context, "Database Initialization Complete.",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO This is not displaying
			Toast.makeText(context, "Upgrading database... Please wait...",
					Toast.LENGTH_LONG).show();
			isUpgrade = true;
			List<Game> allGames = new ArrayList<Game>();
			List<Deck> allDecks = new ArrayList<Deck>();

			allGames = getAllGames(db);
			allDecks = getAllDecks(db);

			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLDECKS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLPLAYERS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLGAMES);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CARDSET_PIC);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLCARDS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLCARDSETS);

			onCreate(db);

			setupPlayersAndDecks(db, allDecks);
			populateAllGames(db, allGames);
			setupCards(db);
			Toast.makeText(context, "Database Upgrade Complete.",
					Toast.LENGTH_SHORT).show();
		}

		// ///////////////////////////////////////////////////////////
		// INITIAL SETUP
		// ///////////////////////////////////////////////////////////

		private void setupPlayersAndDecks(SQLiteDatabase db, List<Deck> allDecks) {
			List<Deck> allNewDecks = new ArrayList<Deck>();
			List<Player> allPlayers = new ArrayList<Player>();

			SAXDataParser sdp = new SAXDataParser();
			try {
				sdp.parseDeckListXml(context);
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
				db.insert(DATABASE_TABLE_ALLPLAYERS, null, cvp);
				System.out.println(p.getName().toString()
						+ " Owner was added\n");
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
				Player p = getPlayer(db, d.getOwner().toString());
				cvd.put(KEY_DECK_OWNERID, p.getId());
				cvd.put(KEY_DECK_ACTIVE, iActive);
				cvd.put(KEY_DECK_MANUAL, iManual);
				db.insert(DATABASE_TABLE_ALLDECKS, null, cvd);
				System.out.println(d.getName().toString()
						+ " Deck was added for " + p.toString() + "\n");
			}
			System.out.println("Done setting up original Decks.");

			iActive = 0;
			for (Deck d : allNewDecks) {
				if (!deckExists(db, d)) {
					iActive = d.isActive() ? 1 : 0;

					ContentValues cvd = new ContentValues();
					cvd.put(KEY_DECK_NAME, d.getName().toString());
					Player p = getPlayer(db, d.getOwner().toString());
					cvd.put(KEY_DECK_OWNERID, p.getId());
					cvd.put(KEY_DECK_ACTIVE, iActive);
					cvd.put(KEY_DECK_MANUAL, 0);
					db.insert(DATABASE_TABLE_ALLDECKS, null, cvd);
					System.out.println(d.getName().toString()
							+ " Deck was added for " + p.toString() + "\n");
				}
			}
			System.out.println("Done setting up new Decks.");
		}

		private void populateAllGames(SQLiteDatabase db, List<Game> allGames) {
			for (Game g : allGames) {
				ContentValues cv = new ContentValues();
				cv.put(KEY_GAME_PLAYER1,
						getOwner(db, g.getPlayer(1).toString()).getId());
				cv.put(KEY_GAME_PLAYER2,
						getOwner(db, g.getPlayer(2).toString()).getId());
				cv.put(KEY_GAME_DECK1, getDeck(db, g.getDeck(1).getName(), g.getDeck(1).getOwner().toString()).getId());
				cv.put(KEY_GAME_DECK2, getDeck(db, g.getDeck(2).getName(), g.getDeck(2).getOwner().toString()).getId());
				cv.put(KEY_GAME_WINNER, getOwner(db, g.getWinner().toString())
						.getId());
				cv.put(KEY_GAME_DATE, g.getDate().getTime());

				db.insert(DATABASE_TABLE_ALLGAMES, null, cv);
				System.out.println("Game " + g.getDeck(1).toString() + " vs "
						+ g.getDeck(2).toString() + " was added.");
			}
			System.out.println("Done Populating all Games.");
		}

		private void setupCards(SQLiteDatabase db) {
			List<CardSet> allCardSets = new ArrayList<CardSet>();
			List<Card> allCards = new ArrayList<Card>();

			SAXDataParser sdp = new SAXDataParser();
			try {
				sdp.parseDeckListXml(context);
			} catch (Exception e) {
				e.printStackTrace();
			}

			allCardSets = sdp.getAllCardSets();

			for (CardSet cs : allCardSets) {
				ContentValues cvCs = new ContentValues();
				cvCs.put(KEY_CARDSET_NAME, cs.getName());
				cvCs.put(KEY_CARDSET_SHORTNAME, cs.getShortName());
				db.insert(DATABASE_TABLE_ALLCARDSETS, null, cvCs);
			}
			System.out.println("Done setting up Card Sets.");

			allCardSets = getAllCardSets(db);
			allCards = sdp.getAllCards();

			int iBlue, iBlack, iWhite, iGreen, iRed;
			for (Card c : allCards) {
				iBlue = c.isBlue() ? 1 : 0;
				iBlack = c.isBlack() ? 1 : 0;
				iWhite = c.isWhite() ? 1 : 0;
				iGreen = c.isGreen() ? 1 : 0;
				iRed = c.isRed() ? 1 : 0;

				ContentValues cvc = new ContentValues();
				cvc.put(KEY_CARD_NAME, c.getName());
				cvc.put(KEY_CARD_DEFAULT_CARDSET,
						getCardSetId(c.getDefaultCardSet().getShortName(),
								allCardSets));
				cvc.put(KEY_CARD_DEFAULT_PICURL, c.getDefaultPicURL()
						.toString());
				cvc.put(KEY_CARD_ISBLACK, iBlack);
				cvc.put(KEY_CARD_ISBLUE, iBlue);
				cvc.put(KEY_CARD_ISWHITE, iWhite);
				cvc.put(KEY_CARD_ISGREEN, iGreen);
				cvc.put(KEY_CARD_ISRED, iRed);
				if (!c.getCardType().contains("Land")
						&& !c.getCardType().contains("Scheme")) {
					cvc.put(KEY_CARD_MANACOST, c.getManaCost());
				}
				cvc.put(KEY_CARD_CMC, c.getCMC());
				cvc.put(KEY_CARD_TYPE, c.getCardType());
				cvc.put(KEY_CARD_SUBTYPES, c.getCardSubTypes());
				if (c.getCardType().contains("Creature")) {
					cvc.put(KEY_CARD_POWER, c.getPower());
					cvc.put(KEY_CARD_TOUGHNESS, c.getToughness());
				}
				cvc.put(KEY_CARD_TEXT, c.getText());
				db.insert(DATABASE_TABLE_ALLCARDS, null, cvc);
			}
			System.out.println("Done setting up Cards.");

			allCards = getAllCards(db);

			for (Card c : allCards) {
				ContentValues cvp = new ContentValues();
				for (CardSet cs : c.getAllCardSets()) {
					cvp.put(KEY_CARDSET_PIC_CARDSET_ID,
							getCardSetId(cs.getShortName(), allCardSets));
					int cardId = 0;
					for (Card cd : allCards) {
						if (cd.getName().equals(cd.getName())) {
							cardId = cd.getId();
						}
					}
					if (cardId == 0) {
						System.out
								.println("MagicHatDB.setupCards: Card Id was not found.");
					}
					cvp.put(KEY_CARDSET_PIC_CARD_ID, cardId);
					cvp.put(KEY_CARDSET_PIC_PICURL, c.getSetsImages().get(cs)
							.toString());
					db.insert(DATABASE_TABLE_CARDSET_PIC, null, cvp);
				}
			}
			System.out.println("Done setting up Card Pictures.");
		}

		// //////////////////////////////////
		// DECKS
		// //////////////////////////////////

		public void addDeck(SQLiteDatabase db, String name, int OwnerId,
				Integer active) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_DECK_NAME, name);
			cv.put(KEY_DECK_OWNERID, OwnerId);
			cv.put(KEY_DECK_ACTIVE, active);
			cv.put(KEY_DECK_MANUAL, 1);
			try {
				db.insert(DATABASE_TABLE_ALLDECKS, null, cv);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}

		public void updateDeck(SQLiteDatabase db, String owner,
				String oldDeckName, String newDeckName, boolean newActive) {

			int deckId = getDeckId(db, oldDeckName, owner);

			ContentValues cv = new ContentValues();
			cv.put(KEY_DECK_NAME, newDeckName);
			if (newActive) {
				cv.put(KEY_DECK_ACTIVE, 1);
			} else {
				cv.put(KEY_DECK_ACTIVE, 0);
			}

			try {
				db.update(DATABASE_TABLE_ALLDECKS, cv, KEY_DECK_ROWID + " = "
						+ deckId, null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}

		public void deleteDecks(SQLiteDatabase db, int[] id) {
			for (int dId : id) {
				try {
					db.delete(DATABASE_TABLE_ALLDECKS, KEY_DECK_ROWID + " = "
							+ dId, null);
				} catch (SQLiteException e) {
					e.printStackTrace();
				}
			}
		}

		public Deck getDeck(SQLiteDatabase db, int deckId) {
			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };

			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
					KEY_DECK_ROWID + " = " + deckId, null, null, null, null);

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

			Player owner = getOwner(db, ownerId);

			return new Deck(deckId, deckName, owner, active, manual);
		}

		public Deck getDeck(SQLiteDatabase db, String sDeckName,
				String sOwnerName) {
			Deck d = new Deck();

			Player p = getPlayer(db, sOwnerName);

			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
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
				d = new Deck(dc.getInt(iDeckId), dc.getString(iDeckName),
						getOwner(db, dc.getInt(iDeckOwnerId)), active, manual);
			} else {
				System.out
						.println("No unique deck was found in MagicHatDB.getDeck(deckName).");
			}
			dc.close();

			return d;
		}

		public List<Deck> getAllDecks(SQLiteDatabase db) {
			List<Deck> allDecks = new ArrayList<Deck>();

			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns, null,
					null, null, null, null);

			int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
			int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
			int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
			int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);
			int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

			Deck d;
			for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
				Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
						KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId),
						null, null, null, null);

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

		public List<Deck> getAllActiveDecks(SQLiteDatabase db) {
			List<Deck> allActiveDecks = new ArrayList<Deck>();

			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
					KEY_DECK_ACTIVE + " = 1", null, null, null, null);

			int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
			int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
			int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
			int iDeckManual = dc.getColumnIndex(KEY_DECK_MANUAL);

			Deck d;
			for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
				Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
						KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId),
						null, null, null, null);

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

		public List<Deck> getAllManualDecks(SQLiteDatabase db) {
			List<Deck> allManDecks = new ArrayList<Deck>();
			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
					KEY_DECK_MANUAL + " = 1", null, null, null, null);

			int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
			int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
			int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
			int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

			Deck d;
			for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
				Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
						KEY_PLAYER_ROWID + " = " + dc.getInt(iDeckOwnerId),
						null, null, null, null);

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
					d = new Deck(deckId, dc.getString(iDeckName), owner,
							active, true);
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

		public List<Deck> getDeckList(SQLiteDatabase db, Player p) {
			List<Deck> deckList = new ArrayList<Deck>();
			Player pReal = getPlayer(db, p.toString());

			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
					KEY_DECK_OWNERID + " = " + pReal.getId(), null, null, null,
					null);

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

		public int getDeckId(SQLiteDatabase db, String sDeckName,
				String sOwnerName) {
			int deckId = 0;

			Player p = getPlayer(db, sOwnerName);

			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
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

		public boolean deckExists(SQLiteDatabase db, Deck d) {
			String[] deckColumns = new String[] { KEY_DECK_ROWID,
					KEY_DECK_NAME, KEY_DECK_OWNERID, KEY_DECK_ACTIVE,
					KEY_DECK_MANUAL };
			Player p = getPlayer(db, d.getOwner().toString());
			Cursor dc = db.query(DATABASE_TABLE_ALLDECKS, deckColumns,
					KEY_DECK_NAME + " = '" + d.getName().toString() + "' AND "
							+ KEY_DECK_OWNERID + " = " + p.getId(), null, null,
					null, null);

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

		public Player getOwner(SQLiteDatabase db, int ownerId) {
			Player p = new Player();
			String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };
			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
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

		public Player getPlayer(SQLiteDatabase db, int playerId) {
			return getOwner(db, playerId);
		}

		public Player getOwner(SQLiteDatabase db, String name) {
			Player p = new Player();
			String[] columns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, columns,
					KEY_PLAYER_NAME + " = '" + name + "'", null, null, null,
					null);

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

		public Player getPlayer(SQLiteDatabase db, String name) {
			return getOwner(db, name);
		}

		public int getPlayerId(SQLiteDatabase db, String name) {
			int playerId = 0;

			String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME };

			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, columns,
					KEY_PLAYER_NAME + " = '" + name + "'", null, null, null,
					null);

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

		public List<Player> getActivePlayers(SQLiteDatabase db) {
			List<Player> allActivePlayers = new ArrayList<Player>();

			String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			// Add p to the list of Players so long as the deck is active,
			// p isn't the Wizards Decks
			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
					KEY_PLAYER_ACTIVE + " = 1 AND " + KEY_PLAYER_NAME
							+ " != 'Wizards of the Coast'", null, null, null,
					null);

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

		public List<Player> getAllPlayers(SQLiteDatabase db) {
			List<Player> allPlayers = new ArrayList<Player>();

			String[] columns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			// Add p to the list of Players so long as
			// p isn't the Wizards Decks
			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, columns,
					KEY_PLAYER_NAME + " != 'Wizards of the Coast'", null, null,
					null, null);

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

		public List<Player> getAllOwners(SQLiteDatabase db) {
			List<Player> allOwners = new ArrayList<Player>();

			String[] columns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor pc = db.query(DATABASE_TABLE_ALLPLAYERS, columns, null,
					null, null, null, null);

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

		public Player flipActiveStatus(SQLiteDatabase db, Player pFake) {
			Player pReal = getOwner(db, pFake.toString());

			ContentValues cv = new ContentValues();
			if (pReal.isActive()) {
				cv.put(KEY_PLAYER_ACTIVE, 0);
			} else {
				cv.put(KEY_PLAYER_ACTIVE, 1);
			}
			try {
				db.update(DATABASE_TABLE_ALLPLAYERS, cv, KEY_PLAYER_ROWID
						+ " = " + pReal.getId(), null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}

			return getOwner(db, pReal.toString());
		}

		public Deck flipActiveStatus(SQLiteDatabase db, String deckName,
				String ownerName) {
			int deckId = getDeckId(db, deckName, ownerName);

			Deck d = getDeck(db, deckId);

			ContentValues cv = new ContentValues();
			if (d.isActive()) {
				cv.put(KEY_DECK_ACTIVE, 0);
			} else {
				cv.put(KEY_DECK_ACTIVE, 1);
			}

			try {
				db.update(DATABASE_TABLE_ALLDECKS, cv, KEY_DECK_ROWID + " = "
						+ d.getId(), null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}

			return getDeck(db, deckId);
		}

		// /////////////////////////////
		// GAMES
		// /////////////////////////////

		public void addGameResult(SQLiteDatabase db, List<Player> Players,
				List<Deck> gameDecks, Player pWinner, Date gameDate) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_GAME_PLAYER1, Players.get(0).getId());
			cv.put(KEY_GAME_PLAYER2, Players.get(1).getId());
			cv.put(KEY_GAME_DECK1, gameDecks.get(0).getId());
			cv.put(KEY_GAME_DECK2, gameDecks.get(1).getId());
			cv.put(KEY_GAME_WINNER, pWinner.getId());
			cv.put(KEY_GAME_DATE, gameDate.getTime());
			try {
				db.insert(DATABASE_TABLE_ALLGAMES, null, cv);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}

		public List<Game> getAllGames(SQLiteDatabase db) {
			List<Game> allGames = new ArrayList<Game>();

			String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
					KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
					KEY_GAME_WINNER, KEY_GAME_DATE };

			Cursor gc = db.query(DATABASE_TABLE_ALLGAMES, columns, null, null,
					null, null, null);

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

				Player p1 = getOwner(db, player1Id);
				Player p2 = getOwner(db, player2Id);
				Deck d1 = getDeck(db, deck1Id);
				Deck d2 = getDeck(db, deck2Id);
				Player pW = getOwner(db, winnerId);

				allGames.add(new Game(gameId, p1, p2, d1, d2, pW, date));
			}
			gc.close();

			return allGames;
		}

		public List<Game> getGames(SQLiteDatabase db, Player p) {
			List<Game> games = new ArrayList<Game>();

			String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
					KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
					KEY_GAME_WINNER, KEY_GAME_DATE };

			Cursor gc = db.query(DATABASE_TABLE_ALLGAMES, columns,
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

				Player p1 = getPlayer(db, player1);
				Player p2 = getPlayer(db, player2);
				Deck d1 = getDeck(db, deck1);
				Deck d2 = getDeck(db, deck2);
				Player pW = getPlayer(db, winner);

				games.add(new Game(gameId, p1, p2, d1, d2, pW, date));
			}
			gc.close();

			return games;
		}

		public List<Game> getGames(SQLiteDatabase db, Deck d) {
			List<Game> games = new ArrayList<Game>();

			String[] gameColumns = new String[] { KEY_GAME_ROWID,
					KEY_GAME_PLAYER1, KEY_GAME_PLAYER2, KEY_GAME_DECK1,
					KEY_GAME_DECK2, KEY_GAME_WINNER, KEY_GAME_DATE };

			Cursor gc = db.query(DATABASE_TABLE_ALLGAMES, gameColumns,
					KEY_GAME_DECK1 + " = " + d.getId() + " OR "
							+ KEY_GAME_DECK2 + " = " + d.getId(), null, null,
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

				Player p1 = getPlayer(db, player1);
				Player p2 = getPlayer(db, player2);
				Deck d1 = getDeck(db, deck1);
				Deck d2 = getDeck(db, deck2);
				Player pW = getPlayer(db, winner);

				games.add(new Game(gameId, p1, p2, d1, d2, pW, date));
			}
			gc.close();

			return games;
		}

		// //////////////////////////////////
		// CARD SETS
		// //////////////////////////////////

		public int getCardSetId(SQLiteDatabase db, String shortName) {
			int cardSetId = 0;

			String[] columns = new String[] { KEY_CARDSET_ROWID,
					KEY_CARDSET_NAME, KEY_CARDSET_SHORTNAME };

			Cursor csc = db.query(DATABASE_TABLE_ALLCARDSETS, columns,
					KEY_CARDSET_SHORTNAME + " = '" + shortName + "'", null,
					null, null, null);

			int iCardSetId = csc.getColumnIndex(KEY_CARDSET_ROWID);

			if (csc.getCount() == 1) {
				csc.moveToFirst();
				cardSetId = csc.getInt(iCardSetId);
			} else {
				System.out
						.println("No unique card set was found in MagicHatDB.getCardSetId.");
			}
			csc.close();

			return cardSetId;
		}

		private List<CardSet> getAllCardSets(SQLiteDatabase db) {
			List<CardSet> allCardSets = new ArrayList<CardSet>();
			String[] cardSetColumns = new String[] { KEY_CARDSET_ROWID,
					KEY_CARDSET_NAME, KEY_CARDSET_SHORTNAME };

			Cursor csc = db.query(DATABASE_TABLE_ALLCARDSETS, cardSetColumns,
					null, null, null, null, null);

			int iCardSetId = csc.getColumnIndex(KEY_CARDSET_ROWID);
			int iCardSetName = csc.getColumnIndex(KEY_CARDSET_NAME);
			int iCardSetShortName = csc.getColumnIndex(KEY_CARDSET_SHORTNAME);

			CardSet cs;
			for (csc.moveToFirst(); !csc.isAfterLast(); csc.moveToNext()) {
				cs = new CardSet(csc.getString(iCardSetName),
						csc.getString(iCardSetShortName),
						csc.getInt(iCardSetId));

				allCardSets.add(cs);
			}
			csc.close();

			Collections.sort(allCardSets);

			return allCardSets;
		}

		private int getCardSetId(String shortName, List<CardSet> allCardSets) {
			for (CardSet cs : allCardSets) {
				if (cs.getShortName().equals(shortName)) {
					return cs.getId();
				}
			}
			System.out.println("The Card Set wasn't found in getCardSetId");
			return 0;
		}

		// //////////////////////////////
		// CARDS
		// //////////////////////////////

		private List<Card> getAllCards(SQLiteDatabase db) {
			List<Card> allCards = new ArrayList<Card>();
			String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_NAME };

			Cursor cc = db.query(DATABASE_TABLE_ALLCARDS, cardColumns, null,
					null, null, null, null);

			int iCardId = cc.getColumnIndex(KEY_CARDSET_ROWID);
			int iCardName = cc.getColumnIndex(KEY_CARDSET_NAME);

			Card c;
			for (cc.moveToFirst(); !cc.isAfterLast(); cc.moveToNext()) {
				c = new Card(cc.getString(iCardName), cc.getInt(iCardId));

				allCards.add(c);
			}
			cc.close();

			Collections.sort(allCards);

			return allCards;
		}

		private int getCardId(String name, List<Card> allCards) {
			for (Card c : allCards) {
				if (c.getName().equals(name)) {
					return c.getId();
				}
			}
			System.out.println("The Card Set wasn't found in getCardSetId");
			return 0;
		}
	}

	// ///////////////////////////////MAIN SETUP//////////////////////////////

	public MagicHatDB(Context c) {
		ourContext = c;
	}

	public MagicHatDB openWritableDB() {
		ourHelper = new DbHelper(ourContext);
		try {
			ourDatabase = ourHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return this;
	}

	public MagicHatDB openReadableDB() {
		ourHelper = new DbHelper(ourContext);
		try {
			ourDatabase = ourHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void closeDB() {
		try {
			ourHelper.close();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	// //////////////////////////////// DECKS //////////////////////////////////

	public void addDeck(String name, int OwnerId, Integer active) {
		ourHelper.addDeck(ourDatabase, name, OwnerId, active);
	}

	public void updateDeck(String owner, String oldDeckName,
			String newDeckName, boolean newActive) {
		ourHelper.updateDeck(ourDatabase, owner, oldDeckName, newDeckName,
				newActive);
	}

	public void deleteDecks(int[] id) {
		ourHelper.deleteDecks(ourDatabase, id);
	}

	public Deck getDeck(int deckId) {
		return ourHelper.getDeck(ourDatabase, deckId);
	}

	public Deck getDeck(String sDeckName, String sOwnerName) {
		return ourHelper.getDeck(ourDatabase, sDeckName, sOwnerName);
	}

	public List<Deck> getAllDecks() {
		return ourHelper.getAllDecks(ourDatabase);
	}

	public List<Deck> getAllActiveDecks() {
		return ourHelper.getAllActiveDecks(ourDatabase);
	}

	public List<Deck> getAllManualDecks() {
		return ourHelper.getAllManualDecks(ourDatabase);
	}

	public List<Deck> getDeckList(Player p) {
		return ourHelper.getDeckList(ourDatabase, p);
	}

	public int getDeckId(String sDeckName, String sOwnerName) {
		return ourHelper.getDeckId(ourDatabase, sDeckName, sOwnerName);
	}

	public boolean deckExists(Deck d) {
		return ourHelper.deckExists(ourDatabase, d);
	}

	// ////////////////////PLAYERS/////////////OWNERS///////////////////////////////

	public Player getOwner(int ownerId) {
		return ourHelper.getOwner(ourDatabase, ownerId);
	}

	public Player getPlayer(int playerId) {
		return ourHelper.getPlayer(ourDatabase, playerId);
	}

	public Player getOwner(String name) {
		return ourHelper.getOwner(ourDatabase, name);
	}

	public Player getPlayer(String name) {
		return ourHelper.getPlayer(ourDatabase, name);
	}

	public int getPlayerId(String name) {
		return ourHelper.getPlayerId(ourDatabase, name);
	}

	public List<Player> getActivePlayers() {
		return ourHelper.getActivePlayers(ourDatabase);
	}

	public List<Player> getAllPlayers() {
		return ourHelper.getAllPlayers(ourDatabase);
	}

	public List<Player> getAllOwners() {
		return ourHelper.getAllOwners(ourDatabase);
	}

	public Cursor getCursorForAllOwners() {
		return ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, new String[] {
				KEY_PLAYER_ROWID, KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE }, null,
				null, null, null, null);
	}

	public Player flipActiveStatus(Player pFake) {
		return ourHelper.flipActiveStatus(ourDatabase, pFake);
	}

	public Deck flipActiveStatus(String deckName, String ownerName) {
		return ourHelper.flipActiveStatus(ourDatabase, deckName, ownerName);
	}

	// /////////////////// GAMES ///////////////////////////////////////////

	public void addGameResult(List<Player> Players, List<Deck> gameDecks,
			Player pWinner, Date gameDate) {
		ourHelper.addGameResult(ourDatabase, Players, gameDecks, pWinner,
				gameDate);
	}

	public List<Game> getAllGames() {
		return ourHelper.getAllGames(ourDatabase);
	}

	public List<Game> getGames(Player p) {
		return ourHelper.getGames(ourDatabase, p);
	}

	public List<Game> getGames(Deck d) {
		return ourHelper.getGames(ourDatabase, d);
	}

	// ///////////////////////////// CARD SET //////////////////////////////////

	public int getCardSetId(String shortName) {
		return ourHelper.getCardSetId(ourDatabase, shortName);
	}

	private List<CardSet> getAllCardSets() {
		return ourHelper.getAllCardSets(ourDatabase);
	}

	private int getCardSetId(String shortName, List<CardSet> allCardSets) {
		return ourHelper.getCardSetId(shortName, allCardSets);
	}

	// ////////////////////////////// CARDS ////////////////////////////////////

	private List<Card> getAllCards() {
		return ourHelper.getAllCards(ourDatabase);
	}

	private int getCardId(String name, List<Card> allCards) {
		return ourHelper.getCardId(name, allCards);
	}
}
