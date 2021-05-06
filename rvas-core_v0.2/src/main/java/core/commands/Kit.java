package core.commands;

import core.backend.Config;
import core.backend.PlayerMeta;
import core.tasks.Analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// funny command haha
public class Kit implements CommandExecutor {
	public static List<UUID> kickedFromKit = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		if (!PlayerMeta.isAdmin(player)) Analytics.kit_cmd++;
		
		if (Config.getValue("funny.kit").equals("true")) {
			
			kickedFromKit.add(player.getUniqueId());
			player.kickPlayer("§6imagine kits in vanilla survival lol [pog]");
			
			if (!PlayerMeta.isMuted(player)) return true;
			
			Bukkit.getServer().spigot().broadcast(new TextComponent(
					"§a" + player.getName() + " got their complimentary starter kit! Get yours by typing /kit."));

		} else {
			
			player.spigot().sendMessage(new TextComponent("§cThis command has been disabled by your administrator."));
		}
		return true;
	}
}
