package core;

/* *
 *  About: Main class for RVAS-Core v0.3.4, Paper-Spigot #446+
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

import core.data.*; import core.events.*;
import core.tasks.*; import core.backend.*;
import core.commands.*; import core.commands.op.*;
import static core.data.ThemeManager.replaceDefaultJSON;

import core.backend.ex.*;
import core.frontend.ChatPrint;
import core.backend.cmd.DupeHand;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;

@SuppressWarnings("SpellCheckingInspection")
public class Main extends JavaPlugin {

	public static Plugin instance; public DiscordBot DiscordHandler;

	public final static Logger console = Bukkit.getLogger();
	public final static String version = "0.3.5"; public final static int build = 327;

	public static long worldAge_atStart;
	public static boolean isNewWorld, isOfficialVersion;
	public static OfflinePlayer Top = null;

	private void initCommand(String label, Class<?> clazz) throws InstantiationException, IllegalAccessException {
		if (Config.debug) System.out.println("/" + label); Objects.requireNonNull(
				this.getCommand(label)).setExecutor((CommandExecutor) clazz.newInstance()); }

	private void shutdownWithException(Exception e) {
		console.log(Level.SEVERE, "Failed to enable a critical feature. Shutting down server..");
		e.printStackTrace(); getServer().shutdown(); }

	private boolean isFatal(Exception exception) {
		if (exception.getClass() != CoreException.class) return false;

		CoreException ce = (CoreException) exception;
		Class<?> source = ce.getSourceClass();

		if (source.isAnnotationPresent(Critical.class))
			return source.getAnnotation(Critical.class).isFatal();

		else return false;
	}

	private boolean isPhoenix(Exception exception) {
		if (exception.getClass() != CoreException.class) return false;

		CoreException ce = (CoreException) exception;
		Method method = ce.getSourceMethod();

		if (method.isAnnotationPresent(Phoenix.class))
			return method.getAnnotation(Phoenix.class).doRestart();

		else return false;
	}

	@Override
	public void onEnable() {
		instance = this;

		System.out.println("[core.main] ______________________________");
		System.out.println("[core.main] --- Initializing RVAS-Core ---");
		System.out.println("[core.main] ______________________________");

		// VERSION CHECK \\
		GitGetter.load(); isOfficialVersion = GitGetter.isVersionCurrent();

		if (isOfficialVersion) console.log(Level.INFO, "RVAS-Core is up-to-date!");
		else if (GitGetter.isVersionBeta()) console.log(Level.WARNING, "This is a beta version of RVAS-Core!");
		else console.log(Level.WARNING, "This version is invalid or unrecognized!");

		System.out.println();
		System.out.println("[core.main] _____________");
		System.out.println("[core.main] Setting properties");

		console.log(Level.INFO, "Forcing default gamemode to Survival Mode");
		getServer().setDefaultGameMode(GameMode.SURVIVAL);

		System.out.println();
		System.out.println("[core.main] _____________");
		System.out.println("[core.main] Loading files");

		// LOADING DATA FROM STORAGE \\
		try { FileManager.setup();
		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }

		try { ThemeManager.load();
		} catch (Exception e) { e.printStackTrace(); }

		try { ChatPrint.init();
		} catch (Exception e) {
			console.log(Level.WARNING, "Exception in ChatPrint.init().. creating a default theme instead");

			if (Config.debug) e.printStackTrace();
			ThemeManager.currentTheme.setToInternalDefaults();

			try { replaceDefaultJSON(ThemeManager.currentTheme);
			} catch (IOException ignore) { }

			ChatPrint.init();
		}

		System.out.println();
		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Loading more files");

		try {
			DonationManager.loadDonors();
			PlayerMeta.loadMuted();
			PrisonerManager.loadPrisoners();

		} catch (Exception e) {
			if (isFatal(e)) shutdownWithException(e); else e.printStackTrace();
		}

		System.out.println();
		System.out.println("[core.main] _________________");
		System.out.println("[core.main] Enabling commands");

		// INIT BUKKIT METHODS \\
		try {
			initCommand("mute", Mute.class); initCommand("dupehand", DupeHand.class);
			initCommand("ninjatp", NinjaTP.class); initCommand("vm", VoteMute.class);
			initCommand("msg", Message.class); initCommand("w", Message.class);
			initCommand("r", Reply.class); initCommand("say", Say.class);
			initCommand("tps", Tps.class); initCommand("kill", Kill.class);
			initCommand("setdonator", SetDonorCmd.class); initCommand("donor", DonorCmd.class);
			initCommand("admin", Admin.class); initCommand("redeem", Redeem.class);
			initCommand("backup", Backup.class); initCommand("prison", Prison.class);
			initCommand("repair", Repair.class); initCommand("slowchat", SlowChat.class);
			initCommand("check", Check.class); initCommand("speeds", Speeds.class);
			initCommand("restart", RestartCmd.class); initCommand("info", core.commands.op.Info.class);
			initCommand("server", ServerCmd.class); initCommand("help", Help.class);
			initCommand("local", Local.class); initCommand("l", Local.class);
			initCommand("stats", Stats.class); initCommand("sign", Sign.class);
			initCommand("discord", Discord.class); initCommand("about", About.class);
			initCommand("vote", VoteCmd.class); initCommand("tjm", ToggleJoinMessages.class);
			initCommand("global", Global.class); initCommand("ignore", Ignore.class);
			initCommand("afk", AFK.class); initCommand("last", Last.class);
			initCommand("fig", ConfigCmd.class); initCommand("donate", Donate.class);

		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }

		System.out.println();
		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Scheduling synced tasks");

		try {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Analytics(), 6000L, 6000L);
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);

		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }

		System.out.println();
		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Loading event listeners");

		PluginManager core_pm = getServer().getPluginManager();

		try {
			core_pm.registerEvents(new PVP(), this);
			core_pm.registerEvents(new ChatListener(), this);
			core_pm.registerEvents(new MoveListener(), this);
			core_pm.registerEvents(new SpawnController(), this);
			core_pm.registerEvents(new ConnectionController(), this);
			core_pm.registerEvents(new LagManager(), this);
			core_pm.registerEvents(new SpeedLimiter(), this);
			core_pm.registerEvents(new ItemCheckTriggers(), this);
			core_pm.registerEvents(new BlockListener(), this);
			core_pm.registerEvents(new ChunkManager(), this);
			core_pm.registerEvents(new OpListener(), this);
			core_pm.registerEvents(new Check(), this);

		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }

		// INIT PROTOCOL_LIB METHODS \\
		try {
			PacketListener.C2S_AnimationPackets();
			PacketListener.S2C_MapChunkPackets();
			PacketListener.S2C_WitherSpawnSound();

		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }

		// OTHER STUFF \\
		System.out.println();
		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] ..finishing up..");

		// Define banned & special blocks
		ItemCheck.Banned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK));
		
		// Items that need to be specially rebuilt.
		ItemCheck.Special.addAll(Arrays.asList(Material.ENCHANTED_BOOK, Material.POTION, Material.LINGERING_POTION,
				Material.TIPPED_ARROW, Material.SPLASH_POTION, Material.WRITTEN_BOOK, Material.FILLED_MAP,
				Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD, Material.WRITABLE_BOOK, Material.BEEHIVE,
				Material.BEE_NEST, Material.RESPAWN_ANCHOR, Material.FIREWORK_ROCKET, Material.FIREWORK_STAR,
				Material.SHIELD));
		
		ItemCheck.LegalHeads.addAll(Arrays.asList(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
				Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD));

		// Enable speed limit
		try { SpeedLimiter.scheduleSlTask();
		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }
		
		// Enable discord notifications for this instance
		try {
			DiscordHandler = new DiscordBot();
			getServer().getPluginManager().registerEvents(DiscordHandler, this);

		} catch (Exception e) { if (isFatal(e)) shutdownWithException(e); else e.printStackTrace(); }


		// Load chunk at 0,0 to test for world age
		for (World thisWorld: getServer().getWorlds()) {
			console.log(Level.INFO, "Checking world age..");
			
			if (thisWorld.getEnvironment().equals(Environment.NORMAL)) {
				
				thisWorld.getChunkAt(0, 0).load(true);
				worldAge_atStart = thisWorld.getChunkAt(0, 0).getChunkSnapshot().getCaptureFullTime();

				if (worldAge_atStart < 710) {

					isNewWorld = true;
					console.log(Level.INFO, "This world is NEW! World Ticks: " + worldAge_atStart);

				} else {

					isNewWorld = false;
					console.log(Level.INFO, "This world is not new! World Ticks: " + worldAge_atStart);

				}
				break; // <- only check first normal dimension found
			}
		}

		System.out.println("[core.main] ________________________________");
		System.out.println("[core.main] -- Finished loading RVAS-Core --");
		System.out.println("[core.main] ________________________________");
	}

	@Override
	public void onDisable() {
		System.out.println("[core.main] _____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabling ---");
		System.out.println("[core.main] _____________________________");

		console.log(Level.INFO, "Capturing remaining analytics data..");
		Analytics.capture(); console.log(Level.INFO, "Creating backups..");
		
		try {
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			FileManager.backupData(FileManager.playtime_user_database, "playtimes-backup-", ".db");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");			
			FileManager.backupData(FileManager.donor_database, "donors-backup-", ".json");
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");			
			
		} catch (IOException ex) {
			console.log(Level.WARNING, "Failed to save one or more backup files");
			ex.printStackTrace();
		}
		
		System.out.println();
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Overwriting save files");
		
		try {
			DonationManager.saveDonors();
			PlayerMeta.saveMuted();
			PrisonerManager.savePrisoners();
			
			PlayerMeta.writePlaytime();
			SettingsManager.writePlayerSettings();
			StatsManager.writePVPStats();
			
		} catch (IOException ex) {
			console.log(Level.SEVERE, "Failed to write one or more files");
			ex.printStackTrace();
		}
		
		System.out.println();
		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Collecting garbage");

		int max_age = Integer.parseInt(Config.getValue("wither.skull.max_ticks"));
		int removed_skulls = LagManager.removeSkulls(max_age);

		console.log(Level.INFO, "Found " + removed_skulls + " remaining skull/s to trash..");
		
		System.out.println();
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Printing session stats");

		console.log(Level.INFO, "New Chunks Generated: " + ChunkManager.newCount);
		console.log(Level.INFO, "New Unique Players: " + SpawnController.sessionNewPlayers);
		console.log(Level.INFO, "Total Respawns: " + SpawnController.sessionTotalRespawns);
		console.log(Level.INFO, "Bedrock Placed: " + BlockListener.placedBedrockCounter);
		console.log(Level.INFO, "Bedrock Broken: " + BlockListener.brokenBedrockCounter);
		
		System.out.println("[core.main] ____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabled ---");
		System.out.println("[core.main] ____________________________");
	}
}
