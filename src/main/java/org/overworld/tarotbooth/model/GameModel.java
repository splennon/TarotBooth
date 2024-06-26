package org.overworld.tarotbooth.model;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class GameModel {
	
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
	
	public Optional<Deck.Card> getCardInPosition(Position position) {
		return switch (position) {
			case PAST -> getPast();
			case PRESENT -> getPresent();
			case FUTURE -> getFuture();
		};
	}

	public void clear() {
		past = Optional.empty();
		present = Optional.empty();
		future = Optional.empty();
	}
}
