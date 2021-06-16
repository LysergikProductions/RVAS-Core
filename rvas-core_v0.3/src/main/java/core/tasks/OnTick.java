package core.tasks;

import core.backend.Scheduler;
import core.commands.VoteMute;
import core.backend.ex.Critical;

import java.util.TimerTask;

@Critical
public class OnTick extends TimerTask {

	@Override
	public void run() {
		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");
	}
}
