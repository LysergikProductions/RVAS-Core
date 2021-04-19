package core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WitherSkull;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import core.backend.*;
import core.commands.*;
import core.events.*;
import core.tasks.*;
import votifier.*;

import java.io.IOException;
import java.util.Arrays;

public class Main extends JavaPlugin implements Listener {
	
	public static Plugin instance;
	public static OfflinePlayer Top = null;

	public Notifications NotificationHandler;

	@Override
	public void onEnable() {

		instance = this;

		System.out.println("[core.main] Initializing RVAS-core");
		System.out.println("[core.main] Loading files..");
		
		try {
			FileManager.setup();
		} catch (IOException e) {
			System.out.println("[core.main] An error occured creating the necessary files.");
		}
		
		System.out.println("[core.main] Loading more files..");
		try {
			PlayerMeta.loadDonators();
			// PlayerMeta.loadMuted(); // throws IOException: 
			PlayerMeta.loadLagfags();
			
		} catch (IOException e) {			
			System.out.println("[core.main] An error occured loading files..");
			System.out.println("[core.main] " + e);
		}
		
		System.out.println("[core.main] Loading commands..");
		this.getCommand("kit").setExecutor(new Kit());
		this.getCommand("mute").setExecutor(new Mute());
		this.getCommand("dupehand").setExecutor(new DupeHand());
		this.getCommand("vm").setExecutor(new VoteMute());
		this.getCommand("msg").setExecutor(new Message());
		this.getCommand("r").setExecutor(new Reply());
		this.getCommand("say").setExecutor(new Say());
		this.getCommand("discord").setExecutor(new Discord());
		this.getCommand("tps").setExecutor(new Tps());
		this.getCommand("kill").setExecutor(new Kill());
		this.getCommand("setdonator").setExecutor(new SetDonator());
		this.getCommand("about").setExecutor(new About());
		this.getCommand("vote").setExecutor(new VoteCmd());
		this.getCommand("restart").setExecutor(new Restart());
		this.getCommand("sign").setExecutor(new Sign());
		this.getCommand("admin").setExecutor(new Admin());
		this.getCommand("stats").setExecutor(new Stats());
		this.getCommand("redeem").setExecutor(new Redeem());
		//this.getCommand("lagfag").setExecutor(new Lagfag());
		this.getCommand("tjm").setExecutor(new ToggleJoinMessages());
		this.getCommand("server").setExecutor(new Server());
		this.getCommand("help").setExecutor(new Help());
		//this.getCommand("global").setExecutor(new Global());

		System.out.println("[core.main] Scheduling synced tasks..");
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);

		System.out.println("[core.main] Loading event listeners..");
		
		getServer().getPluginManager().registerEvents(new Chat(), this);
		getServer().getPluginManager().registerEvents(new Connection(), this);
		getServer().getPluginManager().registerEvents(new Move(), this);
		getServer().getPluginManager().registerEvents(new ItemCheckTriggers(), this);
		getServer().getPluginManager().registerEvents(new LagManager(), this);
		getServer().getPluginManager().registerEvents(new SpeedLimit(), this);
		getServer().getPluginManager().registerEvents(new PVP(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new OpListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnController(), this);
		getServer().getPluginManager().registerEvents(new Voted(), this);
		
		// Disable global wither-spawn sound
		if (Config.getValue("global.sound.no_wither").equals("true")) {
			ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT) {
					
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
		// Banned materials.
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

		System.out.println("[core.main] Finished loading.");
	}

	@Override
	public void onDisable()
	{
		System.out.println("[core.main] Saving files...");
		try {
			PlayerMeta.saveDonators();
			PlayerMeta.saveMuted();
			PlayerMeta.saveLagfags();
			PlayerMeta.writePlaytime();
			PlayerMeta.writePVPStats();
			
		} catch (IOException ex) {
			System.out.println("[core.main] Failed to save one or more files.");
			System.out.println("[core.main] " + ex);
		}
		
		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			System.out.println("Clearing wither skulls in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof WitherSkull) {
					e.remove();
				}
			}
		}
	}
}
