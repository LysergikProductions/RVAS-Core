package protocol3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

// funny command haha

public class ToggleJoinMessages implements CommandExecutor {
	public static List<UUID> disabledJoinMessages = new ArrayList<UUID>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (disabledJoinMessages.contains(player.getUniqueId())) {
			player.spigot().sendMessage(new TextComponent("§6Enabled join and leave messages."));
			disabledJoinMessages.remove(player.getUniqueId());
		} else {
			player.spigot().sendMessage(new TextComponent("§6Disabled join and leave messages."));
			disabledJoinMessages.add(player.getUniqueId());
		}
		return true;
	}

}
