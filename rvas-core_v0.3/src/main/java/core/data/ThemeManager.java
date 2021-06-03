package core.data;

import core.data.objects.Theme;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;

public class ThemeManager {
    public static Theme currentTheme;

    public static void load() throws IOException {

        try { currentTheme = getThemeFromJSON(FileManager.defaultThemeFile);
        } catch (Exception e) {
            currentTheme = createDefaultTheme();
            System.out.println("WARN getThemeFromJSON Exception");
            throw new IOException(e.getMessage());
        }
    }

    public static Theme createDefaultTheme() {
        Map<String, ChatColor> thisMap = new HashMap<>();

        thisMap.putIfAbsent("primary", ChatColor.GOLD);
        thisMap.putIfAbsent("secondary", ChatColor.DARK_AQUA);
        thisMap.putIfAbsent("tertiary", ChatColor.BLUE);

        thisMap.putIfAbsent("clear", ChatColor.WHITE);
        thisMap.putIfAbsent("faded", ChatColor.GRAY);
        thisMap.putIfAbsent("succeed", ChatColor.GREEN);
        thisMap.putIfAbsent("fail", ChatColor.RED);

        thisMap.putIfAbsent("help_title", ChatColor.WHITE);
        thisMap.putIfAbsent("desc", ChatColor.GRAY);
        thisMap.putIfAbsent("cmd", ChatColor.GOLD);
        thisMap.putIfAbsent("controls", ChatColor.AQUA);

        return new Theme(thisMap);
    }

    public static void writeThemeToJSON(Theme thisTheme, File thisFile) throws IOException {
        Gson gson = new Gson();

        Writer writer = new FileWriter(thisFile, false);
        gson.toJson(thisTheme, writer);
        writer.flush(); writer.close();
    }

    public static Theme getThemeFromJSON(File thisFile) throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader (new FileInputStream (thisFile), StandardCharsets.UTF_8);
        Theme thisTheme = gson.fromJson(reader, Theme.class);

        Map<String, ChatColor> themeBuilder = new HashMap<>();

        themeBuilder.put("primary", thisTheme.getPrimary());
        themeBuilder.put("secondary", thisTheme.getSecondary());
        themeBuilder.put("tertiary", thisTheme.getTertiary());

        themeBuilder.put("clear", thisTheme.getClear());
        themeBuilder.put("faded", thisTheme.getFaded());
        themeBuilder.put("succeed", thisTheme.getSucceed());
        themeBuilder.put("fail", thisTheme.getFail());

        return new Theme(themeBuilder);
    }
}
