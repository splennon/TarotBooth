package org.overworld.tarotbooth;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class EzzieMachine extends StateMachine<EzzieMachine.State, EzzieMachine.Trigger> {

	public EzzieMachine(State idle, StateMachineConfig<State, Trigger> config) {
		super(idle, config);
	}

	public enum State {

		LOADED, RUNNING, ATTRACTING, IDLE, CURIOUS, ENGAGED, HELLO, QUINN, ASIDE, INTRO, REQUESTING, REQUESTING_PAST,
		RECEIVING_PAST, REQUESTING_PRESENT, RECEIVING_PRESENT, REQUESTING_FUTURE, RECEIVING_FUTURE, READING,
		READING_INTRO, READING_PAST, READING_PRESENT, READING_FUTURE, READING_CLOSE, PRINTING, PRINTING_INTRO, PRINTING_READING, BANDY, BEACHES,
		ESTRALADA, CLOSING, FIX_PLACEMENT, FIX_PAST, FIX_PRESENT, FIX_FUTURE, FIX_PRINTER, FIX_JOCKER, RESET_BOOTH
	}

	public enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR, TIMEOUT, ADVANCE,
		BAD_PLACEMENT_PAST, BAD_PLACEMENT_PREESNT, BAD_PLACEMENT_FUTURE, BAD_PLACEMENT, BAD_PLACEMENT_JOCKER
	}
}
