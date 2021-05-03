package core.events;

import core.backend.*;
import core.commands.Admin;
import core.commands.Kit;
import core.objects.PlayerSettings;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class Connection implements Listener {
	
	public static String serverHostname = "RVAS";
	
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		
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
				&& !e.getPlayer().isOp() && serverHostname.equals("test.avas.cc")) {
			e.setKickMessage("§6The test server is closed right now. Please try again later.");
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		if (!ServerMeta.canReconnect(e.getPlayer())) {
			e.setKickMessage("§6Connection throttled. Please wait some time before reconnecting.");
			e.setResult(Result.KICK_OTHER);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.JOIN, e.getPlayer());
		}
		
		if(!PlayerMeta.Playtimes.containsKey(e.getPlayer().getUniqueId())) {
			PlayerMeta.Playtimes.put(e.getPlayer().getUniqueId(), 0.0D);
		}

		Kit.kickedFromKit.remove(e.getPlayer().getUniqueId());

		// Full player check on initial join
		if (Config.getValue("item.illegal.onjoin").equals("true")) {
			e.getPlayer().getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_INVENTORY_ITEM", e.getPlayer()));
			e.getPlayer().getEnderChest().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ENDER_CHEST_ITEM", e.getPlayer()));
			Arrays.stream(e.getPlayer().getInventory().getArmorContents()).forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ARMOR_ITEM", e.getPlayer()));

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInMainHand(), "LOGON_MAIN_HAND", e.getPlayer());

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInOffHand(), "LOGON_OFF_HAND", e.getPlayer());
		}

		// Set survival if enabled; exempt ops
		if (Config.getValue("misc.survival").equals("true") && !e.getPlayer().isOp()) {
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
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
		String tps = new DecimalFormat("#.##").format(LagProcessor.getTPS());

		final String msg = "§3§l        RVA-Survival 1.16.5 §r§7 |  TPS: " + tps +
				"        §r§6§o" + allMotds.get(rnd);

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
