package core.commands.op;

import core.data.PlayerMeta;
import core.frontend.ChatPrint;
import core.backend.utils.Restart;
import core.backend.ex.Critical;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// INTERNAL USE ONLY

@Critical
public class RestartCmd implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

		if (!PlayerMeta.isOp(sender)) {
			sender.sendMessage(ChatPrint.fail + "You can't run this"); return false; }

		Restart.restart(args.length != 0); return true;
	}
}
