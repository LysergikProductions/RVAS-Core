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

import core.backend.ChatPrint;
import core.backend.Config;
import core.backend.utils.Util;
import core.backend.utils.Chunks;
import core.commands.restricted.Speeds;
import core.commands.restricted.Check;

import core.data.PlayerMeta;
import core.data.objects.Pair;
import core.data.objects.Aliases;

import java.util.*;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;

import org.bukkit.event.inventory.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@SuppressWarnings("SpellCheckingInspection")
public class OpListener implements Listener {

	static Map<UUID, Pair<Location, Location>> lastTPs = new HashMap<>();
	static HashMap<UUID, Map<Integer, Location>> savedTPs = new HashMap<>();

	public static boolean isSauceInitialized = false;

	@EventHandler(priority = EventPriority.LOW)
	public void onTP(PlayerTeleportEvent event) {
		if (!event.getPlayer().isOp()) return;

		lastTPs.remove(event.getPlayer().getUniqueId());
		lastTPs.put(event.getPlayer().getUniqueId(), new Pair<>(event.getFrom(), event.getTo()));
	}

	// this happens *before* the OP Lock plugin will see the command
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String admin_name = Config.getValue("admin");
		UUID senderID = sender.getUniqueId();
		
		boolean isAdmin = PlayerMeta.isAdmin(sender);
		String msg = event.getMessage();
		
		// allow ops to use /execute, but only for teleporting between dimensions
		if (
				msg.startsWith("/execute in the_end run tp") ||
				msg.startsWith("/execute in the_nether run tp") ||
				msg.startsWith("/execute in overworld run tp") ||
				msg.startsWith("/execute in minecraft:the_end run tp") ||
				msg.startsWith("/execute in minecraft:the_nether run tp") ||
				msg.startsWith("/execute in minecraft:overworld run tp")) {
			
			if (!msg.contains("@a") && !msg.contains(admin_name) && sender.isOp()) return;
		}
		
		// take-over handling of /lr when receiving /lr skulls (lr normally for 'LaggRemover')
		if (msg.startsWith("/lr skulls")) {
			
			event.setCancelled(true);
			if (sender.isOp()) sender.chat("/kill @e[type=minecraft:wither_skull]");

		} else if (msg.startsWith("/lr items")) {
			
			event.setCancelled(true);
			if (sender.isOp()) {
				int removed_items = Chunks.clearChunkItems(sender.getLocation().getChunk());
				sender.sendMessage(ChatPrint.secondary + "Removed " + removed_items + " item stacks.");
			}
		}

		if (msg.startsWith("/tp back")) {
			event.setCancelled(true);

			Location lastLoc = lastTPs.get(senderID).getRight();
			String dim = Util.getDimensionName(lastLoc);
			String loc = lastLoc.getBlockX() + " " + lastLoc.getBlockY() + " " + lastLoc.getBlockZ();

			sender.chat("/execute in " + dim + " run tp @s " + loc);

		} else  if (msg.startsWith("/tp:save ")) {
			event.setCancelled(true);

			String thisIndexStr = msg.split(" ")[1];
			Integer thisIndexInt;

			try { thisIndexInt = Integer.parseInt(thisIndexStr);
			} catch (Exception ignore) {
				sender.sendMessage(new TextComponent(
						ChatPrint.fail + "Oops, " + thisIndexStr + " is not a number lol").toLegacyText());
				thisIndexInt = null;
			}

			if (thisIndexInt != null && thisIndexInt >= 0 && thisIndexInt <= 9) {
				Map<Integer, Location> newMap = savedTPs.getOrDefault(senderID, new HashMap<>());
				savedTPs.remove(senderID);

				newMap.remove(thisIndexInt);
				newMap.put(thisIndexInt, sender.getLocation());

				savedTPs.put(senderID, newMap);

				sender.sendMessage(new TextComponent(
						ChatPrint.succeed + "Successfully saved location #" + thisIndexInt).toLegacyText());

			} else sender.sendMessage(new TextComponent(
					ChatPrint.fail + "Nononono, zeeeeeroooo to niiiiine").toLegacyText());

		} else if (msg.startsWith("/tp:")) {
			event.setCancelled(true);

			String thisIndexStr = msg.split(":")[1];
			Integer thisIndexInt;

			try { thisIndexInt = Integer.parseInt(thisIndexStr);
			} catch (Exception ignore) {
				sender.sendMessage(new TextComponent(
						ChatPrint.fail + "Please use a number from 0 to 9 to choose a location").toLegacyText());
				thisIndexInt = null;
			}

			if (thisIndexInt != null && thisIndexInt >= 0 && thisIndexInt <= 9) {
				Location tpLoc;

				try {
					tpLoc = savedTPs.get(senderID).get(thisIndexInt);
				} catch (Exception ignore) {
					sender.sendMessage(new TextComponent(
							ChatPrint.fail + "There is no saved TP at that index").toLegacyText());
					return;
				}

				String dim = Util.getDimensionName(tpLoc);
				String loc = tpLoc.getBlockX() + " " + tpLoc.getBlockY() + " " + tpLoc.getBlockZ();

				sender.chat("/execute in " + dim + " run tp @s " + loc);

			} else sender.sendMessage(new TextComponent(
					ChatPrint.fail + "Nononono, zeeeeeroooo to niiiiine").toLegacyText());
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!isAdmin) {
			if (msg.contains("/give") && Config.getValue("protect.ops.give").equals("true") ||
					Util.isCmdRestricted(msg)) { // <- LOCKS OUT DANGEROUS COMMANDS

				event.setCancelled(true);
				sender.sendMessage(new TextComponent(ChatPrint.fail + "no").toLegacyText());

			} else if (msg.contains("@a")) {
				
				event.setCancelled(true);
				sender.sendMessage(new TextComponent(ChatPrint.fail + "You cannot target everyone!").toLegacyText());
				
			} else if (msg.contains(admin_name)) {
				
				event.setCancelled(true);
				sender.sendMessage(new TextComponent(ChatPrint.fail + "You cannot target " + admin_name).toLegacyText());
			}

		// 32k commands for testing anti-illegals; owner only
		} else if (msg.startsWith("/op sauce")) {

			event.setCancelled(true); // <- cut-off default command processing
			if (!sender.isOp()) return; // <- fallback security layer

			if (!msg.contains("=") || msg.endsWith("=")) {
				sender.sendMessage("Syntax: /op sauce=[type]");

			} else {
				String[] args = msg.split("=");
				String thisArg = args[1];

				if (Config.verbose) System.out.println(thisArg);

				if (thisArg.contains("armor")) {
					sender.chat(Aliases.armor_a);
					sender.chat(Aliases.armor_b);
					sender.chat(Aliases.totems_armor1);
					sender.chat(Aliases.totems_armor2);

				} else if (thisArg.contains("feather")) {
					sender.chat(Aliases.feather_32k);

				} else if (thisArg.contains("totem")) {
					sender.chat(Aliases.totems_shulker);

				} else if (thisArg.contains("all")) {
					sender.chat(Aliases.armor_a);
					sender.chat(Aliases.armor_b);
					sender.chat(Aliases.totems_armor1);
					sender.chat(Aliases.totems_armor2);
					sender.chat(Aliases.totems_shulker);
					sender.chat(Aliases.feather_32k);

				} else {
					sender.sendMessage(new TextComponent(ChatPrint.fail + "Invalid Argument: " + thisArg).toLegacyText());
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
			if (Config.getValue("protect.lock.creative").equals("true")) event.setCancelled(true);
			
			HumanEntity ePlayer = event.getWhoClicked();
			Player player = Bukkit.getPlayer(ePlayer.getUniqueId());

			if (player != null) {
				if (!PlayerMeta.isAdmin(player) && !player.isOp()) player.setGameMode(GameMode.SURVIVAL);
				else event.setCancelled(false);
			}
		}
	}

	// prevent moving GUI items into player inventories
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == Speeds.speedGUI) event.setCancelled(true);

		else if (!event.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL) &&
				!PlayerMeta.isAdmin((Player)event.getWhoClicked())) {

			event.setCancelled(true);
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryMove(InventoryMoveItemEvent event) {

		if (event.getInitiator() == Speeds.speedGUI) event.setCancelled(true);
		else if (event.getDestination() == Speeds.speedGUI) event.setCancelled(true);
		else if (event.getInitiator() == Check.lagCheckGUI) event.setCancelled(true);
		else if (event.getDestination() == Check.lagCheckGUI) event.setCancelled(true);
	}

	@Deprecated
	public static ArrayList<String> OwnerCommands = new ArrayList<>();/* static {
		OwnerCommands.addAll(Arrays.asList(
				"/op", "/deop", "/ban", "/attribute", "/default", "/execute", "/rl",
				"/summon", "/give", "/set", "/difficulty", "/replace", "/enchant",
				"/function", "/bukkit", "/time", "/weather", "/schedule", "/clone",
				"/data", "/fill", "/save", "/oplock", "/loot", "/default", "/minecraft",
				"/experience", "/forceload", "/function", "/spreadplayers", "/xp",
				"/reload", "/whitelist", "/packet", "/protocol", "/plugins", "/spigot",
				"/restart", "/worldb", "/gamerule", "/score", "/tell", "/dupe", "/global"));
	}*/
}
