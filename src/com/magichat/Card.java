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

		if (manaCost.isEmpty()) {
			return 0;
		} else {
			String[] manaCostArray = manaCost.split("");

			// TODO This does not calculate in the digit portion of the manaCost properly
			Pattern digit = Pattern.compile("\\d");
			Matcher mDigit = digit.matcher(manaCost);
			mDigit.find();
			if (mDigit.groupCount() > 0) {
				CMC += Integer.parseInt(mDigit.group(0));
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

	public String getCardSubType() {
		if (this.subType == null) {
			return "";
		}

		StringBuilder subTypes = new StringBuilder();
		for (String s : this.subType) {
			subTypes.append(s);
			subTypes.append(" ");
		}

		// This will trim off the last space at the end of the String
		return subTypes.toString().replaceAll("\\s+", " ");
	}

	public String[] getCardSubTypes() {
		return this.subType;
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
		if (colors.contains("U")) {
			return true;
		}

		return false;
	}

	public boolean isBlack() {
		if (colors.contains("B")) {
			return true;
		}

		return false;
	}

	public boolean isWhite() {
		if (colors.contains("W")) {
			return true;
		}

		return false;
	}

	public boolean isGreen() {
		if (colors.contains("G")) {
			return true;
		}

		return false;
	}

	public boolean isRed() {
		if (colors.contains("R")) {
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
