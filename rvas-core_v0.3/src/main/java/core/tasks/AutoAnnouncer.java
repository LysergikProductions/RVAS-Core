package core.tasks;

/* *
 *  About: Announce string randomly selected from
 * 			announcements.txt as a synced scheduled task
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
import core.backend.Scheduler;
import core.frontend.ChatPrint;
import core.commands.op.Check;
import core.backend.ex.Critical;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@Critical
public class AutoAnnouncer extends TimerTask {

	String lastAnnouncement = "";
	private final Random r = new Random();

	static List<String> announcements; static {
		try {
			announcements = new ArrayList<>();
			announcements.addAll(Files.readAllLines(Paths.get("plugins/core/announcements.txt")));

		} catch (Exception e) { e.printStackTrace(); }
	}

	static TextComponent source; static {
		source = new TextComponent(ChatColor.GOLD +
				"RVAS-core is open-source! Click this message to access the repository.");
		source.setItalic(true);

		source.setClickEvent(new ClickEvent(ClickEvent.Action
				.OPEN_URL, "https://github.com/LysergikProductions/RVAS-Core"));

		source.setHoverEvent(new HoverEvent(HoverEvent.Action
				.SHOW_TEXT, new Text("see the source code by clicking here")));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		if (Config.getValue("announcer.enabled").equals("false")) return;

		int size = announcements.size();
		int rnd = r.nextInt(size+1);
		String tryMsg;

		if (rnd == size || size == 0) { tryMsg = "default";
		} else try { tryMsg = announcements.get(rnd);
		} catch (IndexOutOfBoundsException ignore) { tryMsg = "default"; }

		while (tryMsg.equals(lastAnnouncement) && size != 0) {
			rnd = r.nextInt(size+1);

			if (rnd == size) tryMsg = "default";
			else try { tryMsg = announcements.get(rnd);
			} catch (IndexOutOfBoundsException ignore) { tryMsg = "default"; }
		}

		if (tryMsg.equals("default")) {
			lastAnnouncement = "default";
			String fig = Config.getValue("announcer.default").trim();

			if (fig.equalsIgnoreCase("default") || fig.isEmpty()) Bukkit.spigot().broadcast(source);
			else Bukkit.spigot().broadcast(new TextComponent(ChatPrint.primary.toString() + fig));

		} else {
			lastAnnouncement = tryMsg;
			Bukkit.spigot().broadcast(new TextComponent(ChatPrint.primary.toString() + tryMsg));
		}
		Scheduler.setLastTaskId("autoAnnounce");
		Check.updateGUI();
	}

	public static boolean init() {
		try {
			announcements = new ArrayList<>();
			announcements.addAll(Files.readAllLines(Paths.get("plugins/core/announcements.txt")));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
