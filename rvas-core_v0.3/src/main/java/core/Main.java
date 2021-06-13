package core;

/* *
 *
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
import static core.data.ThemeManager.replaceDefaultJSON;

import core.commands.*;
import core.commands.restricted.*;
import core.frontend.ChatPrint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World.Environment;

@SuppressWarnings("SpellCheckingInspection")
public class Main extends JavaPlugin {
	public static Plugin instance;

	public final static String version = "0.3.4"; public final static int build = 313;

	public static long worldAge_atStart;
	public static boolean isNewWorld, isOfficialVersion;

	public DiscordBot DiscordHandler;
	public static OfflinePlayer Top = null;

	public final static Logger console = Bukkit.getLogger();

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

		console.log(Level.INFO, "Forcing default gamemode to Survival Mode");
		getServer().setDefaultGameMode(GameMode.SURVIVAL);

		System.out.println();
		System.out.println("[core.main] Loading files");
		System.out.println("[core.main] _____________");

		// LOADING DATA FROM STORAGE \\
		try { FileManager.setup();
		} catch (IOException e) {
			console.log(Level.SEVERE, "Exception in FileManager.setup()"); }

		try { ThemeManager.load();
		} catch (Exception e) { e.printStackTrace(); }

		try { ChatPrint.init();
		} catch (Exception e) {

			console.log(Level.WARNING,
					"Exception in ChatPrint.init().. creating a default theme instead");

			e.printStackTrace();
			ThemeManager.currentTheme.setToInternalDefaults();

			try { replaceDefaultJSON(ThemeManager.currentTheme);
			} catch (IOException ignore) { }

			ChatPrint.init();
		}

		System.out.println();
		System.out.println("[core.main] Loading more files");
		System.out.println("[core.main] __________________");

		try {
			DonationManager.loadDonors();
			PlayerMeta.loadMuted();
			PrisonerManager.loadPrisoners();

		} catch (IOException e) {
			console.log(Level.SEVERE, "Exception while loading data");
			e.printStackTrace();
		}

		System.out.println();
		System.out.println("[core.main] Enabling commands");
		System.out.println("[core.main] _________________");

		// INIT BUKKIT METHODS \\
		System.out.println("/kit");
		Objects.requireNonNull(this.getCommand("kit")).setExecutor(new Kit());

		System.out.println("/mute");
		Objects.requireNonNull(this.getCommand("mute")).setExecutor(new Mute());

		System.out.println("/dupehand");
		Objects.requireNonNull(this.getCommand("dupehand")).setExecutor(new DupeHand());

		System.out.println("/ninjatp");
		Objects.requireNonNull(this.getCommand("ninjatp")).setExecutor(new NinjaTP());

		System.out.println("/vm");
		Objects.requireNonNull(this.getCommand("vm")).setExecutor(new VoteMute());

		System.out.println("/msg");
		Objects.requireNonNull(this.getCommand("msg")).setExecutor(new Message());

		System.out.println("/w");
		Objects.requireNonNull(this.getCommand("w")).setExecutor(new Message());

		System.out.println("/r");
		Objects.requireNonNull(this.getCommand("r")).setExecutor(new Reply());

		System.out.println("/say");
		Objects.requireNonNull(this.getCommand("say")).setExecutor(new Say());

		System.out.println("/discord");
		Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());

		System.out.println("/tps");
		Objects.requireNonNull(this.getCommand("tps")).setExecutor(new Tps());

		System.out.println("/kill");
		Objects.requireNonNull(this.getCommand("kill")).setExecutor(new Kill());

		System.out.println("/setdonator");
		Objects.requireNonNull(this.getCommand("setdonator")).setExecutor(new SetDonator());

		System.out.println("/about");
		Objects.requireNonNull(this.getCommand("about")).setExecutor(new About());

		System.out.println("/vote");
		Objects.requireNonNull(this.getCommand("vote")).setExecutor(new VoteCmd());

		System.out.println("/restart");
		Objects.requireNonNull(this.getCommand("restart")).setExecutor(new RestartCmd());

		System.out.println("/sign");
		Objects.requireNonNull(this.getCommand("sign")).setExecutor(new Sign());

		System.out.println("/admin");
		Objects.requireNonNull(this.getCommand("admin")).setExecutor(new Admin());

		System.out.println("/stats");
		Objects.requireNonNull(this.getCommand("stats")).setExecutor(new Stats());

		System.out.println("/redeem");
		Objects.requireNonNull(this.getCommand("redeem")).setExecutor(new Redeem());

		System.out.println("/tjm");
		Objects.requireNonNull(this.getCommand("tjm")).setExecutor(new ToggleJoinMessages());

		System.out.println("/server");
		Objects.requireNonNull(this.getCommand("server")).setExecutor(new ServerCmd());

		System.out.println("/help");
		Objects.requireNonNull(this.getCommand("help")).setExecutor(new Help());

		System.out.println("/repair");
		Objects.requireNonNull(this.getCommand("repair")).setExecutor(new Repair());

		System.out.println("/slowchat");
		Objects.requireNonNull(this.getCommand("slowchat")).setExecutor(new SlowChat());

		System.out.println("/backup");
		Objects.requireNonNull(this.getCommand("backup")).setExecutor(new Backup());

		System.out.println("/prison");
		Objects.requireNonNull(this.getCommand("prison")).setExecutor(new core.commands.restricted.Prison());

		System.out.println("/info");
		Objects.requireNonNull(this.getCommand("info")).setExecutor(new core.commands.restricted.Info());

		System.out.println("/global");
		Objects.requireNonNull(this.getCommand("global")).setExecutor(new Global());

		System.out.println("/ignore");
		Objects.requireNonNull(this.getCommand("ignore")).setExecutor(new Ignore());

		System.out.println("/afk");
		Objects.requireNonNull(this.getCommand("afk")).setExecutor(new AFK());

		System.out.println("/last");
		Objects.requireNonNull(this.getCommand("last")).setExecutor(new Last());

		System.out.println("/fig");
		Objects.requireNonNull(this.getCommand("fig")).setExecutor(new ConfigCmd());

		System.out.println("/check");
		Objects.requireNonNull(this.getCommand("check")).setExecutor(new Check());

		System.out.println("/speeds");
		Objects.requireNonNull(this.getCommand("speeds")).setExecutor(new Speeds());

		System.out.println("/local");
		Objects.requireNonNull(this.getCommand("local")).setExecutor(new Local());

		System.out.println("/l");
		Objects.requireNonNull(this.getCommand("l")).setExecutor(new Local());

		System.out.println("/donor");
		Objects.requireNonNull(this.getCommand("donor")).setExecutor(new DonorCmd());

		System.out.println("/donate");
		Objects.requireNonNull(this.getCommand("donate")).setExecutor(new Donate());

		System.out.println();
		System.out.println("[core.main] Scheduling synced tasks");
		System.out.println("[core.main] _______________________");

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new Analytics(), 6000L, 6000L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);
		} catch (Exception e) { e.printStackTrace(); }

		System.out.println();
		System.out.println("[core.main] Loading event listeners");
		System.out.println("[core.main] _______________________");

		PluginManager core_pm = getServer().getPluginManager();

		try { core_pm.registerEvents(new ChatListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ConnectionController(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new PVP(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new MoveListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new SpawnController(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new LagManager(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new SpeedLimiter(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ItemCheckTriggers(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new BlockListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ChunkManager(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new OpListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new Check(), this);
		} catch (Exception e) { e.printStackTrace(); }

		// INIT PROTOCOL_LIB METHODS \\
		try {
			PacketListener.C2S_AnimationPackets();
			PacketListener.S2C_MapChunkPackets();
			PacketListener.S2C_WitherSpawnSound();

		} catch (Exception e) { e.printStackTrace(); }

		// OTHER STUFF \\
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
		SpeedLimiter.scheduleSlTask();
		
		// Enable discord notifications for this instance
		DiscordHandler = new DiscordBot();
		getServer().getPluginManager().registerEvents(DiscordHandler, this);

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
			FileManager.backupData(FileManager.donor_database, "donator-backup-", ".db");
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");			
			
		} catch (IOException ex) {
			console.log(Level.WARNING, "Failed to save one or more backup files");
			ex.printStackTrace();
		}
		
		System.out.println();
		System.out.println("[core.main] Overwriting save files");
		System.out.println("[core.main] ______________________");
		
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
		System.out.println("[core.main] Collecting garbage");
		System.out.println("[core.main] __________________");

		int max_age = Integer.parseInt(Config.getValue("wither.skull.max_ticks"));
		int removed_skulls = LagManager.removeSkulls(max_age);

		console.log(Level.INFO, "Found " + removed_skulls + " remaining skull/s to trash..");
		
		System.out.println();
		System.out.println("[core.main] Printing session stats");
		System.out.println("[core.main] ______________________");

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
