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
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;


@SuppressWarnings("SpellCheckingInspection")
public class HelpPages {

	public static void helpGeneral(Player receiver, int page) {
		int maxPage = 2;

		page = (page > maxPage) ? maxPage : Math.max(page, 1);
		int nextPageInt = page + 1; int prevPageInt = page - 1;

		ClickEvent prevPage, nextPage; TextComponent finalFooter;
		TextComponent footer = new TextComponent(" Help Page " + page + "/" + maxPage + " ");

		prevPage = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + prevPageInt);
		nextPage = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + nextPageInt);

		TextComponent prev = new TextComponent(ChatPrint.controls + "<<");
		TextComponent next = new TextComponent(ChatPrint.controls + ">>");

		if (prevPageInt >= 1) prev.setClickEvent(prevPage);
		if (nextPageInt <= maxPage) next.setClickEvent(nextPage);
		finalFooter = new TextComponent(prev, footer, next);

		receiver.sendMessage("");
		receiver.sendMessage(finalFooter);

		// TODO: add hover events to display syntax to users
		switch (page) {
			case 1:
				Arrays.asList(
						"\u00A76/sign: \u00A77Sign the item you are holding. *Cannot undo or overwrite",
						"\u00A76/discord: \u00A77Join the discord",
						"\u00A76/vote: \u00A77Dupe the item in your hand. Only occurs after voting",
						"\u00A76/local, /l: \u00A77Send a message only to players in your render distance",
						"\u00A76/afk: \u00A77Block whispers and tell the whisperer that you are AFK",
						"\u00A76/last: \u00A77Show the last three whispers you've received",
						"\u00A76/ignore: \u00A77Ignore all messages from given player until next restart"

				).forEach(receiver::sendMessage);
				break;

			case 2:
				Arrays.asList(
						"\u00A76/vm: \u00A77Vote to mute a player",
						"\u00A76/msg, /w, /r: \u00A77Message or reply to a player privately",
						"\u00A76/kit: \u00A77Get a small kit with steak and some starter tools (one-time only)",
						"\u00A76/stats help: \u00A77Learn how to hide your PVP stats and more",
						"\u00A76/server: \u00A77See current speed limit and other server info",
						"\u00A76/tjm: \u00A77Toggle join messages",
						"\u00A76/kill: \u00A77Take a guess"

				).forEach(receiver::sendMessage);
				break;
		}
		receiver.sendMessage(finalFooter);
	}

	public static void helpStats(Player receiver) {
		receiver.sendMessage("");

		TextComponent self_a = new TextComponent("/stats");
		TextComponent players_a = new TextComponent("/stats [player name]");
		TextComponent leaders_a = new TextComponent("/stats [3-15]");
		TextComponent mcstats_a = new TextComponent("/stats mc");

		self_a.setColor(ChatPrint.cmd); players_a.setColor(ChatPrint.cmd);
		leaders_a.setColor(ChatPrint.cmd); mcstats_a.setColor(ChatPrint.cmd);

		TextComponent self_b = new TextComponent(ChatPrint.desc + " : Shows you your stats");
		TextComponent players_b = new TextComponent(ChatPrint.desc + " : Shows the stats for that player");
		TextComponent leaders_b = new TextComponent(ChatPrint.desc + " : Shows the top 3-15 players (by play-time)");
		TextComponent mcstats_b = new TextComponent(ChatPrint.desc + " : Shows you some of your MC-tracked world-stats");

		TextComponent toggle_info = new TextComponent(
				ChatPrint.clear + "Use /stats kills | deaths | kd, to toggle hiding them from public view!");

		toggle_info.setItalic(true);
		self_a.setItalic(true); players_a.setItalic(true);
		leaders_a.setItalic(true); mcstats_a.setItalic(true);

		TextComponent self = new TextComponent(self_a, self_b);
		TextComponent players = new TextComponent(players_a, players_b);
		TextComponent leaders = new TextComponent(leaders_a, leaders_b);
		TextComponent mcstats = new TextComponent(mcstats_a, mcstats_b);

		receiver.sendMessage(new TextComponent(
				ChatPrint.controls + "<<" +
						ChatPrint.help_title + " /stats help " +
						ChatPrint.controls + ">>").toLegacyText());

		HoverEvent hover_leaders = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text("Click on a player in the leaderboard to see their stats quickly"));

		leaders.setHoverEvent(hover_leaders);

		ArrayList<TextComponent> list = new ArrayList<>();
		list.add(self); list.add(leaders); list.add(players); list.add(mcstats); list.add(toggle_info);

		list.forEach(ln -> receiver.sendMessage(ln.toLegacyText()));
	}
}
