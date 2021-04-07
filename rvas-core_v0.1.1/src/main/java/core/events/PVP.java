package core.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityResurrectEvent;

import core.backend.Config;
import core.backend.PlayerMeta;

public class PVP implements Listener {
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		String killed = e.getEntity().getName();
		String killer = e.getEntity().getKiller().getName();
		String killerLoc = e.getEntity().getKiller().getLocation().getX()+", "+e.getEntity().getKiller().getLocation().getY()+", "+e.getEntity().getKiller().getLocation().getZ();
	
		//e.setDeathMessage(ChatColor.RED + killed + " has been slain by " + killer);
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core.events.pvp] "+killer+" killed "+killed+" from "+killerLoc);
				System.out.println("[core.events.pvp] Incrementing killTotal for "+killer);
			}
		} else if (Config.getValue("devesp").equals("false")) {
			if (killed != null) {
				System.out.println("[core] "+killer+" killed "+killed+" from "+killerLoc);
			}
		}
		if (killed && killer != null) {
			PlayerMeta.incKillTotal(e.getEntity().getKiller());
		}
	}
}