package core.commands;

import java.util.UUID;
import java.util.ArrayList;

import core.data.ThemeManager;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AFK implements CommandExecutor {

    public static ArrayList<UUID> _AFKs = new ArrayList<>();

    static ChatColor succeed = ThemeManager.currentTheme.getSucceed();
    static ChatColor controls = ThemeManager.currentTheme.getControls();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        UUID playerid = player.getUniqueId();

        TextComponent result;

        if (!_AFKs.contains(playerid)) {
            _AFKs.add(playerid);
            result = new TextComponent(controls + "Your whisperers will now see that you are AFK!");

        } else {
            _AFKs.remove(playerid);
            Message.AFK_warned.remove(playerid);
            result = new TextComponent(succeed + "You are no longer AFK!");
        }

        player.sendMessage(result.toLegacyText());
        return true;
    }
}
