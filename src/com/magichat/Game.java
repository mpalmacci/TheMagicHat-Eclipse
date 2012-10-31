package com.magichat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {
	int id;
	Player p1;
	Player p2;
	Deck d1;
	Deck d2;
	Player pW;
	Date gameDate;
	
	public Game(int id, Player p1, Player p2, Deck d1, Deck d2, Player pW) {
		this.id = id;
		this.p1 = p1;
		this.p2 = p2;
		this.d1 = d1;
		this.d2 = d2;
		this.pW = pW;
	}
	
	public Game(int id, Player p1, Player p2, Deck d1, Deck d2, Player pW, Date gameDate) {
		this(id, p1, p2, d1, d2, pW);
		this.gameDate = gameDate;
	}
	
	public Player getWinner() {
		return pW;
	}
	
	public boolean isWinner(Player pW) {
		return this.pW.equals(pW);
	}
	
	public Player getPlayer(int id) {
		if (id == 1) {
			return this.p1;
		} else if (id == 2) {
			return this.p2;
		}
		return new Player();
	}
	
	public Deck getDeck(int id) {
		if (id == 1) {
			return this.d1;
		} else if (id == 2) {
			return this.d2;
		}
		return new Deck();
	}
	
	public List<Player> getGamePlayers(int id) {
		List<Player> gamers = new ArrayList<Player>();
		gamers.add(p1);
		gamers.add(p2);
		return gamers;
	}
}
