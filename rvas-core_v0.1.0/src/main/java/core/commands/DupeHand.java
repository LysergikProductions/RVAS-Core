package protocol3.commands;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;
import protocol3.backend.PlayerMeta;

// INTERNAL USE ONLY

public class DupeHand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PlayerMeta.isOp(sender)) {
			Player player = (Player) sender;
			player.kickPlayer("§6get fucked newfag [pog]");
			return true;
		} else {
			if (args.length != 1) {
				sender.spigot().sendMessage(new TextComponent("§cInvalid syntax. Syntax: /dupehand [name]"));
				return true;
			}
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
				return true;
			}

			int rewardMultiplier = Integer.parseInt(Config.getValue("vote.multiplier"));
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (Config.getValue("vote.heal").equals("true")) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			IntStream.range(0, rewardMultiplier).mapToObj(x -> itemInHand).forEach(modItemInHand -> {
				if (modItemInHand.getItemMeta() != null) {
					if (modItemInHand.getItemMeta().hasLore()) {
						ItemMeta im = modItemInHand.getItemMeta();
						im.setLore(null);
						modItemInHand.setItemMeta(im);
					}
				}

				HashMap<Integer, ItemStack> didntFit = player.getInventory().addItem(modItemInHand);
				if (!didntFit.isEmpty()) {
					didntFit.forEach((key, value) -> {
						player.getWorld().dropItem(player.getLocation(), value);
					});
				}
			});
			return true;
		}
	}

}
