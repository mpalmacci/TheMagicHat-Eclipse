package com.magichat.players;

import java.util.List;

import com.magichat.decks.Deck;

public class Player implements Comparable<Player> {

	private int id;
	private String name;
	private int dci;
	private boolean active;
	private boolean self;
	private List<Deck> deckList;

	public Player() {
		this.id = 0;
	}

	public Player(String name) {
		this();
		this.name = name;
	}

	public Player(String name, boolean active) {
		this(name);
		this.active = active;
	}

	public Player(int id, String name, boolean active) {
		this(name, active);
		this.id = id;
	}
	
	public Player(int id, String name, boolean active, boolean self) {
		this(name, active);
		this.id = id;
		this.self = self;
	}

	public Player(String name, int dci, boolean active, boolean self) {
		this.name = name;
		this.dci = dci;
		this.active = active;
		this.self = self;
	}

	public Player(int id, String name, int dci, boolean active, boolean self) {
		this(name, dci, active, self);
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDci(int dci) {
		this.dci = dci;
	}

	public int getDci() {
		return this.dci;
	}

	public void setSelf(boolean self) {
		this.self = self;
	}

	public boolean isSelf() {
		return this.self;
	}

	public void setDeckList(List<Deck> deckList) {
		this.deckList = deckList;
	}

	public List<Deck> getDeckList() {
		return this.deckList;
	}

	@Override
	public String toString() {
		if (!this.active) {
			return this.getName() + " (inactive)";
		}
		return this.getName();
	}

	@Override
	public boolean equals(Object o) {
		Player p = (Player) o;

		return this.getName().equalsIgnoreCase(p.getName());
	}

	@Override
	public int compareTo(Player another) {
		return name.compareTo(another.getName());
	}
}
