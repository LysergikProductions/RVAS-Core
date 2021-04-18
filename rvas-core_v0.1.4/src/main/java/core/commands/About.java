package core.commands;

/* *
 * 
 *  About: Give players an easy command to access
 *  	the license or to access the Github repository
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

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
		
		TextComponent by = new TextComponent("RVAS-core v0.1.2 by sinse420");
		TextComponent source = new TextComponent("RVAS-core is open source. Access the GitHub by clicking this message.");
		TextComponent license = new TextComponent("Licensed under AGPL-3.0.");
		
		by.setColor(ChatColor.RED); by.setBold(true);
		source.setColor(ChatColor.GOLD);
		license.setItalic(true);
		
		source.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/LysergikProductions/RVAS-Core"));
		license.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.gnu.org/licenses/agpl-3.0.en.html"));
		license.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("https://www.gnu.org/licenses/agpl-3.0.en.html")));
		
		Arrays.asList(new TextComponent(""), by, source, license)
		.forEach(ln -> sender.spigot().sendMessage(ln));
		return true;
	}
}