package core.events;

/* *
 *  About: Ensure only configured 'admin' account has overpowered
 *  	abilities, allowing ops to only do things that cannot permanently
 *  	and/or negatively affect the world or gameplay; for RVAS-core
 *
 * 		Also grants ops some extra abilities including patches to `/tp`
 * 		that allows for the saving of up to 10 unique privately saved locations per op
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

import core.backend.Config;
import core.backend.utils.Util;
import core.backend.utils.Chunks;
import core.backend.ex.Critical;
import core.frontend.ChatPrint;
import core.frontend.GUI.DonorList;

import core.data.PlayerMeta;
import core.data.objects.Pair;
import core.data.objects.Aliases;
import core.commands.op.Speeds;
import core.commands.op.Check;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@Critical
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
	@SuppressWarnings("SpellCheckingInspection")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String admin_name = Config.getValue("admin");
		UUID senderID = sender.getUniqueId();
		
		boolean isAdmin = PlayerMeta.isAdmin(sender);
		String msg = event.getMessage();

		// ignore (and allow) /execute when used specifically for teleporting
		if (
				msg.startsWith("/execute in the_end run tp") ||
				msg.startsWith("/execute in the_nether run tp") ||
				msg.startsWith("/execute in overworld run tp") ||
				msg.startsWith("/execute in minecraft:the_end run tp") ||
				msg.startsWith("/execute in minecraft:the_nether run tp") ||
				msg.startsWith("/execute in minecraft:overworld run tp")) {
			
			if (!msg.contains("@a") && !msg.contains(admin_name) && sender.isOp()) return;
		}
		
		// take-over handling of /lr when receiving /lr skulls (lr normally for 'LaggRemover' Plugin)
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
				sender.sendMessage(ChatPrint.fail + "Oops, " + thisIndexStr + " is not a number lol");
				thisIndexInt = null;
			}

			if (thisIndexInt != null && thisIndexInt >= 0 && thisIndexInt <= 9) {
				Map<Integer, Location> newMap = savedTPs.getOrDefault(senderID, new HashMap<>());
				savedTPs.remove(senderID);

				newMap.remove(thisIndexInt);
				newMap.put(thisIndexInt, sender.getLocation());

				savedTPs.put(senderID, newMap);

				sender.sendMessage(ChatPrint.succeed + "Successfully saved location #" + thisIndexInt);

			} else sender.sendMessage(ChatPrint.fail + "Nononono, zeeeeeroooo to niiiiine");

		} else if (msg.startsWith("/tp:")) {
			event.setCancelled(true);

			String thisIndexStr = msg.split(":")[1];
			Integer thisIndexInt;

			try { thisIndexInt = Integer.parseInt(thisIndexStr);
			} catch (Exception ignore) {
				sender.sendMessage(ChatPrint.fail + "Please use a number from 0 to 9 to choose a location");
				thisIndexInt = null;
			}

			if (thisIndexInt != null && thisIndexInt >= 0 && thisIndexInt <= 9) {
				Location tpLoc; String dim;

				TextComponent warn = new TextComponent(
						ChatPrint.fail + "There is no saved TP at that index");

				try {
					tpLoc = savedTPs.get(senderID).get(thisIndexInt);
					if (tpLoc == null) { sender.sendMessage(warn.toLegacyText()); return; }
					dim = Util.getDimensionName(tpLoc);

				} catch (Exception ignore) { sender.sendMessage(warn.toLegacyText()); return; }

				String loc = tpLoc.getBlockX() + " " + tpLoc.getBlockY() + " " + tpLoc.getBlockZ();
				sender.chat("/execute in " + dim + " run tp @s " + loc);

			} else sender.sendMessage(ChatPrint.fail + "Nononono, zeeeeeroooo to niiiiine");
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!isAdmin) {
			if (msg.contains("/give") && Config.getValue("protect.ops.give").equals("true") ||
					Util.isCmdRestricted(msg)) { // <- LOCKS OUT DANGEROUS COMMANDS

				event.setCancelled(true);
				sender.sendMessage(ChatPrint.fail + "no");

			} else if (msg.contains("@a")) {
				event.setCancelled(true);
				sender.sendMessage(ChatPrint.fail + "You cannot target everyone!");
				
			} else if (msg.contains(admin_name)) {
				event.setCancelled(true);
				sender.sendMessage(ChatPrint.fail + "You cannot target " + admin_name);
			}

		// 32k commands for testing anti-illegals; owner only
		} else if (msg.startsWith("/op sauce")) {

			event.setCancelled(true); // <- cut-off default command processing
			if (!PlayerMeta.isAdmin(sender)) return; // <- fallback security layer

			if (!msg.contains("=") || msg.endsWith("=")) sender.sendMessage("Syntax: /op sauce=[type]");
			else {
				String[] args = msg.split("=");
				String thisArg = args[1];

				if (Config.verbose) System.out.println(thisArg);

				if (thisArg.contains("armor")) {
					sender.chat(Aliases.armor_a);
					sender.chat(Aliases.armor_b);
					sender.chat(Aliases.totems_armor1);
					sender.chat(Aliases.totems_armor2);
				}
				else if (thisArg.contains("feather")) sender.chat(Aliases.feather_32k);
				else if (thisArg.contains("totem")) sender.chat(Aliases.totems_shulker);

				else if (thisArg.contains("all")) {
					sender.chat(Aliases.armor_a);
					sender.chat(Aliases.armor_b);
					sender.chat(Aliases.totems_armor1);
					sender.chat(Aliases.totems_armor2);
					sender.chat(Aliases.totems_shulker);
					sender.chat(Aliases.feather_32k);

				} else sender.sendMessage(ChatPrint.fail + "Invalid Argument: " + thisArg);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // non-ops cannot be set to a mode besides survival mode
	public void onModeChange(PlayerGameModeChangeEvent event) {
		
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) &&
				!event.getPlayer().isOp()) { event.setCancelled(true); }
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
		Inventory thisInv = event.getClickedInventory();

		if (thisInv == Speeds.speedGUI) event.setCancelled(true);
		else if (thisInv == DonorList._donorGUI) event.setCancelled(true);
		else if (!event.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL) &&
				!PlayerMeta.isAdmin((Player)event.getWhoClicked())) { event.setCancelled(true); }
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryMove(InventoryMoveItemEvent event) {
		Inventory initiator = event.getInitiator();
		Inventory destination = event.getDestination();

		if (initiator == Speeds.speedGUI) event.setCancelled(true);
		else if (initiator == Check.lagCheckGUI) event.setCancelled(true);
		else if (initiator == DonorList._donorGUI) event.setCancelled(true);
		else if (destination == Speeds.speedGUI) event.setCancelled(true);
		else if (destination == Check.lagCheckGUI) event.setCancelled(true);
		else if (destination == DonorList._donorGUI) event.setCancelled(true);
	}
}
