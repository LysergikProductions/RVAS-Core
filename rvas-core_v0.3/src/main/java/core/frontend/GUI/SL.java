package core.frontend.GUI;

import core.Main;
import core.events.SpeedLimiter;
import core.frontend.ChatPrint;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;

public class SL {
    private final static TextComponent _speed_limit = new TextComponent();
    private final static List<Player> _slViewers = new ArrayList<>();

    public static void dispSL() {
        _speed_limit.setExtra(Collections.singletonList(
                new TextComponent(ChatPrint.primary + String.valueOf(SpeedLimiter.currentSpeedLimit) + " bps")));

        long i = 0L;
        final BaseComponent limit = _speed_limit.getExtra().get(0);

        for (Player p: _slViewers) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () ->
                    p.sendMessage(ChatMessageType.ACTION_BAR, limit), i); i++;
        }
    }

    public static void addSlViewer(Player p) {
        if (p == null || !p.isOnline()) return;

        _slViewers.remove(p);
        _slViewers.add(p);
    }

    public static void rmSlViewer(Player p) {
        if (p == null) return;
        _slViewers.remove(p);
    }

    public static boolean isSlViewer(Player p) {
        return _slViewers.contains(p);
    }
}
