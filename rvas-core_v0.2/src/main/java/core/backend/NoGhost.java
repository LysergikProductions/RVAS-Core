package core.backend;

/* *
 * 
 *  About: Listen for client packet events to check for and prevent
 *  	ghost-blocks from either placing or breaking blocks
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

import com.destroystokyo.paper.block.TargetBlockInfo;
import core.Main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoGhost implements Listener {
	
	public static void C2S_UsePackets() {
		// capture use packets (right-click without placing any blocks)
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOW, PacketType.Play.Client.BLOCK_PLACE) {
			
			@Override
			public void onPacketReceiving(PacketEvent event) {
				
				PacketContainer packetContainer = event.getPacket();
				Player sender = event.getPlayer();
				
				//if (debug) System.out.println("DEBUG: RECEIVED USE BUTTON PACKET" + event.getPacketType());
				//if (debug && PlayerMeta.isAdmin(sender)) sender.spigot().sendMessage(new TextComponent(
				//		"I see your client block_place packet! " + event.getPacketType()
				//	)
				//);
			}
		});
	}

	public static void C2S_UseBlockPackets() {

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOW, PacketType.Play.Client.USE_ITEM) {

			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player sender = event.getPlayer();
				Block lookingAt = sender.getTargetBlock(6, TargetBlockInfo.FluidMode.NEVER);

				ItemStack inHand; Material itemType;

				try {
					inHand = sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
					itemType = inHand.getType();
				} catch (Exception e) {
					itemType = null;
				}
			}
		});
	}

	public static void C2S_AnimationPackets() {

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOW, PacketType.Play.Client.ARM_ANIMATION) {

			@Override
			public void onPacketReceiving(PacketEvent event) {
				/*if (event.isCancelled()) return;

				Player sender = event.getPlayer();
				PacketContainer packetContainer = event.getPacket();
				PacketType thisType = packetContainer.getType();

				if (debug && verbose) System.out.println("DEBUG: RECEIVED BLOCK RELATED PACKET" + event.getPacketType());
				if (debug && PlayerMeta.isAdmin(sender)) sender.spigot().sendMessage(new TextComponent(
								"I see your client arm_animation packet! ")
				);*/
			}
		});
	}
}
