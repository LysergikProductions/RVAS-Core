package core.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.UUID;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;

import core.backend.Config;
import core.backend.PlayerMeta;

public class PVP implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		
		if (Config.getValue("debug").equals("true")) {
			System.out.println("[core.events.pvp] onKill has been called");
		}
		
		Player killed = e.getEntity();
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
	
		//e.setDeathMessage(ChatColor.RED + your text here);
		PlayerMeta.incKillTotal(killer, 1);
		
		if (Config.getValue("debug").equals("true")) {
			System.out.println("(DEBUG) killed " + killed + ". Your new total is " + PlayerMeta.getKills(killer));
		}
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core.events.pvp] "+killerName+" killed "+killedName+" from "+killerLoc);
				System.out.println("[core.events.pvp] Incrementing killTotal for "+killerName);
				
				int kills = PlayerMeta.getKills(killer);
				System.out.println("[core.events.pvp] "+killerName+"'s killTotal: "+kills);
			}
		} else if (Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core] "+killerName+" killed "+killedName+" from "+killerLoc);
			}
		}
		
	}
}
