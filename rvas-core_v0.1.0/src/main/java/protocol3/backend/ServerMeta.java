package protocol3.backend;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class ServerMeta {
	// -- SERVER STATISTICS -- //

	private static double _uptimeInSeconds = 0;

	public static void tickUptime(double msToAdd)
	{
		_uptimeInSeconds += msToAdd / 1000;
	}

	public static double getUptime()
	{
		return _uptimeInSeconds;
	}

	public static boolean doAnalytics()
	{
		return Config.getValue("analytics.enabled") == "true";
	}

	// -- RC BLOCKING -- //

	private static HashMap<UUID, Double> Delays = new HashMap<UUID, Double>();
	private static HashMap<UUID, Double> InitialDelays = new HashMap<UUID, Double>();

	public static void kickWithDelay(Player p, double delay) {
		Delays.put(p.getUniqueId(), 0D);
		InitialDelays.put(p.getUniqueId(), delay);
		p.kickPlayer("§6You have lost connection to the server.");
	}
	
	public static void preventReconnect(Player p, double delay) {
		Delays.put(p.getUniqueId(), 0D);
		InitialDelays.put(p.getUniqueId(), delay);
	}

	public static boolean canReconnect(Player p) {
		if (Delays.containsKey(p.getUniqueId())) {
			Delays.put(p.getUniqueId(), 0D);
			return false;
		}
		return true;
	}

	public static void tickRcDelays(double elapsed) {
		for (UUID u : Delays.keySet()) {
			double oldValue = Delays.get(u);
			Delays.put(u, oldValue + elapsed);
			if (oldValue + elapsed >= InitialDelays.get(u)) {
				Delays.remove(u);
				InitialDelays.remove(u);
			}
		}
	}

}
