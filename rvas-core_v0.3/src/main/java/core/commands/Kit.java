package core.commands;

import core.backend.Config;
import core.backend.ChatPrint;
import core.data.PlayerMeta;
import core.tasks.Analytics;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Kit implements CommandExecutor {
	public static List<UUID> kickedFromKit = new ArrayList<>();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		
		Player player = (Player) sender;
		if (!PlayerMeta.isAdmin(player)) Analytics.kit_cmd++;
		
		if (Config.getValue("funny.kit").equals("true")) {
			
			kickedFromKit.add(player.getUniqueId());
			player.kickPlayer("\u00A76imagine kits in vanilla survival lol [pog]");
			
			if (!PlayerMeta.isMuted(player)) return true;
			
			Bukkit.getServer().spigot().broadcast(new TextComponent(
					"\u00A7a" + player.getName() + " got their complimentary starter kit! Get yours by typing /kit."));

		} else player.sendMessage(new TextComponent(ChatPrint.fail +
					"This command has been disabled by your administrator.").toLegacyText());
		return true;
	}
}
