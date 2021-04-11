package core.events;

import java.util.*;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import core.backend.PlayerMeta;
import core.backend.Config;

public class OpListener implements Listener {
	
	// prevent ops from using certain commands
	// this happens *before* OP Lock sees the message,
	// making OPLock a great failsafe for rogue use of /op and /deop
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		String sender_name = event.getPlayer().getName();
		UUID sender_id = event.getPlayer().getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		if (!admin_name.equals(sender_name) || !admin_id.equals(sender_id)) {
			if (event.getMessage().contains("/op") ||
					event.getMessage().contains("/deop") ||
					event.getMessage().contains("/execute") ||
					event.getMessage().contains("/summon") ||
					event.getMessage().contains("/give") ||
					event.getMessage().contains("/set") ||
					event.getMessage().contains("/replace") ||
					event.getMessage().contains("/enchant") ||
					event.getMessage().contains("/time") ||
					event.getMessage().contains("/weather") ||
					event.getMessage().contains("/schedule") ||
					event.getMessage().contains("/data") ||
					event.getMessage().contains("/fill") ||
					event.getMessage().contains("/save") ||
					event.getMessage().contains("/loot") ||
					event.getMessage().contains("/experience") ||
					event.getMessage().contains("/forceload") ||
					event.getMessage().contains("/function") ||
					event.getMessage().contains("/spreadplayers") ||
					event.getMessage().contains("/xp") ||
					event.getMessage().contains("/reload") ||
					event.getMessage().contains("/gamerule")) {
				
				event.setCancelled(true);
				event.getPlayer().spigot().sendMessage(new TextComponent("no"));
			}
		}
	}
	
	// stop every user/admin from changing game modes of non op players to anything other than survival mode
	@EventHandler
	public void onModeChange (PlayerGameModeChangeEvent event) {
		//Bukkit.spigot().broadcast(new TextComponent("PlayerGameModeChangeEvent triggered."));
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().isOp()) {
			
			event.setCancelled(true);
			//Bukkit.spigot().broadcast(new TextComponent("PlayerGameModeChangeEvent cancelled."));
		}
	}
	
	// Only allow admin to shift-duplicate items or take items from the creative inventory
	@EventHandler
	public void onCreativeEvent (InventoryCreativeEvent event) {
		
		String player_name = event.getWhoClicked().getName();
		UUID player_id = event.getWhoClicked().getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		//Bukkit.spigot().broadcast(new TextComponent("InventoryCreativeEvent triggered."));
		if (!admin_name.equals(player_name) || !admin_id.equals(player_id)) {
			event.setCancelled(true);
			//Bukkit.spigot().broadcast(new TextComponent("InventoryCreativeEvent was cancelled for " + player_name));
		}
	}
}
