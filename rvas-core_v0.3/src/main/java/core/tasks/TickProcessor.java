package core.tasks;

/* *
 *  About: Calculate the exponentially weighted moving average of TPS
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021 d2k11 (https://github.com/gcurtiss)
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

public class TickProcessor implements Runnable {

	public static long LAST_TICK_TS = -1;
	public static double AVERAGE_TICK = 50.0; // 20 TPS = 50 ms tick len
	public static double ALPHA = 0.01; // TODO make this a config option

	public static double getTPS() {
		return 1000.0 / AVERAGE_TICK; // one second / tick len == tps
	}

	@Override
	public void run() {
		long now = System.currentTimeMillis();

		if (LAST_TICK_TS < 0) LAST_TICK_TS = now - 50; // exactly one tick ago

		// at 20 TPS tick len can be anywhere from 48 - 52 ms
		long tick_len = now - LAST_TICK_TS;
		if (tick_len <= 52) tick_len = 50;

		AVERAGE_TICK = ALPHA * tick_len + (1 - ALPHA) * AVERAGE_TICK;
		LAST_TICK_TS = now;
	}
}
