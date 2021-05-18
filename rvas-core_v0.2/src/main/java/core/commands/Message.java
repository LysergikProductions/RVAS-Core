package core.commands;

import core.tasks.Analytics;
import core.backend.PlayerMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Message implements CommandExecutor {

	public static HashMap<UUID, UUID> Replies = new HashMap<>();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (args.length < 2) {
			sender.sendMessage("\u00A7cIncorrect syntax. Syntax: /msg [player] [message]");
			return true;
		}

		String sendName;

		if (sender instanceof Player) {
			Player p = ((Player) sender);
			sendName = p.getName();
		} else {
			sendName = "Console";
		}

		if (sender instanceof Player && !PlayerMeta.isAdmin((Player) sender)) Analytics.msg_cmd++;

		// Get recipient
		final Player recv = Bukkit.getPlayer(args[0]);
		// Name to use [for stealth]
		String recvName;
		// Can't send to offline players
		if (recv == null) {
			sender.sendMessage("\u00A7cPlayer is no longer online.");
			return true;
		}

		recvName = recv.getName();

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
				sender.sendMessage("\u00A7cYou can't send messages.");
				return true;
			}

			if (PlayerMeta.isMuted(recv) || (Admin.MsgToggle.contains(recv.getUniqueId()) && !player.isOp())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}

			if(PlayerMeta.isIgnoring(player.getUniqueId(), recv.getUniqueId())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}

			if(PlayerMeta.isIgnoring(recv.getUniqueId(), player.getUniqueId())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}
		}

		// Cycle through online players & if they're an admin with spy enabled, send
		// them a copy of this message
		String finalRecvName = recvName;
		Bukkit.getOnlinePlayers().forEach(p -> { if (Admin.Spies.contains(p.getUniqueId())) {
			p.sendMessage("\u00A75" + sendName + " to " + finalRecvName + ": " + msg[0]);
		}});

		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.sendMessage("\u00A7dfrom " + sendName + ": " + msg[0]);
		}
		if (sender instanceof Player && !Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.sendMessage("\u00A7dto " + recvName + ": " + msg[0]);
		}
		if (sender instanceof Player) {
			Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		}
		if (sender instanceof Player) {
			Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());
		}

		return true;
	}
}
