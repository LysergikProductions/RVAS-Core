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
		
		// ignore blockBreak events when the block isn't bedrock
		if (!block.getType().equals(Material.BEDROCK)) {
			return;
		}
		
		Player breaker = event.getPlayer();
		Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + " just broke a bedrock block!"));
		
		// protect bedrock floor
		if (block.getLocation().getY() < 1) {
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + "'s BlockBreakEvent was cancelled."));
			
		} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() > 126) {
			event.setCancelled(true);
			Bukkit.spigot().broadcast(new TextComponent(breaker.getName() + "'s BlockBreakEvent was cancelled."));
		}
	}
}
