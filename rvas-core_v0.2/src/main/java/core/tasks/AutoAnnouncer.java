package core.tasks;

import core.backend.LagProcessor;
import core.backend.Scheduler;
import core.backend.Config;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AutoAnnouncer extends TimerTask {
	
	private Random r = new Random();

	@Override
	public void run() {
		if (Config.getValue("announcer.enabled").equals("false")) return;
		
		int rnd = r.nextInt(10);

		switch (rnd) {
			case 1:
				Bukkit.spigot()
						.broadcast(new TextComponent("§6You can vote to mute a player by doing §l/vm [playername]."));
				break;
			case 2:
				Bukkit.spigot().broadcast(
						new TextComponent("§6Use /kit anarchy to get some basic starter items, including steak!"));
				break;
			case 3:
				Bukkit.spigot().broadcast(
						new TextComponent("§6You can toggle join messages with /tjm."));
				break;
			//case 4:
				//Bukkit.spigot().broadcast(new TextComponent("§6Lagging the server will result in §lsevere consequences."));
				//break;
			case 5:
				Bukkit.spigot().broadcast(new TextComponent("§6Do /help to see the commands available to you."));
				break;
			case 6:
				Bukkit.spigot().broadcast(new TextComponent("§6Use /server to see the current speed limit and other information."));
				break;
			case 7:
				Bukkit.spigot().broadcast(new TextComponent("§6You can dupe the item/s in your main hand by supporting the server with /vote"));
				break;
			case 8:
				Bukkit.spigot().broadcast(
						new TextComponent("§6You can sign items to show them as uniquely yours by doing §l/sign."));
				break;
			case 9:
				Bukkit.spigot().broadcast(
						new TextComponent("§6Join the Discord by using §l/discord."));
				break;
			default:
				TextComponent source = new TextComponent("RVAS-core is open-source! Click this message to access the repository.");
				source.setColor(ChatColor.GOLD); source.setItalic(true);
				
				source.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/LysergikProductions/RVAS-Core"));
				source.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("see the source code by clicking here")));
				
				Bukkit.spigot().broadcast(source);
				break;
		}
		Scheduler.setLastTaskId("autoAnnounce");
	}
}