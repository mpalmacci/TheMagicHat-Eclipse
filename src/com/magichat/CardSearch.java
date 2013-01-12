package com.magichat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
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
	// TODO Use a HashMap of Names and Ids?
	SparseArray<String> allCardNames = new SparseArray<String>();
	List<Expansion> allExpansions = new ArrayList<Expansion>();

	LinearLayout llSearchResults;
	SlidingDrawer sdCardSearch;
	Button bSearch;
	EditText etRulesText, etCMC;
	AutoCompleteTextView etName;
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
		allCardNames = CardDbUtil.getAllCardNames();
		allExpansions = CardDbUtil.getAllExpansions();
		CardDbUtil.close();

		setupAutoFill();
		populateExpansionSpinner();
	}

	private void setupAutoFill() {
		String[] stAllCardNames = new String[allCardNames.size()];

		for (int i = 0; i < allCardNames.size() - 1; i++) {
			stAllCardNames[i] = allCardNames.get(i);
		}

		ArrayAdapter<String> cardNameAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, stAllCardNames);
		etName.setAdapter(cardNameAdapter);
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
		etName = (AutoCompleteTextView) findViewById(R.id.etName);
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