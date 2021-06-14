package core.frontend;

/* *
 *
 *  About: Things player see and interact with .java
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
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import java.util.List;
import java.util.Arrays;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class GUI {

    public static TextComponent sign_syntax, vote_syntax, local_syntax, afk_syntax, last_syntax,
            ignore_syntax, msg_syntax, vm_syntax, donate_syntax, discord_syntax, kit_syntax,
            stats_help_syntax, server_syntax, tjm_syntax, kill_syntax;

    public static List<TextComponent> getHelpListArray(int page) {

        sign_syntax = new TextComponent(ChatPrint.cmd +
                "/sign: \u00A77Sign the item you are holding. *Cannot undo or overwrite");

        vote_syntax = new TextComponent(ChatPrint.cmd +
                "/vote: \u00A77Dupe the item in your hand. Only occurs after voting");

        local_syntax = new TextComponent(ChatPrint.cmd +
                "/local, /l: \u00A77Send a message only to players in your render distance");

        afk_syntax = new TextComponent(ChatPrint.cmd +
                "/afk: \u00A77Block whispers and tell whisperers that you are AFK");

        last_syntax = new TextComponent(ChatPrint.cmd +
                "/last: \u00A77Show the last three whispers you've received");

        ignore_syntax = new TextComponent(ChatPrint.cmd +
                "/ignore: \u00A77Ignore all messages from given player until next restart");

        msg_syntax = new TextComponent(ChatPrint.cmd +
                "/msg, /w, /r: \u00A77Message or reply to a player privately");

        donate_syntax = new TextComponent(ChatPrint.cmd +
                "/donate: \u00A77Get the link to donate with crypto!");

        discord_syntax = new TextComponent(ChatPrint.cmd +
                "/discord: \u00A77Get the discord invite link");

        kit_syntax = new TextComponent(ChatPrint.cmd +
                "/kit: \u00A77Get a small kit with steak and some starter tools (one-time only)");

        stats_help_syntax = new TextComponent(ChatPrint.cmd +
                "/stats help: \u00A77Learn how to hide your PVP stats and more");

        server_syntax = new TextComponent(ChatPrint.cmd +
                "/server: \u00A77See current speed limit and other server info");

        vm_syntax = new TextComponent(ChatPrint.cmd + "/vm: \u00A77Vote to mute a player");
        tjm_syntax = new TextComponent(ChatPrint.cmd + "/tjm: \u00A77Toggle join messages");
        kill_syntax = new TextComponent(ChatPrint.cmd + "/kill: \u00A77Take a guess");

        // set hover events
        sign_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/sign")));
        vote_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/vote")));
        local_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/l [your local message]")));
        afk_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/afk")));
        last_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/last")));
        ignore_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/ignore [name]")));
        msg_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/w [name]")));
        vm_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/vm [name]")));
        donate_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/donate")));
        discord_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/discord")));
        kit_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/kit")));
        stats_help_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/stats help")));
        server_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/server")));
        tjm_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/tjm")));
        kill_syntax.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatPrint.secondary + "/kill")));

        if (page == 1) {
            return Arrays.asList(sign_syntax, vote_syntax, local_syntax,
                    afk_syntax, last_syntax, ignore_syntax, msg_syntax, vm_syntax);
        } else if (page == 2) {
            return Arrays.asList(donate_syntax, discord_syntax, kit_syntax,
                    stats_help_syntax, server_syntax, tjm_syntax, kill_syntax);
        }
        return null;
    }
}
