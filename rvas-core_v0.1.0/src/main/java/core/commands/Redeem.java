package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

// funny command haha

public class Redeem implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (args.length != 1) {
			player.spigot().sendMessage(new TextComponent("§cSyntax: /redeem [code]"));
			return true;
		}

		if (PlayerMeta.isDonator(player)) {
			player.spigot().sendMessage(new TextComponent("§cYou are already a donator. You keep it for life."));
			return true;
		}

		if (PlayerMeta.DonorCodes.contains(args[0]) && !PlayerMeta.UsedDonorCodes.contains(args[0])) {
			PlayerMeta.UsedDonorCodes.add(args[0]);
			PlayerMeta.setDonator(player, true);
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("§6" + player.getName() + " just donated to the server!"));
			return true;
		} else {
			player.spigot().sendMessage(new TextComponent("§cThis code is not valid."));
			return true;
		}
	}

}
