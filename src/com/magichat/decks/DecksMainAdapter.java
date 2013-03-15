package com.magichat.decks;

import java.util.List;

import com.magichat.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DecksMainAdapter extends BaseAdapter {
	private Activity activity;
	private List<Deck> deckList;
	private static LayoutInflater inflater = null;

	public DecksMainAdapter(Activity a, List<Deck> d) {
		activity = a;
		deckList = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return deckList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.decks_main_list_row, null);

		TextView deckName = (TextView) vi.findViewById(R.id.tvDeckName);
		TextView ownerName = (TextView) vi.findViewById(R.id.tvOwnerName);
		ImageView dropdown = (ImageView) vi.findViewById(R.id.ivDropdown);

		Deck d = new Deck();
		d = deckList.get(position);

		// Setting all values in ListView
		deckName.setText(d.getName());
		ownerName.setText("  " + d.getOwner().getName());
		
		dropdown.setOnClickListener((OnClickListener) activity);
		
		return vi;
	}
}
