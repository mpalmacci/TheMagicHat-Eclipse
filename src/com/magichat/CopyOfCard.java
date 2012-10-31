package com.magichat;

import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyOfCard implements Comparable<CopyOfCard> {
	int id;
	String name;
	CardSet defaultCardSet;
	String defaultPicURL;
	String[] colors;
	String manaCost;
	int CMC;
	String type;
	String[] subType;
	int power;
	int toughness;
	String text;
	HashMap<CardSet, URL> setsImages;

	class SetImage {
		public CardSet cardSet;
		public String picURL;

		public SetImage(CardSet cardSet, String picURL) {
			this.cardSet = cardSet;
			this.picURL = picURL;
		}

		public void setCardSet(CardSet cardSet) {
			this.cardSet = cardSet;
		}

		public void setPicURL(String picURL) {
			this.picURL = picURL;
		}

		public CardSet getCardSet() {
			return this.cardSet;
		}

		public String getPicURL() {
			return this.picURL;
		}
	}
	
	public CopyOfCard(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public CopyOfCard(String name, CardSet[] cardSets, String[] picURL, String[] colors,
			String manaCost, String type, String pt, String text) {
		this.name = name;
		this.colors = colors;
		this.manaCost = manaCost;
		this.CMC = convertManaCost(manaCost);

		this.type = type.substring(0, type.indexOf(" - ") - 1);
		this.subType = type.substring(type.indexOf(" - ") + 3, type.length())
				.split(" ");

		String sPower = pt.substring(0, pt.indexOf("/") - 1);
		String sToughness = pt.substring(pt.indexOf("/") + 1, pt.length());
		this.power = Integer.parseInt(sPower);
		this.toughness = Integer.parseInt(sToughness);

		for (int i = 0; i < cardSets.length; i++) {
			setsImages[i].setCardSet(cardSets[i]);
			setsImages[i].setPicURL(picURL[i]);
		}

		this.defaultCardSet = setsImages[0].getCardSet();
		this.defaultPicURL = setsImages[0].getPicURL();

		this.text = text;
	}

	public CopyOfCard(String name, CardSet[] cardSets, String[] picURL, String[] colors,
			String manaCost, String type, String pt, String text, int id) {
		this(name, cardSets, picURL, colors, manaCost, type, pt, text);
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
	
	public CardSet getDefaultCardSet() {
		return this.defaultCardSet;
	}
	
	public String getDefaultPicURL() {
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
	
	public int getPower() {
		return this.power;
	}
	
	public int getToughness() {
		return this.toughness;
	}
	
	public SetImage[] getSetsImages() {
		return this.setsImages;
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
		CopyOfCard c = (CopyOfCard) o;

		return this.getName().equalsIgnoreCase(c.getName());
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(CopyOfCard c) {
		return name.compareTo(c.getName());
	}
}
