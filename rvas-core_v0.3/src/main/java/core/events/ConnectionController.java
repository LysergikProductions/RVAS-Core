package core.events;

import core.Main;
import core.backend.*;
import core.backend.utils.Util;
import core.frontend.ChatPrint;
import core.commands.Kit;
import core.commands.op.Admin;
import core.frontend.GUI.SL;
import core.tasks.TickProcessor;

import core.data.DonationManager;
import core.data.PlayerMeta;
import core.data.SettingsManager;
import core.data.objects.Donor;
import core.data.objects.SettingsContainer;
import core.backend.ex.Critical;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

@Critical
@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class ConnectionController implements Listener {
	
	public static String serverHostname = "RVAS";
	public static double lastJoinTime = 0.00;
	public static double thisJoinTime = 0.00;

	@EventHandler
	public void onPreJoin(AsyncPlayerPreLoginEvent event) {
		InetAddress thisAddress = event.getAddress();

		String playerName = event.getName();
		String playerIP = thisAddress.getHostName();
		UUID playerID = event.getUniqueId();

		boolean isMulti = thisAddress.isMulticastAddress();
		boolean isLoopback = thisAddress.isLoopbackAddress();

		if (isMulti) {
			Main.console.log(Level.WARNING,
					"MULTI IP " + playerIP + " with name (" + playerName + ") and UUID (" + playerID + ")");

			event.setKickMessage("\u00A76You are using a multi ip. You cannot connect like this.");
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			return;
		}

		if (isLoopback) Main.console.log(Level.WARNING,
				"LOCAL LOOPBACK IP " + playerIP + " with name (" + playerName + ")");

		else Main.console.log(Level.INFO,
				"Connection Attempt: " + playerID + " from IP: " + playerIP + " with name " + playerName);

		//TODO: Get timezone by ip
	}

	@EventHandler (ignoreCancelled = true)
	public void onConnect(PlayerLoginEvent e) {
		thisJoinTime = System.currentTimeMillis();

		if (lastJoinTime > 0.00 && joinCounter > 16) {
			if (thisJoinTime - lastJoinTime < 710) {

				e.setKickMessage("\u00A76The server is getting bombarded with connections. Please try again later.");
				e.setResult(Result.KICK_OTHER);
			}
		}

		lastJoinTime = System.currentTimeMillis();

		// Set server name if it's forced
		if(Config.getValue("motd.force").equals("true")) {
			serverHostname = Config.getValue("motd.force.name"); }
		
		// Get domain name, NOT ip if player is connecting from IP
		if(!Util.validServerIP(e.getHostname()) && serverHostname.equals("unknown")) {
			serverHostname = e.getHostname().split(":")[0]; }
		
		// Custom whitelist kick
		if(Bukkit.hasWhitelist() && !Bukkit.getWhitelistedPlayers().contains(e.getPlayer())
				&& !e.getPlayer().isOp() && serverHostname.equals("rvas.testing")) {
			e.setKickMessage("\u00A76The test server is closed right now. Please try again later.");
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		if (!ServerMeta.canReconnect(e.getPlayer())) {
			e.setKickMessage("\u00A76Connection throttled. Please wait some time before reconnecting.");
			e.setResult(Result.KICK_OTHER);
		}
	}

	public static int joinCounter = 0;

	@EventHandler (ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		joinCounter++; e.setJoinMessage(null);

		Player thisPlayer = e.getPlayer();
		UUID playerid = e.getPlayer().getUniqueId();

		if (!PlayerMeta.isMuted(thisPlayer) && !Kit.kickedFromKit.contains(playerid)) {
			doJoinMessage(MessageType.JOIN, thisPlayer);
		}
		
		if(!PlayerMeta.Playtimes.containsKey(playerid)) {
			PlayerMeta.Playtimes.put(playerid, 0.0D); }

		Kit.kickedFromKit.remove(playerid);

		// Full player check on initial join
		if (Config.getValue("item.illegal.onjoin").equals("true")) {

			thisPlayer.getInventory().forEach(itemStack ->
					ItemCheck.IllegalCheck(itemStack, "LOGON_INVENTORY_ITEM", thisPlayer));

			thisPlayer.getEnderChest().forEach(itemStack ->
					ItemCheck.IllegalCheck(itemStack, "LOGON_ENDER_CHEST_ITEM", thisPlayer));

			Arrays.stream(thisPlayer.getInventory().getArmorContents()).forEach(itemStack ->
					ItemCheck.IllegalCheck(itemStack, "LOGON_ARMOR_ITEM", thisPlayer));

			ItemCheck.IllegalCheck(thisPlayer.getInventory().getItemInMainHand(), "LOGON_MAIN_HAND", thisPlayer);
			ItemCheck.IllegalCheck(thisPlayer.getInventory().getItemInOffHand(), "LOGON_OFF_HAND", thisPlayer);
		}

		// Set survival if enabled; exempt ops
		if (Config.getValue("misc.survival").equals("true") && !thisPlayer.isOp()) {
			thisPlayer.setGameMode(GameMode.SURVIVAL); }

		// Send join messages to joining players
		String everyMsg = Config.getValue("join.message.everyJoin").replace('"', ' ').trim();
		String firstMsg = Config.getValue("join.message.firstJoin").replace('"', ' ').trim();

		TextComponent everyComp = new TextComponent(everyMsg); everyComp.setColor(ChatPrint.tertiary);
		TextComponent firstComp = new TextComponent(firstMsg); firstComp.setColor(ChatPrint.tertiary);

		if (thisPlayer.hasPlayedBefore() && !everyMsg.isEmpty()) thisPlayer.spigot().sendMessage(everyComp);
		else if (!everyMsg.isEmpty()) thisPlayer.spigot().sendMessage(firstComp);
	}

	public enum MessageType { JOIN, LEAVE }

	public void doJoinMessage(MessageType msg, Player player) {
		if (player.isOp()) return;
		
		String messageOut = "\u00A77" + player.getName()
				+ ((msg.equals(MessageType.JOIN)) ? " joined the game." : " left the game.");
		
		Bukkit.getOnlinePlayers().forEach(player1 ->{
			
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player1.getUniqueId());
			
			if (SettingsManager.getSettings(offPlayer).show_player_join_messages) {player1.sendMessage(messageOut);}
			else {
				SettingsContainer newSettings = SettingsManager.getNewSettings(offPlayer);
				PlayerMeta.sPlayerSettings.put(newSettings.playerid, newSettings);
			}
		});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(null);

		Player leaver = e.getPlayer();
		if (leaver.isOp()) return;
		
		if (!PlayerMeta.isMuted(leaver) && !Kit.kickedFromKit.contains(leaver.getUniqueId())) {
			doJoinMessage(MessageType.LEAVE, leaver); }

		Location l = leaver.getLocation();
		Admin.LogOutSpots.put(leaver.getName(), l);
		ServerMeta.preventReconnect(leaver, Integer.parseInt(Config.getValue("speedlimit.rc_delay_safe")));

		SL.rmSlViewer(leaver);
	}

	private static final String[] motds = {
		"⛏ i'm not high, we're high"  , "⛏ vanilla exploits rejoice!" , "⛏ needs more carpet" ,
		"⛏ imagine imagining..", "⛏ what is sleep?" };

	private final Random r = new Random();

	private static List<String> allMotds; static {
		try {
			Main.console.log(Level.INFO, "Loading " + motds.length + " default MOTDs...");

			allMotds = new ArrayList<>(Arrays.asList(motds));
			allMotds.addAll(Files.readAllLines(Paths.get("plugins/core/motds.txt")));

			for (Donor thisDonor: DonationManager._donorList) {
				String thisMOTD = thisDonor.getMsgOtd();

				if (DonationManager.isValidString(thisMOTD)) {
					allMotds.add(thisMOTD);

					Main.console.log(Level.INFO,
							Bukkit.getServer().getPlayer(thisDonor.getUserID()).getName()
									+ " MOTD added: " + thisMOTD);
				}
			}
		} catch (IOException ignore) { allMotds = new ArrayList<>(Arrays.asList(motds)); }

		Main.console.log(Level.INFO, "Loaded " + allMotds.size() + " MOTDs");
	}

	@EventHandler
	public void onPing(ServerListPingEvent e) {

		int rnd = r.nextInt(allMotds.size());
		String tps = new DecimalFormat("0.00").format(TickProcessor.getTPS());

		final String msg1 = "\u00A73\u00A7lRVA-Survival 1.16.5 \u00A7r\u00A77 |  TPS: " + tps;
		final String msg2 = "\u00A7r\u00A76\u00A7o" + allMotds.get(rnd);

		e.setMotd(center(msg1) + msg2);

		if(serverHostname.equals("test")) {
			if(Bukkit.hasWhitelist()) {
				e.setMotd("\u00A79rvas test \u00A77| \u00A74closed \u00A77| \u00A79TPS: " + tps);
			}
			else e.setMotd("\u00A79rvas test \u00A77| \u00A7aopen \u00A77| \u00A79TPS: " + tps);
		}
		e.setMaxPlayers(420);
	}

	private static String center(String ln) {
		StringBuilder whiteSpace = new StringBuilder();

		// trim and truncate strings
		String trimmed = ln.trim();
		int charTotal = trimmed.length();

		final String trunc;
		if (charTotal >= 60) trunc = trimmed.substring(0, 60);
		else trunc = trimmed;

		// count missing whitespace
		int diff = 60 - charTotal;
		int half = diff / 2;

		int i = 1;
		while (i <= half) { i++; whiteSpace.append(" "); }

		// add the whitespace and make the tail end a bit longer
		StringBuilder out = new StringBuilder(whiteSpace + trunc + whiteSpace);
		while (out.length() < 62) out.append(" ");

		return out.toString();
	}

	public static boolean init() {

		try {
			allMotds = new ArrayList<>(Arrays.asList(motds));
			Main.console.log(Level.INFO, "[core.events.connection] Loading " + motds.length + " default MOTDs...");
			allMotds.addAll(Files.readAllLines(Paths.get("plugins/core/motds.txt")));
			return true;

		} catch (Exception e) {
			allMotds = new ArrayList<>(Arrays.asList(motds));
			e.printStackTrace(); return false;
		}
	}
}
