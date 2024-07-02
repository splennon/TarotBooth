package org.overworld.tarotbooth;

import java.util.Optional;

import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.model.GameModel;
import org.overworld.tarotbooth.model.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ModelHealthService {

	@Autowired
	private EzzieMachine stateMachine;

	@Autowired
	private GameModel gameModel;

	@Scheduled(fixedRate = 2000)
	public void checkModel() {

		Optional<Position> err = gameModel.findError();
		if (err.isPresent()) {
			switch (err.get()) {
			case PAST:
				stateMachine.fire(Trigger.CORRUPT_PAST);
				break;
			case PRESENT:
				stateMachine.fire(Trigger.CORRUPT_PRESENT);
				break;
			default:
				break;
			}
		}
		
		if (gameModel.findDuplicate())
			stateMachine.fire(Trigger.CORRUPT_DUPLICATE);
	}
}
