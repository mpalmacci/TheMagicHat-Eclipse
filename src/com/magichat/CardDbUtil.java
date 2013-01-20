package com.magichat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

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

	public static final String DB_TABLE_ALLEXPANSIONS = "Expansions";
	public static final String DB_TABLE_ALLCARDS = "Cards";
	public static final String DB_TABLE_REL_CARD_EXP = "Rel_CardExp";

	private static final String DB_PATH = "/data/data/com.magichat/databases/";
	private static final String DB_NAME = "cards.db";
	private static final int DB_VERSION = 1;

	private static MagicHatDbHelper mhHelper;
	private static SQLiteDatabase cDb;

	public static void initCardDb(Context context) throws IOException {
		boolean createDb = false;

		File dbDir = new File(DB_PATH);
		File dbFile = new File(DB_PATH + DB_NAME);
		if (!dbDir.exists()) {
			dbDir.mkdir();
			createDb = true;
		} else if (!dbFile.exists()) {
			createDb = true;
		} else {
			// Check that we have the latest version of the db
			boolean doUpgrade = false;

			// Insert your own logic here on whether to upgrade the db; I
			// personally just store the db version # in a text file, but you
			// can do whatever you want. I've tried MD5 hashing the db before,
			// but that takes a while.

			// If we are doing an upgrade, basically we just delete the db then
			// flip the switch to create a new one
			if (doUpgrade) {
				dbFile.delete();
				createDb = true;
			}
		}

		if (createDb) {
			System.out.println("CardDb is going to be copied.");
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

			System.out.println("CardDb was copied into place.");
		}
	}

	public static SQLiteDatabase getStaticDb() {
		return cDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	public static void close() {
		cDb.close();
	}

	/*
	 * public MagicHatDB openCardDB() { mhHelper = new MagicHatDbHelper(context,
	 * DB_NAME, DB_VERSION); try { mhDb = mhHelper.getReadableDatabase(); }
	 * catch (SQLiteException e) { e.printStackTrace(); }
	 * 
	 * return this; }
	 */

	// ///////////////////////// EXPANSION ////////////////////////////

	public static int getExpansionId(String shortName) {
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
			System.out
					.println("No unique expansion was found in MagicHatDB.getExpansionId.");
		}
		expC.close();

		return cardSetId;
	}

	public static List<Expansion> getAllExpansions() {
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

	// ////////////////////////////// CARDS ////////////////////////////////////

	public static SparseArray<String> getAllCardNames() {
		// return mhHelper.getAllCardIds(cDb);
		SparseArray<String> allCards = new SparseArray<String>();
		String[] cardColumns = new String[] { KEY_CARD_ROWID, KEY_CARD_NAME };

		Cursor cc = cDb.query(DB_TABLE_ALLCARDS, cardColumns, null, null, null,
				null, null);

		int iCardId = cc.getColumnIndex(KEY_CARD_ROWID);
		int iCardName = cc.getColumnIndex(KEY_CARD_NAME);

		for (cc.moveToFirst(); !cc.isAfterLast(); cc.moveToNext()) {
			allCards.put(cc.getInt(iCardId), cc.getString(iCardName));
		}
		cc.close();

		return allCards;
	}
}
