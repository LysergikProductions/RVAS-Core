package protocol3.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import protocol3.Main;
import protocol3.backend.Config;
import protocol3.backend.LagProcessor;
import protocol3.backend.Pair;
import protocol3.backend.ServerMeta;
import protocol3.commands.Admin;
import net.md_5.bungee.api.chat.TextComponent;

public class SpeedLimit implements Listener
{
	// default: 10 second grace period
	private static final int GRACE_PERIOD = 8;

	private static HashMap<UUID, Location> locs = new HashMap<UUID, Location>();
	private static List<UUID> tped = new ArrayList<UUID>();
	private static HashMap<UUID, Integer> gracePeriod = new HashMap<UUID, Integer>();
	private static long lastCheck = -1;
	private static HashMap<String, Double> speeds = new HashMap<String, Double>();

	public static int totalKicks = 0;

	// Speedlimit monitor
	public static void scheduleSlTask()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {

			if (lastCheck < 0) {
				lastCheck = System.currentTimeMillis();
				return;
			}

			long now = System.currentTimeMillis();
			double duration = (now - lastCheck) / 1000.0;
			lastCheck = now;

			double tps = LagProcessor.getTPS();

			double allowed = (tps >= 15.0) ? Integer.parseInt(Config.getValue("speedlimit.tier_one"))
					: Integer.parseInt(Config.getValue("speedlimit.tier_two"));

			double medium_kick = Integer.parseInt(Config.getValue("speedlimit.medium_kick"));
			double hard_kick = Integer.parseInt(Config.getValue("speedlimit.hard_kick"));

			speeds.clear();

			Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp()).forEach(player -> {
						// updated teleported player position
						if (tped.contains(player.getUniqueId())) {
							tped.remove(player.getUniqueId());
							locs.put(player.getUniqueId(), player.getLocation().clone());
							return;
						}

						// set previous location if it doesn't exist and bail
						Location previous_location = locs.get(player.getUniqueId());
						if (previous_location == null) {
							locs.put(player.getUniqueId(), player.getLocation().clone());
							return;
						}
						Location new_location = player.getLocation().clone();
						if (new_location.equals(previous_location)) {
							return;
						}
						new_location.setY(previous_location.getY()); // only consider movement in X/Z

						if (previous_location.getWorld() != new_location.getWorld())
						{
							locs.remove(player.getUniqueId());
							return;
						}

						Integer grace = gracePeriod.get(player.getUniqueId());
						if (grace == null) {
							grace = GRACE_PERIOD;
						}

						Vector v = new_location.subtract(previous_location).toVector();
						double speed = Math.round(v.length() / duration * 10.0) / 10.0;
						
						if(speed > allowed+1 && (Config.getValue("speedlimit.agro").equals("true") || Admin.disableWarnings)) {
							ServerMeta.kickWithDelay(player,
									Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
							totalKicks++;
							return;
						}

						// insta-kick above hard kick speed
						if (speed > hard_kick)
						{
							gracePeriod.put(player.getUniqueId(), GRACE_PERIOD);
							ServerMeta.kickWithDelay(player,
									Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
							totalKicks++;
							return;
						}

						// medium-kick: set grace period to 2 sec
						if (speed > medium_kick)
						{
							if (grace > 2)
								grace = 2;
						}

						// player is going too fast, warn or kick
						// +1 for leniency
						if (speed > allowed+1)
						{
							if (grace == 0) {
								gracePeriod.put(player.getUniqueId(), GRACE_PERIOD);
								ServerMeta.kickWithDelay(player,
										Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
								totalKicks++;
								return;
							} else {
								// display speed with one decimal
								player.spigot().sendMessage(new TextComponent("§4Your speed is " + speed + ", speed limit is " + allowed + ". Slow down or be kicked in " + grace + " second" + (grace == 1 ? "" : "s")));
							}

							--grace;
							gracePeriod.put(player.getUniqueId(), grace);
						}

						// player isn't going too fast, reset grace period
						else {
							if (grace < GRACE_PERIOD)
								++grace;
						}

						gracePeriod.put(player.getUniqueId(), grace);
						locs.put(player.getUniqueId(), player.getLocation().clone());
						speeds.put(player.getName(), speed);
					});
		}, 20L, 20L);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		tped.remove(e.getPlayer().getUniqueId());
		locs.remove(e.getPlayer().getUniqueId());
	}

	/* get speeds sorted from fastest to lowest */
	public static List< Pair<Double,String> > getSpeeds()
	{
		// create a list from the speeds map
		List<Map.Entry<String, Double> > list =
			new ArrayList<Map.Entry<String, Double> >(speeds.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
			@Override
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// format them into speed strings
		List< Pair<Double, String> > ret = new ArrayList< Pair<Double, String> >();
		for (Map.Entry<String, Double> aa : list) {
			ret.add(new Pair<Double, String>(aa.getValue(), aa.getKey()));
		}
		return ret;
	}
}
