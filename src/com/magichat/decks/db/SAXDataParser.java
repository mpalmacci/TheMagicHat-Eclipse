package com.magichat.decks.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.magichat.R;
import com.magichat.decks.Deck;
import com.magichat.players.Player;

import android.content.Context;

public class SAXDataParser {

	SAXDeckListActivityHandler saxDL_AH = new SAXDeckListActivityHandler();

	protected void parseDeckListXml(Context cont) {

		InputStream istream = null;

		try {
			istream = cont.getResources().openRawResource(R.raw.deck_list);

			XMLReader xr = setupSaxXR();
			xr.setContentHandler(saxDL_AH);

			xr.parse(new InputSource(istream));
		} catch (SAXException saxE) {
			saxE.printStackTrace();
		} catch (IOException ioE) {
			ioE.printStackTrace();
		}
	}

	protected List<Player> getActiveOwners() {
		return saxDL_AH.getActiveOwners();
	}

	protected List<Player> getAllOwners() {
		return saxDL_AH.getAllOwners();
	}

	protected List<Deck> getAllDecks() {
		return saxDL_AH.getAllDecks();
	}

	private XMLReader setupSaxXR() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			return sp.getXMLReader();
		} catch (SAXException saxE) {
			saxE.printStackTrace();
		} catch (ParserConfigurationException pcE) {
			pcE.printStackTrace();
		}

		return null;
	}
}