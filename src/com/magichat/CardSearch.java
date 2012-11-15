package com.magichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CardSearch extends Activity implements View.OnClickListener {
	List<Card> allCards = new ArrayList<Card>();
	List<CardSet> allCardSets = new ArrayList<CardSet>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initialize();

		MagicHatDB getAllInfoDB = new MagicHatDB(this);
		getAllInfoDB.openReadableDB();
		allCards = getAllInfoDB.getAllCards();
		allCardSets = getAllInfoDB.getAllCardSets();
		getAllInfoDB.closeDB();
	}

	@Override
	public void onClick(View arg0) {

	}

	private void initialize() {

	}
}
