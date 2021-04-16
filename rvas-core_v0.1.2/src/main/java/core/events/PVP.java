package core.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.UUID;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.Bukkit;

import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.Utilities;

public class PVP implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			System.out.println("[core.events.pvp] onKill has been called");
		}
		
		Player killed = event.getEntity();
		UUID killedID = killed.getUniqueId();
		Player killer = killed.getKiller();
		UUID killerID = killer.getUniqueId();
		
		String killedName = killed.getName();
		String killerName = "";
		String killerLoc = "";
		
		if (killer != null && Config.getValue("debug").equals("true")) {
			
			killerName = killer.getName();
			killerLoc = killer.getLocation().getX()+", "+killer.getLocation().getY()+", "+killer.getLocation().getZ();
			
			System.out.println("[core.events.pvp] "+killerName+" "+killedName+" "+killerLoc);
			
			// increment appropriate stats
			PlayerMeta.incKillTotal(killer, 1);
			PlayerMeta.incDeathTotal(killed, 1);
			
		} else if (Config.getValue("debug").equals("true")){
			
			System.out.println("[core.events.pvp] killer = null");
		}
		
		// check if victim was in the spawn region on death
		int victim_playtime = Integer.parseInt(Utilities.calculateTime(PlayerMeta.getPlaytime(killed)));
		
		Double cX = killed.getLocation().getX();
		Double cZ = killed.getLocation().getZ();
		
		if (cX == null || cZ == null) {
			
			System.out.println("[core.events.PVP] failed to retrieve location for victim: " + killedName);
			return;
			
		} else if (cX < 710 && cZ < 710 && cX > -710 && cZ > -710) { // spawn region will eventually be config defined
			
			System.out.println(killedName + " was killed in the spawn region!");
			// check if victim is a new player
			if (victim_playtime < 3600 && killer != null) {
				
				System.out.println(killedName + " was also a new player!");
				PlayerMeta.incSpawnKill(killer, 1);
			}
		}
	}
}
