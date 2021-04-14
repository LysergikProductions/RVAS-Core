package core.events;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;

import votifier.*;
import net.md_5.bungee.api.chat.TextComponent;

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

import core.backend.Config;
import core.backend.PlayerMeta;

public class Voted implements VoteListener {

    public void voteMade(Vote vote) {
    	System.out.println("Received: " + vote);
    	/*Player player = vote.getPlayer();
    	
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
		vote.getPlayer().spigot().sendMessage("Thanks for voting!");
		return true;*/
    }
}
