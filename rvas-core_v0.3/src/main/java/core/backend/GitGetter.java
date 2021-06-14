package core.backend;

/* *
 *
 *  About: Read data from the official RVAS-Core Github
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
import org.bukkit.Bukkit;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class GitGetter {

    static String official_version, beta_version;
    static String default_version = "0.2.5";

    public static void load() {

        final List<String> data = new ArrayList<>();
        final URL versionLink; final BufferedReader buffer;

        try {
            versionLink = new URL(
                    "https://raw.githubusercontent.com/LysergikProductions/RVAS-Core/main/core.version");

            buffer = new BufferedReader(new InputStreamReader(versionLink.openStream()));

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.WARNING, "Exception while loading the core.version file");
            Bukkit.getLogger().log(Level.WARNING,
                    "Please write an issue about it here: https://github.com/LysergikProductions/RVAS-Core");

            official_version = default_version;
            beta_version = Main.version;

            e.printStackTrace(); return;
        }

        String ln; // Read lines
        try { while ((ln = buffer.readLine()) != null) data.add(ln.trim()); buffer.close();
        } catch (Exception e) {

            Bukkit.getLogger().log(Level.WARNING, "Exception while reading data from core.version file");
            Bukkit.getLogger().log(Level.WARNING,
                    "Please write an issue about it here: https://github.com/LysergikProductions/RVAS-Core");

            official_version = default_version;
            beta_version = Main.version;
            e.printStackTrace(); return;
        }

        // Parse data
        if (data.isEmpty()) {

            Bukkit.getLogger().log(Level.WARNING, "Github version data is empty");
            Bukkit.getLogger().log(Level.WARNING,
                    "Please write an issue about it here: https://github.com/LysergikProductions/RVAS-Core");

            official_version = default_version;
            beta_version = Main.version;
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            String thisVersion = data.get(i).split("-")[0];
            if (i == 0) official_version = thisVersion;
            else if (i == 1) beta_version = thisVersion;
        }
        Bukkit.getLogger().log(Level.INFO,
                "Latest release: " + official_version + " | Current version: " + Main.version);
    }

    public static String getBeta_version() { return beta_version; }
    public static String getOfficial_version() { return official_version; }

    public static boolean isVersionCurrent() {
        return official_version.equals(Main.version); }

    public static boolean isVersionBeta() {
        return beta_version.equals(Main.version); }
}
