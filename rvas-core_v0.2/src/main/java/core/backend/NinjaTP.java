package core.backend;

import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NinjaTP implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player)sender;

        String dimension = args[0].trim();
        String loc =  args[1].trim() + " " + args[2].trim() + " " + args[3].trim();

        String tp_cmd = "/execute in " + dimension + " run tp @p[name=" + player.getName() + "] " + loc;

        player.chat("/sv on"); // <- requires SuperVanish plugin
        player.chat(tp_cmd); // <- tp command-sender to the location

        return true;
    }
}
