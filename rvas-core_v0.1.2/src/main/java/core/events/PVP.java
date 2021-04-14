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

public class PVP implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		
		if (Config.getValue("debug").equals("true")) {
			System.out.println("[core.events.pvp] onKill has been called");
		}
		
		Player killed = event.getEntity();
		UUID killedID = killed.getUniqueId();
		Player killer = killed.getKiller();
		UUID killerID = killer.getUniqueId();
		
		String killedName = killed.getName();
		String killerName = "";
		String killerLoc = "";
		
		if (killer != null) {
			killerName = killer.getName();
			killerLoc = killer.getLocation().getX()+", "+killer.getLocation().getY()+", "+killer.getLocation().getZ();
			if (Config.getValue("debug").equals("true")) {
				System.out.println("[core.events.pvp] "+killerName+" "+killedName+" "+killerLoc);
			}
		} else {
			if (Config.getValue("debug").equals("true")) {
				System.out.println("[core.events.pvp] killer = null");
			}
		}
		
		PlayerMeta.incKillTotal(killer, 1);
		PlayerMeta.incDeathTotal(killed, 1);
		
		OfflinePlayer p = Bukkit.getOfflinePlayer(killer.getName());
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core.events.pvp] "+killerName+" killed "+killedName+" from "+killerLoc);
				System.out.println("[core.events.pvp] Incrementing killTotal for "+killerName);
				
				int kills = PlayerMeta.getStats(p).killTotal;
				System.out.println("[core.events.pvp] "+killerName+"'s killTotal: "+kills);
			}
		} else if (Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core] "+killerName+" killed "+killedName+" from "+killerLoc);
			}
		}
	}
}
