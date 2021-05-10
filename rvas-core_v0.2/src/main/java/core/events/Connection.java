package core.events;

import core.backend.*;
import core.commands.Admin;
import core.commands.Kit;
import core.objects.PlayerSettings;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("deprecation")
public class Connection implements Listener {
	
	public static String serverHostname = "RVAS";
	public static double lastJoinTime = 0.00;
	public static double thisJoinTime = 0.00;
	
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		thisJoinTime = System.currentTimeMillis();

		if (lastJoinTime > 0.00 && joinCounter > 16) {
			if (thisJoinTime - lastJoinTime < 710) {

				e.setKickMessage("§6The server is getting bombarded with connections. Please try again later.");
				e.setResult(Result.KICK_OTHER);
			}
		}

		lastJoinTime = System.currentTimeMillis();

		// Set server name if it's forced
		if(Config.getValue("motd.force").equals("true")) {
			serverHostname = Config.getValue("motd.force.name");
		}
		
		// Get domain name, NOT ip if player is connecting from IP
		if(!Utilities.validIP(e.getHostname()) && serverHostname.equals("unknown")) {
			serverHostname = e.getHostname().split(":")[0];
		}
		
		// Custom whitelist kick
		if(Bukkit.hasWhitelist() && !Bukkit.getWhitelistedPlayers().contains(e.getPlayer())
				&& !e.getPlayer().isOp() && serverHostname.equals("rvas.testing")) {
			e.setKickMessage("§6The test server is closed right now. Please try again later.");
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		if (!ServerMeta.canReconnect(e.getPlayer())) {
			e.setKickMessage("§6Connection throttled. Please wait some time before reconnecting.");
			e.setResult(Result.KICK_OTHER);
		}
	}

	public static int joinCounter = 0;

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		joinCounter++; e.setJoinMessage(null);

		Player thisPlayer = e.getPlayer();
		UUID playerid = e.getPlayer().getUniqueId();

		if (!PlayerMeta.isMuted(thisPlayer) && !Kit.kickedFromKit.contains(playerid)) {
			doJoinMessage(MessageType.JOIN, thisPlayer);
		}
		
		if(!PlayerMeta.Playtimes.containsKey(playerid)) {
			PlayerMeta.Playtimes.put(playerid, 0.0D);
		}

		Kit.kickedFromKit.remove(playerid);

		// Full player check on initial join
		if (Config.getValue("item.illegal.onjoin").equals("true")) {
			thisPlayer.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_INVENTORY_ITEM", thisPlayer));
			thisPlayer.getEnderChest().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ENDER_CHEST_ITEM", thisPlayer));
			Arrays.stream(thisPlayer.getInventory().getArmorContents()).forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ARMOR_ITEM", thisPlayer));

			ItemCheck.IllegalCheck(thisPlayer.getInventory().getItemInMainHand(), "LOGON_MAIN_HAND", thisPlayer);

			ItemCheck.IllegalCheck(thisPlayer.getInventory().getItemInOffHand(), "LOGON_OFF_HAND", thisPlayer);
		}

		// Set survival if enabled; exempt ops
		if (Config.getValue("misc.survival").equals("true") && !thisPlayer.isOp()) {
			thisPlayer.setGameMode(GameMode.SURVIVAL);
		}

		// Send join messages to joining players
		String everyMsg = Config.getValue("join.message.everyJoin").replace('"', ' ').trim();
		String firstMsg = Config.getValue("join.message.firstJoin").replace('"', ' ').trim();

		TextComponent everyComp = new TextComponent(everyMsg); everyComp.setColor(ChatColor.BLUE);
		TextComponent firstComp = new TextComponent(firstMsg); firstComp.setColor(ChatColor.BLUE);

		if (thisPlayer.hasPlayedBefore()) {
			if (!everyMsg.equals("")) thisPlayer.spigot().sendMessage(everyComp);
		} else {
			if (!firstMsg.equals("")) thisPlayer.spigot().sendMessage(firstComp);
		}
	}

	public enum MessageType {
		JOIN, LEAVE
	}

	public void doJoinMessage(MessageType msg, Player player) {
		if (player.isOp()) return;
		
		String messageOut = "§7" + player.getName()
				+ ((msg.equals(MessageType.JOIN)) ? " joined the game." : " left the game.");
		
		Bukkit.getOnlinePlayers().forEach(player1 ->{
			
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player1.getUniqueId());
			
			if (PlayerMeta.getSettings(offPlayer).show_player_join_messages) {player1.sendMessage(messageOut);}
			else {
				PlayerSettings newSettings = PlayerMeta.getNewSettings(offPlayer);
				PlayerMeta.sPlayerSettings.put(newSettings.playerid, newSettings);
			}
		});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {

		e.setQuitMessage(null);
		if (e.getPlayer().isOp()) return;
		
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.LEAVE, e.getPlayer());
		}
		Location l = e.getPlayer().getLocation();            //store Location floored to block
		Admin.LogOutSpots.put(e.getPlayer().getName(), l);
		ServerMeta.preventReconnect(e.getPlayer(), Integer.parseInt(Config.getValue("speedlimit.rc_delay_safe")));
	}

	private final String[] motds = {
		"i'm not high, we're high" , "RIP boiling water, you will be mist" , "vanilla exploits rejoice!" , "needs more carpet" ,
		"imagine imagining..", "now with less fat!"
	};

	private Random r = new Random();

	private List<String> allMotds = new ArrayList<>();

	private boolean done = false;

	@EventHandler
	public void onPing(ServerListPingEvent e) {

		if (!done) {
			try {
				allMotds = new ArrayList<>(Arrays.asList(motds));
				System.out.println("[core.events.connection] Loading " + motds.length + " default MOTDs...");
				allMotds.addAll(Files.readAllLines(Paths.get("plugins/core/motds.txt")));
			} catch (IOException e1) {
				allMotds = new ArrayList<>(Arrays.asList(motds));
			}
			done = true;
			System.out.println("[core.events.connection] Loaded " + allMotds.size() + " MOTDs");
		}

		int rnd = r.nextInt(allMotds.size());

		String tps = new DecimalFormat("0.00").format(LagProcessor.getTPS());

		final String msg = "§3§l        RVA-Survival 1.16.5 §r§7 |  TPS: " + tps +
				"           §r§6§o" + allMotds.get(rnd);

		e.setMotd(msg);

		if(serverHostname.equals("test")) {
			if(Bukkit.hasWhitelist()) {
				e.setMotd("§9rvas test §7| §4closed §7| §9TPS: " + tps);
			}
			else {
				e.setMotd("§9rvas test §7| §aopen §7| §9TPS: " + tps);
			}
		}
		e.setMaxPlayers(13);
	}
}
