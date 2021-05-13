package core.events;

import core.backend.Config;
import core.backend.PlayerMeta;

import java.util.*;

import core.backend.Utilities;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.EventPriority;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class Move implements Listener {

	static Random r = new Random();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();

		boolean inNether = loc.getWorld().getEnvironment().equals(Environment.NETHER);
		boolean inEnd = loc.getWorld().getEnvironment().equals(Environment.THE_END);
		double yCoord = loc.getY();
		
		// This method is actually fired upon head rotate too, so skip event if the player's coords didn't change
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		// Ensure survival-mode players are not invulnerable
		if (player.getGameMode().equals(GameMode.SURVIVAL) && !player.isOp()) {
			player.setInvulnerable(false);
		}

		// -- ROOF AND FLOOR PATCH -- //

		// kill players on the roof of the nether
		if (inNether && yCoord > 127 && Config.getValue("movement.block.roof").equals("true"))
			player.setHealth(0);

		// kill players below ground in overworld and nether
		if (!inEnd && yCoord < 0 && Config.getValue("movement.block.floor").equals("true"))
			player.setHealth(0);
		
		// Make game unplayable for laggers
		if (PlayerMeta.isPrisoner(player)) {
			int randomNumber = r.nextInt(9);
			
			if (randomNumber == 5 || randomNumber == 6) {
				player.spigot().sendMessage(new TextComponent("§cThis is what you get!"));
				event.setCancelled(true);
				return;
			}

			randomNumber = r.nextInt(250);
			if (randomNumber == 21) {
				player.kickPlayer("§6lmao -tries to move-");
			}
		}
	}
		
	@EventHandler (priority = EventPriority.LOW)
	public void onEntityPortal(EntityPortalEvent e) {
		// Prevent invulnerable end-crystals from breaking spawn chunks
		// https://github.com/PaperMC/Paper/issues/5404

		if(e.getEntityType().equals(EntityType.ENDER_CRYSTAL)) {
			EnderCrystal crystal = (EnderCrystal)e.getEntity();
			Environment portalFrom = e.getFrom().getWorld().getEnvironment();

			World overworld = Utilities.getWorldByDimension(Environment.NORMAL);
			if (overworld == null) {
				System.out.println("WARN couldn't find NORMAL dimension onEntityPortal()");
				return;
			}

			if (crystal.isShowingBottom() || crystal.isInvulnerable()) {

				if (portalFrom.equals(Environment.THE_END)) {
					e.setCancelled(true);

					Location spawnLoc = SpawnController
							.getRandomSpawn(overworld, overworld.getSpawnLocation());

					spawnLoc.setY(spawnLoc.getY()+1);
					TP_InvulCrystal(crystal, spawnLoc);

					if (Config.debug) System.out.println("TP'd invulnerable crystal to " +
							(int)spawnLoc.getX() + " " + (int)spawnLoc.getY() + " " + (int)spawnLoc.getZ());

				} else {
					e.setCancelled(true);

					Location spawnLoc = e.getTo();
					TP_InvulCrystal(crystal, spawnLoc);

					if (Config.debug) System.out.println("TP'd invulnerable crystal to " +
							(int)spawnLoc.getX() + " " + (int)spawnLoc.getY() + " " + (int)spawnLoc.getZ());
				}
			}
		}
	}

	private static void TP_InvulCrystal(EnderCrystal crystal, Location spawnLoc) {
		spawnLoc.getChunk().load();

		EnderCrystal finalCrystal = (EnderCrystal)spawnLoc.getWorld()
				.spawnEntity(spawnLoc, EntityType.ENDER_CRYSTAL);

		finalCrystal.setInvulnerable(true);
		finalCrystal.setPersistent(true);
		finalCrystal.setShowingBottom(true);
		finalCrystal.setBeamTarget(new Location(spawnLoc.getWorld(), 0.5, 128, 0.5));

		crystal.remove();
	}
}
