package com.magichat;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card implements Comparable<Card> {
	int id;
	String name;
	List<Expansion> expansions;
	Expansion defaultExpansion;
	URL defaultPicURL;
	List<String> colors;
	String manaCost;
	int CMC;
	String type;
	String[] subType;
	String sPower;
	String sToughness;
	String text;
	Map<Expansion, URL> expansionImages = new HashMap<Expansion, URL>();

	public Card(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Card(int id, String name, List<Expansion> expansions) {
		this(id, name);
		this.expansions = expansions;
		
		if (expansions.isEmpty()) {
			System.out.println(name + " is not in an expansion?");
		} else {
			this.defaultExpansion = expansions.get(0);
			this.defaultPicURL = expansionImages.get(expansions.get(0));
		}
	}

	public Card(String name, List<Expansion> expansions, List<URL> picURL,
			List<String> colors, String manaCost, String type, String pt,
			String text) {
		this.name = name;
		this.expansions = expansions;
		this.colors = colors;
		this.manaCost = manaCost;
		this.CMC = convertManaCost(manaCost);

		if (type.contains(" - ")) {
			this.type = type.substring(0, type.indexOf(" - "));
			this.subType = type.substring(type.indexOf(" - ") + 3,
					type.length()).split(" ");
		} else {
			this.type = type;
		}

		if (type.contains("Creature")) {
			String sPower = pt.substring(0, pt.indexOf("/"));
			String sToughness = pt.substring(pt.indexOf("/") + 1, pt.length());

			// TODO Need to handle */*, */#, #/*, and #+*/#+* Creatures...
			this.sPower = sPower;
			this.sToughness = sToughness;

		}

		for (int i = 0; i < expansions.size(); i++) {
			expansionImages.put(expansions.get(i), picURL.get(i));
		}

		if (expansions.isEmpty()) {
			System.out.println(name + " is not in an expansion?");
		} else {
			this.defaultExpansion = expansions.get(0);
			this.defaultPicURL = expansionImages.get(expansions.get(0));
		}

		this.text = text;
	}

	public Card(int id, String name, List<Expansion> expansions,
			List<URL> picURL, List<String> colors, String manaCost,
			String type, String pt, String text) {
		this(name, expansions, picURL, colors, manaCost, type, pt, text);
		this.id = id;
	}

	public int convertManaCost(String manaCost) {
		int CMC = 0;

		String[] manaCostArray = manaCost.split("");

		// Need to handle digits (including 0), or the colors
		// { 'U', 'R', 'B', 'G', 'W' }
		// Then also need to handle multiple colors
		// so '(color/color)' will only really be an additional 1 mana
		// 'X' is equal to 0 so can be ignored in this calculation
		for (String s : manaCostArray) {
			Pattern digit = Pattern.compile("/d");
			Pattern slash = Pattern.compile("\\)");
			Pattern color = Pattern.compile("[URBGW]");

			Matcher mDigit = digit.matcher(s);
			Matcher mSlash = slash.matcher(s);
			Matcher mColor = color.matcher(s);

			if (mDigit.matches()) {
				CMC = CMC + Integer.parseInt(s);
			}
			if (mSlash.matches()) {
				// This removes 1 from the CMC because the ")" represents a
				// double color symbol. i.e. "(U/B)"
				CMC--;
			}
			if (mColor.matches()) {
				CMC++;
			}
		}

		return CMC;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Expansion getDefaultExpansion() {
		return this.defaultExpansion;
	}

	public List<Expansion> getAllExpansions() {
		if (expansions.isEmpty()) {
			List<Expansion> allExpansions = new ArrayList<Expansion>();
			allExpansions.addAll(expansionImages.keySet());

			return allExpansions;
		}

		return expansions;
	}

	public URL getDefaultPicURL() {
		return this.defaultPicURL;
	}

	public String getManaCost() {
		return this.manaCost;
	}

	public int getCMC() {
		return this.CMC;
	}

	public String getCardType() {
		return this.type;
	}

	public String getCardSubTypes() {
		return this.subType.toString();
	}

	public String getPower() {
		// TODO Need to handle */*, */#, #/*, and #+*/#+* Creatures...
		return this.sPower;
	}

	public String getToughness() {
		// TODO Need to handle */*, */#, #/*, and #+*/#+* Creatures...
		return this.sToughness;
	}

	public Map<Expansion, URL> getExpansionImages() {
		return this.expansionImages;
	}

	public String getText() {
		return this.text;
	}

	public boolean isBlue() {
		for (String color : colors) {
			if (color.equals("U")) {
				return true;
			}
		}

		return false;
	}

	public boolean isBlack() {
		for (String color : colors) {
			if (color.equals("B")) {
				return true;
			}
		}

		return false;
	}

	public boolean isWhite() {
		for (String color : colors) {
			if (color.equals("W")) {
				return true;
			}
		}

		return false;
	}

	public boolean isGreen() {
		for (String color : colors) {
			if (color.equals("G")) {
				return true;
			}
		}

		return false;
	}

	public boolean isRed() {
		for (String color : colors) {
			if (color.equals("R")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object o) {
		Card c = (Card) o;

		return this.getName().equalsIgnoreCase(c.getName());
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(Card c) {
		return name.compareTo(c.getName());
	}
}
