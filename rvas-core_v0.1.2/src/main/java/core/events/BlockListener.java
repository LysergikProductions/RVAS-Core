package core.events;

import java.util.*;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import core.backend.Config;

public class BlockListener implements Listener {
	
	public static ArrayList<Material> BreakBanned = new ArrayList<>();
	{
		BreakBanned.addAll(Arrays.asList(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
				Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> PlacementBanned = new ArrayList<>();
	{
		PlacementBanned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		
		// get configs
		String roofProt = Config.getValue("protect.bedrock.roof");
		String floorProt = Config.getValue("protect.bedrock.floor");
		String debug = Config.getValue("debug");
		String devesp = Config.getValue("devesp");
		
		// get commonly referenced data
		Block block = event.getBlock();
		Player breaker = event.getPlayer();
		GameMode mode = breaker.getGameMode();
		
		String breaker_name = breaker.getName();
		UUID breaker_id = breaker.getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		// prevent creative players from breaking certain blocks but completely ignore admin account
		if (!admin_name.equals(breaker_name) || !admin_id.equals(breaker_id)) {
			
			if (!breaker.isOp()) {
				breaker.setGameMode(GameMode.SURVIVAL);
			}
			
			if (BreakBanned.contains(block.getType())) {
				
				event.setCancelled(true);
				
				if (debug.equals("true") && devesp.equals("true")) {
					Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
				}
				
			// do things if block == bedrock
			} else if (block.getType().equals(Material.BEDROCK)) {
				if (debug.equals("true") && devesp.equals("true")) {
					Bukkit.spigot().broadcast(new TextComponent(breaker_name + " just broke a bedrock block!"));
				}
				
				// protect bedrock floor
				if (block.getLocation().getY() < 1 && floorProt.equals("true")) {
					
					event.setCancelled(true);

					if (debug.equals("true") && devesp.equals("true")) {
						Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					}
					
				// protect nether roof	
				} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() == 127 && roofProt.equals("true")) {
					
					event.setCancelled(true);

					if (debug.equals("true") && devesp.equals("true")) {
						Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					}
				}/* else if (block.getWorld().getEnvironment().equals(Environment.END) &&
						block.getLocation().getY() == 64 &&
						) {
					
				}*/
			}//else if (block.getType().equals(Material.END_PORTAL_FRAME)) {
				//code
			//}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		
		// get configs
		String debug = Config.getValue("debug");
		String devesp = Config.getValue("devesp");
		
		if (debug.equals("true") && devesp.equals("true")) {
			Bukkit.spigot().broadcast(new TextComponent("BlockPlaceEvent triggered."));
		}
		
		// get commonly referenced data
		Block block = event.getBlockPlaced();
		Player placer = event.getPlayer();
		GameMode mode = placer.getGameMode();
		
		String placer_name = placer.getName();
		UUID placer_id = placer.getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		// for anti-rogue-op meta; cannot place shulker boxes in creative mode
		if (block.getType().toString().contains("SHULKER_BOX") && !placer.getGameMode().equals(GameMode.SURVIVAL)) {
			if (!admin_name.equals(placer_name) || !admin_id.equals(placer_id)) {
				event.setCancelled(true);
			}
		}
		
		// anti roof-placement
		if (Config.getValue("protect.roof.noplacement").equals("true")) {
			if(block.getLocation().getY() > 127 && block.getLocation().getWorld().getName().endsWith("the_nether")) {
				event.setCancelled(true);
			}
		}
		
		// do nothing if user is placing an ender eye into an end portal frame
		if (event.getItemInHand().getType().equals(Material.ENDER_EYE)) {
			return;
		} else if (Config.getValue("protect.banned.place").equals("true")) {
			
			// prevent all players from placing blocks totally unobtainable in survival mode, but ignore admin account
			if (!admin_name.equals(placer_name) || !admin_id.equals(placer_id)) {
				if (PlacementBanned.contains(block.getType())) {
					
					if (Config.getValue("protect.banned.place.ops").equals("false") && placer.isOp()) {
						return;
					}
					
					event.setCancelled(true);
					
					if (debug.equals("true") && devesp.equals("true")) {
						Bukkit.spigot().broadcast(new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
					}
				}
			} else {
				if (debug.equals("true") && devesp.equals("true")) {
					Bukkit.spigot().broadcast(new TextComponent(placer_name + " did this. (admin)"));
				}
			}
		}
	}
}
