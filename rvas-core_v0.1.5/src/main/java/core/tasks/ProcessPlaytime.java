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
	
	public static long lowTpsCounter = 0;
	
	private static long lastTime = 0;
	private static long lastHour = 0;
	private static long timeTillReset = 3600000;
	
	private static double onlinePlayers = 0;
	private static int currentNewChunks = 0;
	private static int lastNewChunks = 0;
	
	private static double lastTPS = 0.00;
	private static double currentTPS = 0.00;
	private static double difference = 0.00;	
	
	@Override
	public void run() {
		
		currentNewChunks = ChunkListener.newCount;
		onlinePlayers = (double)Bukkit.getOnlinePlayers().size();
		
		if ((currentNewChunks - lastNewChunks) / onlinePlayers > 160.0) {
			System.out.println(
					"WARN more than 8 chunks per tick per player on average are being generated every second");
		}
		
		lastNewChunks = currentNewChunks;
		
		currentTPS = LagProcessor.getTPS();
		
		if (lastTPS == 0.00) {difference = 0.00;}
		else {difference = lastTPS - currentTPS;}
		
		// TODO: this line will be used to trigger LagManager.lagFinder()
		if (difference > (lastTPS*0.5)) {
			System.out.println("WARN 50+% tps drop in 20t");
			Analytics.capture();
		}
		
		lastTPS = currentTPS;
		
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
		if (currentTPS < rThreshold) {
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