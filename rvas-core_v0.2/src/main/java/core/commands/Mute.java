package core.commands;

import core.backend.PlayerMeta;
import core.backend.PlayerMeta.MuteType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Mute implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		String name;

		if (sender instanceof Player) name = sender.getName();
		else name = "CONSOLE";

		if (!PlayerMeta.isOp(sender)) {
			sender.sendMessage("§cYou can't use this.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage("§cInvalid syntax. Syntax: /mute <perm/temp/none/all> [player]");
			return true;
		}

		String mode = args[0];
		if (mode.equals("all")) {
			PlayerMeta.MuteAll = !PlayerMeta.MuteAll;
			Bukkit.getServer().spigot()
					.broadcast(PlayerMeta.MuteAll ?
							new TextComponent("§4§l" + name + " §r§4has silenced the chat.") :
							new TextComponent("§a§l" + name + " §r§ahas un-silenced the chat."));
			return true;
		}

		Player toMute = null;
		try {
			if (args[1] != null) toMute = Bukkit.getPlayer(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("/mute probably entered incorrectly..");
			sender.spigot().sendMessage(new TextComponent("Syntax: /mute [type] [player]"));
		} catch (NullPointerException e) {
			System.out.println("/mute target is null");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (toMute == null) {
			sender.sendMessage("Player is not online.");
			return true;
		}
		if (toMute.isOp()) {
			sender.sendMessage("You can't mute this person.");
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
						"§a§l" + name + " §r§ahas un-muted §a§l" + toMute.getName() + "§r§a."));
				PlayerMeta.setMuteType(toMute, MuteType.NONE);
				break;
			case "IP":
				if(PlayerMeta.isMuted(toMute)) {
					sender.spigot().sendMessage(new TextComponent("§cIP is already muted."));
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"§4§l" + name + " §r§4has IP muted §4§l" + toMute.getName() + "§r§4."));
				PlayerMeta.setMuteType(toMute, MuteType.IP);
			default:
				sender.sendMessage("§cInvalid syntax. Syntax: /mute <perm/temp/ip/none/all> [player]");
				return true;
		}
		return true;
	}
}