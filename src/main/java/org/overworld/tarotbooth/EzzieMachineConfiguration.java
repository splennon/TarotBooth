package org.overworld.tarotbooth;

import static org.overworld.tarotbooth.EzzieMachine.State.ASIDE;
import static org.overworld.tarotbooth.EzzieMachine.State.ATTRACTING;
import static org.overworld.tarotbooth.EzzieMachine.State.BANDY;
import static org.overworld.tarotbooth.EzzieMachine.State.BEACHES;
import static org.overworld.tarotbooth.EzzieMachine.State.CLOSING;
import static org.overworld.tarotbooth.EzzieMachine.State.CURIOUS;
import static org.overworld.tarotbooth.EzzieMachine.State.ENGAGED;
import static org.overworld.tarotbooth.EzzieMachine.State.ESTRALADA;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_FUTURE;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_JOCKER;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_PAST;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_PLACEMENT;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_PRESENT;
import static org.overworld.tarotbooth.EzzieMachine.State.FIX_PRINTER;
import static org.overworld.tarotbooth.EzzieMachine.State.HELLO;
import static org.overworld.tarotbooth.EzzieMachine.State.IDLE;
import static org.overworld.tarotbooth.EzzieMachine.State.INTRO;
import static org.overworld.tarotbooth.EzzieMachine.State.LOADED;
import static org.overworld.tarotbooth.EzzieMachine.State.PRINTING;
import static org.overworld.tarotbooth.EzzieMachine.State.PRINTING_INTRO;
import static org.overworld.tarotbooth.EzzieMachine.State.PRINTING_READING;
import static org.overworld.tarotbooth.EzzieMachine.State.QUINN;
import static org.overworld.tarotbooth.EzzieMachine.State.READING;
import static org.overworld.tarotbooth.EzzieMachine.State.READING_CLOSE;
import static org.overworld.tarotbooth.EzzieMachine.State.READING_FUTURE;
import static org.overworld.tarotbooth.EzzieMachine.State.READING_INTRO;
import static org.overworld.tarotbooth.EzzieMachine.State.READING_PAST;
import static org.overworld.tarotbooth.EzzieMachine.State.READING_PRESENT;
import static org.overworld.tarotbooth.EzzieMachine.State.RECEIVING_FUTURE;
import static org.overworld.tarotbooth.EzzieMachine.State.RECEIVING_PAST;
import static org.overworld.tarotbooth.EzzieMachine.State.RECEIVING_PRESENT;
import static org.overworld.tarotbooth.EzzieMachine.State.REQUESTING;
import static org.overworld.tarotbooth.EzzieMachine.State.REQUESTING_FUTURE;
import static org.overworld.tarotbooth.EzzieMachine.State.REQUESTING_PAST;
import static org.overworld.tarotbooth.EzzieMachine.State.REQUESTING_PRESENT;
import static org.overworld.tarotbooth.EzzieMachine.State.RESET_BOOTH;
import static org.overworld.tarotbooth.EzzieMachine.State.RUNNING;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.ADVANCE;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.APPROACH_SENSOR;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.BAD_PLACEMENT;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.BAD_PLACEMENT_FUTURE;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.BAD_PLACEMENT_JOCKER;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.BAD_PLACEMENT_PAST;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.BAD_PLACEMENT_PREESNT;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.FUTURE_READ;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.PAST_READ;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.PRESENCE_SENSOR;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.PRESENT_READ;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.PRINTER_ERROR;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.TIMEOUT;

import org.overworld.tarotbooth.EzzieMachine.State;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.oxo42.stateless4j.StateMachineConfig;

@Configuration
public class EzzieMachineConfiguration {

	@Autowired
	private EzzieMachineActions actions;
	
	@Bean
	public EzzieMachine stateMachine() {

		StateMachineConfig<State, Trigger> config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		/* Unused super state for syntactic simplicity, holds all ignores */
		
		config.configure(LOADED)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)
			.ignore(ADVANCE)
			.ignore(BAD_PLACEMENT_PAST)
			.ignore(BAD_PLACEMENT_PREESNT)
			.ignore(BAD_PLACEMENT_FUTURE)
			.ignore(BAD_PLACEMENT)
			.ignore(BAD_PLACEMENT_JOCKER)
			;

		/* Start the music, Ezzie is in session! */
		
		config.configure(RUNNING)
			.substateOf(LOADED)
			.permit(ADVANCE, ATTRACTING)
			.onEntry(() -> System.out.println("Entering Superstate RUNNING"))
			.onEntry(actions::running)
			.onEntry(actions::advanceAfterFxmlLoadDelay)
			.onExit(actions::runningExit)
			;

		/* Ezzie is attracting people into her empty booth */
		
		config.configure(ATTRACTING)
			.substateOf(RUNNING)
			.permit(ADVANCE, IDLE)
			.onEntry(() -> System.out.println("Entering Superstate ATTRACTING"))
			.onEntry(actions::attracting)
			.onEntry(actions::advance)
			;
		config.configure(IDLE)
			.substateOf(ATTRACTING)
			.ignore(ADVANCE)
			.permit(APPROACH_SENSOR, CURIOUS)
			.permit(PRESENCE_SENSOR, ENGAGED)
			.onEntry(() -> System.out.println("Entering IDLE"))
			.onEntry(actions::idle)
			.onExit(actions::idleExit)
			;
		config.configure(CURIOUS)
			.substateOf(ATTRACTING)
			.permit(PRESENCE_SENSOR, ENGAGED)
			.permit(ADVANCE, IDLE)
			.onEntry(() -> System.out.println("Entering CURIOUS"))
			.onEntry(actions::curious)
			.onExit(actions::curiousExit)
			;
		
		/* Ezzie has a customer! */
		
		config.configure(ENGAGED)
			.substateOf(RUNNING)
			.permit(ADVANCE, HELLO)
			.permit(TIMEOUT, ATTRACTING)
			.onEntry(() -> System.out.println("Entering Superstate ENGAGED"))
			.onEntry(actions::advance)
			;
		config.configure(HELLO)
			.substateOf(ENGAGED)
			.permit(ADVANCE, QUINN)
			.onEntry(() -> System.out.println("Entering HELLO"))
			.onEntry(actions::hello)
			.onExit(actions::helloExit)
			;
		config.configure(QUINN)
			.substateOf(ENGAGED)
			.permit(ADVANCE, ASIDE)
			.onEntry(() -> System.out.println("Entering QUINN"))
			.onEntry(actions::quinn)
			.onExit(actions::quinnExit)
			;
		config.configure(ASIDE)
			.substateOf(ENGAGED)
			.permit(ADVANCE, INTRO)
			.onEntry(() -> System.out.println("Entering ASIDE"))
			.onEntry(actions::aside)
			.onExit(actions::asideExit)
			;
		config.configure(INTRO)
			.substateOf(ENGAGED)
			.permit(ADVANCE, REQUESTING)
			.onEntry(() -> System.out.println("Entering INTRO"))
			.onEntry(actions::intro)
			.onExit(actions::introExit)
			;
		
		/* Ezzie is requesting cards be drawn */
		
		config.configure(REQUESTING)
			.substateOf(RUNNING)
			.permit(ADVANCE, REQUESTING_PAST)
			.permit(TIMEOUT, ATTRACTING)
			.onEntry(() -> System.out.println("Entering Superstate REQUESTING"))
			.onEntry(actions::requesting)
			.onEntry(actions::advance)
			;
		config.configure(REQUESTING_PAST)
			.substateOf(REQUESTING)
			.permit(PAST_READ, RECEIVING_PAST)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_PAST"))
			.onEntry(actions::requestingPast)
			.onExit(actions::requestingPastExit)
			;
		config.configure(RECEIVING_PAST)
			.substateOf(REQUESTING)
			.permit(ADVANCE, REQUESTING_PRESENT)
			.onEntry(() -> System.out.println("Entering RECEIVING_PAST"))
			.onEntry(actions::receivingPast)
			.onExit(actions::receivingPastExit)
			;
		config.configure(REQUESTING_PRESENT)
			.substateOf(REQUESTING)
			.permit(PRESENT_READ, RECEIVING_PRESENT)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_PRESENT"))
			.onEntry(actions::requestingPresent)
			.onExit(actions::requestingPresentExit)
			;
		config.configure(RECEIVING_PRESENT)
			.substateOf(REQUESTING)
			.permit(ADVANCE, REQUESTING_FUTURE)
			.onEntry(() -> System.out.println("Entering RECEIVING_PRESENT"))
			.onEntry(actions::receivingPresent)
			.onExit(actions::receivingPresentExit)
			;
		config.configure(REQUESTING_FUTURE)
			.substateOf(REQUESTING)
			.permit(FUTURE_READ, RECEIVING_FUTURE)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_FUTURE"))
			.onEntry(actions::requestingFuture)
			.onExit(actions::requestingFutureExit)
			;
		config.configure(RECEIVING_FUTURE)
			.substateOf(REQUESTING)
			.permit(ADVANCE, READING)
			.onEntry(() -> System.out.println("Entering RECEIVING_FUTURE"))
			.onEntry(actions::receivingFuture)
			.onExit(actions::receivingFutureExit)
			;
		
		/* Ezzie is offering her reading with a little help from Benny */
		
		config.configure(READING)
			.substateOf(RUNNING)
			.permit(ADVANCE, READING_INTRO)
			.onEntry(() -> System.out.println("Entering Superstate READING"))
			.onEntry(actions::reading)
			.onEntry(actions::advance)
			;
		config.configure(READING_INTRO)
			.substateOf(READING)
			.permit(ADVANCE,  READING_PAST)
			.onEntry(() -> System.out.println("Entering READING_INTRO"))
			.onEntry(actions::readingIntro)
			.onExit(actions::readingIntroExit)
			;
		config.configure(READING_PAST)
			.substateOf(READING)
			.permit(ADVANCE,  READING_PRESENT)
			.onEntry(() -> System.out.println("Entering READING_PAST"))
			.onEntry(actions::readingPast)
			.onExit(actions::readingPastExit)
			;
			config.configure(READING_PRESENT)
			.substateOf(READING)
			.permit(ADVANCE,  READING_FUTURE)
			.onEntry(() -> System.out.println("Entering READING_PRESENT"))
			.onEntry(actions::readingPresent)
			.onExit(actions::readingPresentExit)
			;
			config.configure(READING_FUTURE)
			.substateOf(READING)
			.permit(ADVANCE,  READING_CLOSE)
			.onEntry(() -> System.out.println("Entering READING_FUTURE"))
			.onEntry(actions::readingFuture)
			.onExit(actions::readingFutureExit)
			;
		config.configure(READING_CLOSE)
			.substateOf(READING)
			.permit(ADVANCE,  PRINTING)
			.onEntry(() -> System.out.println("Entering READING_CLOSE"))
			.onEntry(actions::readingClose)
			.onExit(actions::readingCloseExit)
			;

		/* Ezzie is delivering printout and vouchers */
		
		config.configure(PRINTING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate PRINTING"))
			;
		config.configure(PRINTING_INTRO)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering PRINTING_INTRO"))
			;
		config.configure(PRINTING_READING)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering PRINTING_READING"))
			;
		config.configure(BANDY)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering BANDY"))
			;
		config.configure(BEACHES)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering BEACHES"))
			;
		config.configure(ESTRALADA)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("ESTRALADA"))
			;
		
		/* Ezzie's reading is ending */
		
		config.configure(CLOSING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate CLOSING"))
			;
		
		/* Some twat has put cards in the wrong place at the wrong time */
		
		config.configure(FIX_PLACEMENT)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate FIX_PLACEMENT"))
			;
		config.configure(FIX_PAST)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_PAST"))
			;
		config.configure(FIX_PRESENT)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_PRESENT"))
			;
		config.configure(FIX_FUTURE)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_FUTURE"))
			;
		config.configure(FIX_JOCKER)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_JOCKER"))
			;
		
		/* The printer made a booboo */
		
		config.configure(FIX_PRINTER)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate FIX_PRINTER"))
			;
		
		/* Cards were not put away for next reading */
		
		config.configure(RESET_BOOTH)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate RESET_BOOTH"))
			;
					
		/* @formatter:on */

//		try {
//			config.generateDotFileInto(System.out, true);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		EzzieMachine stateMachine = new EzzieMachine(RUNNING, config);
		stateMachine.fireInitialTransition();
		return stateMachine;
	}
}
