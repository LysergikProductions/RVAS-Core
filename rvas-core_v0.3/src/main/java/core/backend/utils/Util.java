package core.backend.utils;

/* *
 *
 *  About: Useful pure and impure methods
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
import core.backend.Config;
import core.commands.op.Admin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class Util {

    // sends a message to all online ops and console
    public static void notifyOps(TextComponent msg) {
        if (msg == null) return;

        for (Player thisPlayer: Bukkit.getOnlinePlayers()) {
            try {
                if (thisPlayer.isOp() && !Admin.doNotDisturb.contains(thisPlayer.getUniqueId())) {
                    thisPlayer.sendMessage(msg); }

            } catch (Exception ignore) { }
        }
        Main.console.log(Level.INFO, msg.getText());
    }

    // converts sum seconds into human-readable string
    public static String durationFormat(double seconds) {

        long hours;
        long days = (long) (seconds / 86400);
        long daysRem = (long) (seconds % 86400);

        if (days < 1) hours = (long) (seconds / 3600);
        else hours = daysRem / 3600;

        long hoursRem = (long) (seconds % 3600);
        long minutes = hoursRem / 60;

        String daysString, hoursString, minutesString;

        if (hours == 1) hoursString = hours + " hour";
        else hoursString = hours + " hours";

        if (days == 1) daysString = days + " day";
        else daysString = days + " days";

        if (minutes == 1) minutesString = minutes + " minute";
        else if (minutes == 0) minutesString = "";
        else minutesString = minutes + " minutes";

        if (minutesString.isEmpty() && hoursString.equals("0 hours")) return "< 1 minute";

        if (days < 1 && minutes == 0) return hoursString;
        else if (days < 1) return hoursString + ", " + minutesString;
        else if (minutes == 0) return daysString + ", " + hoursString;
        else return daysString + ", " + hoursString + ", " + minutesString;
    }

    public static boolean validServerIP(String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) return false;

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) return false;

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) return false;
            }

            return !ip.endsWith(".");

        } catch (NumberFormatException nfe) { return false; }
    }

    public static String getDimensionName (Location thisLoc) {
        org.bukkit.World.Environment thisEnv; String out = null;

        try { thisEnv = thisLoc.getWorld().getEnvironment();
        } catch (Exception ignore) { thisEnv = World.Environment.NORMAL; }

        if (thisEnv.equals(org.bukkit.World.Environment.NORMAL)) out = "overworld";
        else if (thisEnv.equals(org.bukkit.World.Environment.NETHER)) out = "the_nether";
        else if (thisEnv.equals(org.bukkit.World.Environment.THE_END)) out = "the_end";

        return out;
    }

    public static boolean isCmdRestricted (String thisCmd) {

        return thisCmd.contains("/op") || thisCmd.contains("/deop") ||
                thisCmd.contains("/ban") || thisCmd.contains("/attribute") ||
                thisCmd.contains("/default") || thisCmd.contains("/execute") ||
                thisCmd.contains("/rl") || thisCmd.contains("/summon") ||
                thisCmd.contains("/gamerule") || thisCmd.contains("/set") ||
                thisCmd.contains("/difficulty") || thisCmd.contains("/replace") ||
                thisCmd.contains("/enchant") || thisCmd.contains("/time") ||
                thisCmd.contains("/weather") || thisCmd.contains("/schedule") ||
                thisCmd.contains("/data") || thisCmd.contains("/fill") ||
                thisCmd.contains("/save") || thisCmd.contains("/loot") ||
                thisCmd.contains("/experience") || thisCmd.contains("/xp") ||
                thisCmd.contains("/forceload") || thisCmd.contains("/function") ||
                thisCmd.contains("/spreadplayers") || thisCmd.contains("/reload") ||
                thisCmd.contains("/world") || thisCmd.contains("/restart") ||
                thisCmd.contains("/spigot") || thisCmd.contains("/plugins") ||
                thisCmd.contains("/protocol") || thisCmd.contains("/packet") ||
                thisCmd.contains("/whitelist") || thisCmd.contains("/minecraft") ||
                thisCmd.contains("/dupe") || thisCmd.contains("/score") ||
                thisCmd.contains("/tell") || thisCmd.contains("/global") ||
                thisCmd.contains("/core:set") || thisCmd.contains("/core:dupe");
    }

    public static World getWorldByDimension(World.Environment thisEnv) {

        for (org.bukkit.World thisWorld: Bukkit.getServer().getWorlds()) {
            if (thisWorld.getEnvironment().equals(thisEnv)) return thisWorld;
        }
        return null;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    //This code is contributed by Surendra_Gangwar (in-ellipsoid point-check)
    public static int isInEllipse(int h, int k, int x, int y, int a, int b) {

        // h, k are center point | x, y are co-ords to check | a, b are ellipse radii
        return ((int)Math.pow((x - h), 2) / (int)Math.pow(a, 2))
                + ((int)Math.pow((y - k), 2) / (int)Math.pow(b, 2));
    }

    public static boolean isInSpawn(Location thisLoc) {
        double max_x, max_z, min_x, min_z;

        double config_max_x = Double.parseDouble(Config.getValue("spawn.max.X"));
        double config_max_z = Double.parseDouble(Config.getValue("spawn.max.Z"));
        double config_min_x = Double.parseDouble(Config.getValue("spawn.min.X"));
        double config_min_z = Double.parseDouble(Config.getValue("spawn.min.Z"));

        if (Double.isNaN(config_max_x)) max_x = 420.0; else max_x = config_max_x;
        if (Double.isNaN(config_max_z)) max_z = 420.0; else max_z = config_max_z;
        if (Double.isNaN(config_min_x)) min_x = -420.0; else min_x = config_min_x;
        if (Double.isNaN(config_min_z)) min_z = -420.0; else min_z = config_min_z;

        double x = thisLoc.getX(); double z = thisLoc.getZ();
        return x > min_x && x < max_x && z > min_z && z < max_z;
    }

    public static Map<Player, Integer> sortLagMap(Map<Player,Integer> thisMap) {
        Map<Player, Integer> out;

        out = thisMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(thisMap.size())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return out;
    }

    public static  Map<Player, Double> sortSpeedMap(Map<Player,Double> thisMap) {
        Map<Player, Double> out;

        out = thisMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(thisMap.size())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return out;
    }
}
