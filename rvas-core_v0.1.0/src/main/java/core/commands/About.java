package core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class About implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		player.spigot().sendMessage(new TextComponent("§7RVAS-core by sinse420. Originally sourced from core.3.1 by d2k11.")); // :)
		TextComponent message = new TextComponent("§7RVAS-core is §7§lopen source§r§7. You can access the GitHub by clicking this message.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/LysergikProductions/RVAS-Core"));
		player.spigot().sendMessage(message);
		return true;
	}
}