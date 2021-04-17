package core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import core.backend.Config;
import core.backend.PlayerMeta;

// funny command haha

public class Kit implements CommandExecutor {
	public static List<UUID> kickedFromKit = new ArrayList<UUID>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		if (Config.getValue("funny.kit").equals("true")) {
			
			kickedFromKit.add(player.getUniqueId());
			player.kickPlayer("§6imagine kits in vanilla survival lol [pog]");
			
			if (!PlayerMeta.isMuted(player)) {
				
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"§a" + player.getName() + " got their complimentary starter kit! Get yours by typing /kit."));
			}
			
			return true;
			
		} else {
			
			player.spigot().sendMessage(new TextComponent("§cThis command has been disabled by your administrator."));
			return true;
		}
	}
}
