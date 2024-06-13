package org.overworld.tarotbooth;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class EzzieMachine extends StateMachine<EzzieMachine.State, EzzieMachine.Trigger> {

	public EzzieMachine(State idle, StateMachineConfig<State, Trigger> config) {
		super(idle, config);
	}

	public enum State {
		IDLE, CURIOUS, ENGAGED, REQUESTING_PAST, REQUESTING_PRESENT, REQUESTING_FUTURE, FIX_PLACEMENT, READING, CLOSING,
		RESET_BOOTH, FIX_PRINTER, QUINN, ASIDE, INTRO, READING_PAST, READING_PRESENT, READING_FUTURE
	}

	public enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR, TIMEOUT, ADVANCE,
		BAD_PLACEMENT
	}
}
