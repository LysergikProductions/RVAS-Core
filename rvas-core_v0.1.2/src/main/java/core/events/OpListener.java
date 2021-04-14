package core.events;

import java.util.*;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
	
	// this happens *before* OP Lock will see the command,
	// making OPLock a great failsafe for rogue use of /op and /deop
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String sender_name = sender.getName();
		UUID sender_id = sender.getUniqueId();
		
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		// allow ops to use /execute, but only for teleporting between dimensions
		if (event.getMessage().startsWith("/execute in the_end run tp") ||
				event.getMessage().startsWith("/execute in the_nether run tp") ||
				event.getMessage().startsWith("/execute in overworld run tp") &&
				sender.isOp()) {
			return;
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!admin_name.equals(sender_name) || !admin_id.equals(sender_id)) {
			if (event.getMessage().contains("/op") ||
					event.getMessage().contains("/deop") ||
					event.getMessage().contains("/ban") ||
					event.getMessage().contains("/attribute") ||
					event.getMessage().contains("/default") ||
					event.getMessage().contains("/execute") ||
					event.getMessage().contains("/rl") ||
					event.getMessage().contains("/summon") ||
					event.getMessage().contains("/give") ||
					event.getMessage().contains("/setblock") ||
					event.getMessage().contains("/difficulty") ||
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
					event.getMessage().contains("/whitelist") ||
					event.getMessage().contains("/worldborder") ||
					event.getMessage().contains("/gamerule")) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent("no"));
				
			} else if (event.getMessage().contains("@a")) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent("You cannot target everyone!"));
				
			} else if (event.getMessage().contains(admin_name)) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent("You cannot target " + admin_name));
			}
		}
	}
	
	// stop every user/admin from changing game modes of non op players to anything other than survival mode
	@EventHandler
	public void onModeChange(PlayerGameModeChangeEvent event) {
		
		//Bukkit.spigot().broadcast(new TextComponent("PlayerGameModeChangeEvent triggered."));
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().isOp()) {
			
			event.setCancelled(true);
			//Bukkit.spigot().broadcast(new TextComponent("PlayerGameModeChangeEvent cancelled."));
		}
	}
	
	// Only allow admin to shift-duplicate items or take items from the creative inventory
	@EventHandler
	public void onCreativeEvent(InventoryCreativeEvent event) {
		
		if (!Config.getValue("protect.lock.creative").equals("false")) {
			
			HumanEntity player = event.getWhoClicked();
			String player_name = player.getName();
			UUID player_id = player.getUniqueId();
			
			String admin_name = Config.getValue("admin");
			UUID admin_id = UUID.fromString(Config.getValue("adminid"));
			
			//Bukkit.spigot().broadcast(new TextComponent("InventoryCreativeEvent triggered."));
			if (!admin_name.equals(player_name) || !admin_id.equals(player_id)) {
				
				event.setCancelled(true);
				
				if (!player.isOp()) {
					player.setGameMode(GameMode.SURVIVAL);
				}			
				//Bukkit.spigot().broadcast(new TextComponent("InventoryCreativeEvent was cancelled for " + player_name));
			}
		}
	}
}
