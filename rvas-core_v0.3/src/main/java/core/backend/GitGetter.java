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

public class GitGetter {

    static String official_version, beta_version;

    public static void load() {
        // TODO: replace these two instantiations with reading from a github file called `version.core`
        official_version = "0.2.5";
        beta_version = Main.version;
    }

    public static boolean isVersionCurrent() {
        return official_version.equals(Main.version);
    }

    public static boolean isVersionBeta() {
        return beta_version.equals(Main.version);
    }
}
