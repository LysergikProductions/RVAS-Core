package core.backend;

/* *
 *
 *  About: Various commonly-used pure and impure methods
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
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class Utilities {

	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static String timeToString(double seconds) {
		
		long hours;
		
		long days = (long) (seconds / 86400);
		long daysRem = (long) (seconds % 86400);
		
		if (days < 1) hours = (long) (seconds / 3600);
		else hours = daysRem / 3600;
		
		long hoursRem = (long) (seconds % 3600);		
		long minutes = hoursRem / 60;
		
		String daysString;
		String hoursString;
		String minutesString;

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
				System.out.println("60s");

				TimeUnit.SECONDS.sleep(30);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l30 §r§6seconds."));
				System.out.println("30s");

				TimeUnit.SECONDS.sleep(15);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l15 §r§6seconds."));
				System.out.println("15s");

				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l10 §r§6seconds."));
				TimeUnit.SECONDS.sleep(5);
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6seconds."));
				System.out.println("5s");

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
				e.printStackTrace();
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

	public static int banBlockCounter(Chunk chunk) {
		int counter = 0;

		try {
			for (int y = 255; y >= 0; y--) {
				for (int x = 0; x <= 15; x++) {
					for (int z = 0; z <= 15; z++) {
						Material thisMat = chunk.getBlock(x, y, z).getType();

						if (
								thisMat.equals(Material.FURNACE) ||
								thisMat.equals(Material.BLAST_FURNACE) ||
								thisMat.equals(Material.SMOKER) ||
								thisMat.equals(Material.ENCHANTING_TABLE)) {

							counter++;
						}
					}
				}
			} return counter;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return counter;
		}
	}

	public static int banBlockRemover(Chunk chunk, int limiter) {
		int counter = 0;

		for (int y = 255; y >= 0; y--) {
			for (int x = 0; x <= 15; x++) {
				for (int z = 0; z <= 15; z++) {
					Block thisBlock = chunk.getBlock(x, y, z);

					if (
							thisBlock.getType().equals(Material.FURNACE) ||
							thisBlock.getType().equals(Material.BLAST_FURNACE) ||
							thisBlock.getType().equals(Material.SMOKER) ||
							thisBlock.getType().equals(Material.ENCHANTING_TABLE)) {

						thisBlock.setType(Material.AIR);
						counter++;
					}
					if (counter >= limiter) return counter;
				}
			}
		}
		return counter;
	}

	public static int blockCounter(Chunk chunk, Material block) {
		int counter = 0;

		try {
			for (int y = 0; y <= 255; y++) {
				for (int x = 0; x <= 15; x++) {
					for (int z = 0; z <= 15; z++) {
						if(chunk.getBlock(x, y, z).getType() == block) counter++;
					}
				}
			} return counter;

		} catch (Exception e) {
			e.printStackTrace();
			return counter;
		}
	}
	
	// clears a 3x3 chunk grid around the provided chunk
	public static int clearChunkItems(Chunk chunk) {
		
		World world = chunk.getWorld();
		
		Map<String, Chunk> chunks = new HashMap<>();{
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

	// send a message to all online ops and console
	public static void notifyOps(TextComponent msg) {
		if (msg == null) return;

		for (Player thisPlayer: Bukkit.getOnlinePlayers()) {
			try {
				if (thisPlayer.isOp()) thisPlayer.spigot().sendMessage(msg);
			} catch (Exception e) {return;}
		}

		System.out.println(msg.getText());
	}

	public static String getDimensionName (Location thisLoc) {

		String out = null;
		World.Environment thisEnv = thisLoc.getWorld().getEnvironment();

		if (thisEnv.equals(World.Environment.NORMAL)) out = "overworld";
		else if (thisEnv.equals(World.Environment.NETHER)) out = "the_nether";
		else if (thisEnv.equals(World.Environment.THE_END)) out = "the_end";

		return out;
	}

	public static int getExitFloor(Chunk chunk) {
		int y = 257;

		while (y > 1) {
			Material topBlock = chunk.getWorld().getBlockAt(1, y, 1).getType();
			Material bottomBlock = chunk.getWorld().getBlockAt(1, y-1, 1).getType();

			if (topBlock.equals(Material.AIR) ||
					bottomBlock.equals(Material.AIR)) y--;
			else if (topBlock.equals(Material.BEDROCK) &&
					!bottomBlock.equals(Material.BEDROCK)) return y;
			else y--;
		}
		return -1;
	}

	public static boolean isCmdRestricted (String thisCmd) {

		return thisCmd.contains("/op") || thisCmd.contains("/deop") ||
				thisCmd.contains("/ban") || thisCmd.contains("/attribute") ||
				thisCmd.contains("/default") || thisCmd.contains("/execute") ||
				thisCmd.contains("/rl") || thisCmd.contains("/summon") ||
				thisCmd.contains("/gamerule") || thisCmd.contains("/set") ||
				thisCmd.contains("/difficulty") || thisCmd.contains("/replace") ||
				thisCmd.contains("/enchant") || thisCmd.contains("/time") ||
				thisCmd.contains("/weather") || thisCmd.contains("/schedule") ||
				thisCmd.contains("/data") || thisCmd.contains("/fill") ||
				thisCmd.contains("/save") || thisCmd.contains("/loot") ||
				thisCmd.contains("/experience") || thisCmd.contains("/xp") ||
				thisCmd.contains("/forceload") || thisCmd.contains("/function") ||
				thisCmd.contains("/spreadplayers") || thisCmd.contains("/reload") ||
				thisCmd.contains("/world") || thisCmd.contains("/restart") ||
				thisCmd.contains("/spigot") || thisCmd.contains("/plugins") ||
				thisCmd.contains("/protocol") || thisCmd.contains("/packet") ||
				thisCmd.contains("/whitelist") || thisCmd.contains("/minecraft") ||
				thisCmd.contains("/dupe") || thisCmd.contains("/score") ||
				thisCmd.contains("/tell") || thisCmd.contains("/global");
	}

	public static World getWorldByDimension(World.Environment thisEnv) {

		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			if (thisWorld.getEnvironment().equals(thisEnv)) return thisWorld;
		}
		return null;
	}

	@Deprecated
	public static int blocksCounter(Chunk chunk, Material[] blocks) {
		int counter = 0;

		try {
			for (int y = 255; y >= 0; y--) {
				for (int x = 0; x <= 15; x++) {
					for (int z = 0; z <= 15; z++) {

						if (Arrays.stream(blocks).parallel()
								.anyMatch(Predicate.isEqual(chunk.getBlock(x, y, z).getType()))) {
							counter++;
						}
					}
				}
			} return counter;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return counter;
		}
	}

	@Deprecated
	public static int blockRemover(Chunk chunk, Material blockType, int limiter, boolean doPop) {
		int counter = 0;

		for (int y = 255; y >= 0; y--) {
			for (int x = 0; x <= 15; x++) {
				for (int z = 0; z <= 15; z++) {

					Block thisBlock = chunk.getBlock(x, y, z);
					Location thisLoc = thisBlock.getLocation();

					if (thisBlock.getType() == blockType) {
						counter++;

						if (doPop) thisLoc.getWorld().dropItem(thisLoc, new ItemStack(thisBlock.getType(),1));
						thisBlock.setType(Material.AIR);
					}
					if (counter >= limiter) return counter;
				}
			}
		}
		return counter;
	}
}
