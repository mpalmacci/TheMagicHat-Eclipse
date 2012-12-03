package com.magichat;

public class Expansion implements Comparable<Expansion> {
	int id;
	String shortName;
	String name;
	
	public Expansion(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}
	
	public Expansion(int id, String name, String shortName) {
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
	public int compareTo(Expansion exp) {
		return name.compareTo(exp.getName());
	}
}
