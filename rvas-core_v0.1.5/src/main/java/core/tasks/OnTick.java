package core.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import core.backend.Config;
import core.backend.Scheduler;
import core.commands.VoteMute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

// Tps processor
public class OnTick extends TimerTask {

	public static Map<Location, Material> blocksToFix = new HashMap<>();

	public static int lowTpsCounter = 0;
	public static int timeTillReset = 1200;

	public static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));

	@Override
	public void run() {
		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");

		if (blocksToFix.size() > 64) blocksToFix.clear();

		try {
			if (fixGhosts() && debug) System.out.println("Fixed Ghosts!");
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

	public static boolean updateConfigs() {

		try {
			debug = Boolean.parseBoolean(Config.getValue("debug"));
			return true;

		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
