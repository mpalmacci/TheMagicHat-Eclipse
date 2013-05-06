package com.magichat.decks;

import java.util.List;

import com.magichat.cards.Card;
import com.magichat.players.Player;

public class Deck implements Comparable<Deck> {

	private String name;
	private boolean active;
	private Player owner;
	private int id;
	private List<Card> cardList;

	public Deck() {
		this.name = new String();
	}

	/*
	 * public Deck(String name, Player owner) { this.name = name; this.owner =
	 * owner; }
	 */

	public Deck(String name, Player owner, boolean active) {
		this.id = 0;
		// this(name, owner);
		this.name = name;
		this.owner = owner;
		this.active = active;
	}

	public Deck(int id, String name, Player owner, boolean active) {
		this(name, owner, active);
		this.id = id;
	}

	public Deck(int id, String name, Player owner, boolean active,
			List<Card> cardList) {
		this(id, name, owner, active);
		this.cardList = cardList;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player getOwner() {
		return this.owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Card> getCardList() {
		return this.cardList;
	}

	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}

	@Override
	public boolean equals(Object o) {
		Deck d = (Deck) o;

		// So long as the deck ids are equal and the id isn't zero, then they
		// are equal
		if (this.getId() == d.getId() && this.getId() != 0) {
			return true;
		} else {
			return this.getName().equalsIgnoreCase(d.getName())
					&& this.getOwner().getName()
							.equalsIgnoreCase(d.getOwner().getName());
		}
	}

	public boolean equalsOwner(Deck d) {
		// This is case insensitive
		return this.getOwner().getName()
				.equalsIgnoreCase(d.getOwner().getName());
	}

	@Override
	public String toString() {
		if (getName().isEmpty()) {
			// This is here for the UpdateDeck
			return new String();
		}

		StringBuilder sb = new StringBuilder();

		if (this.owner.getName().isEmpty()) {
			sb.append("Unowned ");
		} else {
			sb.append(this.owner.getName());
			sb.append("'s ");
		}

		sb.append(this.name);
		sb.append(" Deck");

		// TODO Remove " (inactive)" from here?
		if (!this.isActive()) {
			sb.append(" (inactive)");
		}

		return sb.toString();
	}

	@Override
	public int compareTo(Deck d) {
		int ownerCmp = this.owner.compareTo(d.getOwner());
		return (ownerCmp != 0 ? ownerCmp : name.compareTo(d.getName()));
	}
}
