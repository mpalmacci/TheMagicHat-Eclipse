package com.magichat;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class CardSearch extends Activity implements OnDrawerOpenListener,
		OnDrawerCloseListener {

	LinearLayout llSearchResults;
	SlidingDrawer sdCardSearch;
	Button bSearch;
	EditText etRulesText, etCMC;
	AutoCompleteTextView etName, etSubtype;
	Spinner sExpansion, sBlock, sType, sCMCEquality;
	ToggleButton tbWhite, tbBlue, tbBlack, tbRed, tbGreen, tbMythic, tbRare,
			tbUncommon, tbCommon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_search);

		initialize();

		sdCardSearch.open();

		new populateExpansions().execute();
		// TODO Find a way to do better multi-threading here
		// new populateSubTypes().execute();
		// new populateAutoFillCardNames().execute();
	}

	private class populateExpansions extends
			AsyncTask<String, Integer, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			CardDbUtil.getStaticDb();
			List<Expansion> allExpansions = CardDbUtil.getAllExpansions();
			CardDbUtil.close();

			if (allExpansions.isEmpty()) {
				System.out.println("allExpansions is empty!");
				finish();
			}

			String[] stAllExpansions = new String[allExpansions.size() + 1];

			stAllExpansions[0] = "Any";

			for (int i = 1; i < allExpansions.size() + 1; i++) {
				stAllExpansions[i] = allExpansions.get(i - 1).toString();
			}

			return stAllExpansions;
		}

		@Override
		protected void onPostExecute(String[] stAllExpansions) {
			super.onPostExecute(stAllExpansions);
			new populateSubTypes().execute();

			ArrayAdapter<String> expAdapter = new ArrayAdapter<String>(
					CardSearch.this, android.R.layout.simple_spinner_item,
					stAllExpansions);

			sExpansion.setAdapter(expAdapter);
		}
	}

	private class populateSubTypes extends AsyncTask<String, Integer, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			CardDbUtil.getStaticDb();
			String[] stAllSubTypes = CardDbUtil.getAllCardSubTypes();
			CardDbUtil.close();

			return stAllSubTypes;
		}

		@Override
		protected void onPostExecute(String[] stAllSubTypes) {
			super.onPostExecute(stAllSubTypes);
			new populateAutoFillCardNames().execute();

			ArrayAdapter<String> cardSubTypesAdapter = new ArrayAdapter<String>(
					CardSearch.this,
					android.R.layout.simple_dropdown_item_1line, stAllSubTypes);
			etSubtype.setAdapter(cardSubTypesAdapter);
		}
	}

	private class populateAutoFillCardNames extends
			AsyncTask<String, Integer, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			CardDbUtil.getStaticDb();
			String[] stAllCardNames = CardDbUtil.getAllCardNames();
			CardDbUtil.close();

			return stAllCardNames;
		}

		@Override
		protected void onPostExecute(String[] stAllCardNames) {
			super.onPostExecute(stAllCardNames);

			ArrayAdapter<String> cardNameAdapter = new ArrayAdapter<String>(
					CardSearch.this,
					android.R.layout.simple_dropdown_item_1line, stAllCardNames);
			etName.setAdapter(cardNameAdapter);
		}
	}

	@Override
	public void onDrawerOpened() {
		llSearchResults.setVisibility(LinearLayout.GONE);
		bSearch.setText("Perform Search");

	}

	@Override
	public void onDrawerClosed() {
		llSearchResults.setVisibility(LinearLayout.VISIBLE);
		bSearch.setText("Perform Another Search");

		// The next two lines of code hide the keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(bSearch.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	private void initialize() {
		llSearchResults = (LinearLayout) findViewById(R.id.llSearchResults);
		sdCardSearch = (SlidingDrawer) findViewById(R.id.sdCardSearch);
		bSearch = (Button) findViewById(R.id.bSearch);
		etName = (AutoCompleteTextView) findViewById(R.id.etName);
		etRulesText = (EditText) findViewById(R.id.etRulesText);
		etCMC = (EditText) findViewById(R.id.etCMC);
		sCMCEquality = (Spinner) findViewById(R.id.sCMCEquality);
		sExpansion = (Spinner) findViewById(R.id.sExpansion);
		sBlock = (Spinner) findViewById(R.id.sBlock);
		sType = (Spinner) findViewById(R.id.sType);
		etSubtype = (AutoCompleteTextView) findViewById(R.id.etSubtype);
		tbMythic = (ToggleButton) findViewById(R.id.tbMythic);
		tbRare = (ToggleButton) findViewById(R.id.tbRare);
		tbUncommon = (ToggleButton) findViewById(R.id.tbUncommon);
		tbCommon = (ToggleButton) findViewById(R.id.tbCommon);
		tbWhite = (ToggleButton) findViewById(R.id.tbWhite);
		tbBlue = (ToggleButton) findViewById(R.id.tbBlue);
		tbBlack = (ToggleButton) findViewById(R.id.tbBlack);
		tbRed = (ToggleButton) findViewById(R.id.tbRed);
		tbGreen = (ToggleButton) findViewById(R.id.tbGreen);

		sdCardSearch.setOnDrawerOpenListener(this);
		sdCardSearch.setOnDrawerCloseListener(this);
	}
}