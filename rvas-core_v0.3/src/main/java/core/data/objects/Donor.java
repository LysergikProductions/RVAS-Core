package core.data.objects;

/* *
 *
 *  About: The data container for players who have donated to the server
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

import java.util.*;

public class Donor {

    final private UUID userID;
    final private Date firstDonationDate;
    private Date recentDonationDate;
    private String donationKey;
    private Double sumDonated;

    public Donor(UUID thisID, String thisKey, Date firstDonationDate, Double amountDonated) {
        this.userID = thisID; this.donationKey = thisKey;
        this.firstDonationDate = firstDonationDate;
        this.recentDonationDate = firstDonationDate;
        this.sumDonated = amountDonated;
    }

    // Getters
    public UUID getUserID() { return userID; }
    public String getDonationKey() { return donationKey; }
    public Date getFirstDonationDate() { return firstDonationDate; }
    public Date getRecentDonationDate() { return recentDonationDate; }
    public Double getSumDonated() { return sumDonated; }

    // Setters
    public void setDonationKey(String donationKey) { this.donationKey = donationKey; }
    public void setSumDonated(Double sumDonated) { this.sumDonated = sumDonated; }

    public void addToSum(Double sumToAdd) {
        this.sumDonated += sumToAdd;
        this.recentDonationDate = new Date();
    }
}
