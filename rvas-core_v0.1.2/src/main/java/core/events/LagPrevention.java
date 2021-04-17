package core.events;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.World.Environment;

import core.backend.Config;

public class LagPrevention implements Listener, Runnable {
	public static int currentWithers = 0;

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {

		if (e.getEntity() instanceof Wither) {
			
			int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
			currentWithers = getWithers();
			
			if (e.getEntity().getTicksLived() > 200) return;
			
			if (currentWithers + 1 > witherLimit) {
				e.setCancelled(true);
				return;
			}
		}
	}

	public static int getWithers() { // disabled for performance reasons; to reimplement later
		/*
		ArrayList<String> worldTypes = new ArrayList<>();
		worldTypes.add("world");// check compatibility for world names other than "world"
		worldTypes.add("world_nether");// check compatibility for world names other than "world"
		worldTypes.add("world_the_end");// check compatibility for world names other than "world"
		final int[] toRet = {0};
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));

		final List<Entity> entities = new ArrayList<>();
		worldTypes.forEach(worldType -> {
				entities.clear();
				entities.addAll(Bukkit.getWorld(worldType).getEntities().stream().filter(e -> (e instanceof Wither))
						.collect(Collectors.toList()));
				toRet[0]=0;
				entities.stream().filter(e -> e.getType().equals(EntityType.WITHER) && e.getCustomName() == null).forEach(e -> {
					toRet[0]++;
					if (toRet[0] > witherLimit) {
						toRet[0]--;
						Wither w = (Wither) e;
						w.setHealth(0);
					}
				});
		});*/
		return 0;
		//return toRet[0];
	}
	
	// clear skulls every 72,000 server-ticks (~ 1 to 2 hours)
	@Override
	public void run() {
		
		Bukkit.getServer().broadcastMessage("Clearing wither skulls because lag sucks");
		
		for (Player onlinePlayer: Bukkit.getServer().getOnlinePlayers()) {
			if (onlinePlayer.isOp()) {
				onlinePlayer.chat("/kill @e[type=minecraft:wither_skull]");
				return;
			}
		}
	}
}
