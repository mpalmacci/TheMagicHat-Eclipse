package com.magichat.decks;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DeckViewAdapter extends ArrayAdapter<DeckViewItem> {
	private LayoutInflater mInflater;
	
	public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }
	
	private List<DeckViewItem> deckViewItems;

	public DeckViewAdapter (Context context, List<DeckViewItem> dvi) {
		super(context, 0, dvi);
		this.deckViewItems = dvi;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return deckViewItems.get(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return deckViewItems.get(position).getView(mInflater, convertView);
    }
}
