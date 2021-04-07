package protocol3.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

public class ItemCheck {

	public static ArrayList<Material> Banned = new ArrayList<>(), Special = new ArrayList<>(), LegalHeads = new ArrayList<>();
	{
		// Banned materials.
		Banned.addAll(Arrays.asList(Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.END_PORTAL_FRAME, Material.SPAWNER, Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.FARMLAND));
		// Items that need to be specially rebuilt.
		Special.addAll(Arrays.asList(Material.ENCHANTED_BOOK, Material.POTION, Material.LINGERING_POTION,
			Material.TIPPED_ARROW, Material.SPLASH_POTION, Material.WRITTEN_BOOK, Material.FILLED_MAP,
			Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD, Material.WRITABLE_BOOK, Material.BEEHIVE,
			Material.BEE_NEST, Material.RESPAWN_ANCHOR, Material.FIREWORK_ROCKET, Material.FIREWORK_STAR,
			Material.SHIELD));
		LegalHeads.addAll(Arrays.asList(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
				Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD));
	}

	public static void IllegalCheck(ItemStack item, String trigger, Player player) {
		if (Config.getValue("item.illegal").equals("false")) return;

		// Dont check null items
		if (item == null) return;

		// Dont check air
		if (item.getType().equals(Material.AIR)) return;
		
		if(Config.getValue("debug").equals("true")) {
			if(player != null) {
				System.out.println("[protocol3] CHECK: "+trigger+", "+item.getType().toString()+", "+player.getName()+", ("+player.getLocation().getX()+", "+player.getLocation().getY()+", "+player.getLocation().getZ()+")");
			}
			else {
				System.out.println("[protocol3] CHECK: "+trigger+", "+item.getType().toString()+", NON-PLAYER");
			}
		}
		
		// Iterate through shulker boxes

		if (item.getItemMeta() instanceof BlockStateMeta) {
			BlockStateMeta itemstack_metadata = (BlockStateMeta) item.getItemMeta();
			if (itemstack_metadata.getBlockState() instanceof ShulkerBox) {
				((ShulkerBox) itemstack_metadata.getBlockState()).getInventory().forEach(itemStack -> {
					if (isShulker(itemStack)){
						itemStack.setAmount(0);
						return;
					}
					IllegalCheck(itemStack, "RECURSION_SHULKER", player);
				});

				if (item.getAmount() > 1) item.setAmount(1);
				return;
			}
		}

		// Delete banned items
		Banned.forEach(material -> {
			if (item.getType().equals(material)) item.setAmount(0);
			return;
		});

		// Determine if item needs to be specially rebuilt
		final boolean[] specialRebuild = {false};
		Special.forEach(material -> {
			if (item.getType().equals(material))
				specialRebuild[0] = true;
		});

		// Delete spawn eggs

		if (item.getType().toString().toUpperCase().contains("SPAWN") && !specialRebuild[0]) {
			item.setAmount(0);
			return;
		}

		// Patch illegal stacked items
		if (item.getAmount() > item.getMaxStackSize() && Config.getValue("item.illegal.stacked").equals("false")) {
			boolean skipUnstack = false;
			// https://github.com/gcurtiss/protocol3/issues/6
			if(item.getType().equals(Material.ENCHANTED_BOOK)) {
				EnchantmentStorageMeta esm = (EnchantmentStorageMeta)item.getItemMeta();
				Set<Enchantment> enchantments = esm.getStoredEnchants().keySet();
				if(enchantments.contains(Enchantment.VANISHING_CURSE) && enchantments.size() == 1) {
					skipUnstack = true;
				}
			}
			if(!skipUnstack) {
				item.setAmount(item.getMaxStackSize());
			}
		}

		// Reset item meta

		if (item.hasItemMeta() && !specialRebuild[0] && !(item.getItemMeta() instanceof BannerMeta)) {

			ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(item.getType());

			// Rebuild Basic Item Attribs

			if (item.getItemMeta().hasDisplayName()) newMeta.setDisplayName(item.getItemMeta().getDisplayName());
			if (item.getItemMeta().hasLore()) newMeta.setLore(item.getItemMeta().getLore());

			// Rebuild Item Enchants

			if (item.getItemMeta().hasEnchants()) {

				try {
					for (Enchantment e : item.getEnchantments().keySet()) {

						// If this item does not support this enchantment
						if (!e.canEnchantItem(item) && Config.getValue("item.illegal.invalid").equals("false")) continue;

						if (Config.getValue("item.illegal.invalid").equals("false")) {
							// If this item has a conflict with another enchantment on the same item
							boolean hasConflict = false;

							if (newMeta.getEnchants() != null) {
								for (Enchantment etwo : newMeta.getEnchants().keySet()) {
									// Ignore self
									if (etwo.equals(e))
										continue;

									// Remove conflicts
									if (etwo.conflictsWith(e)) {
										hasConflict = true;
									}

									// Except Infinity + Mending
									if ((etwo.equals(Enchantment.ARROW_INFINITE) && e.equals(Enchantment.MENDING))) {
										hasConflict = false;
									} else if ((etwo.equals(Enchantment.MENDING)
											&& e.equals(Enchantment.ARROW_INFINITE))) {
										hasConflict = false;
									}
								}
							}

							if (hasConflict) continue;
						}

						if (item.getEnchantmentLevel(e) > e.getMaxLevel()) {
							newMeta.addEnchant(e, e.getMaxLevel(), false);
						} else {
							newMeta.addEnchant(e, item.getEnchantmentLevel(e), false);
						}
					}
				} catch (IllegalArgumentException e) {
					item.setAmount(0);
					return;
				}
			}

			// Rebuild Item Durability

			if (newMeta instanceof Damageable) {
				Damageable dmg = (Damageable) newMeta;
				Damageable oldDmg = (Damageable) item.getItemMeta();
				dmg.setDamage(oldDmg.getDamage());
				newMeta = (ItemMeta) dmg;
				if (Config.getValue("item.illegal.unbreakable").equals("false")) {
					newMeta.setUnbreakable(item.getItemMeta().isUnbreakable());
				} else {
					newMeta.setUnbreakable(false);
				}
			}

			// Set item to rebuilt item

			item.setItemMeta(newMeta);
		}

		// Rebuild enchanted books

		if (item.getType().equals(Material.ENCHANTED_BOOK)) {

			EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory()
					.getItemMeta(Material.ENCHANTED_BOOK);

			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

			if (meta.getStoredEnchants().size() == 0) {
				item.setAmount(0);
				return;
			}

			// Rebuild stored enchants

			for (Enchantment e : meta.getStoredEnchants().keySet()) {
				if (meta.getStoredEnchantLevel(e) > e.getMaxLevel()) {
					newMeta.addStoredEnchant(e, e.getMaxLevel(), false);
					continue;
				}
				newMeta.addStoredEnchant(e, meta.getStoredEnchantLevel(e), false);
			}

			if (meta.hasDisplayName()) newMeta.setDisplayName(meta.getDisplayName());
			if (meta.hasLore()) newMeta.setLore(meta.getLore());

			item.setItemMeta(newMeta);
			return;
		}

		// Fix potions

		if (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION)
				|| item.getType().equals(Material.TIPPED_ARROW) || item.getType().equals(Material.LINGERING_POTION)) {

			PotionMeta meta = (PotionMeta) item.getItemMeta();

			// Remove uncraftable potions or those with custom effects

			if (meta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
				item.setAmount(0);
				return;
			}
			meta.clearCustomEffects();

			item.setItemMeta(meta);

			removeEnchants(item);
			return;
		}

		// Fix written books

		if (item.getType().equals(Material.WRITTEN_BOOK) || item.getType().equals(Material.WRITABLE_BOOK)) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasEnchants()) {
				for (Enchantment ench : meta.getEnchants().keySet()) {
					meta.removeEnchant(ench);
				}
			}
			item.setItemMeta(meta);
			BookMeta bm = (BookMeta) item.getItemMeta();
			if (bm.getPageCount() > 5) {
				for (int x = 5; x < bm.getPageCount(); x++) {
					bm.setPage(x, "");
				}
			}
			if (item.getAmount() > 16) {
				item.setAmount(16);
			}
			item.setItemMeta(bm);
			return;
		}

		// Fix maps
		if (item.getType().equals(Material.FILLED_MAP)) {
			removeEnchants(item);
			return;
		}

		// Fix banners
		if (item.getItemMeta() instanceof BannerMeta) {
			removeEnchants(item);
			return;
		}

		// Fix respawn anchors (?)
		if (item.getType().equals(Material.RESPAWN_ANCHOR)) {
			removeEnchants(item);
			return;
		}

		// Fix beehives
		if (item.getType().equals(Material.BEEHIVE)) {
			removeEnchants(item);
			return;
		}

		// Fix beenests
		if (item.getType().equals(Material.BEE_NEST)) {
			removeEnchants(item);
			return;
		}

		// Fix fireworks and firework stars
		if (item.getType().equals(Material.FIREWORK_ROCKET) || item.getType().equals(Material.FIREWORK_STAR)) {
			removeEnchants(item);
			return;
		}

		// Fix shields
		if (item.getType().equals(Material.SHIELD)) {
			removeEnchants(item);
			return;
		}

		// Delete player heads (exempt wither heads)
		if (item.getItemMeta() instanceof SkullMeta && Config.getValue("item.illegal.heads").equals("false")) {
			for (Material m : LegalHeads) {
				if (item.getType().equals(m)) {
					return;
				}
			}
			item.setAmount(0);
			return;
		}

		// Fix player heads
		else if (item.getItemMeta() instanceof SkullMeta && Config.getValue("item.illegal.heads").equals("true")) {
			removeEnchants(item);
			return;
		}
	}

	// Remove item enchants.
	private static void removeEnchants(ItemStack item) {
		if (item == null)
			return;
		ItemMeta meta = item.getItemMeta();
		if (meta.hasEnchants()) {
			for (Enchantment ench : meta.getEnchants().keySet()) {
				if (!ench.canEnchantItem(item)) {
					meta.removeEnchant(ench);
					continue;
				}
				if (ench.getMaxLevel() > meta.getEnchantLevel(ench)) {
					meta.removeEnchant(ench);
					continue;
				}
			}
		}
		item.setItemMeta(meta);
	}

	// Determine if item is shulker.
	private static boolean isShulker(ItemStack i) {
		if (i == null) return false;
		if (!i.hasItemMeta()) return false;
		if (i.getItemMeta() instanceof BlockStateMeta) {
			BlockStateMeta im = (BlockStateMeta) i.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox) {
				return true;
			}
		}
		return false;
	}

}
