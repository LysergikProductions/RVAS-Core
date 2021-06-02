package core.commands.restricted;

import core.data.objects.Pair;
import core.events.SpeedLimiter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Speeds implements CommandExecutor {

    private static Player thisPlayer = null;
    public static Inventory speedGUI; static {
        speedGUI = Bukkit.createInventory(thisPlayer, 54, ChatColor.RED + "Speeds List");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            System.out.println("[INFO] This command cannot be sent from the console!");
            return false;
        }

        Player sender = (Player) commandSender;
        if (!sender.isOp()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "no").toLegacyText());
            return false;

        } else thisPlayer = sender;

        updateGUI();
        sender.openInventory(speedGUI);
        return true;
    }

    public static void updateGUI() throws IllegalArgumentException {
        speedGUI.clear();
        List<Pair<Double, String>> speeds = SpeedLimiter.getSpeeds();

        for (Pair<Double, String> speedEntry : speeds) {
            double speed = speedEntry.getLeft();
            if(speed == 0) continue;

            String thisName = speedEntry.getRight();
            ItemStack newHead = new ItemStack(Material.PLAYER_HEAD, 1);

            ItemMeta thisHeadMeta = newHead.getItemMeta();
            thisHeadMeta.setDisplayName(thisName);

            String color = "\u00A7";
            if (speed >= SpeedLimiter.currentSpeedLimit * 0.4) {
                color += "c"; // red
                List<String> lore = Collections.singletonList(color + String.format("%4.1f", speed));
                thisHeadMeta.setLore(lore);
                newHead.setItemMeta(thisHeadMeta);
                speedGUI.addItem(newHead);
            }
        }
    }
}
