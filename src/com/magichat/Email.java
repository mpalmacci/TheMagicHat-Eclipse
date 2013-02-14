package com.magichat;

import com.magichat.decks.Deck;

import android.content.Context;
import android.content.Intent;

public class Email {
	Context cont;
	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	final String emailAddress[] = { "mpalmacci@gmail.com" };
	String message;

	public Email(Context cont) {
		this.cont = cont;
		// Common email features are listed here
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddress);
		emailIntent.setType("plain/text");
	}

	public void addDeck(Deck d) {
		// TODO search the deck_list.xml for the specific number to give this
		// deck
		message = "<Deck\nname=\"" + d.getName() + "\"\nactive=\""
				+ d.isActive() + "\"\nnumber=\"000\"\nowner=\""
				+ d.getOwner().toString() + "\" />";

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Add Deck");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		cont.startActivity(emailIntent);
	}

	public void updateDeck(Deck d) {
		message = "";

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Update Deck");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		cont.startActivity(emailIntent);
	}

}
