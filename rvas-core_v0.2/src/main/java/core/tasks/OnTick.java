package core.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import core.backend.Config;
import core.backend.Scheduler;
import core.commands.VoteMute;
import org.bukkit.Location;
import org.bukkit.Material;

// Tps processor
public class OnTick extends TimerTask {

	public static Map<Location, Material> blocksToFix = new HashMap<>();

	@Override
	public void run() {
		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");

		if (blocksToFix.size() > 64) blocksToFix.clear();

		try {
			if (fixGhosts() && Config.debug) System.out.println("Fixed Ghosts!");
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	private boolean fixGhosts() {
		if (blocksToFix.size() == 0) return false;

		try {
			for (Location loc: blocksToFix.keySet()) {

				loc.getBlock().setType(blocksToFix.get(loc));
				blocksToFix.remove(loc);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
