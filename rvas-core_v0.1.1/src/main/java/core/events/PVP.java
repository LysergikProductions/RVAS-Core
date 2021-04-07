package core.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.event.entity.PlayerQuitEvent;
//import org.bukkit.event.entity.EntityResurrectEvent;

import core.backend.Config;
import core.backend.PlayerMeta;

public class PVP implements Listener {
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killed = e.getEntity();
		Player killer = killed.getKiller();
		
		String killedName = killed.getName();
		String killerName = killer.getName();
		String killerLoc = killer.getLocation().getX()+", "+killer.getLocation().getY()+", "+killer.getLocation().getZ();
	
		//e.setDeathMessage(ChatColor.RED + your text here);
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core.events.pvp] "+killerName+" killed "+killedName+" from "+killerLoc);
				System.out.println("[core.events.pvp] Incrementing killTotal for "+killerName);
			}
		} else if (Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core] "+killerName+" killed "+killedName+" from "+killerLoc);
			}
		}
		PlayerMeta.incKillTotal(killer, 1);
	}
}
