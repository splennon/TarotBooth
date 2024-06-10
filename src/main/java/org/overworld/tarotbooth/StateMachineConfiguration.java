package org.overworld.tarotbooth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

@Configuration
public class StateMachineConfiguration {
	
	public enum State {
		IDLE, CURIOUS, ENGAGED, REQUESTING_PAST, REQUESTING_PRESENT, REQUESTING_FUTURE, FIX_PLACEMENT, READING, CLOSING,
		RESET_BOOTH, FIX_PRINTER
	}

	public enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR, TIMEOUT, ADVANCE,
		BAD_PLACEMENT
	}

	@Bean
	public StateMachine<State, Trigger> stateMachine() {

		StateMachineConfig<State, Trigger> config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		config.configure(State.IDLE)
			.permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.onEntry(StateMachineConfiguration::idle)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.ADVANCE)
			.ignore(Trigger.BAD_PLACEMENT);
	
		config.configure(State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.onEntry(StateMachineConfiguration::curious)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.ENGAGED)
			.permit(Trigger.PAST_READ, State.REQUESTING_PRESENT)
			.permit(Trigger.ADVANCE, State.REQUESTING_PAST)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.RESET_BOOTH)
			.onEntry(StateMachineConfiguration::engaged)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR);
		
		config.configure(State.REQUESTING_PAST)
			.permit(Trigger.PAST_READ, State.REQUESTING_PRESENT)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(StateMachineConfiguration::requestingPast)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.REQUESTING_PRESENT)
			.permit(Trigger.PRESENT_READ, State.REQUESTING_FUTURE)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(StateMachineConfiguration::requestingPresent)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.REQUESTING_FUTURE)
			.permit(Trigger.FUTURE_READ, State.READING)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(StateMachineConfiguration::requestingFuture)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.READING)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(StateMachineConfiguration::reading)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.FIX_PLACEMENT)
			.permit(Trigger.ADVANCE, State.REQUESTING_PAST)
			.permit(Trigger.TIMEOUT, State.RESET_BOOTH)
			.onEntry(StateMachineConfiguration::fixPlacement)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.RESET_BOOTH)
			.permit(Trigger.ADVANCE, State.ENGAGED)
			.onEntry(StateMachineConfiguration::resetBooth)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.CLOSING)
			.permit(Trigger.ADVANCE, State.IDLE)
			.permit(Trigger.PRINTER_ERROR, State.FIX_PRINTER)
			.onEntry(StateMachineConfiguration::closing)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.FIX_PRINTER)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(StateMachineConfiguration::fixPrinter)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
					
		/* @formatter:on */

		// config.generateDotFileInto(System.out, true);
		StateMachine<State, Trigger> stateMachine = new StateMachine<State, Trigger>(State.IDLE, config);
		stateMachine.fireInitialTransition();
		return stateMachine;
	}

	private static void idle() {
		System.out.println("idle");
	}

	private static void curious() {
		System.out.println("curious");
	}

	private static void engaged() {
		System.out.println("engaged");
	}

	private static void requestingPast() {
		System.out.println("requesting past");
	}

	private static void requestingPresent() {
		System.out.println("requesting present");
	}

	private static void requestingFuture() {
		System.out.println("requesting future");
	}

	private static void reading() {
		System.out.println("reading");
	}

	private static void fixPlacement() {
		System.out.println("fix placement");
	}

	private static void resetBooth() {
		System.out.println("reset booth");
	}

	private static void closing() {
		System.out.println("closing");
	}

	private static void fixPrinter() {
		System.out.println("fix printer");
	}
}
