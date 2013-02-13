package com.magichat;

import java.util.List;

public class Player implements Comparable<Player> {
	String name;
	int id;
	boolean active;
	List<Deck> deckList;
	
	public Player() {
	}
	
	public Player(String name) {
		this.name = name;
	}

	public Player(int id, String name, boolean active) {
		this.id = id;
		this.name = name;
		this.active = active;
	}

	public String getName() {
		return name;
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
