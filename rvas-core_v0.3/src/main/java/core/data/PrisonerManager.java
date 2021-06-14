package core.data;

/* *
 *
 *  About: Reads, writes, and mutates lag-prisoner data
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import core.annotations.Critical;
import org.bukkit.entity.Player;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class PrisonerManager {

    public static HashMap<UUID, String> _prisonerList = new HashMap<>();

    public static void togglePrisoner(Player p) throws Exception {

        if (!_prisonerList.containsKey(p.getUniqueId())) {
            _prisonerList.put(p.getUniqueId(), Objects.requireNonNull(p.getAddress()).toString().split(":")[0]);
        } else {
            try { _prisonerList.remove(p.getUniqueId());
            } catch (Exception e) { throw new Exception(e); }
        }

        try { savePrisoners();
        } catch (IOException e) {
            Main.console.log(Level.WARNING, "[core.backend.playermeta] Failed to save lag priosners.");
        }
    }

    public static boolean isPrisoner(Player p) {
        return _prisonerList.containsKey(p.getUniqueId()) ||
                _prisonerList.containsValue(Objects.requireNonNull(p.getAddress()).toString().split(":")[0]);
    }

    public static void loadPrisoners() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("plugins/core/prisoners.db"));
        lines.forEach(val -> _prisonerList.put(UUID.fromString(val.split(":")[0]), val.split(":")[1]));
    }

    public static void savePrisoners() throws IOException {
        List<String> list = _prisonerList.keySet().stream().map(u -> u.toString() + ":" + _prisonerList.get(u))
                .collect(Collectors.toList());

        Files.write(Paths.get("plugins/core/prisoners.db"), String.join("\n", list).getBytes());
    }
}
