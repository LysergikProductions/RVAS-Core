package protocol3.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

// Message

public class Message implements CommandExecutor {

	public static HashMap<UUID, UUID> Replies = new HashMap();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 2) {
			sender.spigot().sendMessage(new TextComponent("§cIncorrect syntax. Syntax: /msg [player] [message]"));
			return true;
		}

		String sendName;

		if (sender instanceof Player) {
			Player p = ((Player) sender);
			sendName = p.getName();
		} else {
			sendName = "Console";
		}

		// Get recipient
		final Player recv = Bukkit.getPlayer(args[0]);
		// Name to use [for stealth]
		String recvName = "";
		// Can't send to offline players
		if (recv == null) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is no longer online."));
			return true;
		}

		if (recvName.equals("")) {
			recvName = recv.getName();
		}

		// Concatenate all messages
		final String[] msg = {""};

		final int[] x = {0};

		Arrays.stream(args).forEach(s ->  {
			if (x[0] == 0) {
				x[0]++;
				return;
			}
			msg[0] += s + " ";
		});
		msg[0] = msg[0].trim();


		// If either player is muted, refuse message.
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerMeta.isMuted(player)) {
				sender.spigot().sendMessage(new TextComponent("§cYou can't send messages."));
				return true;
			}
			if (PlayerMeta.isMuted(recv) || (Admin.MsgToggle.contains(recv.getUniqueId()) && !player.isOp())) {
				sender.spigot().sendMessage(new TextComponent("§cYou can't send messages to this person."));
				return true;
			}
		}

		// Cycle through online players & if they're an admin with spy enabled, send
		// them a copy of this message
		String finalRecvName = recvName;
		Bukkit.getOnlinePlayers().forEach(p -> { if (Admin.Spies.contains(p.getUniqueId())) {
			p.spigot().sendMessage(new TextComponent("§5" + sendName + " to " + finalRecvName + ": " + msg[0]));
		}});

		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.spigot().sendMessage(new TextComponent("§dfrom " + sendName + ": " + msg[0]));
		}
		if (!Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.spigot().sendMessage(new TextComponent("§dto " + recvName + ": " + msg[0]));
		}
		Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());

		return true;
	}

}
