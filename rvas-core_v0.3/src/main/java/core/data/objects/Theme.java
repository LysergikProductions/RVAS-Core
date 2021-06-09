package core.data.objects;

/* *
 *
 *  About: The data container that stores ChatColor objects and their theme-based ids
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

import java.util.Map;
import net.md_5.bungee.api.ChatColor;

public class Theme {

    private ChatColor primary, secondary, tertiary, clear, faded, succeed, fail;
    private ChatColor help_title, cmd, desc, controls;

    public Theme(Map<String, ChatColor> thisMap) {
        this.primary = thisMap.get("primary");
        this.secondary = thisMap.get("secondary");
        this.tertiary = thisMap.get("tertiary");
        this.clear = thisMap.get("clear");
        this.faded = thisMap.get("faded");
        this.succeed = thisMap.get("succeed");
        this.fail = thisMap.get("fail");
        this.help_title = thisMap.get("help_title");
        this.cmd = thisMap.get("cmd");
        this.desc = thisMap.get("desc");
        this.controls = thisMap.get("controls");
    }

    // Setters \\

    public void setPrimary(ChatColor primary) {
        this.primary = primary;
    }

    public void setSecondary(ChatColor secondary) {
        this.secondary = secondary;
    }

    public void setTertiary(ChatColor tertiary) {
        this.tertiary = tertiary;
    }

    public void setClear(ChatColor clear) {
        this.clear = clear;
    }

    public void setFaded(ChatColor faded) {
        this.faded = faded;
    }

    public void setSucceed(ChatColor succeed) {
        this.succeed = succeed;
    }

    public void setFail(ChatColor fail) {
        this.fail = fail;
    }

    public void setHelp_title(ChatColor help_title) {
        this.help_title = help_title;
    }

    public void setDesc(ChatColor desc) {
        this.desc = desc;
    }

    public void setCmd(ChatColor cmd) {
        this.cmd = cmd;
    }

    public void setControls(ChatColor controls) {
        this.controls = controls;
    }

    // Getters \\

    public ChatColor getPrimary() {
        return ChatColor.getByChar(primary.toString().charAt(1));
    }

    public ChatColor getSecondary() {
        return ChatColor.getByChar(secondary.toString().charAt(1));
    }

    public ChatColor getTertiary() {
        return ChatColor.getByChar(tertiary.toString().charAt(1));
    }

    public ChatColor getClear() {
        return ChatColor.getByChar(clear.toString().charAt(1));
    }

    public ChatColor getFaded() {
        return ChatColor.getByChar(faded.toString().charAt(1));
    }

    public ChatColor getSucceed() {
        return ChatColor.getByChar(succeed.toString().charAt(1));
    }

    public ChatColor getFail() {
        return fail;
    }

    public ChatColor getHelp_title() {
        return ChatColor.getByChar(help_title.toString().charAt(1));
    }

    public ChatColor getCmd() {
        return ChatColor.getByChar(cmd.toString().charAt(1));
    }

    public ChatColor getDesc() {
        return ChatColor.getByChar(desc.toString().charAt(1));
    }

    public ChatColor getControls() {
        return ChatColor.getByChar(controls.toString().charAt(1));
    }
}
