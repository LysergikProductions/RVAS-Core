package core.commands.restricted;

import core.backend.Config;
import core.backend.ChatPrint;
import core.data.objects.Donor;
import core.data.DonationManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
public class SetDonator implements CommandExecutor {

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.sendMessage("\u00A7cYou can't run this."); return true;
		}

		if (args.length != 3) {
			sender.sendMessage("\u00A7cInvalid syntax. Syntax: /setdonator [name] [key] [$amount]"); return true;
		}

		Player donator = sender.getServer().getPlayer(args[0].trim());
		if (donator == null) {
			sender.sendMessage("\u00A7cPlayer is not online.");
			return true;

		}

		String key = args[1].trim();

		try {
			if (!DonationManager.setDonor(new Donor(
					donator.getUniqueId(), key, new Date(), Double.parseDouble(args[2])))
			) return false; // < - return false when setDonor returns false

		} catch (Exception e) {

			sender.sendMessage(new TextComponent(ChatPrint.fail +
					"Failed to set donator. Please report this to " + Config.getValue("admin")).toLegacyText());

			sender.sendMessage(new TextComponent(ChatPrint.faded + e.getMessage()).toLegacyText());
			e.printStackTrace(); return false;
		}

		if (DonationManager.isDonor(donator)) {
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("\u00A76" + donator.getName() + " just donated to the server!"));
			return true;
		}
		return false;
	}
}
