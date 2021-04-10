package core.events;

import net.md_5.bungee.api.chat.TextComponent;

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
	
	@EventHandler//(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBedrockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Player breaker = event.getPlayer();
		
		if (!block.getType().equals(Material.BEDROCK)) {
			breaker.spigot().sendMessage(new TextComponent("That was not bedrock, right?!"));
			return;
		}
		
		if (block.getLocation().getY() < 3) {
			event.setCancelled(true);
			breaker.spigot().sendMessage(new TextComponent("You can't break bedrock here!"));
			
		} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() > 125) {
			event.setCancelled(true);
			breaker.spigot().sendMessage(new TextComponent("You can't break bedrock here!"));
		}
		
		breaker.spigot().sendMessage(new TextComponent("You just broke a bedrock block!"));
	}
}
