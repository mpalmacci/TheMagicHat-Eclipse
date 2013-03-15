package com.magichat.decks.games;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.magichat.decks.Deck;
import com.magichat.players.Player;

public class Game {
	private int id;
	private Map<Player, Deck> playersAndDecks;
	private Player pW;
	private Date gameDate;
	
	public Game(Map<Player, Deck> playersAndDecks, Date gameDate) {
		this.id = 0;
		this.playersAndDecks = playersAndDecks;
		this.gameDate = gameDate;
	}

	public Game(int id, Map<Player, Deck> playersAndDecks, Player pW,
			Date gameDate) {
		this(playersAndDecks, gameDate);
		this.setId(id);
		this.pW = pW;
	}

	public Player getWinner() {
		return pW;
	}

	public boolean isWinner(Player pW) {
		return this.pW.equals(pW);
	}
	
	public void setWinner(Player pW) {
		this.pW = pW;
	}

	public Player getPlayer(int pNum) {
		List<Player> gamers = new ArrayList<Player>();
		gamers.addAll(playersAndDecks.keySet());

		return gamers.get(pNum);
	}

	public Deck getDeck(int dNum) {
		List<Deck> gameDecks = new ArrayList<Deck>();
		gameDecks.addAll(playersAndDecks.values());

		return gameDecks.get(dNum);
	}
	
	public Map<Player, Deck> getPlayersAndDecks() {
		return this.playersAndDecks;
	}
	
/*	public void setPlayersAndDecks(Map<Player, Deck> playersAndDecks) {
		this.playersAndDecks = playersAndDecks;
	}*/

	public List<Player> getGamePlayers() {
		List<Player> gamers = new ArrayList<Player>();
		gamers.addAll(playersAndDecks.keySet());

		return gamers;
	}

	public Date getDate() {
		return this.gameDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
