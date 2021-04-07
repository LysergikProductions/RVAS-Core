package core.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDeathEvent;

import core.backend.Config;
import core.backend.PlayerMeta;

@EventHandler
public void onKill(PlayerDeathEvent e) {
	String killed = e.getEntity().getName();
	String killer = e.getEntity().getKiller().getName();
	//e.setDeathMessage(ChatColor.RED + killed + " has been slain by " + killer);
	
	if(Config.getValue("debug").equals("true")) {
		if(player != null) {
			System.out.println("[core.backend.pvp] "+killed+" slain by "+killer+" near "+killer.getLocation().getX()+", "+player.getLocation().getY()+", "+player.getLocation().getZ());
		}
	}
}
