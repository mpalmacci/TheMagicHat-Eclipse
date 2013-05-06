package com.magichat.decks.db;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.magichat.decks.Deck;
import com.magichat.players.Player;

public class SAXDeckListActivityHandler extends DefaultHandler {
	List<Deck> allDecks = new ArrayList<Deck>();
	List<Player> allOwners = new ArrayList<Player>();
	List<Player> activeOwners = new ArrayList<Player>();
	Deck d;
	Player p;

	protected List<Deck> getAllDecks() {
		return allDecks;
	}

	protected List<Player> getActiveOwners() {
		return activeOwners;
	}

	protected List<Player> getAllOwners() {
		return allOwners;
	}

	protected Player getOwner(int id) {
		for (Player p : allOwners) {
			if (p.getId() == id) {
				return p;
			}
		}

		return null;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (localName.equals("Owner")) {
			String name = attributes.getValue("name");
			String sActive = attributes.getValue("active");
			boolean active = Boolean.parseBoolean(sActive);
			String sId = attributes.getValue("number");
			int id = Integer.parseInt(sId);

			p = new Player(id, name, active);
		} else if (localName.equals("Deck")) {
			String name = attributes.getValue("name");

			// this is now an integer value that represents the Owner Number
			String sOwnerId = attributes.getValue("owner");
			int ownerId = Integer.parseInt(sOwnerId);
			if (getOwner(ownerId) != null) {
				Player owner = getOwner(ownerId);
				String sActive = attributes.getValue("active");
				boolean active = Boolean.parseBoolean(sActive);
				String sId = attributes.getValue("number");
				int id = Integer.parseInt(sId);

				d = new Deck(id, name, owner, active);
			} else {
				System.out.println("No deck owner was found for Owner Id"
						+ ownerId);
				return;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("Deck")) {
			allDecks.add(d);
		} else if (localName.equals("Owner")) {
			// Add p to the list of Players so long as the deck is active,
			// p isn't already in the list of Players, and
			// p isn't the Wizards Decks
			// TODO: Separate out owners of decks with players of games
			if (!activeOwners.contains(p) && p.isActive()
					&& !p.equals(new Player("Wizards of the Coast"))) {
				activeOwners.add(p);
			}
			if (!allOwners.contains(p)) {
				allOwners.add(p);
			}
		}
	}
}