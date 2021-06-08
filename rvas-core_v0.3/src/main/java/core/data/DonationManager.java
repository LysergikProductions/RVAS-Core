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

import core.data.objects.Donor;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;

import org.bukkit.Bukkit;
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
                thisPlayer.getUniqueId(), thisKey, new Date(), donationSum));

        saveDonors(); return true;
    }

    public static boolean isDonor(Player p) {
        if (p == null) return false;

        System.out.println(p.getUniqueId());

        try {
            UUID playerID = p.getUniqueId();
            for (Donor thisDonator: _donorList) {
                System.out.println(thisDonator.getUserID());
                if (thisDonator.getUserID().equals(playerID)) return true;
            }

        } catch (Exception e) { return false; }
        return false;
    }

    public static Donor getDonorByUUID(UUID thisID) {
        for (Donor thisDonor: _donorList) {
            if (thisDonor.getUserID().equals(thisID)) return thisDonor;
        }
        return null;
    }

    // TODO: add easy way for ops to verify a player is a donator
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

    // JSON management
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

    public static List<Donor> getDonorsFromJSON(File thisFile) throws IOException, ClassCastException {
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
}