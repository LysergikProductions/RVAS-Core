package core.commands;

import core.backend.Config;
import core.backend.PlayerMeta;

import java.util.*;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

// INTERNAL USE ONLY

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class DupeHand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player;

		try {
			player = (Player) sender;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			player = null;
		}
		
		if (!PlayerMeta.isOp(sender)) {
			player.kickPlayer("\u00A76imagine kits in anarchy lmao");
			return true;
		} else {
			if (args.length != 1) {
				sender.spigot().sendMessage(new TextComponent("\u00A7cInvalid syntax. Syntax: /dupehand [name]"));
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.spigot().sendMessage(new TextComponent("\u00A7cPlayer is not online."));
				return true;
			}

			int rewardMultiplier = Integer.parseInt(Config.getValue("vote.multiplier"));
			ItemStack itemInHand = target.getInventory().getItemInMainHand();
			if (Config.getValue("vote.heal").equals("true")) {
				target.setHealth(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
			}
			IntStream.range(0, rewardMultiplier).mapToObj(x -> itemInHand).forEach(modItemInHand -> {
				if (modItemInHand.getItemMeta() != null) {
					if (modItemInHand.getItemMeta().hasLore()) {
						ItemMeta im = modItemInHand.getItemMeta();
						im.setLore(null);
						modItemInHand.setItemMeta(im);
					}
				}

				HashMap<Integer, ItemStack> didntFit = target.getInventory().addItem(modItemInHand);
				if (!didntFit.isEmpty()) {
					didntFit.forEach((key, value) -> {
						target.getWorld().dropItem(target.getLocation(), value);
					});
				}
			});
		} return true;
	}
}
