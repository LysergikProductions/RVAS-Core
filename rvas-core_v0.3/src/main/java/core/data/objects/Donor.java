package core.data.objects;

/* *
 *  About: The data container for players who have donated to the server
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

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class Donor {

    final private UUID userID;
    final private Date firstDonationDate;
    final private String donationKey;

    private Date recentDonationDate;
    private boolean validity;

    private Double sumDonated;
    private String tagLine, msgOtd, customIGN, realIGN;

    public Donor(UUID thisID, String thisKey, Double amountDonated) {
        this.userID = thisID; this.donationKey = thisKey;
        this.firstDonationDate = new Date();

        this.recentDonationDate = this.firstDonationDate;
        this.sumDonated = amountDonated;

        this.msgOtd = "tbd"; this.tagLine = "tbd"; this.customIGN = "tbd";
        this.realIGN = Bukkit.getServer().getOfflinePlayer(this.userID).getName();
        this.validity = this.sumDonated >= 25.0;
    }

    // Setters
    public void setTagLine(String tagLine) { this.tagLine = tagLine; }
    public void setMsgOtd(String msgOtd) { this.msgOtd = msgOtd; }
    public void setCustomIGN(String customIGN) { this.customIGN = customIGN; }
    public void setRecentDonationDate() { this.recentDonationDate = new Date(); }

    public void addToSum(Double sumToAdd) {
        this.sumDonated += sumToAdd;
        this.recentDonationDate = new Date();
        this.updateAboveThreshold();
    }

    public void setSumDonated(Double sumDonated) {
        this.sumDonated = sumDonated;
        this.updateAboveThreshold();
    }

    // Getters
    public UUID getUserID() { return userID; }
    public String getRealIGN() { return realIGN; }
    public String getDonationKey() { return donationKey; }
    public boolean isAboveThreshold() { return validity; }

    public Date getFirstDonationDate() { return firstDonationDate; }
    public Date getRecentDonationDate() { return recentDonationDate; }

    public Double getSumDonated() { return sumDonated; }
    public String getMsgOtd() { return msgOtd; }
    public String getTagLine() { return tagLine; }
    public String getCustomIGN() { return customIGN; }

    // Bukkit Getters
    public OfflinePlayer getOfflinePlayer() { return Bukkit.getOfflinePlayer(this.userID); }

    // Actions
    public void updateAboveThreshold() { this.validity = this.sumDonated >= 25.0; }

    public void updateDonorIGN() {
        OfflinePlayer p = this.getOfflinePlayer(); if (p == null) return;
        this.realIGN = p.getName();
    }

    public void sendMessage(@NotNull TextComponent msg) {
        OfflinePlayer p = this.getOfflinePlayer(); if (!p.isOnline()) return;
        p.getPlayer().sendMessage(msg);
    }
}
