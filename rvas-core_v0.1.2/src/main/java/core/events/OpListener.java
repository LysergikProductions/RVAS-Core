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
	
	public static ArrayList<String> OwnerCommands = new ArrayList<>(); {
		OwnerCommands.addAll(Arrays.asList(
				"/op", "/deop", "/ban", "/attribute", "/default", "/execute", "/rl",
				"/summon", "/give", "/set", "/difficulty", "/replace", "/enchant",
				"/function", "/bukkit", "/time", "/weather", "/schedule", "/clone",
				"/data", "/fill", "/save", "/oplock", "/loot", "/default", "/minecraft",
				"/experience", "/forceload", "/function", "/spreadplayers", "/xp",
				"/reload", "/whitelist", "/packet", "/protocol", "/plugins", "/spigot",
				"/restart", "/world", "/gamerule"));
	};
	
	// this happens *before* the OP Lock plugin will see the command
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String sender_name = sender.getName();
		UUID sender_id = sender.getUniqueId();
		
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		// allow ops to use /execute, but only for teleporting between dimensions
		if (
				event.getMessage().startsWith("/execute in the_end run tp") ||
				event.getMessage().startsWith("/execute in the_nether run tp") ||
				event.getMessage().startsWith("/execute in overworld run tp") &&
				!event.getMessage().contains("@a") && !event.getMessage().contains(admin_name) &&
				sender.isOp()) {
			return;
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!admin_name.equals(sender_name) || !admin_id.equals(sender_id)) {
			if (OwnerCommands.contains(event.getMessage())) {
				
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
	
	@EventHandler // non-op players cannot be set to a mode besides survival mode
	public void onModeChange(PlayerGameModeChangeEvent event) {
		
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler // only allow owner account to duplicate/get items from creative mode
	public void onCreativeEvent(InventoryCreativeEvent event) {
		
		if (!Config.getValue("protect.lock.creative").equals("false")) {
			
			HumanEntity player = event.getWhoClicked();
			String player_name = player.getName();
			UUID player_id = player.getUniqueId();
			
			String admin_name = Config.getValue("admin");
			UUID admin_id = UUID.fromString(Config.getValue("adminid"));
			
			if (!admin_name.equals(player_name) || !admin_id.equals(player_id)) {
				
				event.setCancelled(true);
				
				if (!player.isOp()) {
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
		}
	}
}
