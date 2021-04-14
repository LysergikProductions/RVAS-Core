package core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

public class Help implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			displayPage(Integer.parseInt(args[0]), sender);
		} catch (Exception ex) {
			displayPage(1, sender);
		}
		return true;
	}

	// ** remake this method in ChatPrint.java that displays a more configurable
	// TextComponent meta and use a method here to check cases and such and call
	// the relevant help page display method (btw make /sign warning suffix bold)
	private void displayPage(int page, CommandSender sender) {
		int maxPage = 2;

		page = (page > maxPage) ? maxPage : Math.max(page, 1);

		sender.spigot().sendMessage(new TextComponent("§6--- Help Page " + page + "/" + maxPage + " ---"));
		switch (page) {
			case 1:
				Arrays.asList(
						"§6/stats help: §7List all /stats commands",
						"§6/sign: §7Sign the item you are holding. *Cannot undo or overwrite",
						"§6/kill: §7Take a guess",
						"§6/discord: §7Join the discord.",
						//"§6/kit: §7Get a netherite kit with steak and more useful tools.",
						//"§6/vote: §7Dupe the item in your hand. Only occurs after voting.",
						"§6/server: §7See current speed limit and other server info"			
				).forEach(message -> sender.spigot().sendMessage(new TextComponent(message)));
				break;
			case 2:
				Arrays.asList("§6/vm [player]: §7Vote to mute a player",
						"§6/msg, /w, /r: §7Message or reply to a player",
						"§6/tdm: §7Toggle death messages",
						"§6/tjm: §7Toggle join messages"
				).forEach(message -> sender.spigot().sendMessage(new TextComponent(message)));
				break;
		}
		sender.spigot().sendMessage(new TextComponent("§6--- Help Page " + page + "/" + maxPage + " ---"));
	}
}
