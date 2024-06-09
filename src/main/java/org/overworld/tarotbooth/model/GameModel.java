package org.overworld.tarotbooth.model;

import java.util.Optional;

import lombok.Getter;

public class GameModel {

	@Getter
	private static final Deck deck = new Deck();
	
	@Getter
	private Optional<Deck.Card> past = Optional.empty();
	
	@Getter
	private Optional<Deck.Card> present = Optional.empty();
	
	@Getter
	private Optional<Deck.Card> future = Optional.empty();
	
	public void setPast(Deck.Card past) {
		this.past = Optional.ofNullable(past);
	}
	
	public void setPresent(Deck.Card present) {
		this.present = Optional.ofNullable(present);
	}
	
	public void setFuture(Deck.Card future) {
		this.future = Optional.ofNullable(future);
	}
}
