package com.magichat.cards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CardDbUtil {
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

	public static final String KEY_SUB_TYPE_ROWID = "_id";
	public static final String KEY_SUB_TYPE_NAME = "SubType_Name";

	public static final String DB_TABLE_ALLEXPANSIONS = "Expansions";
	public static final String DB_TABLE_ALLCARDS = "Cards";
	public static final String DB_TABLE_REL_CARD_EXP = "Rel_CardExp";
	public static final String DB_TABLE_SUB_TYPES = "SubTypes";

	protected static final String DB_PATH = "/data/data/com.magichat/databases/";
	protected static final String DB_NAME = "cards.db";
	private static final String FILE_DB_VERSION = "cardsDbVersion.txt";
	private static final int DB_VERSION = 1;

	public static boolean createDb;
	public static boolean isUpgrade;

	// private static MagicHatDbHelper mhHelper;
	private static SQLiteDatabase cDb;

	public static void initCardDb(Context context) throws IOException {
		createDb = false;

		File dbPath = new File(DB_PATH);
		File dbFile = new File(DB_PATH + DB_NAME);
		File fDbVersion = new File(DB_PATH + FILE_DB_VERSION);

		if (!dbPath.exists()) {
			dbPath.mkdir();
			createDb = true;
		} else if (!dbFile.exists()) {
			createDb = true;
		} else {
			// Insert your own logic here on whether to upgrade the db; I
			// personally just store the db version # in a text file, but you
			// can do whatever you want. I've tried MD5 hashing the db before,
			// but that takes a while.
			if (fDbVersion.exists()) {
				int dbVersion = 0;
				if (fDbVersion.canRead()) {
					try {
						// FileInputStream fin = new
						// FileInputStream(fDbVersion);
						// BufferedReader brVersion = new BufferedReader(new
						// InputStreamReader(fin));
						FileReader frVersion = new FileReader(fDbVersion);
						BufferedReader brVersion = new BufferedReader(frVersion);
						String sDbVersion = brVersion.readLine();
						dbVersion = Integer.parseInt(sDbVersion);
						brVersion.close();
					} catch (IOException exc) {
						exc.printStackTrace();
					}
				}

				isUpgrade = dbVersion != DB_VERSION ? true : false;
			} else {
				try {
					fDbVersion.createNewFile();
					if (fDbVersion.canWrite()) {
						FileWriter fwVersion = new FileWriter(fDbVersion);
						BufferedWriter bwVersion = new BufferedWriter(fwVersion);
						bwVersion.write(String.valueOf(DB_VERSION));
						bwVersion.close();
					}
				} catch (IOException exc) {
					exc.printStackTrace();
				}
			}

			// If we are doing an upgrade, basically we just delete the db then
			// flip the switch to create a new one
			if (isUpgrade) {
				dbFile.delete();
				createDb = true;
			}
		}

		if (createDb) {
			Log.i("CardDbUtil.initCardDb",
					"CardDb is going to be copied into place");
			// Open your local db as the input stream
			InputStream myInput = context.getAssets().open(DB_NAME);

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(dbFile);

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

			try {
				fDbVersion.createNewFile();
				if (fDbVersion.canWrite()) {
					// This will always overwrite the data that was already
					// stored in the dbVersion file
					FileWriter fwVersion = new FileWriter(fDbVersion, false);
					BufferedWriter bwVersion = new BufferedWriter(fwVersion);
					bwVersion.write(String.valueOf(DB_VERSION));
					bwVersion.close();
				}
			} catch (IOException exc) {
				exc.printStackTrace();
			}

			Log.i("CardDbUtil.initCardDb", "CardDb was copied into place");
		}
	}

	public static SQLiteDatabase getStaticDb() {
		return cDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	public static void close() {
		cDb.close();
	}

	// ///////////////////////// EXPANSION ////////////////////////////

	protected static int getExpansionId(String shortName) {
		// return mhHelper.getExpansionId(shortName, cDb);
		int cardSetId = 0;

		String[] columns = new String[] { KEY_EXPANSION_ROWID,
				KEY_EXPANSION_NAME, KEY_EXPANSION_SHORTNAME };

		Cursor expC = cDb.query(DB_TABLE_ALLEXPANSIONS, columns,
				KEY_EXPANSION_SHORTNAME + " = '" + shortName + "'", null, null,
				null, null);

		int iExpansionId = expC.getColumnIndex(KEY_EXPANSION_ROWID);

		if (expC.getCount() == 1) {
			expC.moveToFirst();
			cardSetId = expC.getInt(iExpansionId);
		} else {
			Log.d("MagicHatDB.getExpansionId(shortName)",
					"No unique expansion was for short Name: " + shortName);
		}
		expC.close();

		return cardSetId;
	}

	protected static List<Expansion> getAllExpansions() {
		// return mhHelper.getAllExpansions(cDb);
		List<Expansion> allExpansions = new ArrayList<Expansion>();
		String[] expansionColumns = new String[] { KEY_EXPANSION_ROWID,
				KEY_EXPANSION_NAME, KEY_EXPANSION_SHORTNAME };

		Cursor expC = cDb.query(DB_TABLE_ALLEXPANSIONS, expansionColumns, null,
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

	protected static Expansion getExpansion(int expId) {
		Expansion exp = null;
		String[] expColumns = new String[] { KEY_EXPANSION_ROWID,
				KEY_EXPANSION_NAME, KEY_EXPANSION_SHORTNAME };

		Cursor ec = cDb.query(DB_TABLE_ALLEXPANSIONS, expColumns,
				KEY_EXPANSION_ROWID + " = " + expId, null, null, null, null);

		if (ec.getCount() == 1) {
			ec.moveToFirst();
			int iExpName = ec.getColumnIndex(KEY_EXPANSION_NAME);
			int iExpShortName = ec.getColumnIndex(KEY_EXPANSION_SHORTNAME);

			String expName = ec.getString(iExpName);
			String expShortName = ec.getString(iExpShortName);

			exp = new Expansion(expId, expName, expShortName);
		} else {
			Log.d("CardDbUtil.getCardImages",
					"No unique expansion was found with Expansion Id: " + expId);
		}
		ec.close();
		return exp;
	}

	// ////////////////////////////// CARDS ////////////////////////////////////

	protected static Card getCard(String name) {
		Card c = new Card(0, null);
		String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_ISBLUE,
				KEY_CARD_ISBLACK, KEY_CARD_ISWHITE, KEY_CARD_ISGREEN,
				KEY_CARD_ISRED, KEY_CARD_MANACOST, KEY_CARD_CMC, KEY_CARD_TYPE,
				KEY_CARD_SUBTYPES, KEY_CARD_POWER, KEY_CARD_TOUGHNESS,
				KEY_CARD_RARITY, KEY_CARD_TEXT };

		Cursor cc = cDb.query(DB_TABLE_ALLCARDS, cardColumns, KEY_CARD_NAME
				+ " = '" + name + "'", null, null, null, null);

		int iCardId = cc.getColumnIndex(KEY_CARD_ROWID);
		int iCardIsBlue = cc.getColumnIndex(KEY_CARD_ISBLUE);
		int iCardIsBlack = cc.getColumnIndex(KEY_CARD_ISBLACK);
		int iCardIsWhite = cc.getColumnIndex(KEY_CARD_ISWHITE);
		int iCardIsGreen = cc.getColumnIndex(KEY_CARD_ISGREEN);
		int iCardIsRed = cc.getColumnIndex(KEY_CARD_ISRED);
		int iCardManaCost = cc.getColumnIndex(KEY_CARD_MANACOST);
		int iCardType = cc.getColumnIndex(KEY_CARD_TYPE);
		int iCardSubTypes = cc.getColumnIndex(KEY_CARD_SUBTYPES);
		int iCardPower = cc.getColumnIndex(KEY_CARD_POWER);
		int iCardToughness = cc.getColumnIndex(KEY_CARD_TOUGHNESS);
		// int iCardRarity = cc.getColumnIndex(KEY_CARD_RARITY);
		int iCardText = cc.getColumnIndex(KEY_CARD_TEXT);

		if (cc.getCount() == 1) {
			int cardId = cc.getInt(iCardId);
			Map<Expansion, URL> expansionImages = getExpansionImages(cardId);
			boolean isBlue = cc.getInt(iCardIsBlue) == 1 ? true : false;
			boolean isBlack = cc.getInt(iCardIsBlack) == 1 ? true : false;
			boolean isWhite = cc.getInt(iCardIsWhite) == 1 ? true : false;
			boolean isGreen = cc.getInt(iCardIsGreen) == 1 ? true : false;
			boolean isRed = cc.getInt(iCardIsRed) == 1 ? true : false;
			String manaCost = cc.getString(iCardManaCost);
			String type = cc.getString(iCardType);
			String subTypes = cc.getString(iCardSubTypes);
			String sPower = cc.getString(iCardPower);
			String sToughness = cc.getString(iCardToughness);
			String text = cc.getString(iCardText);

			c = new Card(cardId, name, expansionImages, isBlue, isBlack, isRed,
					isGreen, isWhite, manaCost, type, subTypes, sPower,
					sToughness, text);
		} else {
			Log.d("CardDbUtil.getCard(name)",
					"No unique Card found with name: " + name);
		}

		return c;
	}

	protected static List<Card> getAllCardNames() {
		// return mhHelper.getAllCardIds(cDb);
		// SparseArray<String> allCards = new SparseArray<String>();
		String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_NAME };
		List<Card> allCardNames = new ArrayList<Card>();

		Cursor cc = cDb.query(DB_TABLE_ALLCARDS, cardColumns, null, null, null,
				null, null);

		int iCardId = cc.getColumnIndex(KEY_CARD_ROWID);
		int iCardName = cc.getColumnIndex(KEY_CARD_NAME);

		Card c;
		for (cc.moveToFirst(); !cc.isAfterLast(); cc.moveToNext()) {
			c = new Card(cc.getInt(iCardId), cc.getString(iCardName));
			allCardNames.add(c);
		}
		cc.close();

		Collections.sort(allCardNames);

		return allCardNames;
	}

	protected static Map<Expansion, URL> getExpansionImages(int cardId) {
		Map<Expansion, URL> allExpansionImages = new HashMap<Expansion, URL>();

		String[] relColumns = new String[] { KEY_REL_EXP_ID, KEY_REL_PIC_URL };

		Cursor rc = cDb.query(DB_TABLE_REL_CARD_EXP, relColumns,
				KEY_REL_CARD_ID + " = " + cardId, null, null, null, null);

		int iRelExpId = rc.getColumnIndex(KEY_REL_EXP_ID);
		int iUrl = rc.getColumnIndex(KEY_REL_PIC_URL);

		Expansion exp = null;
		URL expUrl = null;

		for (rc.moveToFirst(); !rc.isAfterLast(); rc.moveToNext()) {
			int relExpId = rc.getInt(iRelExpId);

			exp = getExpansion(relExpId);

			if (exp == null) {
				return null;
			}

			try {
				expUrl = new URL(rc.getString(iUrl));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			allExpansionImages.put(exp, expUrl);
		}
		rc.close();
		return allExpansionImages;
	}

	protected static int getCardId(String name) {
		String[] cardColumns = new String[] { KEY_CARD_ROWID };
		int cardId = 0;

		name = name.replace("'", "''");

		Cursor cc = cDb.query(DB_TABLE_ALLCARDS, cardColumns, "lower("
				+ KEY_CARD_NAME + ") = lower('" + name + "')", null, null,
				null, null);

		if (cc.getCount() == 1) {
			cc.moveToFirst();
			int iCardId = cc.getColumnIndex(KEY_CARD_ROWID);
			cardId = cc.getInt(iCardId);
		} else {
			Log.d("CardDbUtil.getCardImages",
					"No unique card was found with card name: " + name);
		}

		cc.close();
		return cardId;
	}

	protected static List<Card> getAllCardIds() {
		List<Card> allCards = new ArrayList<Card>();
		String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_NAME };

		Cursor cc = cDb.query(DB_TABLE_ALLCARDS, cardColumns, null, null, null,
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

	protected static String[] getAllCardSubTypes() {
		String[] cardColumns = new String[] { KEY_SUB_TYPE_NAME };
		String[] allSubTypes;

		Cursor cc = cDb.query(DB_TABLE_SUB_TYPES, cardColumns, null, null,
				null, null, null);

		int iCardSubTypes = cc.getColumnIndex(KEY_SUB_TYPE_NAME);

		allSubTypes = new String[cc.getCount()];

		int i = 0;

		for (cc.moveToFirst(); !cc.isAfterLast(); cc.moveToNext()) {
			allSubTypes[i] = cc.getString(iCardSubTypes).trim();
			i++;
		}
		cc.close();

		return allSubTypes;
	}
}
