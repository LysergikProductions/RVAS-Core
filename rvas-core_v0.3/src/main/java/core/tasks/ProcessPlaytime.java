package core.tasks;

import core.backend.Config;
import core.backend.Scheduler;
import core.backend.ServerMeta;
import core.backend.utils.Restart;
import core.backend.LagProcessor;

import core.data.PlayerMeta;
import core.events.ChatListener;
import core.commands.VoteMute;
import core.commands.restricted.Check;
import core.commands.restricted.Speeds;

import java.util.TimerTask;
import org.bukkit.Bukkit;

// Playtime processor (every 20 ticks)
public class ProcessPlaytime extends TimerTask {
	public static long lowTpsCounter = 0;

	private static long lastTime, lastHour = 0;
	private static long timeTillReset = 3600000;

	private static double lastTPS = 0.00;

	@Override
	public void run() {
		double currentTPS = LagProcessor.getTPS();
		double difference;

		if (lastTPS == 0.00) difference = 0.00;
		else difference = lastTPS - currentTPS;
		
		if (difference > (lastTPS * 0.5)) {
			System.out.println("WARN 50+% tps drop in 20t");
			Analytics.capture();
		}
		
		lastTPS = currentTPS;
		
		// get time since last tick in milliseconds
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
			lastHour = System.currentTimeMillis();
			return;
		}
		
		long sinceLast = System.currentTimeMillis() - lastTime;		
		if (sinceLast > 3000) Analytics.capture();

		// Tick playtime, temporary mutes, server uptime, and reconnect delays
		Bukkit.getOnlinePlayers().forEach(p -> PlayerMeta.tickPlaytime(p, sinceLast));
		PlayerMeta.tickTempMutes(sinceLast);

		ServerMeta.tickUptime(sinceLast);
		ServerMeta.tickRcDelays(sinceLast);

		if (System.currentTimeMillis() - lastHour >= 3600000) {
			lastHour = System.currentTimeMillis();

			ChatListener.violationLevels.clear();
			VoteMute.clear();
		}

		// Check if we need a restart		
		double rThreshold = Double.parseDouble(Config.getValue("restart.threshold"));
		if (currentTPS < rThreshold) {
			lowTpsCounter += sinceLast;
			if (lowTpsCounter >= 300000) Restart.restart(true);
		}

		timeTillReset = timeTillReset - sinceLast;

		if (timeTillReset <= 0) {
			lowTpsCounter = 0;
			timeTillReset = 3600000;
		}

		lastTime = System.currentTimeMillis();
		Scheduler.setLastTaskId("oneSecondTasks");

		// updateGUIs
		Speeds.updateGUI();
		Check.updateGUI();
	}
}
