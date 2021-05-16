package core.backend;

/* *
 * 
 *  About: Store methods that print information
 *  	from RVAS-Core to a given user's chat
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

import core.objects.*;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class ChatPrint {
	
	// - PLAYER STATS PAGES - \\

	public static void printMcStats(Player receiver, OfflinePlayer target) {
		
		receiver.spigot().sendMessage(new TextComponent(""));
		
		int gaps_eaten = target.getStatistic(Statistic.USE_ITEM, Material.ENCHANTED_GOLDEN_APPLE);
		int mined_obi = target.getStatistic(Statistic.MINE_BLOCK, Material.OBSIDIAN);
		int mined_ancientDebris = target.getStatistic(Statistic.MINE_BLOCK, Material.ANCIENT_DEBRIS);
		int placed_obi = target.getStatistic(Statistic.USE_ITEM, Material.OBSIDIAN);
		
		TextComponent sep = new TextComponent("---");
		TextComponent title = new TextComponent(" " + target.getName() + "'s MC-Stats ");
		
		sep.setColor(ChatColor.GRAY);
		title.setColor(ChatColor.GOLD); title.setBold(true);
		
		TextComponent head = new TextComponent(sep, title, sep);
		receiver.spigot().sendMessage(head);
		
		receiver.spigot().sendMessage(new TextComponent("Gaps Eaten: " + gaps_eaten));
		receiver.spigot().sendMessage(new TextComponent("Mined Ancient Debris: " + mined_ancientDebris));

		receiver.spigot().sendMessage(new TextComponent("Mined Obsidian: " + mined_obi));
		receiver.spigot().sendMessage(new TextComponent("Placed Obsidian: " + placed_obi));
	}
	
	public static void printLeaders(Player receiver) {
		
		HashMap<UUID, Double> leaders = PlayerMeta.getTopFivePlayers();
		HashMap<UUID, Double> realLeaders = PlayerMeta.getTopFivePlayers();
		
		for (UUID u : leaders.keySet()) realLeaders.put(u, leaders.get(u));
		ArrayList<TextComponent> list = new ArrayList<>();

		int x = 0;
		for (UUID pid : realLeaders.keySet()) {
			x++;
			
			TextComponent a1 = new TextComponent("#" + x + ": "); a1.setBold(true);
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(pid);
			String target_name = offPlayer.getName();
			
			if (target_name == null) {
				
				TextComponent b = new TextComponent("[unknown], " + Utilities.timeToString(realLeaders.get(pid)));
				TextComponent c = new TextComponent(a1, b);
				
				c.setColor(ChatColor.GOLD);
				
				list.add(c);
				
			} else {// this leader name != null
				
				int kills = PVPdata.getStats(offPlayer).killTotal;
				int deaths = PVPdata.getStats(offPlayer).deathTotal;
				String kd = PVPdata.getStats(offPlayer).kd;
				
				TextComponent a2 = new TextComponent(target_name + ", ");
				TextComponent b = new TextComponent(Utilities.timeToString(realLeaders.get(pid)));
				
				HoverEvent hoverStats = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Kills: "+kills+" | Deaths: "+deaths+" | K/D: "+kd));
				ClickEvent shortcut = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + target_name);
				
				a2.setColor(ChatColor.GOLD);
				b.setItalic(true);
				b.setHoverEvent(hoverStats);
				
				TextComponent c = new TextComponent(a1, a2, b);
				
				c.setClickEvent(shortcut);
				list.add(c);
			}
		}
		
		TextComponent top5_head = new TextComponent("--- Top Five Players ---");		
		TextComponent ujoins_a = new TextComponent("Unique Joins: ");
		TextComponent ujoins_b = new TextComponent("" + PlayerMeta.Playtimes.keySet().size());
		TextComponent msg = new TextComponent(ujoins_a, ujoins_b);

		top5_head.setColor(ChatColor.GOLD); top5_head.setBold(true);
		msg.setColor(ChatColor.GRAY); msg.setItalic(true);
		
		receiver.spigot().sendMessage(top5_head);
		list.forEach(ln -> receiver.spigot().sendMessage(ln));
		receiver.spigot().sendMessage(msg);
	}
	
	public static void printStats(Player receiver, OfflinePlayer target) {

		PlayerSettings targetSettings = PlayerMeta.getSettings(target);

		Date date = new Date(target.getFirstPlayed());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");		
		String firstPlayed = sdf.format(date);
		String lastPlayed = sdf.format(new Date(target.getLastPlayed()));

		// get all uniquely styleable components
		TextComponent title_pre = new TextComponent("--- ");
		TextComponent title_name = new TextComponent(target.getName());
		TextComponent title_suf = new TextComponent("'s Statistics ---");
		
		TextComponent joined_a = new TextComponent("Joined: ");
		TextComponent joined_b = new TextComponent(firstPlayed);
		TextComponent lastSeen_a = new TextComponent("Last seen: ");
		TextComponent lastSeen_b = new TextComponent(lastPlayed);
		TextComponent rank_a = new TextComponent("Ranking: ");
		TextComponent rank_b = new TextComponent("" + PlayerMeta.getRank(target));
		TextComponent playtime_a = new TextComponent("Time played: ");
		TextComponent playtime_b = new TextComponent(Utilities.timeToString(PlayerMeta.getPlaytime(target)));

		double hours = PlayerMeta.getPlaytime(target) / 3600;
		Text playtime_hover = new Text(new DecimalFormat("0.00").format(hours) + " hours");
		
		TextComponent tkills_a = new TextComponent("PVP Kills: ");
		TextComponent tkills_b = new TextComponent("" + PVPdata.getStats(target).killTotal);
		TextComponent tdeaths_a = new TextComponent("PVP Deaths: ");
		TextComponent tdeaths_b = new TextComponent("" + PVPdata.getStats(target).deathTotal);
		
		String spawnKills = String.valueOf(PVPdata.getStats(target).spawnKills);
		HoverEvent hover_killDetail = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Spawn Kills: " + spawnKills));
		HoverEvent hover_showHours = new HoverEvent(HoverEvent.Action.SHOW_TEXT, playtime_hover);
		
		// style individual components
		joined_a.setColor(ChatColor.BLUE); joined_a.setBold(true);
		
		lastSeen_a.setColor(ChatColor.BLUE); lastSeen_a.setBold(true);
		
		rank_a.setColor(ChatColor.BLUE); rank_a.setBold(true);
		
		playtime_a.setColor(ChatColor.BLUE); playtime_a.setBold(true);
		playtime_b.setHoverEvent(hover_showHours);
		
		tkills_a.setColor(ChatColor.BLUE); tkills_a.setBold(true);
		tkills_b.setHoverEvent(hover_killDetail);
		
		tdeaths_a.setColor(ChatColor.BLUE); tdeaths_a.setBold(true);
		
		// parse components into 1-line components
		TextComponent title = new TextComponent(title_pre, title_name, title_suf);
		TextComponent joined = new TextComponent(joined_a, joined_b);
		TextComponent lastSeen = new TextComponent(lastSeen_a, lastSeen_b);
		
		TextComponent rank = new TextComponent(rank_a, rank_b);
		TextComponent playtime = new TextComponent(playtime_a, playtime_b);
		
		TextComponent tkills = new TextComponent(tkills_a, tkills_b);
		TextComponent tdeaths = new TextComponent(tdeaths_a, tdeaths_b);
		TextComponent kd;
		
		try {
			kd = new TextComponent("K/D: " + new DecimalFormat("#.###").format(Double.parseDouble(PVPdata.getStats(target).kd)));
		} catch (NumberFormatException e) {
			kd = new TextComponent("K/D: " + PVPdata.getStats(target).kd);
		}
		
		title.setColor(ChatColor.YELLOW); title.setBold(true);
		kd.setColor(ChatColor.GRAY);
		
		ArrayList<TextComponent> statsLines; {
			statsLines = new ArrayList<>(Arrays.asList(title, joined, lastSeen, rank, playtime));
		}
		
		if (targetSettings.show_PVPstats) {
			
			if (targetSettings.show_kills ||
					receiver.getUniqueId().equals(target.getUniqueId())) statsLines.add(tkills);
			if (targetSettings.show_deaths ||
					receiver.getUniqueId().equals(target.getUniqueId())) statsLines.add(tdeaths);
			if (targetSettings.show_kd ||
					receiver.getUniqueId().equals(target.getUniqueId())) statsLines.add(kd);
		}
		
		// send final message to receiver
		statsLines.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
	
	public static void printPlayerSettings(Player receiver) {
		
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(receiver.getUniqueId());
		
		PlayerSettings theseSettings = PlayerMeta.getSettings(offPlayer);
		ArrayList<TextComponent> list = new ArrayList<>();
		
		TextComponent sep = new TextComponent("---");
		TextComponent title = new TextComponent(" Your Stats Settings ");
		
		sep.setColor(ChatColor.GRAY);
		title.setColor(ChatColor.GOLD); title.setBold(true);
		
		TextComponent head = new TextComponent(sep, title, sep);
		list.add(new TextComponent("")); list.add(head);
		
		TextComponent showPVP_a = new TextComponent("PVP-Stats Display: ");
		TextComponent showKills_a = new TextComponent("Kills: ");
		TextComponent showDeaths_a = new TextComponent("Deaths: ");
		TextComponent showKD_a = new TextComponent("K/D: ");
		TextComponent showJoinMsgs_a = new TextComponent("All join messages: ");
		TextComponent showDeathMsgs_a = new TextComponent("All death messages: ");
		
		showPVP_a.setColor(ChatColor.GOLD);
		showKills_a.setColor(ChatColor.GOLD);
		showDeaths_a.setColor(ChatColor.GOLD);
		showKD_a.setColor(ChatColor.GOLD);
		showJoinMsgs_a.setColor(ChatColor.GOLD);
		showDeathMsgs_a.setColor(ChatColor.GOLD);
		
		String spvp; String kill; String die;
		String kdr; String jMsgs; String dMsgs;
		
		if (theseSettings.show_PVPstats) spvp = "Enabled"; else spvp = "Disabled";
		if (theseSettings.show_kills) kill = "Enabled"; else kill = "Disabled";
		if (theseSettings.show_deaths) die = "Enabled"; else die = "Disabled";
		if (theseSettings.show_kd) kdr = "Enabled"; else kdr = "Disabled";
		if (theseSettings.show_player_join_messages) jMsgs = "Enabled"; else jMsgs = "Disabled";
		if (theseSettings.show_player_death_messages) dMsgs = "Enabled"; else dMsgs = "Disabled";
		
		TextComponent showPVP_b = new TextComponent("" + spvp);
		TextComponent showKills_b = new TextComponent("" + kill);
		TextComponent showDeaths_b = new TextComponent("" + die);
		TextComponent showKD_b = new TextComponent("" + kdr);
		TextComponent showJoinMsgs_b = new TextComponent("" + jMsgs);
		TextComponent showDeathMsgs_b = new TextComponent("" + dMsgs);
		
		TextComponent showPVP = new TextComponent(showPVP_a, showPVP_b);
		TextComponent showKills = new TextComponent(showKills_a, showKills_b);
		TextComponent showDeaths = new TextComponent(showDeaths_a, showDeaths_b);
		TextComponent showKD = new TextComponent(showKD_a, showKD_b);
		TextComponent showJoinMsgs = new TextComponent(showJoinMsgs_a, showJoinMsgs_b);
		TextComponent showDeathMsgs = new TextComponent(showDeathMsgs_a, showDeathMsgs_b);
		
		list.add(showPVP); list.add(showKills); list.add(showDeaths);
		list.add(showKD); list.add(showJoinMsgs); list.add(showDeathMsgs);
		
		list.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
}
