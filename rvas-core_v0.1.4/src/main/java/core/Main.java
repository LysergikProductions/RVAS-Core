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
import org.bukkit.plugin.PluginManager;
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
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));

	public Notifications NotificationHandler;

	@Override
	public void onEnable() {

		instance = this;

		System.out.println("[core.main] --- Initializing RVAS-Core ---");
		System.out.println("[core.main] Loading files..");
		
		try {
			FileManager.setup();
		} catch (IOException e) {
			System.out.println("[core.main] An error occured in FileManager.setup()");
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
		
		System.out.println("[core.main] Enabling commands..");
		
		if (debug) System.out.println(".. /kit ..");
		this.getCommand("kit").setExecutor(new Kit());
		
		if (debug) System.out.println(".. /mute ..");
		this.getCommand("mute").setExecutor(new Mute());
		
		if (debug) System.out.println(".. /dupehand ..");
		this.getCommand("dupehand").setExecutor(new DupeHand());
		
		if (debug) System.out.println(".. /vm ..");
		this.getCommand("vm").setExecutor(new VoteMute());
		
		if (debug) System.out.println(".. /msg ..");
		this.getCommand("msg").setExecutor(new Message());
		
		if (debug) System.out.println(".. /r ..");
		this.getCommand("r").setExecutor(new Reply());
		
		if (debug) System.out.println(".. /say ..");
		this.getCommand("say").setExecutor(new Say());
		
		if (debug) System.out.println(".. /discord ..");
		this.getCommand("discord").setExecutor(new Discord());
		
		if (debug) System.out.println(".. /tps ..");
		this.getCommand("tps").setExecutor(new Tps());
		
		if (debug) System.out.println(".. /kill ..");
		this.getCommand("kill").setExecutor(new Kill());
		
		if (debug) System.out.println(".. /setdonator ..");
		this.getCommand("setdonator").setExecutor(new SetDonator());
		
		if (debug) System.out.println(".. /about ..");
		this.getCommand("about").setExecutor(new About());
		
		if (debug) System.out.println(".. /vote ..");
		this.getCommand("vote").setExecutor(new VoteCmd());
		
		if (debug) System.out.println(".. /restart ..");
		this.getCommand("restart").setExecutor(new Restart());
		
		if (debug) System.out.println(".. /sign ..");
		this.getCommand("sign").setExecutor(new Sign());
		
		if (debug) System.out.println(".. /admin ..");
		this.getCommand("admin").setExecutor(new Admin());
		
		if (debug) System.out.println(".. /stats ..");
		this.getCommand("stats").setExecutor(new Stats());
		
		if (debug) System.out.println(".. /redeem ..");
		this.getCommand("redeem").setExecutor(new Redeem());
		
		if (debug) System.out.println(".. /tjm ..");
		this.getCommand("tjm").setExecutor(new ToggleJoinMessages());
		
		if (debug) System.out.println(".. /server ..");
		this.getCommand("server").setExecutor(new Server());
		
		if (debug) System.out.println(".. /help ..");
		this.getCommand("help").setExecutor(new Help());
		
		if (debug) System.out.println(".. /repair ..");
		this.getCommand("repair").setExecutor(new Repair());
		
		//this.getCommand("lagfag").setExecutor(new Lagfag());

		System.out.println("[core.main] Scheduling synced tasks..");
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);

		System.out.println("[core.main] Loading event listeners..");
		
		PluginManager core_pm = getServer().getPluginManager();
		
		core_pm.registerEvents(new Chat(), this);
		core_pm.registerEvents(new Connection(), this);
		core_pm.registerEvents(new Move(), this);
		core_pm.registerEvents(new ItemCheckTriggers(), this);
		core_pm.registerEvents(new LagManager(), this);
		core_pm.registerEvents(new SpeedLimit(), this);
		core_pm.registerEvents(new PVP(), this);
		core_pm.registerEvents(new BlockListener(), this);
		core_pm.registerEvents(new ChunkListener(), this);
		core_pm.registerEvents(new OpListener(), this);
		core_pm.registerEvents(new SpawnController(), this);
		core_pm.registerEvents(new Voted(), this);
		
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

		System.out.println("[core.main] -- Finished loading. --");
	}

	@Override
	public void onDisable()
	{
		System.out.println("[core.main] --- RVAS-Core : Disabling.. ---");
		System.out.println(ChunkListener.newCount + " brand new chunks were generated since previous restart");
		System.out.println("[core.main] Saving files...");
		
		try {
			PlayerMeta.saveDonators();
			PlayerMeta.saveMuted();
			PlayerMeta.saveLagfags();
			PlayerMeta.writePlaytime();
			PVPdata.writePVPStats();
			
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
		System.out.println("[core.main] --- RVAS-Core : Disabled ---");
	}
}
