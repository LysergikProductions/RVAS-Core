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

import java.util.*;
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

@SuppressWarnings("SpellCheckingInspection")
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
				"/restart", "/worldb", "/gamerule", "/score"));
	}
	
	// this happens *before* the OP Lock plugin will see the command
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preCommandSend(PlayerCommandPreprocessEvent event) {
		
		Player sender = event.getPlayer();
		String admin_name = Config.getValue("admin");
		
		boolean isAdmin = PlayerMeta.isAdmin(sender);
		
		// allow ops to use /execute, but only for teleporting between dimensions
		if (
				event.getMessage().startsWith("/execute in the_end run tp") ||
				event.getMessage().startsWith("/execute in the_nether run tp") ||
				event.getMessage().startsWith("/execute in overworld run tp")) {
			
			if (!event.getMessage().contains("@a") && !event.getMessage().contains(admin_name) && sender.isOp()) {
				return;
			}
		}
		
		// take-over handling of /lr when receiving /lr skulls (lr normally for 'LaggRemover')
		if (event.getMessage().startsWith("/lr skulls")) {
			
			event.setCancelled(true);
			if (sender.isOp()) sender.chat("/kill @e[type=minecraft:wither_skull]");
		}
		
		if (event.getMessage().startsWith("/lr items")) {
			
			event.setCancelled(true);
			if (sender.isOp()) {
				int removed_items = Utilities.clearChunkItems(sender.getLocation().getChunk());
				sender.spigot().sendMessage(new TextComponent("Removed " + removed_items + " item stacks."));
			}
		}
		
		// prevent ops from using certain commands, but allow for admin (config.txt)
		if (!isAdmin) {
			if (
					event.getMessage().contains("/op") ||
					event.getMessage().contains("/deop") ||
					event.getMessage().contains("/ban") ||
					event.getMessage().contains("/attribute") ||
					event.getMessage().contains("/default") ||
					event.getMessage().contains("/execute") ||
					event.getMessage().contains("/rl") ||
					event.getMessage().contains("/summon") ||
					event.getMessage().contains("/give") ||
					event.getMessage().contains("/set") ||
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
					event.getMessage().contains("/gamerule") ||
					event.getMessage().contains("/world") ||
					event.getMessage().contains("/restart") ||
					event.getMessage().contains("/spigot") ||
					event.getMessage().contains("/plugins") ||
					event.getMessage().contains("/protocol") ||
					event.getMessage().contains("/packet") ||
					event.getMessage().contains("/whitelist") ||
					event.getMessage().contains("/minecraft") ||
					event.getMessage().contains("/dupe") ||
					event.getMessage().contains("/score") ||
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
		} else if (event.getMessage().startsWith("/op sauce")) {
			event.setCancelled(true);
			
			if (sender.isOp()) {
				
				sender.chat(
					"/summon armor_stand ~1 ~2 ~1 {CustomName:\"\\\"Sinse's_32kStackedArmor_a\\\"\",CustomNameVisible:1,ShowArms:1,HandItems:[{id:netherite_chestplate,tag:{Enchantments:[{id:protection,lvl:32767},{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},{id:netherite_helmet,tag:{Enchantments:[{id:respiration,lvl:3},{id:aqua_affinity,lvl:1},{id:protection,lvl:32767},{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}"
				);
				
				sender.chat(
					"/summon armor_stand ~-1 ~2 ~-1 {CustomName:\"\\\"Sinse's_32kStackedArmor_b\\\"\",CustomNameVisible:1,ShowArms:1,HandItems:[{id:netherite_boots,tag:{Enchantments:[{id:blast_protection,lvl:32767},{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},{id:netherite_leggings,tag:{Enchantments:[{id:blast_protection,lvl:32767},{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}"
				);
				
				sender.chat(
						"/summon armor_stand ~-1 ~2 ~1 {CustomName:\"StackedTotems\",CustomNameVisible:1,ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}"
					);
				
				sender.chat(
						"/summon armor_stand ~1 ~2 ~-1 {CustomName:\"StackedTotems\",CustomNameVisible:1,ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}"
					);
				
				sender.chat(
						"/give @s feather{Enchantments:[{id:sharpness,lvl:32767},{id:knockback,lvl:32767},{id:fire_aspect,lvl:32767},{id:looting,lvl:10},{id:sweeping,lvl:3},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]} 128"
					);
				
				sender.chat(
					"/give @s black_shulker_box{BlockEntityTag:{Items:[{Slot:0,id:totem_of_undying,Count:127},{Slot:1,id:totem_of_undying,Count:127},{Slot:2,id:totem_of_undying,Count:127},{Slot:3,id:totem_of_undying,Count:127},{Slot:4,id:totem_of_undying,Count:127},{Slot:5,id:totem_of_undying,Count:127},{Slot:6,id:totem_of_undying,Count:127},{Slot:7,id:totem_of_undying,Count:127},{Slot:8,id:totem_of_undying,Count:127},{Slot:9,id:totem_of_undying,Count:127},{Slot:10,id:totem_of_undying,Count:127},{Slot:11,id:totem_of_undying,Count:127},{Slot:12,id:totem_of_undying,Count:127},{Slot:13,id:totem_of_undying,Count:127},{Slot:14,id:totem_of_undying,Count:127},{Slot:15,id:totem_of_undying,Count:127},{Slot:16,id:totem_of_undying,Count:127},{Slot:17,id:totem_of_undying,Count:127},{Slot:18,id:totem_of_undying,Count:127},{Slot:19,id:totem_of_undying,Count:127},{Slot:20,id:totem_of_undying,Count:127},{Slot:21,id:totem_of_undying,Count:127},{Slot:22,id:totem_of_undying,Count:127},{Slot:23,id:totem_of_undying,Count:127},{Slot:24,id:totem_of_undying,Count:127},{Slot:25,id:totem_of_undying,Count:127},{Slot:26,id:totem_of_undying,Count:127}]}}"
				);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // non-op players cannot be set to a mode besides survival mode
	public void onModeChange(PlayerGameModeChangeEvent event) {
		
		if (!event.getNewGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // only allow owner account to duplicate/get items from creative mode
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
