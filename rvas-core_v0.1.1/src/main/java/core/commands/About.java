package core.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class About implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		
		TextComponent by = new TextComponent("RVAS-core by sinse420. Originally sourced from protocol3 by d2k11.");
		TextComponent source = new TextComponent("RVAS-core is open source. Access the GitHub by clicking this message.");
		TextComponent license = new TextComponent("Licensed under AGPL-3.0.");
		
		by.setColor(ChatColor.RED); by.setBold(true);
		source.setColor(ChatColor.GREEN);
		license.setItalic(true);
		
		source.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/LysergikProductions/RVAS-Core"));
		license.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.gnu.org/licenses/agpl-3.0.en.html"));
		license.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("https://www.gnu.org/licenses/agpl-3.0.en.html")));
		
		Arrays.asList(new TextComponent(""), by, source, license)
		.forEach(ln -> sender.spigot().sendMessage(ln));
		return true;
	}
}
