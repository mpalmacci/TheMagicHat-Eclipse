package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
	// TODO Use a HashMap of Names and Ids?
	List<Card> allCardIds = new ArrayList<Card>();
	List<Expansion> allExpansions = new ArrayList<Expansion>();

	LinearLayout llSearchResults;
	SlidingDrawer sdCardSearch;
	Button bSearch;
	EditText etName, etRulesText, etCMC;
	Spinner sExpansion, sBlock, sType, sSubtype, sCMCEquality;
	ToggleButton tbWhite, tbBlue, tbBlack, tbRed, tbGreen, tbMythic, tbRare,
			tbUncommon, tbCommon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_search);

		initialize();

		sdCardSearch.open();

		CardDbUtil.getStaticDb();
		allCardIds = CardDbUtil.getAllCardIds();
		allExpansions = CardDbUtil.getAllExpansions();
		CardDbUtil.close();

		populateExpansionSpinner();
	}

	private void populateExpansionSpinner() {
		if (allExpansions.isEmpty()) {
			System.out.println("allExpansions is empty!");
			finish();
		}

		String[] stAllExpansions = new String[allExpansions.size() + 1];

		stAllExpansions[0] = "Any";

		for (int i = 1; i < allExpansions.size() + 1; i++) {
			stAllExpansions[i] = allExpansions.get(i - 1).toString();
		}

		ArrayAdapter<String> expAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, stAllExpansions);

		sExpansion.setAdapter(expAdapter);
	}

	@Override
	public void onDrawerOpened() {
		llSearchResults.setVisibility(LinearLayout.GONE);
		bSearch.setText("Perform Search");

		// TODO Show the Keyboard by default
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

	private void initialize() {
		llSearchResults = (LinearLayout) findViewById(R.id.llSearchResults);
		sdCardSearch = (SlidingDrawer) findViewById(R.id.sdCardSearch);
		bSearch = (Button) findViewById(R.id.bSearch);
		etName = (EditText) findViewById(R.id.etName);
		etRulesText = (EditText) findViewById(R.id.etRulesText);
		etCMC = (EditText) findViewById(R.id.etCMC);
		sCMCEquality = (Spinner) findViewById(R.id.sCMCEquality);
		sExpansion = (Spinner) findViewById(R.id.sExpansion);
		sBlock = (Spinner) findViewById(R.id.sBlock);
		sType = (Spinner) findViewById(R.id.sType);
		sSubtype = (Spinner) findViewById(R.id.sSubtype);
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