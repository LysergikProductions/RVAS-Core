package core.events;

/* *
 * 
 *  About: Listen for packet events to help accomplish some goals
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
import core.backend.ItemCheck;
import core.backend.anno.Critical;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;

import com.comphenix.protocol.events.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.nbt.NbtBase;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Critical
public class PacketListener implements Listener {

	public static ProtocolManager PacketManager = ProtocolLibrary.getProtocolManager();

	// prevent use of illegal weapons, tools, and other items
	public static void C2S_AnimationPackets() {

		PacketManager.addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOW, PacketType.Play.Client.ARM_ANIMATION) {

			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player sender = event.getPlayer();

				ItemStack inHand;
				try { inHand = sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
				} catch (Exception e) { inHand = null; }

				try {
					if (inHand != null) {
						if (Config.debug && Config.verbose) Main.console.log(Level.INFO, "Checking item for legality..");
						ItemCheck.IllegalCheck(inHand, "Animation Packet", sender);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	// listen for PacketPLayOutMapChunk packets to prevent chunk-bans
	public static void S2C_MapChunkPackets() {

		PacketManager.addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOWEST, PacketType.Play.Server.MAP_CHUNK) {

			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.isCancelled()) return;

				PacketContainer thisPacket = event.getPacket();
				World thisWorld = event.getPlayer().getWorld();

				int chunk_x = thisPacket.getIntegers().read(0);
				int chunk_z = thisPacket.getIntegers().read(1);

				List<NbtBase<?>> blockEntityData = thisPacket.getListNbtModifier().read(0);
				int thisSize = blockEntityData.size();

				// limit BE list size in Map_Chunk packets
				if (thisSize > ChunkManager.TE_limiter) {

					event.setCancelled(true); // <- always protect players from these packets
					Main.console.log(Level.WARNING,
							"Packet MAP_CHUNK contains " + thisSize + " entries in getListNbtModifier().read(0)");

					if (Config.getValue("remove.chunk_bans").equals("true")) {
						if (Config.debug) Main.console.log(Level.INFO, "Calling ChunkManager.removeChunkBan()..");

						// count the block entities and remove any discovered chunk bans
						Chunk thisChunk = thisWorld.getChunkAt(chunk_x, chunk_z);
						ChunkManager.removeChunkBan(thisChunk);

						// save and reload the chunk
						thisChunk.unload(true); int i = 0;
						while (!thisChunk.isLoaded()) {
							thisChunk.load(); i++;
							if (i > 2) break;
						}

					} else { // truncate the list and fix the packet
						List<NbtBase<?>> truncatedList = new ArrayList<>(
								blockEntityData.subList(0, ChunkManager.TE_limiter));

						thisPacket.getListNbtModifier().write(0, truncatedList);
						event.setCancelled(false);
					}
				}
			}
		});
	}

	// Disable global wither-spawn sound
	public static void S2C_WitherSpawnSound() {
		PacketManager.addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT) {

			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packetContainer = event.getPacket();

				if (Config.getValue("global.sound.no_wither").equals("true")) {
					if (packetContainer.getIntegers().read(0) == 1023) {
						packetContainer.getBooleans().write(0, false);
					}
				}
			}
		});
	}
}
