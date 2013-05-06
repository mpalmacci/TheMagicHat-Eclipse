package com.magichat.decks;

import android.view.LayoutInflater;
import android.view.View;

public interface DeckViewItem {
	public int getViewType();

	public View getView(LayoutInflater inflater, View convertView);
}
