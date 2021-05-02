package core.backend;

/* *
 * 
 *  About: Store methods that print help information
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

import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.entity.Player;

public class HelpPages {

	public static boolean helpGeneral(Player receiver, int page) {
		return true;
	}
	
	public static boolean helpStats(Player receiver) {
		
		TextComponent head = new TextComponent("--- /stats help ---");
		
		TextComponent self_a = new TextComponent("/stats");
		TextComponent players_a = new TextComponent("/stats [player name]");
		TextComponent leaders_a = new TextComponent("/stats 5");
		TextComponent mcstats_a = new TextComponent("/stats mc");
		
		TextComponent self_b = new TextComponent(" : Shows you your stats");
		TextComponent players_b = new TextComponent(" : Shows the stats for that player");
		TextComponent leaders_b = new TextComponent(" : Shows the top 5 players (by play-time)");
		TextComponent mcstats_b = new TextComponent(" : Shows you some of your MC-tracked world-stats");
		
		TextComponent toggle_info = new TextComponent(
				"Use /stats kills | deaths | kd, to toggle hiding them from public view!");
		
		toggle_info.setColor(ChatColor.GOLD); toggle_info.setItalic(true);
		
		self_b.setColor(ChatColor.GRAY);
		players_b.setColor(ChatColor.GRAY);
		leaders_b.setColor(ChatColor.GRAY);
		mcstats_b.setColor(ChatColor.GRAY);
		
		self_a.setItalic(true);
		players_a.setItalic(true);
		leaders_a.setItalic(true);
		mcstats_a.setItalic(true);
		
		TextComponent self = new TextComponent(self_a, self_b);
		TextComponent players = new TextComponent(players_a, players_b);
		TextComponent leaders = new TextComponent(leaders_a, leaders_b);
		TextComponent mcstats = new TextComponent(mcstats_a, mcstats_b);
		
		receiver.spigot().sendMessage(head);
		
		HoverEvent hover_leaders = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text("Click on a player in the leaderboard to see their stats quickly"));
		
		leaders.setHoverEvent(hover_leaders);
		
		ArrayList<TextComponent> list = new ArrayList<>();
		list.add(self); list.add(leaders); list.add(players); list.add(mcstats); list.add(toggle_info);
		
		list.forEach(ln -> receiver.spigot().sendMessage(ln));		
		
		return true;
	}
}
