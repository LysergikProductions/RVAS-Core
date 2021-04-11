package core.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import core.backend.Config;
import core.backend.ItemCheck;
import core.backend.PlayerMeta;

import java.util.Arrays;
import java.util.Random;

public class ItemCheckTriggers implements Listener {

	static Material[] lagItems = { Material.REDSTONE, Material.REDSTONE_BLOCK, Material.ARMOR_STAND,
			Material.STICKY_PISTON, Material.PISTON, Material.REDSTONE_WALL_TORCH, Material.COMPARATOR,
			Material.REDSTONE_WIRE, Material.REPEATER, Material.OBSERVER, Material.LEVER };

	static Random r = new Random();

	// --- stuff to redo in BlockListener
	/*@EventHandler
	public void onPlace(BlockPlaceEvent e) {

		if (PlayerMeta.isLagfag(e.getPlayer())) {
			Arrays.stream(lagItems).filter(m -> e.getBlock().getType().equals(m)).forEach(m -> e.setCancelled(true));

			int randomNumber = r.nextInt(9);

			if (randomNumber == 5 || randomNumber == 6) {
				e.getPlayer().spigot().sendMessage(new TextComponent("§cThis is what you get for being a lagfag!"));
				e.setCancelled(true);
				return;
			}
		}

			// Check if item isn't placeable

			ItemCheck.Banned.stream().filter(m -> e.getBlock().getType().equals(m)).forEach(m -> {
				e.setCancelled(true);
				ItemCheck.IllegalCheck(e.getItemInHand(), "ILLEGAL_BLOCK_PLACED", e.getPlayer());
				if (Config.getValue("item.illegal.agro").equals("true")) {
					e.getPlayer().getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "ILLEGAL_BLOCK_PLACED_AGGRESSIVE", e.getPlayer()));
				}
			});

		}
	}*/

	@EventHandler
	public void onDispense(BlockDispenseArmorEvent e) {
		ItemCheck.IllegalCheck(e.getItem(), "DISPENSED_ARMOR", null);
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		event.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "INVENTORY_OPENED", (Player)event.getPlayer()));
	}

	// Prevents hopper exploits.
	@EventHandler
	public void onInventoryMovedItem(InventoryMoveItemEvent event) {
		if (Config.getValue("item.illegal.agro").equals("true")) {
			ItemCheck.IllegalCheck(event.getItem(), "INVENTORY_MOVED_ITEM", null);
			event.getSource().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "INVENTORY_MOVED_ITEM_INVENTORY", null));
		}
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e) {
		if (Config.getValue("item.illegal.agro").equals("true"))
		{
			if (e.getEntityType().equals(EntityType.PLAYER)) {
				Player player = (Player) e.getEntity();
				ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", player);
				player.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "ITEM_PICKUP_INVENTORY", null));
			}
			else {
				ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", null);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (Config.getValue("item.illegal.agro").equals("true")) {
			ItemCheck.IllegalCheck(e.getCurrentItem(), "INVENTORY_CLICK", (Player)e.getWhoClicked());
		}
	}
}
