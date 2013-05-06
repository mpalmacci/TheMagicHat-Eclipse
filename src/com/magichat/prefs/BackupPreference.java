package com.magichat.prefs;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

public class BackupPreference extends DialogPreference {
	
	Context context;
	
	public BackupPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		setDialogLayoutResource(R.layout.main_view);
		setDialogIcon(null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "Yes", persist the new value
		if (positiveResult) {
			new backupDb().execute();
		}
	}
	
	private class backupDb extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(context);
			mhDb.openWritableDB();
			mhDb.backupDb();
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Toast.makeText(context, "Database is backed up",
					Toast.LENGTH_SHORT).show();
		}
	}
}
