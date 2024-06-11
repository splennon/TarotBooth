package org.overworld.tarotbooth;

import org.overworld.tarotbooth.EzzieMachine.State;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.github.oxo42.stateless4j.StateMachineConfig;

@Configuration
public class BeansConfiguration {
	
	StateActions actions;
	@Autowired
	public void setStateActions(@Lazy StateActions actions) {
		this.actions = actions;
	}
	
	@Bean
	public EzzieMachine stateMachine() {

		StateMachineConfig<State, Trigger> config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		config.configure(State.IDLE)
			.permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.onEntry(actions::idle)
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
			.onEntry(actions::curious)
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
			.onEntry(actions::engaged)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR);
		
		config.configure(State.REQUESTING_PAST)
			.permit(Trigger.PAST_READ, State.REQUESTING_PRESENT)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(actions::requestingPast)
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
			.onEntry(actions::requestingPresent)
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
			.onEntry(actions::requestingFuture)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.READING)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(actions::reading)
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
			.onEntry(actions::fixPlacement)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.RESET_BOOTH)
			.permit(Trigger.ADVANCE, State.ENGAGED)
			.onEntry(actions::resetBooth)
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
			.onEntry(actions::closing)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.FIX_PRINTER)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(actions::fixPrinter)
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
		EzzieMachine stateMachine = new EzzieMachine(State.IDLE, config);
		return stateMachine;
	}
}
