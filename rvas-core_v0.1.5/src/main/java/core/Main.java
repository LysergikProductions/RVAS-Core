package core;

import core.backend.*;
import core.commands.*;
import core.events.*;
import core.tasks.*;
import votifier.*;

import java.io.IOException;
import java.util.Arrays;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WitherSkull;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.GameMode;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public static Plugin instance;
	
	public static OfflinePlayer Top = null;
	public Notifications NotificationHandler;
	
	public static long worldAge_atStart;
	public static boolean isNewWorld;
	
	@Override
	public void onEnable() {

		instance = this;
		boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
		
		System.out.println("[core.main] ______________________________");
		System.out.println("[core.main] --- Initializing RVAS-Core ---");
		System.out.println("[core.main] ______________________________");
		
		System.out.println("forcing default gamemode..");
		getServer().setDefaultGameMode​(GameMode.SURVIVAL);
		
		System.out.println("[core.main] _____________");
		System.out.println("[core.main] Loading files");
		System.out.println("[core.main] _____________");
		
		try {
			FileManager.setup();
		} catch (IOException e) {
			System.out.println("[core.main] An error occured in FileManager.setup()");
		}
		
		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Loading more files");
		System.out.println("[core.main] __________________");
		try {
			PlayerMeta.loadDonators();
			PlayerMeta.loadMuted();
			PlayerMeta.loadLagfags();
			
		} catch (IOException e) {			
			System.out.println("[core.main] An error occured loading files..");
			System.out.println("[core.main] " + e);
		}
		
		System.out.println("[core.main] _________________");
		System.out.println("[core.main] Enabling commands");
		System.out.println("[core.main] _________________");
		
		System.out.println("/kit");
		this.getCommand("kit").setExecutor(new Kit());
		
		System.out.println("/mute");
		this.getCommand("mute").setExecutor(new Mute());
		
		System.out.println("/dupehand");
		this.getCommand("dupehand").setExecutor(new DupeHand());
		
		System.out.println("/vm");
		this.getCommand("vm").setExecutor(new VoteMute());
		
		System.out.println("/msg");
		this.getCommand("msg").setExecutor(new Message());
		
		System.out.println("/r");
		this.getCommand("r").setExecutor(new Reply());
		
		System.out.println("/say");
		this.getCommand("say").setExecutor(new Say());
		
		System.out.println("/discord");
		this.getCommand("discord").setExecutor(new Discord());
		
		System.out.println("/tps");
		this.getCommand("tps").setExecutor(new Tps());
		
		System.out.println("/kill");
		this.getCommand("kill").setExecutor(new Kill());
		
		System.out.println("/setdonator");
		this.getCommand("setdonator").setExecutor(new SetDonator());
		
		System.out.println("/about");
		this.getCommand("about").setExecutor(new About());
		
		System.out.println("/vote");
		this.getCommand("vote").setExecutor(new VoteCmd());
		
		System.out.println("/restart");
		this.getCommand("restart").setExecutor(new Restart());
		
		System.out.println("/sign");
		this.getCommand("sign").setExecutor(new Sign());
		
		System.out.println("/admin");
		this.getCommand("admin").setExecutor(new Admin());
		
		System.out.println("/stats");
		this.getCommand("stats").setExecutor(new Stats());
		
		System.out.println("/redeem");
		this.getCommand("redeem").setExecutor(new Redeem());
		
		System.out.println("/tjm");
		this.getCommand("tjm").setExecutor(new ToggleJoinMessages());
		
		System.out.println("/server");
		this.getCommand("server").setExecutor(new Server());
		
		System.out.println("/help");
		this.getCommand("help").setExecutor(new Help());
		
		System.out.println("/repair");
		this.getCommand("repair").setExecutor(new Repair());
		
		System.out.println("/slowchat");
		this.getCommand("slowchat").setExecutor(new SlowChat());
		
		System.out.println("/backup");
		this.getCommand("backup").setExecutor(new Backup());
		
		System.out.println("/prison");
		this.getCommand("prison").setExecutor(new Prison());
		
		System.out.println("/info");
		this.getCommand("info").setExecutor(new Info());
		
		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Scheduling synced tasks");
		System.out.println("[core.main] _______________________");
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Analytics(), 6000L, 6000L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);			
		
		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Loading event listeners");
		System.out.println("[core.main] _______________________");
		
		PluginManager core_pm = getServer().getPluginManager();
		ProtocolManager plib_manager = ProtocolLibrary.getProtocolManager();
		
		core_pm.registerEvents(new Chat(), this);
		core_pm.registerEvents(new Connection(), this);
		core_pm.registerEvents(new Voted(), this);
		
		core_pm.registerEvents(new PVP(), this);
		core_pm.registerEvents(new Move(), this);
		core_pm.registerEvents(new SpawnController(), this);
		
		core_pm.registerEvents(new LagManager(), this);
		core_pm.registerEvents(new SpeedLimit(), this);
		core_pm.registerEvents(new ItemCheckTriggers(), this);
		
		NoGhost.C2S_Packets();
		core_pm.registerEvents(new BlockListener(), this);
		core_pm.registerEvents(new ChunkListener(), this);
		core_pm.registerEvents(new OpListener(), this);
		
		
		// Disable global wither-spawn sound
		if (Config.getValue("global.sound.no_wither").equals("true")) {
			plib_manager.addPacketListener(new PacketAdapter(
					this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT) {
					
					@Override
					public void onPacketSending(PacketEvent event) {
						
						PacketContainer packetContainer = event.getPacket();
						
						if (packetContainer.getIntegers().read(0) == 1023) {
							packetContainer.getBooleans().write(0, false);
						}
					}
				});
		}
		
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
		SpeedLimit.scheduleSlTask();
		
		// Enable discord notifications for this instance
		NotificationHandler = new Notifications();
		getServer().getPluginManager().registerEvents(NotificationHandler, this);
		
		// Load chunk at 0,0 to test for world age
		for (World thisWorld: getServer().getWorlds()) {
			
			if (thisWorld.getEnvironment().equals(Environment.NORMAL)) {
				
				thisWorld.getChunkAt(0, 0).load(true);
				
				worldAge_atStart = thisWorld.getChunkAt(0, 0)
						.getChunkSnapshot().getCaptureFullTime();
				
				if (worldAge_atStart < 600) {
					
					System.out.println("[core.main] This world is NEW! World Ticks: " + worldAge_atStart);
					isNewWorld = true; break; // <- only check first normal dimension found
				}
				else {
					
					System.out.println("[core.main] This world is not new! World Ticks: " + worldAge_atStart);
					isNewWorld = false; break; // <- only check first normal dimension found
				}
			}
		}
		
		System.out.println("[core.main] ________________________________");
		System.out.println("[core.main] -- Finished loading RVAS-Core --");
		System.out.println("[core.main] ________________________________");
	}

	@Override
	public void onDisable()
	{
		System.out.println("[core.main] _____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabling ---");
		System.out.println("[core.main] _____________________________");
		
		// final analytics capture for this session
		Analytics.capture();
		
		System.out.println("[core.main] ________________");
		System.out.println("[core.main] Creating backups");
		System.out.println("[core.main] ________________");
		
		try {
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			FileManager.backupData(FileManager.playtime_user_database, "playtimes-backup-", ".db");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");			
			FileManager.backupData(FileManager.donor_list, "donator-backup-", ".db");
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");			
			
		} catch (IOException ex) {
			System.out.println("[core.main] WARNING - Failed to save one or more backup files.");
			System.out.println("[core.main] " + ex);
		}
		
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Overwriting save files");
		System.out.println("[core.main] ______________________");
		
		try {
			PlayerMeta.saveDonators();
			PlayerMeta.saveMuted();
			PlayerMeta.saveLagfags();
			
			PlayerMeta.writePlaytime();
			PlayerMeta.writePlayerSettings();
			PVPdata.writePVPStats();
			
		} catch (IOException ex) {
			System.out.println("[core.main] WARNING - Failed to write one or more files.");
			System.out.println("[core.main] " + ex);
		}
		
		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Collecting garbage");
		System.out.println("[core.main] __________________");
		
		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			System.out.println("Clearing wither skulls in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof WitherSkull) {
					e.remove();
				}
			}
		}
		
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Printing session stats");
		System.out.println("[core.main] ______________________");
		
		System.out.println("New Chunks Generated: " + ChunkListener.newCount);
		System.out.println("New Unique Players: " + SpawnController.sessionNewPlayers);
		System.out.println("Total Respawns: " + SpawnController.sessionTotalRespawns);
		System.out.println("Bedrock Placed: " + BlockListener.placedBedrockCounter);
		System.out.println("Bedrock Broken: " + BlockListener.brokenBedrockCounter);
		
		System.out.println("[core.main] ____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabled ---");
		System.out.println("[core.main] ____________________________");
	}
}
