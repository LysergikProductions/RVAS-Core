package core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import core.backend.Config;


public class Discord implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String link = Config.getValue("discord.link");
		
		if (link != "" && link != "" && link != null) {
			TextComponent message = new TextComponent("Click this message to join the Discord.");
		} else {
			TextComponent message = new TextComponent("Discord coming soon!");
		}
		
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Config.getValue("discord.link")));
		message.setColor(ChatColor.GOLD);
		sender.spigot().sendMessage(message);
		// If the player (or console) uses our command correct, we can return true
		return true;
	}
}
