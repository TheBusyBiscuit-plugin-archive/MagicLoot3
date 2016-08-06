package me.mrCookieSlime.MagicLoot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.EmeraldEnchants.CustomEnchantment;
import me.mrCookieSlime.EmeraldEnchants.EmeraldEnchants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemManager {
	
	public static List<Enchantment> ENCHANTMENTS = new ArrayList<Enchantment>();
	public static List<CustomEnchantment> EEENCHANTMENTS = new ArrayList<CustomEnchantment>();
	public static List<PotionEffectType> POTIONEFFECTS = new ArrayList<PotionEffectType>();
	public static List<String> PREFIX = new ArrayList<String>();
	public static List<String> SUFFIX = new ArrayList<String>();
	public static List<String> COLOR = new ArrayList<String>();
	public static List<String> EFFECTS = new ArrayList<String>();
	public static List<Material> TOOLS = new ArrayList<Material>();
	public static List<Material> TREASURE = new ArrayList<Material>();
	public static List<ItemStack> SLIMEFUN = new ArrayList<ItemStack>();
	
	public static List<LootType> types = new ArrayList<LootType>();
	public static Map<String, PotionEffectType> potion = new HashMap<String, PotionEffectType>();
	
	public static final int min_items = main.cfg.getInt("items.min");
	public static final int max_items = main.cfg.getInt("items.max");
	public static final int min_enchantments = main.cfg.getInt("enchantments.min");
	public static final int max_enchantments = main.cfg.getInt("enchantments.max");
	public static final int min_potions = main.cfg.getInt("potions.min");
	public static final int max_potions = main.cfg.getInt("potions.max");
	public static final int min_effects = main.cfg.getInt("effects.min");
	public static final int max_effects = main.cfg.getInt("effects.max");
	public static final int min_slimefun = main.cfg.getInt("slimefun.min");
	public static final int max_slimefun = main.cfg.getInt("slimefun.max");
	
	public static boolean emeraldenchants = false;
	
	public static ItemStack createItem(LootType type) {
		if (type == LootType.RANDOM) type = types.get(CSCoreLib.randomizer().nextInt(types.size()));
		if (type == LootType.SLIMEFUN && !Bukkit.getPluginManager().isPluginEnabled("Slimefun")) type = LootType.TREASURE;
		
		ItemStack item = new ItemStack(Material.AIR);
		try {
			switch(type) {
			case BOOK: {
				item.setType(Material.ENCHANTED_BOOK);
				item = applyTier(item, LootTier.getRandom());
				break;
			}
			case TREASURE: {
				item.setType(TREASURE.get(CSCoreLib.randomizer().nextInt(TREASURE.size())));
				item.setAmount(CSCoreLib.randomizer().nextInt(max_items - min_items) + min_items);
				break;
			}
			case POTION: {
				item.setType(Material.POTION);
				item.setDurability(CSCoreLib.randomizer().nextInt(100) > 50 ? (short) 8194: 16386);
				String name = COLOR.get(CSCoreLib.randomizer().nextInt(COLOR.size())) + PREFIX.get(CSCoreLib.randomizer().nextInt(PREFIX.size())) + " " + SUFFIX.get(CSCoreLib.randomizer().nextInt(SUFFIX.size()));
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				meta.setMainEffect(PotionEffectType.HEAL);
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
				for (int i = 0; i < CSCoreLib.randomizer().nextInt(max_potions - min_potions) + min_potions; i++) {
					PotionEffectType e = POTIONEFFECTS.get(CSCoreLib.randomizer().nextInt(POTIONEFFECTS.size()));
					meta.addCustomEffect(new PotionEffect(e, (CSCoreLib.randomizer().nextInt(max_items - min_items) + min_items) * 10 * 20, CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1), true);
				}
				item.setItemMeta(meta);
				break;
			}
			case TOOL: {
				if (CSCoreLib.randomizer().nextInt(100) < 10) item = new ItemStack(Material.ARROW, 4 + CSCoreLib.randomizer().nextInt(20));
				else {
					item.setType(TOOLS.get(CSCoreLib.randomizer().nextInt(TOOLS.size())));
					if (item.getType().getMaxDurability() == 0) break;
					item = applyTier(item, LootTier.getRandom());
					
					item.setDurability((short) (CSCoreLib.randomizer().nextInt(item.getType().getMaxDurability() / 4) * 3));
				}
				break;
			}
			case SLIMEFUN: {
				item = SLIMEFUN.get(CSCoreLib.randomizer().nextInt(SLIMEFUN.size())).clone();
				item.setAmount(CSCoreLib.randomizer().nextInt(max_slimefun - min_slimefun) + min_slimefun);
				if (!item.getType().equals(Material.SKULL_ITEM) && item.getType().getMaxStackSize() < item.getAmount()) item.setAmount(item.getType().getMaxStackSize());
				break;
			}
			case UNANALIZED: {
				item.setType(TOOLS.get(CSCoreLib.randomizer().nextInt(TOOLS.size())));
				ItemMeta im = item.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7&kMEH WANNA BE EXAMINED"));
				List<String> lore = new ArrayList<String>();
				lore.add("");
				lore.add("§8Tier: §b§d§e§cUnknown");
				im.setLore(lore);
				item.setItemMeta(im);
				if (item.getType().getMaxDurability() > 0) item.setDurability((short) (CSCoreLib.randomizer().nextInt(item.getType().getMaxDurability() / 4) * 3));
				break;
			}
			default:
				break;
			}
		} catch(Exception x) {
			x.printStackTrace();
		}
		return item;
	}
	
	public static ItemStack applyTier(ItemStack item, LootTier tier) {
		String name = COLOR.get(CSCoreLib.randomizer().nextInt(COLOR.size())) + PREFIX.get(CSCoreLib.randomizer().nextInt(PREFIX.size())) + " " + SUFFIX.get(CSCoreLib.randomizer().nextInt(SUFFIX.size()));
		
		for(Enchantment e : item.getEnchantments().keySet()) {
		    item.removeEnchantment(e);
		}
		
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> lore = new ArrayList<String>();
		
		if (MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".effects.max") > 0) {
			int random = MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".effects.max") - MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".effects.min");
			if (random <= 0) random = 1;
			for (int i = 0; i < CSCoreLib.randomizer().nextInt(random) + MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".effects.min"); i++) {
				String e = EFFECTS.get(CSCoreLib.randomizer().nextInt(EFFECTS.size()));
				String apply = CSCoreLib.randomizer().nextInt(10) > 5 ? "+": "-";
				if (MagicLoot.getMaxLevel(e) > 0) {
					int level = MagicLoot.getMaxLevel(e) > 1 ? (CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1): 1;
					lore.add(ChatColor.translateAlternateColorCodes('&', COLOR.get(CSCoreLib.randomizer().nextInt(COLOR.size())) + apply + " " + e + " " + level));
				}
			}
		}

		lore.add("");
		lore.add("§8Tier: §b§d§e" + tier.getTag());
		im.setLore(lore);
		if (im instanceof LeatherArmorMeta) ((LeatherArmorMeta) im).setColor(Color.fromRGB(CSCoreLib.randomizer().nextInt(255), CSCoreLib.randomizer().nextInt(255), CSCoreLib.randomizer().nextInt(255)));;
		if (im instanceof EnchantmentStorageMeta) {
			if (MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.max") > 0) {
				int random = MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.max") - MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.min");
				if (random <= 0) random = 1;
				for (int i = 0; i < CSCoreLib.randomizer().nextInt(random) + MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.min"); i++) {
					Enchantment e = ENCHANTMENTS.get(CSCoreLib.randomizer().nextInt(ENCHANTMENTS.size()));
					if (MagicLoot.getMaxLevel(e) > 0) {
						((EnchantmentStorageMeta) im).addStoredEnchant(e, MagicLoot.getMaxLevel(e) == 1 ? 1: CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1, true);
					}
				}
			}
		}
		item.setItemMeta(im);
		
		if (MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.max") > 0) {
			int random = MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.max") - MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.min");
			if (random <= 0) random = 1;
			for (int i = 0; i < CSCoreLib.randomizer().nextInt(random) + MagicLoot.getConfig(ConfigType.LOOT_TIER).getInt(tier.toString() + ".enchantments.min"); i++) {
				if (emeraldenchants && CSCoreLib.randomizer().nextInt(100) < 40) {
					CustomEnchantment e = EEENCHANTMENTS.get(CSCoreLib.randomizer().nextInt(EEENCHANTMENTS.size()));
					if (MagicLoot.getMaxLevel(e) > 0) {
						if (im instanceof EnchantmentStorageMeta || EmeraldEnchants.getInstance().getRegistry().isApplicable(item, e)) {
							EmeraldEnchants.getInstance().getRegistry().applyEnchantment(item, e, MagicLoot.getMaxLevel(e) == 1 ? 1: CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1);
						}
					}
				}
				else {
					Enchantment e = ENCHANTMENTS.get(CSCoreLib.randomizer().nextInt(ENCHANTMENTS.size()));
					if (MagicLoot.getMaxLevel(e) > 0) {
						item.addUnsafeEnchantment(e, MagicLoot.getMaxLevel(e) == 1 ? 1: CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1);
					}
				}
			}
		}
		return item;
	}

	public static void fillChest(Block block) {
		int min, max;
		min = main.cfg.getInt("items.min");
		max = main.cfg.getInt("items.max");
		if (block.getState() instanceof Chest) {
			Chest chest = (Chest) block.getState();
			Inventory inv = chest.getBlockInventory();
			for (int i = 0; i < CSCoreLib.randomizer().nextInt(max - min) + min; i++) {
				inv.setItem(CSCoreLib.randomizer().nextInt(inv.getSize()), createItem(LootType.RANDOM));
			}
		}
	}
	
	public static void equipEntity(Entity n) {
		if (!(n instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) n;
		
		entity.getEquipment().setHelmet(null);
		entity.getEquipment().setChestplate(null);
		entity.getEquipment().setLeggings(null);
		entity.getEquipment().setBoots(null);
		
		Calendar calendar = Calendar.getInstance();
		if (CSCoreLib.randomizer().nextInt(100) < 30 && calendar.get(Calendar.MONTH) == 11 && calendar.get(Calendar.DAY_OF_MONTH) < 26 && calendar.get(Calendar.DAY_OF_MONTH) > 21) {
			entity.getEquipment().setHelmetDropChance(0.2F);
			entity.getEquipment().setChestplateDropChance(0F);
			entity.getEquipment().setLeggingsDropChance(0F);
			entity.getEquipment().setBootsDropChance(0F);
			entity.getEquipment().setItemInHandDropChance(0.7F);
			
			try {
				entity.getEquipment().setHelmet(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjYmIzZTRhMzhhYzJhMDVmNjk1NWNkMmM5ODk1YWQ5ZjI4NGM2ZTgyZTc1NWM5NGM1NDljNWJkYzg1MyJ9fX0="), "§4Santa's Head", "", "§rDid you just kill Santa?"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			entity.getEquipment().setItemInHand(applyTier(createItem(LootType.TOOL), LootTier.LEGENDARY));
		}
		else {
			entity.getEquipment().setHelmetDropChance(0.7F);
			entity.getEquipment().setChestplateDropChance(0.7F);
			entity.getEquipment().setLeggingsDropChance(0.7F);
			entity.getEquipment().setBootsDropChance(0.7F);
			entity.getEquipment().setItemInHandDropChance(0.7F);
			
			for (int i = 0; i < CSCoreLib.randomizer().nextInt(3); i++) {
				ItemStack item = createItem(LootType.TOOL);
				if (item.getType().toString().endsWith("_HELMET")) entity.getEquipment().setHelmet(item);
				else if (item.getType().toString().endsWith("_CHESTPLATE")) entity.getEquipment().setChestplate(item);
				else if (item.getType().toString().endsWith("_LEGGINGS")) entity.getEquipment().setLeggings(item);
				else if (item.getType().toString().endsWith("_BOOTS")) entity.getEquipment().setBoots(item);
				else entity.getEquipment().setItemInHand(item);
			}
		}
	}

}
