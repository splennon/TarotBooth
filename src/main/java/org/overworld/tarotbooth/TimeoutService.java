package org.overworld.tarotbooth;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
public class TimeoutService implements InitializingBean {
	
	@Value("${idleTimeoutSeconds}")
	private String timeoutSeconds;

	private Instant poked = Instant.now();
	
	@Autowired
	private EzzieMachine stateMachine;
	
	@Setter
	@Getter
	private Duration timeout;
	
	@Scheduled(fixedRate = 5000)
	public void checkTimeout() {
		
		if (this.poked.isBefore(Instant.now().minus(timeout))) {
			stateMachine.fire(EzzieMachine.Trigger.TIMEOUT);
			System.out.println("Firing TIMEOUT trigger");
		}
	}
	
	public synchronized void poke() {
		
		poked = Instant.now();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		timeout = Duration.of(Integer.valueOf(timeoutSeconds), ChronoUnit.SECONDS);
	}
}
