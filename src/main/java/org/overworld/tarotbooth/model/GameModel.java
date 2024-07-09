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
	
	private boolean locked;
	
	public void lock() {
		this.locked = true;
	}
	
	public void setPast(Deck.Card past) {
			
		if (locked)
			System.out.println("Trying to modify the game model in locked state");
		else
			this.past = Optional.ofNullable(past);
	}
	
	public void setPresent(Deck.Card present) {
		
		if (locked)
			System.out.println("Trying to modify the game model in locked state");
		else
			this.present = Optional.ofNullable(present);
	}
	
	public void setFuture(Deck.Card future) {
		
		if (locked)
			System.out.println("Trying to modify the game model in locked state");
		else
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
		locked = false;
	}
	
	public void clearPast() {
		if (!locked)
			past = Optional.empty();
	}
	
	public void clearPresent() {
		if (!locked)
			present = Optional.empty();
	}
	
	public void clearFuture() {
		if (!locked)
			future = Optional.empty();
	}
}
