package core.backend;

/* *
 *
 *  About: Aliases for exceptionally long and / or complex minecraft commands
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

public class Aliases {

    // chestplate and helmet
    public static String armor_a; static {

        StringBuilder sb1 = new StringBuilder();
        sb1.append("/summon armor_stand ~1 ~2 ~1 {CustomName:\"\\\"Sinse's_32kStackedArmor_a\\\"\",CustomNameVisible:1,");

        sb1.append("ShowArms:1,HandItems:[{id:netherite_chestplate,tag:{Enchantments:[{id:protection,lvl:32767},");
        sb1.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},");

        sb1.append("{id:netherite_helmet,tag:{Enchantments:[{id:respiration,lvl:3},{id:aqua_affinity,lvl:1},{id:protection,lvl:32767},");
        sb1.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}");

        armor_a = sb1.toString();
    }

    // boots and leggings
    public static String armor_b; static {

        StringBuilder sb2 = new StringBuilder();
        sb2.append("/summon armor_stand ~-1 ~2 ~-1 {CustomName:\"\\\"Sinse's_32kStackedArmor_b\\\"\",CustomNameVisible:1,");

        sb2.append("ShowArms:1,HandItems:[{id:netherite_boots,tag:{Enchantments:[{id:blast_protection,lvl:32767},");
        sb2.append("{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127},");

        sb2.append("{id:netherite_leggings,tag:{Enchantments:[{id:blast_protection,lvl:32767},{id:thorns,lvl:32767},");
        sb2.append("{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}");

        armor_b = sb2.toString();
    }

    // totems on armor stands
    public static String totems_armor1; public static String totems_armor2; static {

        StringBuilder sb3a = new StringBuilder(); StringBuilder sb3b = new StringBuilder();

        sb3a.append("/summon armor_stand ~-1 ~2 ~1 {CustomName:\"StackedTotems\",CustomNameVisible:1,");
        sb3a.append("ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}");

        sb3b.append("/summon armor_stand ~1 ~2 ~-1 {CustomName:\"StackedTotems\",CustomNameVisible:1,");
        sb3b.append("ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}");

        totems_armor1 = sb3a.toString();
        totems_armor2 = sb3b.toString();
    }

    // totems in shulker box
    public static String totems_shulker; static {

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

        totems_shulker = sb4.toString();
    }

    // 32k feathers
    public static String feather_32k; static {

        StringBuilder sb5 = new StringBuilder();
        sb5.append("/give @s feather{Enchantments:[{id:sharpness,lvl:32767},{id:knockback,lvl:32767},");
        sb5.append("{id:fire_aspect,lvl:32767},{id:looting,lvl:10},{id:sweeping,lvl:3},{id:unbreaking,lvl:32767},");
        sb5.append("{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]} 128");

        feather_32k = sb5.toString();
    }
}
