package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.backend.Utilities;

// INTERNAL USE ONLY

public class Restart implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PlayerMeta.isOp(sender)) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		Utilities.restart(args.length != 0);
		return true;
	}

}
