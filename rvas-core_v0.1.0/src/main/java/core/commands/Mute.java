package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.backend.PlayerMeta.MuteType;

// Mute somebody. OPs only.

public class Mute implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String name = "";
		
		if (!PlayerMeta.isOp(sender)) {
			sender.sendMessage("§cYou can't use this.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage("§cInvalid syntax. Syntax: /mute <perm/temp/none/all> [player]");
			return true;
		}
		
		if(sender instanceof Player) { name = ((Player)sender).getName(); }
		else { name = "CONSOLE"; }

		String mode = args[0];
		if (mode.equals("all")) {
			PlayerMeta.MuteAll = !PlayerMeta.MuteAll;
			Bukkit.getServer().spigot()
					.broadcast(PlayerMeta.MuteAll ?
							new TextComponent("§4§l" + name + " §r§4has silenced the chat.") :
							new TextComponent("§a§l" + name + " §r§ahas unsilenced the chat."));
			return true;
		}

		Player toMute = Bukkit.getPlayer(args[1]);
		if (toMute == null) {
			sender.sendMessage("§cPlayer is not online.");
			return true;
		}
		if (toMute.isOp()) {
			sender.sendMessage("§cYou can't mute this person.");
			return true;
		}

		switch (mode.toUpperCase()) {
			case "PERM":
				if(PlayerMeta.isMuted(toMute)) {
					sender.spigot().sendMessage(new TextComponent("§cPlayer is already muted."));
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"§4§l" + name + " §r§4has permanently muted §4§l" + toMute.getName() + " §r§4."));
				PlayerMeta.setMuteType(toMute, MuteType.PERMANENT);
				break;
			case "TEMP":
				if(PlayerMeta.isMuted(toMute)) {
					sender.spigot().sendMessage(new TextComponent("§cPlayer is already muted."));
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"§c§l" + name + " §r§chas temporarily muted §c§l" + toMute.getName() + " §r§c."));
				PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
				break;
			case "NONE":
				if(!PlayerMeta.isMuted(toMute)) {
					sender.spigot().sendMessage(new TextComponent("§cPlayer isn't muted."));
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"§a§l" + name + " §r§ahas unmuted §a§l" + toMute.getName() + "§r§a."));
				PlayerMeta.setMuteType(toMute, MuteType.NONE);
				break;
			default:
				sender.sendMessage("§cInvalid syntax. Syntax: /mute <perm/temp/none/all> [player]");
				return true;
		}
		return true;
	}

}
