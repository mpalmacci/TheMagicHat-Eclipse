package com.magichat.decks;

import com.magichat.R;
import com.magichat.decks.DeckViewAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DeckViewCard implements DeckViewItem {
	private final String cardName;
	private final String cardQuantity;
	private final LayoutInflater inflater;

	public DeckViewCard(LayoutInflater inflater, String cardName, String cardQuantity) {
		this.cardName = cardName;
		this.cardQuantity = cardQuantity;
		this.inflater = inflater;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	private static class ViewHolder {
		TextView cardQuantity, cardName;
		ImageView manaCost;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		ViewHolder holder = new ViewHolder();

		if (convertView == null) {
			convertView = (View) inflater
					.inflate(R.layout.deck_view_card, null);

			holder.cardQuantity = (TextView) convertView
					.findViewById(R.id.tvCardQuantity);
			holder.cardName = (TextView) convertView
					.findViewById(R.id.tvCardName);
			holder.manaCost = (ImageView) convertView
					.findViewById(R.id.ivManaCost);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.cardQuantity.setText(cardQuantity);
		holder.cardName.setText(cardName);

		return convertView;
	}
}
