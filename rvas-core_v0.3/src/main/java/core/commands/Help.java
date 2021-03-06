package core.commands;

import core.frontend.HelpPages;
import core.tasks.Analytics;
import core.data.PlayerMeta;

import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Help implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		Player player = (Player)sender;
		if (!PlayerMeta.isAdmin(player)) Analytics.help_cmd++;
		
		try { HelpPages.helpGeneral(player, Integer.parseInt(args[0]));
		} catch (Exception ex) { HelpPages.helpGeneral(player, 1); }

		return true;
	}
}
