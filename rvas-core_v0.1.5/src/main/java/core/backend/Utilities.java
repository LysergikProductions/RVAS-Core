package core.backend;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Utilities {
	
	public static String calculateTime(double seconds) {
		
		long hours;
		
		long days = (long) (seconds / 86400);
		long daysRem = (long) (seconds % 86400);
		
		if (days < 1) hours = (long) (seconds / 3600);
		else hours = (long)daysRem / 3600;
		
		long hoursRem = (long) (seconds % 3600);		
		long minutes = hoursRem / 60;
		
		String daysString = "";
		String hoursString = "";
		String minutesString = "";

		if (hours == 1) {
			hoursString = hours + " hour";
		} else {
			hoursString = hours + " hours";
		}
		
		if (days == 1) {
			daysString = days + " day";
		} else {
			daysString = days + " days";
		}

		if (minutes == 1) {
			minutesString = minutes + " minute";
		} else if (minutes == 0) {
			minutesString = "";
		} else {
			minutesString = minutes + " minutes";
		}

		if (minutesString.isEmpty() && hoursString.equals("0 hours")) return "< 1 minute";

		if (days < 1 && minutes == 0) {
			return hoursString;
			
		} else if (days < 1) {
			return hoursString + ", " + minutesString;
			
		} else if (minutes == 0) {
			return daysString + ", " + hoursString;
		} else {
			return daysString + ", " + hoursString + ", " + minutesString;
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
				System.out.println(e);
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
			return !ip.endsWith(".");

		} catch (NumberFormatException nfe) {
	        return false;
	    }
	}
	
	public static int blockCounter(Chunk chunk, Material block) {
		int counter = 0;
		
		try {
		    for(int y = 0; y <= 255; y++) {
		        for(int x = 0; x <= 15; x++) {
		            for(int z = 0; z <= 15; z++) {
		            	
		                if(chunk.getBlock(x, y, z).getType() == block) counter++;
		            }
		        }
		    }
		    return counter;
		} catch (Exception e) {
			System.out.println(e);
			return counter;
		}
	}
	
	public static int blockRemover(Chunk chunk, Material blockType, int limiter) {
		
	    int counter = 0;
	    for (int y = 0; y <= 255; y++) {
	        for (int x = 0; x <= 15; x++) {
	            for (int z = 0; z <= 15; z++) {
	            	
	            	Block thisBlock = chunk.getBlock(x, y, z);
	            	
	                if (thisBlock.getType() == blockType) {
	                	counter++;
	                	thisBlock.setType(Material.AIR);
	                }
	                if (counter >= limiter) return counter;
	            }
	        }
	    }
	    return counter;
	}
	
	// clears a 3x3 chunk grid around the provided chunk
	public static int clearChunkItems(Chunk chunk) {
		
		World world = chunk.getWorld();
		
		Map<String, Chunk> chunks = new HashMap<String, Chunk>();{
			chunks.put("C", chunk);
			chunks.put("N", world.getChunkAt(chunk.getX(), chunk.getZ() - 1));
			chunks.put("NE", world.getChunkAt(chunk.getX() + 1, chunk.getZ() - 1));
			chunks.put("E", world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
			chunks.put("SE", world.getChunkAt(chunk.getX() + 1, chunk.getZ() + 1));
			chunks.put("S", world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
			chunks.put("SW", world.getChunkAt(chunk.getX() - 1, chunk.getZ() + 1));
			chunks.put("W", world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
			chunks.put("NW", world.getChunkAt(chunk.getX() - 1, chunk.getZ() - 1));
		}
		
		int counter = 0;
		for (Chunk thisChunk: chunks.values()) {
			for (Entity entity: thisChunk.getEntities()) {				
				if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
					
					entity.remove();				
					counter++;
				}
			}
		}
		return counter;
	}
}
