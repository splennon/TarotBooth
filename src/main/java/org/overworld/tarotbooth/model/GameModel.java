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
	
	public Optional<Position> findError() {

		if (present.isPresent() && past.isEmpty())
			return Optional.of(Position.PAST);
		
		if (future.isPresent() && past.isEmpty())
			return Optional.of(Position.PAST);
		
		if (future.isPresent() && present.isEmpty())
			return Optional.of(Position.PRESENT);
		
		return Optional.empty();
	}
	
	public boolean findDuplicate() {

		return (past.isPresent() && present.isPresent() && past.get().equals(present.get()))
				|| (past.isPresent() && future.isPresent() && past.get().equals(future.get()))
				|| (present.isPresent() && future.isPresent() && present.get().equals(future.get()));
	}

	public void clear() {
		past = Optional.empty();
		present = Optional.empty();
		future = Optional.empty();
	}
	
	public void clearPast() {
		past = Optional.empty();
	}
	
	public void clearPresent() {
		present = Optional.empty();
	}
	
	public void clearFuture() {
		future = Optional.empty();
	}
}
