package protocol3.commands;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.ItemCheck;

// Tag command

public class Sign implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;
		if (p.getInventory().getItemInMainHand() != null
				&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if (sign(item, p)) {
				p.spigot()
						.sendMessage(new TextComponent(
								"§a§i" + WordUtils.capitalizeFully(item.getType().toString().replaceAll("_", " "))
										+ "§r§a was successfully signed."));
				return true;
			}
		}
		p.spigot().sendMessage(new TextComponent("§cYou cannot sign this item."));
		return true;
	}

	private boolean sign(ItemStack i, Player p) {

		ItemCheck.IllegalCheck(i, "ITEM_SIGNED", p);

		if (i == null || i.getType().equals(Material.AIR)) {
			return false;
		}

		double x = p.getLocation().getX();
		double y = p.getLocation().getY();
		double z = p.getLocation().getZ();

		String xi = String.valueOf(Math.round(x));
		String yi = String.valueOf(Math.round(y));
		String zi = String.valueOf(Math.round(z));

		String code = String.valueOf(Calendar.getInstance().getTimeInMillis());

		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		m.reset();
		m.update(code.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
		String verifier = "§f§oThis item is signed.";
		String date = "§fat: " + dateTimeInstance.format(Calendar.getInstance().getTime());
		String signer = "§fby: " + p.getName();
		String coords = "§fin: " + xi + " " + yi + " " + zi;
		String verify = "§fverify: " + hashtext;

		ItemMeta im;

		if (i.getItemMeta() == null) {
			im = Bukkit.getItemFactory().getItemMeta(i.getType());
		} else {
			im = i.getItemMeta();
		}

		if (im.hasLore()) {
			return false;
		}

		List<String> lores = new ArrayList<String>();

		lores.add(verifier);
		lores.add(date);
		lores.add(signer);
		lores.add(coords);
		lores.add(verify);

		im.setLore(lores);

		i.setItemMeta(im);

		return true;
	}

}
