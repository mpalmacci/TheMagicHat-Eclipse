package com.magichat.players;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.magichat.R;
import com.magichat.decks.Deck;

public class PlayerViewAdapter extends BaseAdapter {
	private Activity activity;
	private List<Deck> deckList;
	private Player currentPlayer;
	private SparseBooleanArray itemsChecked;
	private static LayoutInflater inflater = null;

	public PlayerViewAdapter(Activity act, List<Deck> deckList, Player p) {
		this.activity = act;
		this.deckList = deckList;
		this.currentPlayer = p;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setupItemsChecked();
	}

	private void setupItemsChecked() {
		itemsChecked = new SparseBooleanArray();
		for (int i = 0; i < deckList.size(); i++) {
			Deck d = deckList.get(i);

			itemsChecked.put(i, d.getOwner().equals(currentPlayer));
		}
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

	public SparseBooleanArray getItemsChecked() {
		return itemsChecked;
	}

	private static class ViewHolder {
		TextView tvDeckName;
		TextView tvOwnerName;
		CheckBox cbOwner;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.player_view_deck_list_row,
					null);
			holder = new ViewHolder();
			holder.tvDeckName = (TextView) convertView
					.findViewById(R.id.tvDeckName);
			holder.tvOwnerName = (TextView) convertView
					.findViewById(R.id.tvOwnerName);
			holder.cbOwner = (CheckBox) convertView.findViewById(R.id.cbOwner);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Deck d = deckList.get(position);

		// Setting all values in ListView
		holder.tvDeckName.setText(d.getName());
		holder.tvOwnerName.setText(d.getOwner().getName());

		boolean isChecked = d.getOwner().equals(currentPlayer);
		// Must setTag prior to putting the item into the itemsChecked bucket
		// otherwise the getTag hits a NPE
		holder.cbOwner.setTag(position);
		itemsChecked.put((Integer) holder.cbOwner.getTag(), isChecked);

		holder.cbOwner.setChecked(isChecked);

		holder.cbOwner
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton view,
							boolean isChecked) {
						if (isChecked) {
							itemsChecked.put((Integer) view.getTag(), true);
						} else {
							itemsChecked.put((Integer) view.getTag(), false);
						}
					}
				});

		return convertView;
	}
}
