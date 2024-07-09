package org.overworld.tarotbooth.process;

import java.io.IOException;
import java.time.Instant;

import org.overworld.tarotbooth.EzzieMachine;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.model.Deck;
import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.model.GameModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NfcService implements InitializingBean {

	@Value("${pastNfcProcess}")
	private String pastNfcProcess;
	@Value("${presentNfcProcess}")
	private String presentNfcProcess;
	@Value("${futureNfcProcess}")
	private String futureNfcProcess;
	
	@Value("${envKey}")
	private String envKey;
	
	@Value("${pastNfcEnv}")
	private String pastNfcEnv;
	@Value("${presentNfcEnv}")
	private String presentNfcEnv;
	@Value("${futureNfcEnv}")
	private String futureNfcEnv;
	
	@Value("${emptySlotTimeout}")
	private int emptySlotTimeout;
	
	private NfcMonitor pastMonitor, presentMonitor, futureMonitor;
	
	@Autowired
	private EzzieMachine stateMachine;
	
	@Autowired
	private Deck deck;
	
	@Autowired
	private GameModel model;
	
	@Override
	public void afterPropertiesSet() throws IOException {
		
		pastMonitor = new NfcMonitor();
		pastMonitor.attach("PAST", pastNfcProcess, envKey, pastNfcEnv);
	
		presentMonitor = new NfcMonitor();
		presentMonitor.attach("PRESENT", presentNfcProcess, envKey, presentNfcEnv);
		
		futureMonitor = new NfcMonitor();
		futureMonitor.attach("FUTURE", futureNfcProcess, envKey, futureNfcEnv);
	}
	
	@Scheduled(initialDelay=10000, fixedRate=3000)
	public void pollCards() throws IOException {
		
		pastMonitor.poll();
		presentMonitor.poll();
		futureMonitor.poll();
		
		Instant threshold = Instant.now().minusMillis(emptySlotTimeout);
		
		if (pastMonitor.getLastInstant().isBefore(threshold)) {
			model.clearPast();
		} else {
			Card newCard = deck.lookupByNfcId(pastMonitor.getLastCard());
			
			if (newCard == null) {
				System.out.println("Cannot find card in PAST position with id " + newCard);
			} else if (model.getPast().isEmpty() || model.getPast().get() != newCard) {
				model.setPast(newCard);
				stateMachine.fire(Trigger.PAST_READ);
			}
		}
		
		if (presentMonitor.getLastInstant().isBefore(threshold)) {
			model.clearPresent();
		} else {
			Card newCard = deck.lookupByNfcId(presentMonitor.getLastCard());

			if (newCard == null) {
				System.out.println("Cannot find card in PRESENT position with id " + newCard);
			} else if (model.getPresent().isEmpty() || model.getPresent().get() != newCard) {
				model.setPresent(newCard);
				stateMachine.fire(Trigger.PRESENT_READ);
			}
		}
		
		if (futureMonitor.getLastInstant().isBefore(threshold)) {
			model.clearFuture();
		} else {
			Card newCard = deck.lookupByNfcId(futureMonitor.getLastCard());

			if (newCard == null) {
				System.out.println("Cannot find card in FUTURE position with id " + newCard);
			} else if (model.getFuture().isEmpty() || model.getFuture().get() != newCard) {
				model.setFuture(newCard);
				stateMachine.fire(Trigger.FUTURE_READ);
			}
		}
	}
}
