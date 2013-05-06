package com.magichat.prefs;

import com.magichat.R;
import com.magichat.decks.db.MagicHatDb;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

public class RestorePreference extends DialogPreference {
	
	Context context;
	
	public RestorePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		setDialogLayoutResource(R.layout.main_view);
		setDialogIcon(null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "Yes", persist the new value
		if (positiveResult) {
			new restoreDb().execute();
		}
	}

	private class restoreDb extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			MagicHatDb mhDb = new MagicHatDb(context);
			mhDb.openWritableDB();
			mhDb.restoreDb();
			mhDb.closeDB();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Toast.makeText(context, "Database has been restored",
					Toast.LENGTH_SHORT).show();
		}
	}
}
