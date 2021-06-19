package core.frontend.GUI;

/* *
 *  About: Action Message GUI element for displaying the current speed limit
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021 Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * */

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
            if (!p.isOnline()) continue;

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
        if (p == null) return false;
        return _slViewers.contains(p);
    }
}
