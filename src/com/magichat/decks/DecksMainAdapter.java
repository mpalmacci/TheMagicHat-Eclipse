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
		return deckList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	protected static class DeckMainViewHolder {
		TextView tvDeckName;
		TextView tvOwnerName;
		ImageView ivDropdown;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		DeckMainViewHolder holder = new DeckMainViewHolder();
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.decks_main_list_row, null);
			
			holder.tvDeckName = (TextView) convertView.findViewById(R.id.tvDeckName);
			holder.tvOwnerName = (TextView) convertView.findViewById(R.id.tvOwnerName);
			holder.ivDropdown = (ImageView) convertView.findViewById(R.id.ivDropdown);
			
			convertView.setTag(holder);
		} else {
			holder = (DeckMainViewHolder) convertView.getTag();
		}

		Deck d = deckList.get(position);

		// Setting all values in ListView
		holder.tvDeckName.setText(d.getName());
		holder.tvOwnerName.setText(d.getOwner().getName());
		
		holder.ivDropdown.setOnClickListener((OnClickListener) activity);
		
		return convertView;
	}
}
