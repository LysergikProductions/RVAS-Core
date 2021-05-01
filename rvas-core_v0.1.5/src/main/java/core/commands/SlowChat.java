package core.commands;



import core.backend.Config;
import core.events.Chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

public class SlowChat implements CommandExecutor {
	
	static String msg;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		if (!player.isOp()) return false;
		
		if (args.length == 0) Chat.slowChatEnabled = !Chat.slowChatEnabled;
		
		if (Chat.slowChatEnabled) msg = "enabled!"; else msg = "disabled!";
		
		player.spigot().sendMessage(new TextComponent("Slow chat is " + msg));
		
		return true;
	}
}
