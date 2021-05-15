package core.tasks;

import core.backend.Scheduler;
import core.commands.VoteMute;

import java.util.TimerTask;

// Tps processor
public class OnTick extends TimerTask {

	@Override
	public void run() {

		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");
	}
}
