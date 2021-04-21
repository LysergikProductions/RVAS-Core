package core.events;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import net.md_5.bungee.api.chat.TextComponent;
import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.ServerMeta;
import core.backend.PlayerMeta.MuteType;
import core.commands.Admin;

public class Chat implements Listener {
	// yes dupehand belongs below, it sends a rude message to non-admins if they try to use it
	private static Set<String> allUserCommands = new HashSet<>(Arrays.asList(
		"about", "admin", "discord", "dupehand", "help", "kill", "kit", "kys", "msg", "r",
		"redeem", "server", "sign", "stats", "suicide", "tdm", "tjm", "tps", "vm", "vote", "w"
	));
	
	private HashMap<UUID, Long> lastChatTimes = new HashMap<UUID, Long>();
	private HashMap<UUID, String> lastChatMessages = new HashMap<UUID, String>();
	
	public static HashMap<UUID, Integer> violationLevels = new HashMap<UUID, Integer>();
	public static boolean slowChatEnabled = false;

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		// Cancel this event so we can override vanilla chat
		e.setCancelled(true);

		// Don't execute if the player is muted
		if (PlayerMeta.isMuted(e.getPlayer()) || (PlayerMeta.MuteAll && !e.getPlayer().isOp()))
			return;

		// -- CREATE PROPERTIES --
		
		boolean doSend = true;
		String finalMessage = e.getMessage();
		String color;
		String usernameColor;

		// -- SET CHAT COLORS -- //

		switch (e.getMessage().charAt(0)) {
			case '>':
				color = "§a"; // Greentext
				break;
			case '$':
				if (PlayerMeta.isDonator(e.getPlayer())) {
					color = "§6"; // Donator text
					break;
				}
			default:
				color = "§f"; // Normal text
				break;
		}

		if (PlayerMeta.isDonator(e.getPlayer()) && !Admin.UseRedName.contains(e.getPlayer().getUniqueId())) {
			usernameColor = "§6";
		} else if (Admin.UseRedName.contains(e.getPlayer().getUniqueId())) {
			usernameColor = "§c";
		} else {
			usernameColor = "§f";
		}

		// -- STRING MODIFICATION -- //

		// Remove section symbols
		finalMessage = finalMessage.replace('§', ' ');

		// -- CHECKS -- //

		if (isBlank(finalMessage)) {
			doSend = false;
		} else if (PlayerMeta.isLagfag(e.getPlayer())) {
			finalMessage = ":'(";
		}
		
		if (finalMessage.contains("/deop") || finalMessage.contains("/op")) {
			
		}

		// -- SEND FINAL MESSAGE -- //

		if (doSend) {
			String username = e.getPlayer().getName();

			TextComponent finalCom = new TextComponent("§f<" + usernameColor + username + "§f> " + color + finalMessage);
			
			if(Config.getValue("spam.enable").equals("true")) {	
				boolean censored = false;
				
				if(lastChatTimes.containsKey(e.getPlayer().getUniqueId())) {
					if(lastChatTimes.get(e.getPlayer().getUniqueId()) + Integer.parseInt(Config.getValue("spam.wait_time")) > System.currentTimeMillis()) {
						
						censored = true;
						
						if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
							violationLevels.put(e.getPlayer().getUniqueId(), violationLevels.get(e.getPlayer().getUniqueId()) + 1);
						}
						else {
							violationLevels.put(e.getPlayer().getUniqueId(), 1);
						}
					}
				}
				
				if(lastChatMessages.containsKey(e.getPlayer().getUniqueId())) {
					// player sent two messages in a row, but now how similar are they?
					if(similarity(lastChatMessages.get(e.getPlayer().getUniqueId()), finalMessage) * 100 > Integer.parseInt(Config.getValue("spam.min_similarity"))) {
						
						censored = true;
						
						if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
							violationLevels.put(e.getPlayer().getUniqueId(), violationLevels.get(e.getPlayer().getUniqueId()) + 1);
						}
						else {
							violationLevels.put(e.getPlayer().getUniqueId(), 1);
						}
					}
				}
				
				lastChatTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				lastChatMessages.put(e.getPlayer().getUniqueId(), finalMessage);
				
				if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
					if(violationLevels.get(e.getPlayer().getUniqueId()) == Integer.parseInt(Config.getValue("spam.minimum_vl"))) {
						PlayerMeta.setMuteType(e.getPlayer(), MuteType.TEMPORARY);
						return;
					}
				}
				
				// op bypass
				if(e.getPlayer().isOp() && Config.getValue("spam.ops").equals("true")) {
					if(censored) {
						e.getPlayer().sendMessage(new TextComponent("§cYour message was flagged as spam, but since you are an OP, it was not filtered."));
						censored = false;
					}
					violationLevels.remove(e.getPlayer().getUniqueId());
				}
			
				if(censored) {
					Bukkit.getLogger().log(Level.INFO, "§4<" + username + "> " + finalMessage + " [deleted, vl="+violationLevels.get(e.getPlayer().getUniqueId())+"]");
					return;
				}
			}
			
			Bukkit.getLogger().log(Level.INFO, "§f<" + usernameColor + username + "§f> " + color + finalMessage);
			Bukkit.getServer().spigot().broadcast(finalCom);
		}
	}
	
	public double similarity(String s1, String s2) {
		    String longer = s1, shorter = s2;
		    if (s1.length() < s2.length()) { // longer should always have greater length
		      longer = s2; shorter = s1;
		    }
		    int longerLength = longer.length();
		    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
		    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
		    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
		    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}
	
	public int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

	@EventHandler
	public boolean onCommand(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().split(" ")[0].contains(":") && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().spigot().sendMessage(new TextComponent("§cUnknown command."));
		} else if (e.getMessage().split("")[1].contains(Config.getValue("admin")) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().spigot().sendMessage(new TextComponent("§cCannot target admin account."));
		}
		return true;
	}

	@EventHandler
	public void onPlayerTab(PlayerCommandSendEvent e) {
		if (!e.getPlayer().isOp()) {
			e.getCommands().clear();
			e.getCommands().addAll(allUserCommands);
		}
	}

	private boolean isBlank(String check) {
		return check == null || check.isEmpty() || check.length() == 0 || check.trim().isEmpty();
	}
}
