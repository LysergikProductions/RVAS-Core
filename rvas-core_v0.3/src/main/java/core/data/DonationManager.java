package core.data;

/* *
 *
 *  About: Reads, writes, and mutates Donor objects
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

import core.backend.Config;
import core.data.objects.Donor;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("SpellCheckingInspection")
public class DonationManager {

    public final static List<Donor> _donorList = new ArrayList<>();
    public static List<String> DonorCodes = new ArrayList<>();
    public static List<String> UsedDonorCodes = new ArrayList<>();

    public static boolean setDonor(Player thisPlayer, String thisKey, Double donationSum) throws IOException {

        if (thisPlayer == null || thisKey == null || donationSum == null) return false;

        if (isDonor(thisPlayer)) _donorList.remove(getDonorByUUID(
                thisPlayer.getUniqueId()));
        else _donorList.add(new Donor(
                thisPlayer.getUniqueId(), thisKey, donationSum));

        saveDonors(); return true;
    }

    public static Donor getDonorByUUID(UUID thisID) {
        for (Donor thisDonor: _donorList) {
            if (thisDonor.getUserID().equals(thisID)) return thisDonor;
        }
        return null;
    }

    public static Donor getDonorByKey(String thisKey) {
        for (Donor thisDonor: _donorList) {
            if (thisDonor.getDonationKey().equals(thisKey)) return thisDonor;
        }
        return null;
    }

    public static Donor getDonorByName(String thisName) {
        for (Donor thisDonor: _donorList) {
            if (Objects.equals(
                    Bukkit.getOfflinePlayer(thisDonor.getUserID()).getName(), thisName)
            ) return thisDonor;
        }
        return null;
    }

    // JSON management \\
    public static void loadDonors() {
        _donorList.clear();

        try { _donorList.addAll(
                Optional.of(getDonorsFromJSON(FileManager.donor_database))
                        .orElse(Collections.emptyList()));
        } catch (Exception e) {
            _donorList.addAll(Collections.emptyList());
            e.printStackTrace(); }
    }

    public static void saveDonors() {
        try {
            writeDonorsToJSON(_donorList, FileManager.donor_database);
            Files.write(
                    Paths.get("plugins/core/codes/used.db"), String.join("\n", UsedDonorCodes).getBytes());

        } catch (Exception e) { e.printStackTrace(); }
    }

    static boolean isAboveThreshold(Donor donor) {
        return donor.getSumDonated() >= 25.0;
    }

    static List<Donor> getDonorsFromJSON(File thisFile) throws IOException {
        Gson gson = new Gson();

        if (!thisFile.getName().endsWith(".json")) {
            System.out.println("WARN tried reading a non-json as json");
            return Collections.emptyList();
        }

        Reader reader = new InputStreamReader (
                new FileInputStream (thisFile), StandardCharsets.UTF_8);

        try { return gson.fromJson(reader, new TypeToken<List<Donor>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void writeDonorsToJSON(List<Donor> theseDonors, File thisFile) throws IOException {
        Gson gson = new Gson(); Writer writer = new FileWriter(thisFile, false);

        gson.toJson(theseDonors, writer);
        writer.flush(); writer.close();
    }

    // Donor-specific utils \\
    public static boolean isDonor(Player p) {
        if (p == null) return false;

        try {
            UUID playerID = p.getUniqueId();
            for (Donor thisDonator: _donorList) {
                if (thisDonator.getUserID().equals(playerID)) return true;
            }
        } catch (Exception ignore) { return false; }

        return false;
    }

    public static boolean isValidDonor(Player p) {
        String key = Objects.requireNonNull(getDonorByUUID(p.getUniqueId())).getDonationKey();
        return isDonor(p) && isAboveThreshold(DonationManager.getDonorByUUID(p.getUniqueId()))
                && !key.equalsIgnoreCase("INVALID") && !key.isEmpty();
    }

    public static boolean isValidString(String thisString) {
        return thisString != null && !thisString.isEmpty() && !thisString.equals("tbd");
    }

    public static boolean isValidKey(String thisKey) {
        System.out.println("Checking: " + thisKey + " | Length: " + thisKey.length());
        if (thisKey.length() != 19) return false;

        for (int i = 0; i < thisKey.length(); i++){
            char c = thisKey.charAt(i);

            //0123456789012345678
            //abcd-2021-jhas-06ds

            if (i < 4 && c == '-') return false;
            else if (i == 4 && c != '-') return false;
            else if (i > 4 && i < 9 && c == '-') return false;
            else if (i == 9 && c != '-') return false;
            else if (i > 9 && i < 14 && c == '-') return false;
            else if (i == 14 && c != '-') return false;
            else if (i > 14 && c == '-') return false;
        }
        return true;
    }

    public static boolean isRestrictedIGN(String ign) {
        List<String> opNames = new ArrayList<>();
        for (OfflinePlayer op: Bukkit.getServer().getOperators()) opNames.add(op.getName());

        return  ign.equalsIgnoreCase("server") || ign.equalsIgnoreCase("console") ||
                ign.equalsIgnoreCase(Config.getValue("admin")) || opNames.contains(ign);
    }

    public static boolean isExistingCustomIGN(String ign) {
        for (Donor thisDonor: _donorList) {
            String thisIGN = thisDonor.getCustomIGN();

            if (!thisIGN.equalsIgnoreCase("tbd") && !thisIGN.isEmpty())
                return thisIGN.equalsIgnoreCase(ign);
        }
        return false;
    }
}
