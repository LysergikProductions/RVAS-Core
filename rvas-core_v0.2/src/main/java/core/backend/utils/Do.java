package core.backend.utils;

/* *
 *
 *  About: Void methods that _do_ stuff
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

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Do {

	// restart countdown
	public static boolean restarting = false;

	public static void restart() {
		restart(false);
	}

	public static void restart(boolean slow) {
		if (restarting) return;
		else restarting = true;

		new Thread(() -> {
			try {
				
				if (slow) {
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l5 \u00A7r\u00A76minutes."));
					TimeUnit.MINUTES.sleep(4);
				}
				
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l1 \u00A7r\u00A76minute."));
				System.out.println("60s");

				TimeUnit.SECONDS.sleep(30);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l30 \u00A7r\u00A76seconds."));
				System.out.println("30s");

				TimeUnit.SECONDS.sleep(15);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l15 \u00A7r\u00A76seconds."));
				System.out.println("15s");

				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l10 \u00A7r\u00A76seconds."));
				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l5 \u00A7r\u00A76seconds."));
				System.out.println("5s");

				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l4 \u00A7r\u00A76seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l3 \u00A7r\u00A76seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l2 \u00A7r\u00A76seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("\u00A76Server restarting in \u00A76\u00A7l1 \u00A7r\u00A76second."));
				TimeUnit.SECONDS.sleep(1);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			Bukkit.getServer().spigot().broadcast(new TextComponent("\u00A76Server is restarting."));
			Bukkit.shutdown();
		}).start();
	}

	// sends a message to all online ops and console
	public static void notifyOps(TextComponent msg) {
		if (msg == null) return;

		for (Player thisPlayer: Bukkit.getOnlinePlayers()) {
			try {
				if (thisPlayer.isOp()) thisPlayer.sendMessage(msg);
			} catch (Exception e) {return;}
		}

		System.out.println(msg.getText());
	}
}
