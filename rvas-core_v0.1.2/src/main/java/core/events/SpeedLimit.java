package core.events;

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

import core.Main;
import core.backend.Config;
import core.backend.LagProcessor;
import core.backend.Pair;
import core.backend.ServerMeta;
import core.commands.Admin;
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
	public static void scheduleSlTask() {
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {

			if (lastCheck < 0) {
				lastCheck = System.currentTimeMillis();
				return;
			}
			
			double tier1 = Double.parseDouble(Config.getValue("speedlimit.tier_one"));
			double tier2 = Double.parseDouble(Config.getValue("speedlimit.tier_two"));
			double tier3 = Double.parseDouble(Config.getValue("speedlimit.tier_three"));
			double tier4 = Double.parseDouble(Config.getValue("speedlimit.tier_four"));
			double tier5 = Double.parseDouble(Config.getValue("speedlimit.tier_five"));
			double medium_kick = Integer.parseInt(Config.getValue("speedlimit.medium_kick"));
			double hard_kick = Integer.parseInt(Config.getValue("speedlimit.hard_kick"));
			final double speed_limit;

			long now = System.currentTimeMillis();
			double duration = (now - lastCheck) / 1000.0;
			lastCheck = now;

			double tps = LagProcessor.getTPS();
			
			if (tps >= 16.0) {
				speed_limit = tier1;
				
			} else if (tps < 16.0 && tps >= 14.0) {
				speed_limit = tier2;
				
			} else if (tps < 14.0 && tps >= 10.0) {
				speed_limit = tier3;
				
			} else if (tps < 10.0 && tps >= 7.0) {
				speed_limit = tier4;
				
			} else if (tps < 7) {
				speed_limit = tier5;
			} else {
				speed_limit = tier1;
			}

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
				
				if(speed > speed_limit+ 1 && (Config.getValue("speedlimit.agro").equals("true") || Admin.disableWarnings)) {
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
				if (speed > speed_limit+1)
				{
					if (grace == 0) {
						gracePeriod.put(player.getUniqueId(), GRACE_PERIOD);
						ServerMeta.kickWithDelay(player,
								Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
						totalKicks++;
						return;
					} else {
						// display speed with one decimal
						player.spigot().sendMessage(new TextComponent("§4Your speed is " + speed + ", speed limit is " + speed_limit + ". Slow down or be kicked in " + grace + " second" + (grace == 1 ? "" : "s")));
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
