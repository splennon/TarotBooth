package org.overworld.tarotbooth;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
public class TimeoutService {

	private Instant poked = Instant.now();
	
	@Autowired
	private EzzieMachine stateMachine;
	
	@Setter
	@Getter
	private Duration timeout = Duration.of(1, ChronoUnit.MINUTES);
	
	@Scheduled(fixedRate = 5000)
	public void checkTimeout() {
		
		if (this.poked.isBefore(Instant.now().minus(timeout))) {
			stateMachine.fire(EzzieMachine.Trigger.TIMEOUT);
		}
	}
	
	public synchronized void poke() {
		
		poked = Instant.now();
	}
}
