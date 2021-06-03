package core.commands;

import core.backend.Config;
import core.backend.ChatPrint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VoteCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		TextComponent message = new TextComponent("Click this message to vote.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Config.getValue("vote.url")));
		message.setColor(ChatPrint.primary);

		sender.sendMessage(message);
		return true;
	}
}
