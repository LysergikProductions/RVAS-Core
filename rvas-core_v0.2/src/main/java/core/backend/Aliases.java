package core.backend;

import core.events.OpListener;
import net.md_5.bungee.api.chat.TextComponent;

public class Aliases {

    // build massive 32k commands for OpListener.java
    public static void initSauce() {

        if (Config.debug) {
            System.out.println("[rvas] initializing saucy 32k commands");

            if (Config.verbose) Utilities.notifyOps(new TextComponent(
                    "[rvas] initializing saucy 32k commands"));
        }

        // chestplate and helmet
        StringBuilder sb1 = new StringBuilder();
        sb1.append("/summon armor_stand ~1 ~2 ~1 {CustomName:\"\\\"Sinse's_32kStackedArmor_a\\\"\",CustomNameVisible:1,");

        sb1.append("ShowArms:1,HandItems:[{id:netherite_chestplate,tag:{Enchantments:[{id:protection,lvl:32767},");
        sb1.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},");

        sb1.append("{id:netherite_helmet,tag:{Enchantments:[{id:respiration,lvl:3},{id:aqua_affinity,lvl:1},{id:protection,lvl:32767},");
        sb1.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}");

        OpListener.armor_a = sb1.toString();

        // boots and leggings
        StringBuilder sb2 = new StringBuilder();
        sb2.append("/summon armor_stand ~-1 ~2 ~-1 {CustomName:\"\\\"Sinse's_32kStackedArmor_b\\\"\",CustomNameVisible:1,");

        sb2.append("ShowArms:1,HandItems:[{id:netherite_boots,tag:{Enchantments:[{id:blast_protection,lvl:32767},");
        sb2.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},");

        sb2.append("{id:netherite_leggings,tag:{Enchantments:[{id:blast_protection,lvl:32767},{id:thorns,lvl:32767},");
        sb2.append("{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}");

        OpListener.armor_b = sb2.toString();

        // totems on armor stands
        StringBuilder sb3a = new StringBuilder(); StringBuilder sb3b = new StringBuilder();

        sb3a.append("/summon armor_stand ~-1 ~2 ~1 {CustomName:\"StackedTotems\",CustomNameVisible:1,");
        sb3a.append("ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}");

        sb3b.append("/summon armor_stand ~1 ~2 ~-1 {CustomName:\"StackedTotems\",CustomNameVisible:1,");
        sb3b.append("ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}");

        OpListener.totems_armor1 = sb3a.toString();
        OpListener.totems_armor2 = sb3b.toString();

        // totems in shulker box
        StringBuilder sb4 = new StringBuilder();
        sb4.append("/give @s black_shulker_box{BlockEntityTag:{Items:[{Slot:0,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:1,id:totem_of_undying,Count:127},{Slot:2,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:3,id:totem_of_undying,Count:127},{Slot:4,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:5,id:totem_of_undying,Count:127},{Slot:6,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:7,id:totem_of_undying,Count:127},{Slot:8,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:9,id:totem_of_undying,Count:127},{Slot:10,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:11,id:totem_of_undying,Count:127},{Slot:12,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:13,id:totem_of_undying,Count:127},{Slot:14,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:15,id:totem_of_undying,Count:127},{Slot:16,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:17,id:totem_of_undying,Count:127},{Slot:18,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:19,id:totem_of_undying,Count:127},{Slot:20,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:21,id:totem_of_undying,Count:127},{Slot:22,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:23,id:totem_of_undying,Count:127},{Slot:24,id:totem_of_undying,Count:127},");
        sb4.append("{Slot:25,id:totem_of_undying,Count:127},{Slot:26,id:totem_of_undying,Count:127}]}}");

        OpListener.totems_shulker = sb4.toString();

        // 32k feathers
        StringBuilder sb5 = new StringBuilder();
        sb5.append("/give @s feather{Enchantments:[{id:sharpness,lvl:32767},{id:knockback,lvl:32767},");
        sb5.append("{id:fire_aspect,lvl:32767},{id:looting,lvl:10},{id:sweeping,lvl:3},{id:unbreaking,lvl:32767},");
        sb5.append("{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]} 128");

        OpListener.feather_32k = sb5.toString();

        // mark init as completed
        OpListener.isSauceInitialized = true;

        if (Config.verbose) Utilities.notifyOps(new TextComponent(
                "Saucy commands initialized!"));
    }
}
