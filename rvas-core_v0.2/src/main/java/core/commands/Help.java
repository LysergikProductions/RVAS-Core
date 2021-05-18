package core.commands;

import core.tasks.Analytics;
import core.backend.PlayerMeta;

import java.util.Arrays;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Help implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!PlayerMeta.isAdmin((Player)sender)) Analytics.help_cmd++;
		
		try {
			displayPage(Integer.parseInt(args[0]), sender);
		} catch (Exception ex) {
			displayPage(1, sender);
		}
		return true;
	}

	// ** remake this method in ChatPrint.java that uses TextComponents
	// Clicking on a line indicating it's purpose bring user to second page
	private void displayPage(int page, CommandSender sender) {
		int maxPage = 2;

		page = (page > maxPage) ? maxPage : Math.max(page, 1);

		sender.sendMessage("\u00A76--- Help Page " + page + "/" + maxPage + " ---");
		switch (page) {
			case 1:
				Arrays.asList(
					"\u00A76/stats help: §7Learn how to hide your PVP stats and more",
					"\u00A76/sign: §7Sign the item you are holding. *Cannot undo or overwrite",
					"\u00A76/discord: §7Join the discord",
					"\u00A76/vote: §7Dupe the item in your hand. Only occurs after voting",
					"\u00A76/ignore [player_name]: §7Ignore all messages from given player until next restart"
						
				).forEach(sender::sendMessage);
				break;

			case 2:
				Arrays.asList(
					"\u00A76/vm [player]: §7Vote to mute a player",
					"\u00A76/kit: §7Get a small kit with steak and some starter tools (one-time only)",
					"\u00A76/msg, /w, /r: §7Message or reply to a player",
					"\u00A76/kill: §7Take a guess",
					"\u00A76/server: §7See current speed limit and other server info",
					"\u00A76/tjm: §7Toggle join messages"
					
				).forEach(sender::sendMessage);
				break;
		}
		sender.sendMessage("\u00A76--- Help Page " + page + "/" + maxPage + " ---");
	}
}
