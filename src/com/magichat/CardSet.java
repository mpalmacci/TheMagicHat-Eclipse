package com.magichat;

public class CardSet implements Comparable<CardSet> {
	int id;
	String shortName;
	String name;
	
	public CardSet(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}
	
	public CardSet(String name, String shortName, int id) {
		this(name, shortName);
		this.id = id;
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
	
	public String getShortName() {
		return this.shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(CardSet cs) {
		return name.compareTo(cs.getName());
	}
}
