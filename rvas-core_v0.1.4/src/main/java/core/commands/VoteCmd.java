package core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class VoteCmd implements CommandExecutor {

	// This method is called, when somebody uses our command
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		TextComponent message = new TextComponent("Click this message to vote.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraftservers.org/server/612428"));
		message.setColor(ChatColor.GOLD);
		sender.spigot().sendMessage(message);
		// If the player (or console) uses our command correct, we can return true
		return true;
	}
}
