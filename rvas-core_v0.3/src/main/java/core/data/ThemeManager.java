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

        thisMap.put("primary", ChatColor.GOLD);
        thisMap.put("secondary", ChatColor.DARK_AQUA);
        thisMap.put("tertiary", ChatColor.BLUE);

        thisMap.put("clear", ChatColor.WHITE);
        thisMap.put("faded", ChatColor.GRAY);
        thisMap.put("succeed", ChatColor.GREEN);
        thisMap.put("fail", ChatColor.RED);

        thisMap.put("help_title", ChatColor.WHITE);
        thisMap.put("desc", ChatColor.GRAY);
        thisMap.put("cmd", ChatColor.GOLD);
        thisMap.put("controls", ChatColor.AQUA);

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
        System.out.println(themeBuilder.get("primary"));
        themeBuilder.put("secondary", thisTheme.getSecondary());
        System.out.println(themeBuilder.get("secondary"));
        themeBuilder.put("tertiary", thisTheme.getTertiary());
        System.out.println(themeBuilder.get("tertiary"));

        themeBuilder.put("clear", thisTheme.getClear());
        themeBuilder.put("faded", thisTheme.getFaded());
        themeBuilder.put("succeed", thisTheme.getSucceed());
        themeBuilder.put("fail", thisTheme.getFail());

        return new Theme(themeBuilder);
    }
}
