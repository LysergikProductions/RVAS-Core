package protocol3.backend;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.TextComponent;

public class Utilities {
	public static String calculateTime(double seconds) {
		long hours = (long) (seconds / 3600);
		long hoursRem = (long) (seconds % 3600);
		long minutes = hoursRem / 60;

		String hoursString = "";
		String minutesString = "";

		if (hours == 1) {
			hoursString = hours + " hour";
		} else {
			hoursString = hours + " hours";
		}

		if (minutes == 1) {
			minutesString = minutes + " minute";
		} else if (minutes == 0) {
			minutesString = "";
		} else {
			minutesString = minutes + " minutes";
		}

		if (minutesString.isEmpty() && hoursString.equals("0 hours")) return "None";

		if (minutes == 0) {
			return hoursString;
		} else {
			return hoursString + ", " + minutesString;
		}
	}

	public static boolean restarting = false;

	public static void restart() {
		restart(false);
	}

	public static void restart(boolean slow) {
		if (restarting) {
			return;
		} else {
			restarting = true;
		}
		new Thread(() -> {
			try {
				if (slow) {
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6minutes."));
					TimeUnit.MINUTES.sleep(4);
				}
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6minute."));
				TimeUnit.SECONDS.sleep(30);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l30 §r§6seconds."));
				TimeUnit.SECONDS.sleep(15);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l15 §r§6seconds."));
				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l10 §r§6seconds."));
				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l4 §r§6seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l3 §r§6seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l2 §r§6seconds."));
				TimeUnit.SECONDS.sleep(1);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6second."));
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception e) {
			}
			Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server is restarting."));
			Bukkit.shutdown();
		}).start();
	}

	public static boolean validIP (String ip) {
	    try {
	        if ( ip == null || ip.isEmpty() ) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if (ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}
}
