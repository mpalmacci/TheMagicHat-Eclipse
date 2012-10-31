package com.magichat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXCardsActivityHandler extends DefaultHandler {
	List<CardSet> allCardSets = new ArrayList<CardSet>();
	List<Card> allCards = new ArrayList<Card>();
	Card c;
	CardSet cs;

	String charVal = "";
	boolean isSet = true;

	// Card Set data types
	String name = "", longname = "";

	// Card data types
	String manaCost = "", type = "", pt = "", text = "";
	String[] color = {};
	CardSet[] cardSets = {};
	URL[] picURL = {};
	HashMap<CardSet, URL> setImages;
	// i is used as the index tracker for the set input values
	// j is used for picURL in the same manner
	// k is used for color
	int power = 0, toughness = 0, i = 0, j = 0, k = 0;

	public List<CardSet> getAllCardSets() {
		return allCardSets;
	}

	public List<Card> getAllCards() {
		return allCards;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("cards")) {
			isSet = false;
		} else if (localName.equals("set") && !isSet) {
			try {
				picURL[j] = new URL(attributes.getValue("picURL"));
			} catch (MalformedURLException urlEx) {
				urlEx.printStackTrace();
			}
			j++;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		charVal = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (isSet) {
			if (localName.equals("name")) {
				name = charVal;
			} else if (localName.equals("longname")) {
				longname = charVal;

				cs = new CardSet(longname, name);
				allCardSets.add(cs);
			}
		} else {
			if (localName.equals("name")) {
				name = charVal;
			} else if (localName.equals("set")) {
				for (CardSet c_s : allCardSets) {
					if (c_s.getShortName().equals(charVal)) {
						cardSets[i] = c_s;
						break;
					}
				}
				i++;
			} else if (localName.equals("color")) {
				color[k] = charVal;
				k++;
			} else if (localName.equals("manacost")) {
				manaCost = charVal;
			} else if (localName.equals("type")) {
				type = charVal;
			} else if (localName.equals("pt")) {
				pt = charVal;
			} else if (localName.equals("text")) {
				text = charVal;

				c = new Card(name, cardSets, picURL, color, manaCost, type, pt,
						text);
				allCards.add(c);

				i = 0;
				cardSets = null;
				j = 0;
				picURL = null;
				k = 0;
				color = null;
			}
		}
	}
}
