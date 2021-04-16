package core.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class World implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		// check args
		if (args.length != 0) {
			
			switch (args[0]) {
				case "lightning":		
					for(Player p : Bukkit.getServer().getOnlinePlayers()) {
						p.getWorld().strikeLightning(p.getLocation());
					}	
					return true;
			}
		}
		return true;
	}
}
