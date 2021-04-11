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
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import core.backend.Config;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBedrockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Player breaker = event.getPlayer();
		
		String breaker_name = breaker.getName();
		UUID breaker_id = breaker.getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id = UUID.fromString(Config.getValue("adminid"));
		
		String debug = Config.getValue("debug");
		String devesp = Config.getValue("devesp");
		
		// ignore blockBreak events when the block isn't bedrock or if user / uuid is listed as admin
		if (!block.getType().equals(Material.BEDROCK)) {
			return;
		} else if (admin_name.equals(breaker_name) && admin_id.equals(breaker_id)) {
			if (debug.equals("true") && devesp.equals("false")) {
				System.out.println("admin: " + admin_name);
				System.out.println("breaker: " + breaker_name);
				System.out.println("adminid: " + admin_id);
				System.out.println("breakerid: " + breaker_id);
			}
			return;
		}	
		
		Bukkit.spigot().broadcast(new TextComponent(breaker_name + " just broke a bedrock block!"));
		
		// protect bedrock floor
		if (block.getLocation().getY() < 1 && Config.getValue("protect.bedrock.floor").equals("true")) {
			
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
		// protect nether roof	
		} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() == 127 &&
				Config.getValue("protect.bedrock.roof").equals("true")) {
			
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
		}
	}
}
