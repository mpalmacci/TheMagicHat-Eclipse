package com.magichat;

import java.util.Date;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MagicHatDB {

	public static final String KEY_DECK_ROWID = "_id";
	public static final String KEY_DECK_NAME = "deck_name";
	public static final String KEY_DECK_OWNERID = "owner_id";
	public static final String KEY_DECK_ACTIVE = "active_deck";
	public static final String KEY_DECK_MANUAL = "man_created";

	public static final String KEY_PLAYER_ROWID = "_id";
	public static final String KEY_PLAYER_NAME = "player_name";
	public static final String KEY_PLAYER_ACTIVE = "player_active";

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
		Context cont;

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.cont = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
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
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_ALLGAMES + " ("
					+ KEY_GAME_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_GAME_PLAYER1 + " INTEGER NOT NULL, "
					+ KEY_GAME_PLAYER2 + " INTEGER NOT NULL, " + KEY_GAME_DECK1
					+ " INTEGER NOT NULL, " + KEY_GAME_DECK2
					+ " INTEGER NOT NULL, " + KEY_GAME_WINNER
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

			setupPlayersAndDecks(db);
			setupCards(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLDECKS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLPLAYERS);
			// TODO Keep games record when updating db
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLGAMES);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CARDSET_PIC);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLCARDS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALLCARDSETS);

			onCreate(db);
		}

		private void setupPlayersAndDecks(SQLiteDatabase db) {
			List<Deck> allDecks = new ArrayList<Deck>();
			List<Player> allPlayers = new ArrayList<Player>();

			SAXDataParser sdp = new SAXDataParser();
			try {
				sdp.parseDeckListXml(cont);
			} catch (Exception e) {
				e.printStackTrace();
			}

			allPlayers = sdp.getAllOwners();
			allDecks = sdp.getAllDecks();

			// Players must be added before Decks
			int iActive = 0;
			for (Player p : allPlayers) {
				if (p.isActive()) {
					iActive = 1;
				} else {
					iActive = 0;
				}

				ContentValues cvp = new ContentValues();
				cvp.put(KEY_PLAYER_NAME, p.getName().toString());
				cvp.put(KEY_PLAYER_ACTIVE, iActive);
				db.insert(DATABASE_TABLE_ALLPLAYERS, null, cvp);
			}

			iActive = 0;
			for (Deck d : allDecks) {
				if (d.isActive()) {
					iActive = 1;
				} else {
					iActive = 0;
				}

				ContentValues cvd = new ContentValues();
				cvd.put(KEY_DECK_NAME, d.getName().toString());
				int iOwner = getOwnerId(db, d.getOwner().toString());
				cvd.put(KEY_DECK_OWNERID, iOwner);
				cvd.put(KEY_DECK_ACTIVE, iActive);
				cvd.put(KEY_DECK_MANUAL, 0);
				db.insert(DATABASE_TABLE_ALLDECKS, null, cvd);
			}
		}

		// TODO Duplicate code with getPlayerId in the main MagicHatDB class
		private int getOwnerId(SQLiteDatabase db, String name) {
			String[] columns = new String[] { KEY_PLAYER_ROWID,
					KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

			Cursor dc = db.query(DATABASE_TABLE_ALLPLAYERS, columns, null,
					null, null, null, null);

			int iOwner = dc.getColumnIndex(KEY_PLAYER_NAME);
			int iOwnerId = dc.getColumnIndex(KEY_PLAYER_ROWID);

			for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
				String sOwner = dc.getString(iOwner);
				if (sOwner.equals(name)) {
					return dc.getInt(iOwnerId);
				}
			}
			return 0;
		}

		private void setupCards(SQLiteDatabase db) {
			List<CardSet> allCardSets = new ArrayList<CardSet>();
			List<Card> allCards = new ArrayList<Card>();

			SAXDataParser sdp = new SAXDataParser();
			try {
				sdp.parseDeckListXml(cont);
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
				cvc.put(KEY_CARD_DEFAULT_PICURL, c.getDefaultPicURL().toString());
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

			allCards = getAllCards(db);

			for (Card c : allCards) {
				ContentValues cvp = new ContentValues();
				for (CardSet cs : c.getAllCardSets()) {
					cvp.put(KEY_CARDSET_PIC_CARDSET_ID,
							getCardSetId(cs.getShortName(),
									allCardSets));
					cvp.put(KEY_CARDSET_PIC_CARD_ID,
							getCardId(c.getName(), allCards));
					cvp.put(KEY_CARDSET_PIC_PICURL, c.getSetsImages().get(cs).toString());
					db.insert(DATABASE_TABLE_CARDSET_PIC, null, cvp);
				}
			}
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
			ourDatabase = ourHelper.getReadableDatabase();
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

	public void addDeck(String name, int OwnerId, Integer active) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_NAME, name);
		cv.put(KEY_DECK_OWNERID, OwnerId);
		cv.put(KEY_DECK_ACTIVE, active);
		cv.put(KEY_DECK_MANUAL, 1);
		try {
			ourDatabase.insert(DATABASE_TABLE_ALLDECKS, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public void deleteDecks(int[] id) {
		for (int dId : id) {
			try {
				ourDatabase.delete(DATABASE_TABLE_ALLDECKS, KEY_DECK_ROWID
						+ " = " + dId, null);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}
		}
	}

	public Deck getDeck(int deckId) {
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_ROWID + " = " + deckId, null, null, null, null);
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
				null, null, null, null, null);

		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d = new Deck();
		dc.moveToFirst();
		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			if (pc.getInt(iOwnerId) == dc.getInt(iDeckOwnerId)) {
				boolean active = (dc.getInt(iDeckActive) == 1);
				boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
				Player owner = new Player(pc.getString(iOwnerName),
						ownerActive, pc.getInt(iOwnerId));
				d = new Deck(dc.getString(iDeckName), owner, active, deckId);
				break;
			} else if (pc.isLast()) {
				System.out.println("No player matched the OwnerId.");
			}
		}

		return d;
	}

	public List<Deck> getAllDecks() {
		List<Deck> allDecks = new ArrayList<Deck>();
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				null, null, null, null, null);
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
				null, null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
				if (pc.getInt(iOwnerId) == dc.getInt(iDeckOwnerId)) {
					int deckId = dc.getInt(iDeckId);
					boolean active = (dc.getInt(iDeckActive) == 1);
					boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
					Player owner = new Player(pc.getString(iOwnerName),
							ownerActive, pc.getInt(iOwnerId));
					d = new Deck(dc.getString(iDeckName), owner, active, deckId);
					allDecks.add(d);
					break;
				} else if (pc.isLast()) {
					System.out
							.println("You've missed at least one Deck from your decklist.");
				}
			}
		}

		Collections.sort(allDecks);

		return allDecks;
	}

	public List<Deck> getAllManualDecks() {
		List<Deck> allManDecks = new ArrayList<Deck>();
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_MANUAL + " = 1", null, null, null, null);
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
				null, null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iDeckName = dc.getColumnIndex(KEY_DECK_NAME);
		int iDeckOwnerId = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);
		int iDeckActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
				if (pc.getInt(iOwnerId) == dc.getInt(iDeckOwnerId)) {
					int deckId = dc.getInt(iDeckId);
					boolean active = (dc.getInt(iDeckActive) == 1);
					boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
					Player owner = new Player(pc.getString(iOwnerName),
							ownerActive, pc.getInt(iOwnerId));
					d = new Deck(dc.getString(iDeckName), owner, active, deckId);
					allManDecks.add(d);
					break;
				}
			}
			if (dc.isLast()) {
				System.out
						.println("There are no manually created decks to delete.");
			}
		}

		Collections.sort(allManDecks);

		return allManDecks;
	}

	public List<Deck> getDeckList(Player p) {
		List<Deck> deckList = new ArrayList<Deck>();
		int pId = getPlayerId(p.toString());

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_OWNERID + " = " + pId, null, null, null, null);

		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);
		int iName = dc.getColumnIndex(KEY_DECK_NAME);
		int iActive = dc.getColumnIndex(KEY_DECK_ACTIVE);

		Deck d;
		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			int deckId = dc.getInt(iDeckId);
			boolean active = (dc.getInt(iActive) == 1);
			d = new Deck(dc.getString(iName), p, active, deckId);
			deckList.add(d);
		}

		Collections.sort(deckList);

		return deckList;
	}

	// Deck name is in the first position, Owner is in the second position
	public int getDeckId(String sDeckName, String sOwnerName) {
		int deckId = 0;

		int ownerId = getPlayerId(sOwnerName);

		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				KEY_DECK_NAME + " = '" + sDeckName + "' AND "
						+ KEY_DECK_OWNERID + " = " + ownerId, null, null, null,
				null);
		int iDeckId = dc.getColumnIndex(KEY_DECK_ROWID);

		if (dc.getCount() == 1) {
			dc.moveToFirst();
			deckId = dc.getInt(iDeckId);
		} else {
			System.out
					.println("No unique deck was found in MagicHatDB.getDeckId.");
		}

		return deckId;
	}

	public boolean deckExists(Deck d) {
		String[] deckColumns = new String[] { KEY_DECK_ROWID, KEY_DECK_NAME,
				KEY_DECK_OWNERID, KEY_DECK_ACTIVE, KEY_DECK_MANUAL };
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };

		Cursor dc = ourDatabase.query(DATABASE_TABLE_ALLDECKS, deckColumns,
				null, null, null, null, null);
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
				null, null, null, null, null);

		int iName = dc.getColumnIndex(KEY_DECK_NAME);
		int iOwnerIdD = dc.getColumnIndex(KEY_DECK_OWNERID);
		int iOwnerIdP = pc.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);

		for (dc.moveToFirst(); !dc.isAfterLast(); dc.moveToNext()) {
			for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
				if (pc.getInt(iOwnerIdP) == dc.getInt(iOwnerIdD)) {
					Player owner = new Player(pc.getString(iOwnerName));
					if (d.equals(new Deck(dc.getString(iName).toString(),
							owner, true, 0))) {
						return true;
					}
					break;
				} else if (pc.isLast()) {
					System.out
							.println("You've missed at least one Deck from your decklist.");
				}
			}
		}

		return false;
	}

	public void updateDeck(String owner, String oldDeckName,
			String newDeckName, boolean newActive) {

		int deckId = getDeckId(oldDeckName, owner);

		ContentValues cv = new ContentValues();
		cv.put(KEY_DECK_NAME, newDeckName);
		if (newActive) {
			cv.put(KEY_DECK_ACTIVE, 1);
		} else {
			cv.put(KEY_DECK_ACTIVE, 0);
		}

		try {
			ourDatabase.update(DATABASE_TABLE_ALLDECKS, cv, KEY_DECK_ROWID
					+ " = " + deckId, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public Player getOwner(int ownerId) {
		String[] playerColumns = new String[] { KEY_PLAYER_ROWID,
				KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE };
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, playerColumns,
				KEY_PLAYER_ROWID + " = " + ownerId, null, null, null, null);

		int iOwnerName = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerActive = pc.getColumnIndex(KEY_PLAYER_ACTIVE);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			boolean ownerActive = (pc.getInt(iOwnerActive) == 1);
			return new Player(pc.getString(iOwnerName), ownerActive, ownerId);
		} else {
			System.out
					.println("No unique owner was found in MagicHatDB.getOwner.");
		}

		return new Player("Error");
	}

	public Player getPlayer(int playerId) {
		return getOwner(playerId);
	}

	public int getPlayerId(String name) {
		int playerId = 0;

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME };

		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, columns,
				KEY_PLAYER_NAME + " = '" + name + "'", null, null, null, null);

		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);

		if (pc.getCount() == 1) {
			pc.moveToFirst();
			playerId = pc.getInt(iOwnerId);
		} else {
			System.out
					.println("No unique player was found in MagicHatDB.getPlayerId.");
		}

		return playerId;
	}

	public List<Player> getActivePlayers() {
		// TODO: Separate out owners of decks with players of games
		List<Player> allActivePlayers = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		// Add p to the list of Players so long as the deck is active,
		// p isn't the Wizards Decks
		Cursor pc = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, columns,
				KEY_PLAYER_ACTIVE + " = 1 AND " + KEY_PLAYER_NAME
						+ " != 'Wizards of the Coast'", null, null, null, null);

		int iOwner = pc.getColumnIndex(KEY_PLAYER_NAME);
		int iOwnerId = pc.getColumnIndex(KEY_PLAYER_ROWID);

		for (pc.moveToFirst(); !pc.isAfterLast(); pc.moveToNext()) {
			int ownerId = pc.getInt(iOwnerId);
			String sOwner = pc.getString(iOwner);
			Player owner = new Player(sOwner);

			// Add p to the list of Players so long as p isn't already in the
			// list of Players
			if (!allActivePlayers.contains(owner)) {
				allActivePlayers.add(new Player(sOwner, true, ownerId));
			}
		}

		Collections.sort(allActivePlayers);

		return allActivePlayers;
	}

	// TODO Duplicate code with getAllOwners()
	public List<Player> getAllPlayers() {
		// TODO: Separate out owners of decks with players of games
		List<Player> allPlayers = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		// Add p to the list of Players so long as
		// p isn't the Wizards Decks
		Cursor c = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, columns,
				KEY_PLAYER_NAME + " != 'Wizards of the Coast'", null, null,
				null, null);

		int iOwnerId = c.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = c.getColumnIndex(KEY_PLAYER_NAME);
		int iActive = c.getColumnIndex(KEY_PLAYER_ACTIVE);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int ownerId = c.getInt(iOwnerId);
			boolean active = (c.getInt(iActive) == 1);
			String sOwner = c.getString(iOwner);
			Player owner = new Player(sOwner);

			// Add p to the list of Players so long as
			// p isn't already in the list of Players, and
			if (!allPlayers.contains(owner)) {
				allPlayers.add(new Player(sOwner, active, ownerId));
			}
		}

		Collections.sort(allPlayers);

		return allPlayers;
	}

	// TODO Duplicate code with getAllPlayers()
	public List<Player> getAllOwners() {
		// TODO: Separate out owners of decks with players of games
		List<Player> allOwners = new ArrayList<Player>();

		String[] columns = new String[] { KEY_PLAYER_ROWID, KEY_PLAYER_NAME,
				KEY_PLAYER_ACTIVE };

		Cursor c = ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, columns, null,
				null, null, null, null);

		int iOwnerId = c.getColumnIndex(KEY_PLAYER_ROWID);
		int iOwner = c.getColumnIndex(KEY_PLAYER_NAME);
		int iActive = c.getColumnIndex(KEY_PLAYER_ACTIVE);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int ownerId = c.getInt(iOwnerId);
			boolean active = (c.getInt(iActive) == 1);
			String sOwner = c.getString(iOwner);
			Player owner = new Player(sOwner);

			// Add p to the list of Players so long as p isn't already in the
			// list of Players
			if (!allOwners.contains(owner)) {
				allOwners.add(new Player(sOwner, active, ownerId));
			}
		}

		Collections.sort(allOwners);

		return allOwners;
	}

	public Cursor getCursorForAllOwners() {
		return ourDatabase.query(DATABASE_TABLE_ALLPLAYERS, new String[] {
				KEY_PLAYER_ROWID, KEY_PLAYER_NAME, KEY_PLAYER_ACTIVE }, null,
				null, null, null, null);
	}

	public Player flipActiveStatus(Player pFake) {
		int pId = getPlayerId(pFake.toString());

		Player pReal = getOwner(pId);

		ContentValues cv = new ContentValues();
		if (pReal.isActive()) {
			cv.put(KEY_PLAYER_ACTIVE, 0);
		} else {
			cv.put(KEY_PLAYER_ACTIVE, 1);
		}
		try {
			ourDatabase.update(DATABASE_TABLE_ALLPLAYERS, cv, KEY_PLAYER_ROWID
					+ " = " + pId, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		pReal = getOwner(pId);

		return pReal;
	}

	public Deck flipActiveStatus(String deckName, String ownerName) {
		int deckId = getDeckId(deckName, ownerName);

		Deck dReal = getDeck(deckId);

		ContentValues cv = new ContentValues();
		if (dReal.isActive()) {
			cv.put(KEY_DECK_ACTIVE, 0);
		} else {
			cv.put(KEY_DECK_ACTIVE, 1);
		}

		try {
			ourDatabase.update(DATABASE_TABLE_ALLDECKS, cv, KEY_DECK_ROWID
					+ " = " + deckId, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		return getDeck(deckId);
	}

	/*
	 * public String[] parseDeckName(String deckName) { Pattern pDeckName =
	 * Pattern.compile("(\\s)"); String[] sDeckNameWords =
	 * pDeckName.split(deckName); String sOwner, owner, name = ""; int
	 * iDeckNameWordsLen, index = 1;
	 * 
	 * // This goes from 1 -> length-1 because 0 is the Owner's Name, then item
	 * // 'length' is the word "Deck" // If the Deck is inactive, then it will
	 * end with an ")" and we should // loop through 1 -> length-2 if
	 * (sDeckNameWords[sDeckNameWords.length - 1].endsWith(")")) {
	 * iDeckNameWordsLen = sDeckNameWords.length - 2; } else { iDeckNameWordsLen
	 * = sDeckNameWords.length - 1; }
	 * 
	 * if (sDeckNameWords[0].equals("Wizards")) { // Stores the words
	 * "Wizards of the Coast" from sDeckName sOwner = sDeckNameWords[0]
	 * .concat(" ") .concat(sDeckNameWords[1]) .concat(" ")
	 * .concat(sDeckNameWords[2].concat(" ").concat( sDeckNameWords[3])); //
	 * Trim out the "'s" piece of the owner owner = sOwner.substring(0,
	 * sOwner.indexOf("'"));
	 * 
	 * index = 4; } else { // This case is for all single named Owners // Above
	 * splits the String into words stored in sDeckName sOwner =
	 * sDeckNameWords[0]; // Trim out the "'s" piece of the owner owner =
	 * sOwner.substring(0, sOwner.indexOf("'"));
	 * 
	 * index = 1; }
	 * 
	 * for (int i = index; i < iDeckNameWordsLen; i++) { if (i > index) { name =
	 * name.concat(" "); } name = name.concat(sDeckNameWords[i]); }
	 * 
	 * String[] sDeckName = { name, owner }; return sDeckName; }
	 */

	public void addGameResult(List<Player> Players, List<Deck> gameDecks,
			Player pWinner) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_GAME_PLAYER1, Players.get(0).getId());
		cv.put(KEY_GAME_PLAYER2, Players.get(1).getId());
		cv.put(KEY_GAME_DECK1, gameDecks.get(0).getId());
		cv.put(KEY_GAME_DECK2, gameDecks.get(1).getId());
		cv.put(KEY_GAME_WINNER, pWinner.getId());
		cv.put(KEY_GAME_DATE,
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		try {
			ourDatabase.insert(DATABASE_TABLE_ALLGAMES, null, cv);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public List<Game> getAllGames() {
		List<Game> allGames = new ArrayList<Game>();

		String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER };

		Cursor c = ourDatabase.query(DATABASE_TABLE_ALLGAMES, columns, null,
				null, null, null, null);

		int iGameId = c.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = c.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = c.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = c.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = c.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = c.getColumnIndex(KEY_GAME_WINNER);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int gameId = c.getInt(iGameId);
			int player1 = c.getInt(iPlayer1);
			int player2 = c.getInt(iPlayer2);
			int deck1 = c.getInt(iDeck1);
			int deck2 = c.getInt(iDeck2);
			int winner = c.getInt(iGameWinner);

			Player p1 = getOwner(player1);
			Player p2 = getOwner(player2);
			Deck d1 = getDeck(deck1);
			Deck d2 = getDeck(deck2);
			Player pW = getOwner(winner);

			allGames.add(new Game(gameId, p1, p2, d1, d2, pW));
		}

		return allGames;
	}

	public List<Game> getGames(Player p) {
		List<Game> games = new ArrayList<Game>();

		String[] columns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER };

		Cursor c = ourDatabase.query(DATABASE_TABLE_ALLGAMES, columns,
				KEY_GAME_PLAYER1 + " = " + p.getId() + " OR "
						+ KEY_GAME_PLAYER2 + " = " + p.getId(), null, null,
				null, null);

		int iGameId = c.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = c.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = c.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = c.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = c.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = c.getColumnIndex(KEY_GAME_WINNER);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int gameId = c.getInt(iGameId);
			int player1 = c.getInt(iPlayer1);
			int player2 = c.getInt(iPlayer2);
			int deck1 = c.getInt(iDeck1);
			int deck2 = c.getInt(iDeck2);
			int winner = c.getInt(iGameWinner);

			Player p1 = getPlayer(player1);
			Player p2 = getPlayer(player2);
			Deck d1 = getDeck(deck1);
			Deck d2 = getDeck(deck2);
			Player pW = getPlayer(winner);

			games.add(new Game(gameId, p1, p2, d1, d2, pW));
		}

		return games;
	}

	public List<Game> getGames(Deck d) {
		List<Game> games = new ArrayList<Game>();

		String[] gameColumns = new String[] { KEY_GAME_ROWID, KEY_GAME_PLAYER1,
				KEY_GAME_PLAYER2, KEY_GAME_DECK1, KEY_GAME_DECK2,
				KEY_GAME_WINNER };

		Cursor gc = ourDatabase.query(DATABASE_TABLE_ALLGAMES, gameColumns,
				KEY_GAME_DECK1 + " = " + d.getId() + " OR " + KEY_GAME_DECK2
						+ " = " + d.getId(), null, null, null, null);

		int iGameId = gc.getColumnIndex(KEY_GAME_ROWID);
		int iPlayer1 = gc.getColumnIndex(KEY_GAME_PLAYER1);
		int iPlayer2 = gc.getColumnIndex(KEY_GAME_PLAYER2);
		int iDeck1 = gc.getColumnIndex(KEY_GAME_DECK1);
		int iDeck2 = gc.getColumnIndex(KEY_GAME_DECK2);
		int iGameWinner = gc.getColumnIndex(KEY_GAME_WINNER);

		for (gc.moveToFirst(); !gc.isAfterLast(); gc.moveToNext()) {
			int gameId = gc.getInt(iGameId);
			int player1 = gc.getInt(iPlayer1);
			int player2 = gc.getInt(iPlayer2);
			int deck1 = gc.getInt(iDeck1);
			int deck2 = gc.getInt(iDeck2);
			int winner = gc.getInt(iGameWinner);

			Player p1 = getPlayer(player1);
			Player p2 = getPlayer(player2);
			Deck d1 = getDeck(deck1);
			Deck d2 = getDeck(deck2);
			Player pW = getPlayer(winner);

			games.add(new Game(gameId, p1, p2, d1, d2, pW));
		}
		return games;
	}

	public int getCardSetId(String shortName) {
		int cardSetId = 0;

		String[] columns = new String[] { KEY_CARDSET_ROWID, KEY_CARDSET_NAME,
				KEY_CARDSET_SHORTNAME };

		Cursor csc = ourDatabase.query(DATABASE_TABLE_ALLCARDSETS, columns,
				KEY_CARDSET_SHORTNAME + " = '" + shortName + "'", null, null,
				null, null);

		int iCardSetId = csc.getColumnIndex(KEY_CARDSET_ROWID);

		if (csc.getCount() == 1) {
			csc.moveToFirst();
			cardSetId = csc.getInt(iCardSetId);
		} else {
			System.out
					.println("No unique card set was found in MagicHatDB.getCardSetId.");
		}

		return cardSetId;
	}
}
