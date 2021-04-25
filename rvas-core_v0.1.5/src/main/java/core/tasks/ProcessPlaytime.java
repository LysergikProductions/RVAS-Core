package core.tasks;

import core.events.Chat;
import core.events.ChunkListener;

import core.backend.Config;
import core.backend.LagProcessor;
import core.backend.PlayerMeta;
import core.backend.Scheduler;
import core.backend.ServerMeta;
import core.backend.Utilities;
import core.commands.VoteMute;

import java.util.TimerTask;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Playtime processor (every 20 ticks)
public class ProcessPlaytime extends TimerTask {
	private static long lastTime = 0;
	private static long lastHour = 0;

	public static long lowTpsCounter = 0;
	private static long timeTillReset = 3600000;
	
	public static int currentNewChunks = 0;
	public static int lastNewChunks = 0;

	@Override
	public void run() {
		
		currentNewChunks = ChunkListener.newCount;
		
		if (currentNewChunks - lastNewChunks > 320) {
			System.out.println(
					"!!!WARNING: more than 16 chunks per tick on average are being generated every second!!!");
		}
		
		lastNewChunks = currentNewChunks;
		
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
			lastHour = System.currentTimeMillis();
			return;
		}

		long sinceLast = System.currentTimeMillis() - lastTime;

		// Tick playtime and temporary mutes
		Bukkit.getOnlinePlayers().forEach(p -> PlayerMeta.tickPlaytime(p, sinceLast));
		PlayerMeta.tickTempMutes(sinceLast);

		// Tick server uptime and reconnect delays
		ServerMeta.tickUptime(sinceLast);
		ServerMeta.tickRcDelays(sinceLast);

		if (System.currentTimeMillis() - lastHour >= 3600000) {
			lastHour = System.currentTimeMillis();

			Chat.violationLevels.clear();
			VoteMute.clear();
		}

		// Check if we need a restart		
		Double rThreshold = Double.parseDouble(Config.getValue("restart.threshold"));
		if (LagProcessor.getTPS() < rThreshold) {
			lowTpsCounter += sinceLast;
			if (lowTpsCounter >= 300000) {
				Utilities.restart(true);
			}
		}

		timeTillReset = timeTillReset - sinceLast;

		if (timeTillReset <= 0) {
			lowTpsCounter = 0;
			timeTillReset = 3600000;
		}

		lastTime = System.currentTimeMillis();

		// Log this
		Scheduler.setLastTaskId("oneSecondTasks");
	}
}