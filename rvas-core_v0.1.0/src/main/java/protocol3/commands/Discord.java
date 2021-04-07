package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;

// Discord command

public class Discord implements CommandExecutor {

	// This method is called, when somebody uses our command
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		TextComponent message = new TextComponent("Click this message to join the Discord.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Config.getValue("discord.link")));
		message.setColor(ChatColor.GOLD);
		sender.spigot().sendMessage(message);
		// If the player (or console) uses our command correct, we can return true
		return true;
	}

}
