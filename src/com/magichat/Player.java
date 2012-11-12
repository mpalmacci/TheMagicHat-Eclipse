package com.magichat;

public class Player implements Comparable<Player> {
	String name;
	int id;
	boolean active;
	
	public Player() {
	}
	
	public Player(String name) {
		this.name = name;
	}

	public Player(int id, String name, boolean active) {
		this(name);
		this.active = active;
		this.id = id;
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

	@Override
	public String toString() {
		// TODO Return "playerName (inactive)" from here?
		return getName();
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
