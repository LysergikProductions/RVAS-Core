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

import core.Main;
import core.backend.Config;
import core.backend.PlayerMeta;
import core.tasks.Analytics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class NoGhost implements Listener {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	
	public static void C2S_Packets() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				Main.instance, ListenerPriority.LOW, PacketType.Play.Client.BLOCK_PLACE) {
			
			@Override
			public void onPacketReceiving(PacketEvent event) {
				
				PacketContainer packetContainer = event.getPacket();
				Player sender = event.getPlayer();
				
				if (debug) System.out.println("DEBUG: RECEIVED BLOCK PLACE PACKET" + event.getPacketType());
				if (debug && PlayerMeta.isAdmin(sender)) sender.spigot().sendMessage(new TextComponent(
						"I see your placement packet! " + event.getPacketType()
					)
				);
			}
		});
	}
}
