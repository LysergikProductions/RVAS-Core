package core.data.objects;

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

    public ChatColor getHelp_title() {
        return help_title;
    }

    public void setHelp_title(ChatColor help_title) {
        this.help_title = help_title;
    }

    public ChatColor getCmd() {
        return cmd;
    }

    public void setCmd(ChatColor cmd) {
        this.cmd = cmd;
    }

    public ChatColor getDesc() {
        return desc;
    }

    public void setDesc(ChatColor desc) {
        this.desc = desc;
    }

    public ChatColor getControls() {
        return controls;
    }

    public void setControls(ChatColor controls) {
        this.controls = controls;
    }

    public ChatColor getSucceed() {
        return succeed;
    }

    public void setSucceed(ChatColor succeed) {
        this.succeed = succeed;
    }

    public ChatColor getFail() {
        return fail;
    }

    public void setFail(ChatColor fail) {
        this.fail = fail;
    }

    public ChatColor getPrimary() {
        return primary;
    }

    public void setPrimary(ChatColor primary) {
        this.primary = primary;
    }

    public ChatColor getSecondary() {
        return secondary;
    }

    public void setSecondary(ChatColor secondary) {
        this.secondary = secondary;
    }

    public ChatColor getTertiary() {
        return tertiary;
    }

    public void setTertiary(ChatColor tertiary) {
        this.tertiary = tertiary;
    }

    public ChatColor getClear() {
        return clear;
    }

    public void setClear(ChatColor clear) {
        this.clear = clear;
    }

    public ChatColor getFaded() {
        return faded;
    }

    public void setFaded(ChatColor faded) {
        this.faded = faded;
    }
}
