package org.overworld.tarotbooth.sensors;

import org.overworld.tarotbooth.EzzieMachine;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.TimeoutService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;

@Component
public class SensorService implements InitializingBean {
	
	@Value("${presencePin}")
	private int presencePin;

	@Value("${approachPin}")
	private int approachPin;

	@Value("${sensorDisable}")
	private boolean sensorDisable;

	@Autowired
	private TimeoutService timeout;
	
	@Autowired
	private EzzieMachine stateMachine;
	
	private DigitalInput approach, presence;
	
	private boolean warned = false;

	@Scheduled(fixedRate = 2000)
	public void sense() {

		if (sensorDisable) {
			if (!warned) {
				System.err.println("Sensor bean is disabled, consider removing bean");
				warned = true;
			}
			return;
		}

		if (presence.state() == DigitalState.HIGH) {
			timeout.poke();
			stateMachine.fire(Trigger.PRESENCE_SENSOR);
		}
		if (approach.state() == DigitalState.HIGH) {
			stateMachine.fire(Trigger.APPROACH_SENSOR);
		}
	}

	@Override
	public void afterPropertiesSet() {

		if (sensorDisable) {
			return;
		}

		Context pi4j = Pi4J.newAutoContext();
		approach = pi4j.digitalInput().create(approachPin);
		presence = pi4j.digitalInput().create(presencePin);
	}
}