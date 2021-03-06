package core.commands;

import core.data.PlayerMeta;
import core.tasks.Analytics;
import core.frontend.ChatPrint;
import core.commands.op.Admin;
import core.backend.ex.Critical;

import java.util.UUID;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class Reply implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		Player p = (Player) sender;
		String sendName = p.getName();

		if (args.length < 1) {
			p.sendMessage(ChatPrint.fail + "Incorrect syntax. Syntax: /r [message]");
			return true;
		}
		if (!Message.Replies.containsKey(p.getUniqueId())) {
			p.sendMessage(ChatPrint.fail + "Nobody to reply to.");
			return true;
		}
		if (Message.Replies.get(p.getUniqueId()) == null) {
			p.sendMessage(ChatPrint.fail + "Can't reply to Console.");
			return true;
		}
		
		if (!PlayerMeta.isAdmin(p)) Analytics.r_cmd++;

		// remove AFK statuses
		UUID pid = p.getUniqueId();
		if (AFK._AFKs.contains(pid)) {

			Message.AFK_warned.remove(pid);
			AFK._AFKs.remove(pid);

			p.sendMessage(ChatPrint.succeed + "You are no longer AFK!");
		}

		// Get recipient
		Player recv = Bukkit.getPlayer(Message.Replies.get(p.getUniqueId()));
		String recvName;

		if (recv == null) {
			sender.sendMessage(ChatPrint.fail + "Player is not online.");
			return true;
		} else if (AFK._AFKs.contains(recv.getUniqueId())) {
			sender.sendMessage(ChatPrint.fail + "Player is currently AFK.");
			return true;
		}

		recvName = recv.getName();

		// Muted players can't send or recieve messages.
		if (PlayerMeta.isMuted(p)) {
			sender.sendMessage(ChatPrint.fail + "You can't send messages.");
			return true;
		} else if (PlayerMeta.isMuted(recv)) {
			sender.sendMessage(ChatPrint.fail + "You can't send messages to this person.");
			return true;
		}

		// Concatenate
		final String[] msg = new String[]{""};
		Arrays.asList(args).forEach(ln -> msg[0] += ln + " ");
		msg[0] = msg[0].trim();

		String finalRecvName = recvName;
		Bukkit.getOnlinePlayers().forEach(pl -> {
			if (Admin.Spies.contains(pl.getUniqueId())) {
				pl.sendMessage("\u00A75" + sendName + " to " + finalRecvName + ": " + msg[0]);
			}
		});

		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.sendMessage("\u00A7dfrom " + sendName + ": " + msg[0]);
		}
		if (!Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.sendMessage("\u00A7dto " + recvName + ": " + msg[0]);
		}
		Message.Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		Message.Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());
		return true;
	}
}
