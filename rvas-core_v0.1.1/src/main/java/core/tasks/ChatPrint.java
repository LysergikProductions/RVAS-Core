package core.tasks;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

import core.events.Chat;
import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.Utilities;

public class ChatPrint {
	public static void printLeaders(Player sendTo) {
		HashMap<UUID, Double> leaders = PlayerMeta.getTopFivePlayers();
		int x = 0;
		HashMap<UUID, Double> realLeaders = PlayerMeta.getTopFivePlayers();
		for (UUID u : leaders.keySet()) {
			realLeaders.put(u, leaders.get(u));
		}
		
		ArrayList<TextComponent> list = new ArrayList<>();
		for (UUID pid : realLeaders.keySet()) {
			x++;
			TextComponent a = new TextComponent("#" + x + ": "); a.setBold(true);
			
			if (Bukkit.getOfflinePlayer(pid).getName() == null) {
				TextComponent b = new TextComponent("[unknown], " + Utilities.calculateTime(realLeaders.get(pid)));
				TextComponent c = new TextComponent(a, b);
				
				c.setColor(ChatColor.GOLD);
				list.add(c);
			} else {
				TextComponent b = new TextComponent(Bukkit.getOfflinePlayer(pid).getName() + ", " + Utilities.calculateTime(realLeaders.get(pid)));
				TextComponent c = new TextComponent(a, b);
				
				c.setColor(ChatColor.GOLD);
				list.add(c);
			}
		}
		list.forEach(ln -> sendTo.spigot().sendMessage(ln));
	}
	
	public static void printServerHealth(Player sendTo, double tps, int slimit, int skicks, String acr) {
		return;
	}
}
