package core.commands;

import core.backend.PlayerMeta;
import core.backend.Utilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

// INTERNAL USE ONLY

@SuppressWarnings("deprecation")
public class Restart implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (!PlayerMeta.isOp(sender)) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		Utilities.restart(args.length != 0);
		return true;
	}
}
