package me.mrCookieSlime.MagicLoot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.EmeraldEnchants.CustomEnchantment;
import me.mrCookieSlime.EmeraldEnchants.EmeraldEnchants;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class MagicLoot {
	
    public static List<String> prefixes = new ArrayList<String>();
    public static List<String> suffixes = new ArrayList<String>();
    public static List<String> colors = new ArrayList<String>();
    public static List<String> effects = new ArrayList<String>();
    public static List<EntityType> mobs = new ArrayList<EntityType>();
	
	public static void registerHandler(RuinHandler handler) {
		main.handlers.add(handler);
	}
	
	public static Config getConfig(ConfigType type) {
		switch(type) {
		case EFFECTS:
			return main.config_effects;
		case ENCHANTMENTS:
			return main.config_ench;
		case ITEMS:
			return main.config_items;
		case NAMES:
			return main.config_names;
		case POTIONS:
			return main.config_potions;
		case LOOT_TIER:
			return main.tiers;
		default:
			return null;
		}
    }

    public static void setupConfigs() {
		loadNames();
		
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.DIAMOND", true);
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.GOLD_INGOT", true);
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.IRON_INGOT", true);
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.EMERALD", true);
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.QUARTZ", true);
		getConfig(ConfigType.ITEMS).setDefaultValue("treasure.IRON_INGOT", true);
		
		for (LootTier tier: LootTier.values()) {
			getConfig(ConfigType.LOOT_TIER).setDefaultValue(tier.toString() + ".enchantments.min", 1 + (tier.getLevel() / 2));
			getConfig(ConfigType.LOOT_TIER).setDefaultValue(tier.toString() + ".enchantments.max", 1 + tier.getLevel());
			getConfig(ConfigType.LOOT_TIER).setDefaultValue(tier.toString() + ".effects.min", tier.getLevel() / 2);
			getConfig(ConfigType.LOOT_TIER).setDefaultValue(tier.toString() + ".effects.max", tier.getLevel());
		}
		
		for (Material m: Material.values()) {
			if (!m.isBlock()) {
				for (Enchantment e: Enchantment.values()) {
					if (e.canEnchantItem(new ItemStack(m)) && !m.toString().contains("BOOK")) getConfig(ConfigType.ITEMS).setDefaultValue("loot." + m.toString(), true);
				}
			}
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
			for (String item: Slimefun.listIDs()) {
				getConfig(ConfigType.ITEMS).setDefaultValue("Slimefun-Item." + item, true);
			}
		}
		
		for (Enchantment e: Enchantment.values()) {
			getConfig(ConfigType.ENCHANTMENTS).setDefaultValue(e.getName() + ".max-level", 10);
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("EmeraldEnchants")) {
			for (CustomEnchantment e: EmeraldEnchants.getInstance().getRegistry().getEnchantments()) {
				getConfig(ConfigType.ENCHANTMENTS).setDefaultValue(e.getName() + ".max-level", 10);
			}
		}
		
		getConfig(ConfigType.NAMES).setDefaultValue("prefixes", prefixes);
		getConfig(ConfigType.NAMES).setDefaultValue("suffixes", suffixes);
		getConfig(ConfigType.NAMES).setDefaultValue("colors", colors);
		
		for (PotionEffectType e: PotionEffectType.values()) {
			if (e != null) {
				getConfig(ConfigType.POTIONS).setDefaultValue(e.getName() + ".max-level", 5);
				getConfig(ConfigType.EFFECTS).setDefaultValue(StringUtils.format(e.getName()) + ".max-level", 10);
			}
		}
		
		if (!new File("plugins/MagicLoot/schematics").exists()) {
			new File("plugins/MagicLoot/schematics").mkdir();
			loadRuin("GasStation");
			loadRuin("House");
			loadRuin("Outpost");
			loadRuin("Tent");
			loadRuin("Shop");
			loadRuin("Farm");
			loadRuin("Railstation");
		}

		if (!new File("plugins/MagicLoot/buildings").exists()) new File("plugins/MagicLoot/buildings").mkdir();
		loadBuilding("Lost_Library");
		
		for (String mob: main.cfg.getStringList("spawners")) {
			mobs.add(EntityType.valueOf(mob));
		}
		
		for (ConfigType type: ConfigType.values()) {
			getConfig(type).save();
		}
	}

	private static void loadBuilding(String name) {
		InputStream stream = MagicLoot.class.getResourceAsStream("buildings/" + name + ".schematic");
	    OutputStream out = null;
	    int read;
	    byte[] buffer = new byte[4096];
	    try {
	        out = new FileOutputStream(new File("plugins/MagicLoot/buildings/" + name + ".schematic"));
	        while ((read = stream.read(buffer)) > 0) {
	            out.write(buffer, 0, read);
	        }
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    } finally {
	        try {
				stream.close();
				out.close();
			} catch (IOException e) {
			}
	    }
	}
	
    private static void loadNames() {
        prefixes.add("Precious");
        prefixes.add("Mighty");
        prefixes.add("Fabulous");
        prefixes.add("Devil's");
        prefixes.add("Herobrine's");
        prefixes.add("Hellish");
        prefixes.add("Enchanted");
        prefixes.add("Magical");
        prefixes.add("Strange");
        prefixes.add("Fancy");
        prefixes.add("Emerald");
        prefixes.add("Ruby");
        prefixes.add("Sapphire");
        prefixes.add("Golden");
        prefixes.add("Asian");
        prefixes.add("Enhanced");
        prefixes.add("Advanced");
        prefixes.add("Awkward");
        prefixes.add("Pointless");
        prefixes.add("Brave");
        prefixes.add("Awesome");
        prefixes.add("Holy");
        prefixes.add("Unholy");
        prefixes.add("Hallowed");
        prefixes.add("Dark");
        prefixes.add("Timelord");
        prefixes.add("Master");
        prefixes.add("Gallifreyan");
        prefixes.add("Helpful");
        prefixes.add("Trusty");
        prefixes.add("Faithful");
        prefixes.add("Mysterious");
        prefixes.add("Legendary");
        prefixes.add("Amazing");
        prefixes.add("Old");
        prefixes.add("Unbelievable");
        prefixes.add("Godly");
        prefixes.add("Frozen");
        prefixes.add("Awakened");
        prefixes.add("Deadly");
        prefixes.add("Cursed");
        prefixes.add("Elemental");
        prefixes.add("Sharp");
        prefixes.add("Travelling");
        prefixes.add("Doomed");
        prefixes.add("Ghostly");
        prefixes.add("Dirty");
        prefixes.add("Faithful");
        prefixes.add("Bad");
        prefixes.add("Great");
        prefixes.add("Crying");
        
        suffixes.add("Tool");
        suffixes.add("Wizard");
        suffixes.add("Magician");
        suffixes.add("Kindness");
        suffixes.add("Spirit");
        suffixes.add("Darkness");
        suffixes.add("Lion");
        suffixes.add("King");
        suffixes.add("Dragon");
        suffixes.add("Heaven");
        suffixes.add("Swiftness");
        suffixes.add("Tool");
        suffixes.add("Absorption");
        suffixes.add("Spell");
        suffixes.add("Lump");
        suffixes.add("Glory");
        suffixes.add("Demon");
        suffixes.add("Fury");
        suffixes.add("Challenge");
        suffixes.add("Wolf");
        suffixes.add("Ghost");
        suffixes.add("Fire");
        suffixes.add("Night");
        suffixes.add("Day");
        suffixes.add("Rose");
        suffixes.add("Crime");
        suffixes.add("Cry");
        suffixes.add("Screwdriver");
        suffixes.add("Intelligence");
        suffixes.add("Madness");
        suffixes.add("Skill");
        suffixes.add("Skull");
        suffixes.add("Sun");
        suffixes.add("Monster");
        suffixes.add("Treasure");
        
        colors.add("&9");
        colors.add("&a");
        colors.add("&6");
        colors.add("&c");
        colors.add("&b");
        colors.add("&e");
    }

    public static void loadSettings() {
		for (Enchantment e: Enchantment.values()) {
			if (getMaxLevel(e) > 0) ItemManager.ENCHANTMENTS.add(e);
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("EmeraldEnchants")) {
			for (CustomEnchantment e: EmeraldEnchants.getInstance().getRegistry().getEnchantments()) {
				if (getMaxLevel(e) > 0) ItemManager.EEENCHANTMENTS.add(e);
			}
		}
		
		for (PotionEffectType e: PotionEffectType.values()) {
			if (e != null) {
				if (getMaxLevel(e) > 0) {
					ItemManager.POTIONEFFECTS.add(e);
					ItemManager.potion.put(StringUtils.format(e.getName()), e);
				}
			}
		}
        ItemManager.PREFIX = MagicLoot.getConfig(ConfigType.NAMES).getStringList("prefixes");
        ItemManager.SUFFIX = MagicLoot.getConfig(ConfigType.NAMES).getStringList("suffixes");
        ItemManager.COLOR = MagicLoot.getConfig(ConfigType.NAMES).getStringList("colors");
        
		for (PotionEffectType e: PotionEffectType.values()) {
			if (e != null) {
				if (getMaxLevel(StringUtils.format(e.getName())) > 0) ItemManager.EFFECTS.add(StringUtils.format(e.getName()));
			}
		}
		for (Material m: Material.values()) {
			if (getConfig(ConfigType.ITEMS).contains("treasure." + m.toString())) {
				if (getConfig(ConfigType.ITEMS).getBoolean("treasure." + m.toString())) ItemManager.TREASURE.add(m);
			}
			if (getConfig(ConfigType.ITEMS).contains("loot." + m.toString())) {
				if (getConfig(ConfigType.ITEMS).getBoolean("loot." + m.toString())) ItemManager.TOOLS.add(m);
			}
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
			for (String item: Slimefun.listIDs()) {
				if (getConfig(ConfigType.ITEMS).getBoolean("Slimefun-Item." + item)) ItemManager.SLIMEFUN.add(SlimefunItem.getByName(item).getItem());
			}
		}
		for (LootType type: LootType.values()) {
			if (main.cfg.contains("enable." + type.toString())) {
				if (main.cfg.getBoolean("enable." + type.toString())) ItemManager.types.add(type);
			}
		}
    }

	public static void loadRuin(String name) {
		InputStream stream = MagicLoot.class.getResourceAsStream("schematics/" + name + ".schematic");
	    OutputStream out = null;
	    int read;
	    byte[] buffer = new byte[4096];
	    try {
	        out = new FileOutputStream(new File("plugins/MagicLoot/schematics/" + name + ".schematic"));
	        while ((read = stream.read(buffer)) > 0) {
	            out.write(buffer, 0, read);
	        }
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    } finally {
	        try {
				stream.close();
				out.close();
			} catch (IOException e) {
			}
	    }
	}
	
	public static int getMaxLevel(Enchantment e) {
		return getConfig(ConfigType.ENCHANTMENTS).getInt(e.getName() + ".max-level");
	}

	public static int getMaxLevel(CustomEnchantment e) {
		return getConfig(ConfigType.ENCHANTMENTS).getInt(e.getName() + ".max-level");
	}
	
	public static int getMaxLevel(PotionEffectType e) {
		return getConfig(ConfigType.POTIONS).getInt(e.getName() + ".max-level");
	}
	
	public static int getMaxLevel(String e) {
		return getConfig(ConfigType.EFFECTS).getInt(e + ".max-level");
	}
	
	public static boolean isSlimefunItemEnabled(String item) {
		return getConfig(ConfigType.ITEMS).getBoolean("Slimefun-Item." + item);
	}
	
}
