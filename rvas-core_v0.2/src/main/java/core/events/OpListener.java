package core.events;

/* *
 * 
 *  About: Ensure only configured 'admin' account has overpowered
 *  	abilities, allowing ops to only do things that cannot permanently
 *  	and/or negatively affect the world or gameplay; for RVAS-core
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

import core.backend.PlayerMeta;
import core.backend.Config;
import core.backend.Utilities;
import core.backend.Aliases;

import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.inventory.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class OpListener implements Listener {
	
	// currently not in use
	public static ArrayList<String> OwnerCommands = new ArrayList<>(); static {
		OwnerCommands.addAll(Arrays.asList(
				"/op", "/deop", "/ban", "/attribute", "/default", "/execute", "/rl",
				"/summon", "/give", "/set", "/difficulty", "/replace", "/enchant",
				"/function", "/bukkit", "/time", "/weather", "/schedule", "/clone",
				"/data", "/fill", "/save", "/oplock", "/loot", "/default", "/minecraft",
				"/experience", "/forceload", "/function", "/spreadplayers", "/xp",
				"/reload", "/whitelist", "/packet", "/protocol", "/plugins", "/spigot",
				"/restart", "/worldb", "/gamerule", "/score", "/tell", "/dupe", "/global"));
	}

	public static boolean isSauceInitialized = false;

	public static String armor_a; public static String armor_b;
	public static String totems_armor1; public static String totems_armor2;
	public static String feather_32k; public static String totems_shulker;

	// this happens *before* the OP Lock plugin will see the command
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String admin_name = Config.getValue("admin");
		
		boolean isAdmin = PlayerMeta.isAdmin(sender);
		String msg = event.getMessage();
		
		// allow ops to use /execute, but only for teleporting between dimensions
		if (
				msg.startsWith("/execute in the_end run tp") ||
				msg.startsWith("/execute in the_nether run tp") ||
				msg.startsWith("/execute in overworld run tp")) {
			
			if (!msg.contains("@a") && !msg.contains(admin_name) && sender.isOp()) {
				return;
			}
		}
		
		// take-over handling of /lr when receiving /lr skulls (lr normally for 'LaggRemover')
		if (msg.startsWith("/lr skulls")) {
			
			event.setCancelled(true);
			if (sender.isOp()) sender.chat("/kill @e[type=minecraft:wither_skull]");
		}
		
		if (msg.startsWith("/lr items")) {
			
			event.setCancelled(true);
			if (sender.isOp()) {
				int removed_items = Utilities.clearChunkItems(sender.getLocation().getChunk());
				sender.spigot().sendMessage(new TextComponent("Removed " + removed_items + " item stacks."));
			}
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!isAdmin) {
			if (Utilities.isCmdRestricted(msg)) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "no"));
				
			} else if (msg.contains("@a")) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent("You cannot target everyone!"));
				
			} else if (msg.contains(admin_name)) {
				
				event.setCancelled(true);
				sender.spigot().sendMessage(new TextComponent("You cannot target " + admin_name));
			}

		// 32k commands for testing anti-illegals; owner only
		} else if (msg.startsWith("/op sauce")) {
			event.setCancelled(true);

			if (!sender.isOp()) return; // <- fallback security layer
			if (!isSauceInitialized) Aliases.initSauce();

			if (!msg.contains("=") || msg.endsWith("=")) {
				sender.spigot().sendMessage(new TextComponent("Syntax: /op sauce=[type]"));

			} else {
				String[] args = msg.split("=");
				String thisArg = args[1];

				if (Config.verbose) System.out.println(thisArg);

				if (thisArg.contains("armor")) {
					sender.chat(armor_a);
					sender.chat(armor_b);
					sender.chat(totems_armor1);
					sender.chat(totems_armor2);

				} else if (thisArg.contains("feather")) {
					sender.chat(feather_32k);

				} else if (thisArg.contains("totem")) {
					sender.chat(totems_shulker);

				} else if (thisArg.contains("all")) {
					sender.chat(armor_a);
					sender.chat(armor_b);
					sender.chat(totems_armor1);
					sender.chat(totems_armor2);
					sender.chat(totems_shulker);
					sender.chat(feather_32k);

				} else {
					sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "Invalid Argument: " + thisArg));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // non-ops cannot be set to a mode besides survival mode
	public void onModeChange(PlayerGameModeChangeEvent event) {
		
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // only owner account can dupe/get items from creative mode
	public void onCreativeEvent(InventoryCreativeEvent event) {
		
		if (!Config.getValue("protect.lock.creative").equals("false")) {
			
			HumanEntity ePlayer = event.getWhoClicked();
			Player player = Bukkit.getPlayer(ePlayer.getUniqueId());

			assert player != null;
			if (!PlayerMeta.isAdmin(player)) {
				event.setCancelled(true);
				
				if (!player.isOp()) {
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
		}
	}
}
