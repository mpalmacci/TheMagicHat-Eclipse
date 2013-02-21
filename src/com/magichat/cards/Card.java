package com.magichat.cards;

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
	List<String> colors;
	String manaCost;
	int CMC;
	String type;
	String[] subTypes;
	String sPower;
	String sToughness;
	// TODO Handle Card Rarities
	// String rarity;
	String text;
	Map<Expansion, URL> expansionImages = new HashMap<Expansion, URL>();

	public Card(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Card(int id, String name, List<Expansion> expansions) {
		this(id, name);
		this.expansions = expansions;
	}

	public Card(int id, String name, Map<Expansion, URL> expansionImages,
			boolean isBlue, boolean isBlack, boolean isRed, boolean isGreen,
			boolean isWhite, String manaCost, String type, String subTypes,
			String sPower, String sToughness, String text) {
		this.id = id;
		this.name = name;
		this.expansionImages = expansionImages;
		
		if(isBlue) this.colors.add("U");
		if(isBlack) this.colors.add("B");
		if(isWhite) this.colors.add("W");
		if(isRed) this.colors.add("R");
		if(isGreen) this.colors.add("G");
		
		this.manaCost = manaCost;
		this.CMC = convertManaCost(manaCost);
		this.type = type;
		this.subTypes = subTypes.split(" ");
		this.sPower = sPower;
		this.sToughness = sToughness;
		this.text = text;
	}

	public Card(String name, List<Expansion> expansions, List<URL> picURL,
			List<String> colors, String manaCost, String type, String pt,
			String text) {
		this.name = name;
		this.expansions = expansions;
		this.colors = colors;
		// TODO Add in Rarity for Cards
		// this.rarity = rarity;
		this.manaCost = manaCost;
		this.CMC = convertManaCost(manaCost);

		if (type.contains(" - ")) {
			this.type = type.substring(0, type.indexOf(" - "));
			this.subTypes = type.substring(type.indexOf(" - ") + 3,
					type.length()).split(" ");
		} else {
			this.type = type;
		}

		if (type.contains("Creature")) {
			String sPower = pt.substring(0, pt.indexOf("/")).replace("\\", "");
			String sToughness = pt.substring(pt.indexOf("/") + 1, pt.length()).replace("\\", "");

			this.sPower = sPower;
			this.sToughness = sToughness;

		}

		for (int i = 0; i < expansions.size(); i++) {
			expansionImages.put(expansions.get(i), picURL.get(i));
		}

		this.text = text;
	}

	public Card(int id, String name, List<Expansion> expansions,
			List<URL> picURL, List<String> colors, String manaCost,
			String type, String pt, String rarity, String text) {
		this(name, expansions, picURL, colors, manaCost, type, pt, text);
		this.id = id;
	}

	public int convertManaCost(String manaCost) {
		int CMC = 0;

		if (manaCost.isEmpty()) {
			return 0;
		} else {
			String[] manaCostArray = manaCost.split("");

			if (!manaCost.equals("")) {
				if (!manaCost.replaceAll("\\D+", "").equals("")) {
					CMC = Integer.parseInt(manaCost.replaceAll("\\D+", ""));
				}
			}

			// Need to handle digits (including 0), or the colors
			// { 'U', 'R', 'B', 'G', 'W' }
			// Then also need to handle multiple colors
			// so '(color/color)' will only really be an additional 1 mana
			// 'X' is equal to 0 so can be ignored in this calculation
			for (String s : manaCostArray) {
				Pattern paren = Pattern.compile("\\)");
				Pattern color = Pattern.compile("[URBGW]");

				Matcher mParen = paren.matcher(s);
				Matcher mColor = color.matcher(s);

				if (mParen.matches()) {
					// This removes 1 from the CMC because the ")" represents a
					// double color symbol. i.e. "(U/B)"
					CMC--;
				}
				if (mColor.matches()) {
					CMC++;
				}
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

	/*
	 * public String getRarity() { return this.rarity; }
	 */

	public Expansion getDefaultExpansion() {
		return this.expansions.get(0);
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
		return this.expansionImages.get(expansions.get(0));
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

	public String getCardSubType() {
		if (this.subTypes == null) {
			return "";
		}

		StringBuilder subTypes = new StringBuilder();
		for (String s : this.subTypes) {
			subTypes.append(s);
			subTypes.append(" ");
		}

		// This will trim off the last space at the end of the String
		return subTypes.toString().replaceAll("\\s+", " ");
	}

	public String[] getCardSubTypes() {
		return this.subTypes;
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
		if (this.colors.contains("U")) {
			return true;
		}

		return false;
	}

	public boolean isBlack() {
		if (this.colors.contains("B")) {
			return true;
		}

		return false;
	}

	public boolean isWhite() {
		if (this.colors.contains("W")) {
			return true;
		}

		return false;
	}

	public boolean isGreen() {
		if (this.colors.contains("G")) {
			return true;
		}

		return false;
	}

	public boolean isRed() {
		if (this.colors.contains("R")) {
			return true;
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
