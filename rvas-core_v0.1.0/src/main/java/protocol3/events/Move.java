package protocol3.events;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import protocol3.backend.Config;
import protocol3.backend.ItemCheck;
import protocol3.backend.LruCache;
import protocol3.backend.PlayerMeta;

import java.util.*;

public class Move implements Listener {

	public static HashMap<UUID, Chunk> lastChunks = new HashMap<UUID, Chunk>();

	// per-player cache of chunks that have been checked aggressively for illegal items
	private static LruCache<Player, LruCache<Chunk, Boolean>> playerChunks
			= new LruCache<>(Integer.parseInt(Config.getValue("item.illegal.agro.player_count")));

	private static long lastCacheFlush = System.currentTimeMillis() / 1000;

	static Random r = new Random();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// This method is actually fired upon head rotate to; if the player's coords did
		// not change,
		// don't fire this event

		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
		{
			event.getPlayer().setInvulnerable(false);
		}

		// lagfag torture
		if (PlayerMeta.isLagfag(event.getPlayer()))
		{
			int randomNumber = r.nextInt(9);

			if (randomNumber == 5 || randomNumber == 6)
			{
				event.getPlayer().spigot().sendMessage(new TextComponent("§cThis is what you get for being a lagfag!"));
				event.setCancelled(true);
				return;
			}

			randomNumber = r.nextInt(250);

			if (randomNumber == 21)
			{
				event.getPlayer().kickPlayer("§6fuck you lol");
				return;
			}
		}

		Player p = event.getPlayer();
		UUID playerUuid = p.getUniqueId();
		boolean needsCheck = false;
		boolean inNether = p.getLocation().getWorld().getName().endsWith("the_nether");
		boolean inEnd = p.getLocation().getWorld().getName().endsWith("the_end");

		// -- ILLEGAL PLACEMENT PATCH -- //
		boolean illegalItemAgro = Boolean.parseBoolean(Config.getValue("item.illegal.agro"));
		int cacheFlushPeriod = Integer.parseInt(Config.getValue("item.illegal.agro.flush_period"));

		// Check every chunk the player enters

		if (!lastChunks.containsKey(playerUuid))
		{
			lastChunks.put(playerUuid, p.getLocation().getChunk());
			needsCheck = true;
		} else {
			if (lastChunks.get(playerUuid) != p.getLocation().getChunk()) {
				lastChunks.put(playerUuid, p.getLocation().getChunk());
				needsCheck = true;
			}
		}

		if (Config.getValue("movement.block.chunkcheck").equals("false"))
			needsCheck = false;
		
		if (inEnd) {
			needsCheck = false;
		}

		if (inEnd) {
			needsCheck = false;
		}

		if (needsCheck) {
			boolean containsSpawner = false;
			boolean portalsIllegal = false;
			boolean solidifyBedrock = Boolean.parseBoolean(Config.getValue("movement.block.chunkcheck.solidify_bedrock"));
			Chunk c = p.getLocation().getChunk();

			// Portals dont spawn PAST! a 25000 block radius of spawn

			int X = c.getX() * 16;
			int Z = c.getZ() * 16;
			if (X <= -25000 || X >= 25000 || Z <= -25000 || Z >= 25000)
			{
				portalsIllegal = true;
			}

			// Create an array of frames because a certain amount of frames are necessary
			// for an end portal
			// If the number of end portals is exactly 12, allow it to exist
			// Also consider silverfish spawners, they don't occur naturally anywhere except
			// for strongholds

			List<Block> frames = new ArrayList();

			// Todo : Read Docs on what the server considers tile entities. From there we
			// can check even quicker at containers and other blocks.
			// Arrays.stream(c.getTileEntities()).filter(tileEntities -> tileEntities
			// instanceof Container)

			// aggressive mode: check all containers for illegal items and destroy them
			// TODO check if this misses any containers
			if (illegalItemAgro)
			{
				boolean doAgroCheck = true;

				// flush chunks if it's been long enough
				if (cacheFlushPeriod > 0)
				{
					long now = System.currentTimeMillis() / 1000;
					if (now - lastCacheFlush >= cacheFlushPeriod)
					{
						System.out.println("[protocol3] flushing agro chunk caches");
						playerChunks.clear();
						lastCacheFlush = now;
					}
				}

				LruCache<Chunk, Boolean> currentPlayerChunks = playerChunks.get(p);

				// new player, make a new cache
				if (currentPlayerChunks == null)
				{
					currentPlayerChunks = new LruCache<>(Integer.parseInt(Config.getValue("item.illegal.agro.chunk_count")));
					playerChunks.put(p, currentPlayerChunks);
				}

				// check all player caches
				for (Map.Entry<Player, LruCache<Chunk, Boolean> > e : playerChunks.entrySet())
				{
					if (e.getValue().get(c) != null)
					{
						doAgroCheck = false;
						break;
					}
				}

				// if it's not in any player's cache, check it and add it to current player's cache
				if (doAgroCheck)
				{
					// Containers.
					Arrays.stream(c.getTileEntities()).filter(tileEntities -> tileEntities instanceof Container)
							.forEach(blockState -> ((Container) blockState).getInventory()
									.forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "CONTAINER_CHECK", event.getPlayer())));
				}

				// it was either previously checked or we just checked it, so add it to the cache
				currentPlayerChunks.put(c, true);
			}

			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					for (int y = 0; y < 256; y++)
					{
						Block block = p.getWorld().getBlockAt(X + x, y, Z + z);

						// handle unbreakable objects
						if (block.getType().getHardness() == -1)
						{

							// ignore piston heads
							if (block.getType().equals(Material.PISTON_HEAD)
									|| block.getType().equals(Material.MOVING_PISTON))
								continue;

							// ignore nether portals (the purple part)
							if (block.getType().equals(Material.NETHER_PORTAL))
								continue;

							// eliminiate illegal end portals (too close to spawn)
							if (portalsIllegal && (block.getType().equals(Material.END_PORTAL_FRAME)
									|| block.getType().equals(Material.END_GATEWAY)
									|| block.getType().equals(Material.END_PORTAL)))
							{
								block.setType(Material.AIR);
								continue;
							}

							// allow bedrock at y <= 4 in all worlds
							if (block.getType().equals(Material.BEDROCK) && y <= 4)
								continue;

							// allow bedrock at y >= 123 in the nether
							if (block.getType().equals(Material.BEDROCK) && inNether && y >= 123)
								continue;

							// check for silverfish spawners
							if (block.getType().equals(Material.SPAWNER))
							{
								CreatureSpawner cs = ((CreatureSpawner) block.getState());
								if (cs.getSpawnedType().equals(EntityType.SILVERFISH))
								{
									containsSpawner = true;
								}
							}

							if (block.getType().equals(Material.END_PORTAL_FRAME)
									|| block.getType().equals(Material.END_PORTAL))
							{
								frames.add(block);
								continue;
							}

							block.setType(Material.AIR);
						}

						// make sure the floor is solid in both dimensions at y=1
						if (solidifyBedrock && y == 1 && !(block.getType().equals(Material.BEDROCK)))
						{
							block.setType(Material.BEDROCK);
							continue;
						}

						// make sure the nether ceiling is solid at y=127
						if (solidifyBedrock && inNether && y == 127 && !(block.getType().equals(Material.BEDROCK)))
						{
							block.setType(Material.BEDROCK);
							continue;
						}
					}
				}
			}

			// If frames and no spawner, make sure there's not more than 12.
			// Sometimes portal rooms generate half in one chunk and half in another chunk,
			// but no portal will ever contain
			// more than 12 frames

			if (!frames.isEmpty() && !containsSpawner)
			{
				frames.forEach(block -> {
					if (!block.getType().equals(Material.END_PORTAL_FRAME))
						frames.remove(block);
				});
				if (frames.size() > 12)
				{
					frames.forEach(block -> block.setType(Material.AIR));
				}
			}
		}

		// -- ROOF AND FLOOR PATCH -- //

		// kill players on the roof of the nether
		if (inNether && p.getLocation().getY() > 127 && Config.getValue("movement.block.roof").equals("true"))
			p.setHealth(0);

		// kill players below ground in overworld and nether
		if (!inEnd && p.getLocation().getY() <= 0 && Config.getValue("movement.block.floor").equals("true"))
			p.setHealth(0);
	}
	
	@EventHandler
	public void onEntityMove(EntityMoveEvent e) {
		boolean inNether = e.getEntity().getLocation().getWorld().getName().equals("world_nether");
		boolean inEnd = e.getEntity().getLocation().getWorld().getName().equals("world_the_end");
		if (inNether && e.getEntity().getLocation().getY() > 127 && Config.getValue("movement.block.roof").equals("true")) {
			e.getEntity().setHealth(0);
			return;
		}
		if (!inEnd && e.getEntity().getLocation().getY() <= 0 && Config.getValue("movement.block.floor").equals("true")) {
			e.getEntity().setHealth(0);
			return;
		}
	}
	
	@EventHandler
	public void onEntityPortal(EntityPortalEvent e) {
		if(e.getEntityType().equals(EntityType.ENDER_CRYSTAL)) {
			EnderCrystal entity = (EnderCrystal)e.getEntity();
			if(entity.isShowingBottom())
			{
				e.setCancelled(true);
				return;
			}
		}
	}

}
