import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import org.overworld.tarotbooth.StateMachineConfiguration.Trigger;

import com.github.oxo42.stateless4j.StateMachine;

@Component
public class TimeoutService {

	private Instant poked = new Instant();
	
	@Autowired
	StateMachine<S, T> stateMachine;
	
	@Setter
	@Getter
	private Duration timeout = Duration.of(1, TimeUnit.MINUTES);
	
	@Scheduled(fixedRate = 5000)
	public void checkTimeout() {
		
		if (this.poked.isBefore(Instant.now().minus(timeout))) {
			stateMachine.fire(Trigger.TIMEOUT);
		}
	}
	
	public synchronized void poke() {
		
		poked = Instant.now();
	}
}
