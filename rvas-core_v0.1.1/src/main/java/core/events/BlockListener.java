package core.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;

import core.backend.Config;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBedrockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Player breaker = event.getPlayer();
		
		// ignore blockBreak events when the block isn't bedrock or if user / uuid is listed as admin
		if (!block.getType().equals(Material.BEDROCK)) {
			return;
		} else if (Config.getValue("admin").equals(breaker.getName()) && Config.getValue("adminid").equals(breaker.getUniqueId())) {
			if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
				System.out.println("admin: " + Config.getValue("admin"));
				System.out.println("breaker: " + breaker.getName());
				System.out.println("adminid: " + Config.getValue("adminid"));
				System.out.println("breakerid: " + breaker.getUniqueId());
			}
			return;
		}
		
		Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + " just broke a bedrock block!"));
		
		// protect bedrock floor
		if (block.getLocation().getY() < 1 && Config.getValue("protect.bedrock.floor").equals("true")) {
			
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + "'s BlockBreakEvent was cancelled."));
			
		} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() == 127) {
			
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + "'s BlockBreakEvent was cancelled."));
		}
	}
}
