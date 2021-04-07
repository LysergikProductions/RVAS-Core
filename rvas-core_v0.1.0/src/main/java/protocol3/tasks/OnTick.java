package protocol3.tasks;

import java.util.TimerTask;

import protocol3.backend.Scheduler;
import protocol3.commands.VoteMute;

// Tps processor
public class OnTick extends TimerTask {
	public static int lowTpsCounter = 0;
	public static int timeTillReset = 1200;

	@Override
	public void run() {
		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");
	}
}