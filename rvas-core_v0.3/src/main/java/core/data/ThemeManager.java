package core.data;

/* *
 *  About: Reads, writes, and mutates Theme objects
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
import core.data.objects.Theme;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;

public class ThemeManager {
    public static Theme currentTheme;

    public static void load() throws IOException, NoSuchMethodException, SecurityException {
        File thisFile = Objects.requireNonNull(FileManager.getConfiguredThemeFile());

        if (thisFile.exists()) {
            try { currentTheme = getThemeFromJSON(thisFile);
            } catch (Exception e) {
                currentTheme.setToInternalDefaults();
                Main.console.log(Level.WARNING, "getThemeFromJSON Exception");
                throw new IOException(e.getMessage());
            }
        } else {
            Main.console.log(Level.WARNING, "Failed to load the specified theme :(");
            Main.console.log(Level.WARNING, "Creating the default theme from scratch..");

            currentTheme.setToInternalDefaults();
        }
    }

    // - Read and rebuild new Theme object from file
    public static Theme getThemeFromJSON(File thisFile) throws FileNotFoundException {
        Gson gson = new Gson();

        if (!thisFile.getName().endsWith(".json")) {
            Main.console.log(Level.WARNING, "Tried reading a non-json as json!");
            return null;
        }

        Reader reader = new InputStreamReader (
                new FileInputStream (thisFile), StandardCharsets.UTF_8);

        Theme thisTheme = gson.fromJson(reader, Theme.class);
        Map<String, ChatColor> themeBuilder = new HashMap<>();

        themeBuilder.put("primary", thisTheme.getPrimary());
        themeBuilder.put("secondary", thisTheme.getSecondary());
        themeBuilder.put("tertiary", thisTheme.getTertiary());

        themeBuilder.put("clear", thisTheme.getClear());
        themeBuilder.put("faded", thisTheme.getFaded());
        themeBuilder.put("succeed", thisTheme.getSucceed());
        themeBuilder.put("fail", thisTheme.getFail());

        themeBuilder.put("help_title", thisTheme.getHelp_title());
        themeBuilder.put("desc", thisTheme.getDesc());
        themeBuilder.put("cmd", thisTheme.getCmd());
        themeBuilder.put("controls", thisTheme.getControls());

        return new Theme(themeBuilder);
    }

    public static void writeThemeToJSON(Theme thisTheme, File thisFile) throws IOException {
        String file_name = thisFile.getName();

        if (!file_name.contains("custom") && !file_name.contains("halloween")) return;
        Gson gson = new Gson(); Writer writer = new FileWriter(thisFile, false);

        gson.toJson(thisTheme, writer);
        writer.flush(); writer.close();
    }

    public static void replaceDefaultJSON(Theme thisTheme) throws IOException {
        Gson gson = new Gson(); Writer writer = new FileWriter(FileManager.defaultThemeFile, false);

        gson.toJson(thisTheme, writer);
        writer.flush(); writer.close();
    }
}
