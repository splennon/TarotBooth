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
import static org.overworld.tarotbooth.EzzieMachine.Trigger.CORRUPT_DUPLICATE;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.CORRUPT_PAST;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.CORRUPT_PRESENT;
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
			.ignore(CORRUPT_PAST)
			.ignore(CORRUPT_PRESENT)
			.ignore(CORRUPT_DUPLICATE)
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
			.permit(PRESENT_READ, FIX_PAST)
			.permit(FUTURE_READ, FIX_PAST)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_PAST"))
			.onEntry(actions::requestingPast)
			.onExit(actions::requestingPastExit)
			;
		config.configure(RECEIVING_PAST)
			.substateOf(REQUESTING)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
			.permit(ADVANCE, REQUESTING_PRESENT)
			.onEntry(() -> System.out.println("Entering RECEIVING_PAST"))
			.onEntry(actions::receivingPast)
			.onExit(actions::receivingPastExit)
			;
		config.configure(REQUESTING_PRESENT)
			.substateOf(REQUESTING)
			.permit(PAST_READ, FIX_PRESENT)
			.permit(PRESENT_READ, RECEIVING_PRESENT)
			.permit(FUTURE_READ, FIX_PRESENT)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_PRESENT"))
			.onEntry(actions::requestingPresent)
			.onExit(actions::requestingPresentExit)
			;
		config.configure(RECEIVING_PRESENT)
			.substateOf(REQUESTING)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
			.permit(ADVANCE, REQUESTING_FUTURE)
			.onEntry(() -> System.out.println("Entering RECEIVING_PRESENT"))
			.onEntry(actions::receivingPresent)
			.onExit(actions::receivingPresentExit)
			;
		config.configure(REQUESTING_FUTURE)
			.substateOf(REQUESTING)
			.permit(PAST_READ, FIX_FUTURE)
			.permit(PRESENT_READ, FIX_FUTURE)
			.permit(FUTURE_READ, RECEIVING_FUTURE)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
			.ignore(ADVANCE)
			.onEntry(() -> System.out.println("Entering REQUESTING_FUTURE"))
			.onEntry(actions::requestingFuture)
			.onExit(actions::requestingFutureExit)
			;
		config.configure(RECEIVING_FUTURE)
			.substateOf(REQUESTING)
			.permit(CORRUPT_PAST, FIX_PAST)
			.permit(CORRUPT_PRESENT, FIX_PRESENT)
			.permit(CORRUPT_DUPLICATE, FIX_PAST)
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
			.permit(ADVANCE,  PRINTING_READING)
			.onEntry(() -> System.out.println("Entering Superstate PRINTING"))
			.onEntry(actions::advance)
			;
			config.configure(PRINTING_READING)
			.substateOf(PRINTING)
			.permit(ADVANCE,  PRINTING_INTRO)
			.onEntry(() -> System.out.println("Entering PRINTING_READING"))
			.onEntry(actions::printingReading)
			.onExit(actions::printingReadingExit)
			;
		config.configure(PRINTING_INTRO)
			.substateOf(PRINTING)
			.permit(ADVANCE, BEACHES)
			.onEntry(() -> System.out.println("Entering PRINTING_INTRO"))
			.onEntry(actions::printingIntro)
			.onExit(actions::printingIntroExit)
			;
			config.configure(BEACHES)
			.substateOf(PRINTING)
			.permit(ADVANCE,  BANDY)
			.onEntry(() -> System.out.println("Entering BEACHES"))
			.onEntry(actions::beaches)
			.onExit(actions::beachesExit)
			;
		config.configure(BANDY)
			.substateOf(PRINTING)
			.permit(ADVANCE,  ESTRALADA)
			.onEntry(() -> System.out.println("Entering BANDY"))
			.onEntry(actions::bandy)
			.onExit(actions::bandyExit)
			;
		config.configure(ESTRALADA)
			.substateOf(PRINTING)
			.permit(ADVANCE,  CLOSING)
			.onEntry(() -> System.out.println("Entering ESTRALADA"))
			.onEntry(actions::estalada)
			.onExit(actions::estraladaExit)
			;
		
		/* Ezzie's reading is ending */
		
		config.configure(CLOSING)
			.substateOf(RUNNING)
			.permit(ADVANCE, RESET_BOOTH)
			.onEntry(() -> System.out.println("Entering Superstate CLOSING"))
			.onEntry(actions::closing)
			.onExit(actions::closingExit)
			;
		
		/* Clear out for the next reading */
		
		config.configure(RESET_BOOTH)
			.substateOf(RUNNING)
			.permit(ADVANCE,  ATTRACTING)
			.onEntry(() -> System.out.println("Entering Superstate RESET_BOOTH"))
			.onEntry(actions::resetBooth)
			.onExit(actions::resetBoothExit)	
			;
		
		/* Some twat has put cards in the wrong place at the wrong time */
		
		config.configure(FIX_PAST)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering FIX_PAST"))
			.permit(ADVANCE, REQUESTING_PAST)
			.onEntry(actions::fixSinglePlacement)
			.onExit(actions::fixSinglePlacementExit)
			;
		config.configure(FIX_PRESENT)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering FIX_PRESENT"))
			.permit(ADVANCE, REQUESTING_PRESENT)
			.onEntry(actions::fixSinglePlacement)
			.onExit(actions::fixSinglePlacementExit)
			;
		config.configure(FIX_FUTURE)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering FIX_FUTURE"))
			.permit(ADVANCE, REQUESTING_FUTURE)
			.onEntry(actions::fixSinglePlacement)
			.onExit(actions::fixSinglePlacementExit)
			;
		config.configure(FIX_JOCKER)
			.substateOf(RUNNING)
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
