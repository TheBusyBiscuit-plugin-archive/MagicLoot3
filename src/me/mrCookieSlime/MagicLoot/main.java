package me.mrCookieSlime.MagicLoot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.MenuItem;
import me.mrCookieSlime.CSCoreLibSetup.CSCoreLibLoader;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunMachine;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	
	public static Config config_items;
	public static Config config_names;
	public static Config config_ench;
	public static Config config_potions;
	public static Config config_effects;
	public static Config cfg;
	public static Config tiers;
    public static ItemStack BOOK;
	
	public static List<RuinHandler> handlers = new ArrayList<RuinHandler>();
	
	@Override
	public void onEnable() {
		CSCoreLibLoader loader = new CSCoreLibLoader(this);
		
		if (loader.load()) {
			PluginUtils utils = new PluginUtils(this);
			utils.setupConfig();
			utils.setupMetrics();
			utils.setupUpdater(74010, getFile());
			
			config_items = new Config(new File("plugins/MagicLoot/Items.yml"));
			config_names = new Config(new File("plugins/MagicLoot/Names.yml"));
			config_ench = new Config(new File("plugins/MagicLoot/Enchantments.yml"));
			config_potions = new Config(new File("plugins/MagicLoot/Potions.yml"));
			config_effects = new Config(new File("plugins/MagicLoot/Effects.yml"));
			cfg = new Config(new File("plugins/MagicLoot/config.yml"));
			tiers = new Config(new File("plugins/MagicLoot/loot_tiers.yml"));
            BOOK = new CustomItem(Material.ENCHANTED_BOOK, "&7Tome of Analizing", 0, new String[]{"", "&eRight Click &7to analize Items"});
			
			MagicLoot.setupConfigs();
			
			try {
				RuinBuilder.loadRuins();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ItemManager.emeraldenchants = Bukkit.getPluginManager().isPluginEnabled("EmeraldEnchants");
			
			if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
				Category category = new Category(new MenuItem(Material.BOOKSHELF, "§5MagicLoot", 0, "open"));
				new SlimefunItem(category, new CustomItem(new MaterialData(Material.ENCHANTED_BOOK), "§dTome of Analizing", "", "§eRight Click §rto analize Items", ""), "TOMB_OF_ANALIZING", RecipeType.MOB_DROP,
				new ItemStack[] {null, null, null, null, new CustomItem(Material.MONSTER_EGG, "&a&oWitch", 99), null, null, null, null})
				.register();
				
				new SlimefunItem(category, new CustomItem(new MaterialData(Material.BOOKSHELF), "§dLost Bookshelf", "", "§rScrambled Parts of an", "§rancient Library..."), "LOST_BOOKSHELF", RecipeType.ENHANCED_CRAFTING_TABLE,
				new ItemStack[] {new ItemStack(Material.BOOKSHELF), null, new ItemStack(Material.BOOKSHELF), SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_BOOK_COVER, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.BOOKSHELF), null, new ItemStack(Material.BOOKSHELF)}, new CustomItem(new CustomItem(new MaterialData(Material.BOOKSHELF), "§dLost Bookshelf", "", "§rScrambled Parts of an", "§rancient Library..."), 2))
				.register();
				
				new SlimefunItem(category, new CustomItem(new MaterialData(Material.WORKBENCH), "§dLost Librarian's Desk", "", "§rBasically like a Lost Librarian"), "LOST_LIBRARIANS_DESK", RecipeType.ENHANCED_CRAFTING_TABLE,
				new ItemStack[] {SlimefunItem.getItem("LOST_BOOKSHELF"), null, SlimefunItem.getItem("LOST_BOOKSHELF"), null, SlimefunItems.TALISMAN, null, SlimefunItem.getItem("LOST_BOOKSHELF"), null, SlimefunItem.getItem("LOST_BOOKSHELF")})
				.register(new ItemInteractionHandler() {
					
					@Override
					public boolean onRightClick(ItemUseEvent e, Player p, ItemStack stack) {
						if (e.getClickedBlock() == null) return false;
						SlimefunItem item = BlockStorage.check(e.getClickedBlock());
						if (item == null || !item.getName().equals("LOST_LIBRARIANS_DESK")) return false;
						try {
							e.setCancelled(true);
							LostLibrarian.openMenu(p);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return true;
					}
				});
			}
			
			final main plugin = this;
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
						for (String item: Slimefun.listIDs()) {
							if (!(SlimefunItem.getByName(item) instanceof SlimefunMachine)) MagicLoot.getConfig(ConfigType.ITEMS).setDefaultValue("Slimefun-Item." + item, true);
						}
						System.out.println("[MagicLoot] Slimefun has been found!");
						System.out.println("[MagicLoot] I will now generate Slimefun Loot as well!");
					}
					MagicLoot.loadSettings();
					
					new MLListener(plugin);
				}
			}, 10);
		}
	}
	
	@Override
	public void onDisable() {
		cfg = null;
		config_effects = null;
		config_ench = null;
		config_items = null;
		config_names = null;
		config_potions = null;
		handlers = null;
        BOOK = null;
		
		ItemManager.COLOR = null;
		ItemManager.ENCHANTMENTS = null;
		ItemManager.EFFECTS = null;
		ItemManager.potion = null;
		ItemManager.POTIONEFFECTS = null;
		ItemManager.PREFIX = null;
		ItemManager.SLIMEFUN = null;
		ItemManager.SUFFIX = null;
		ItemManager.TOOLS = null;
		ItemManager.TREASURE = null;
		ItemManager.types = null;
		
		MagicLoot.colors = null;
		MagicLoot.effects = null;
		MagicLoot.prefixes = null;
		MagicLoot.suffixes = null;
		MagicLoot.mobs = null;
		
		RuinBuilder.schematics = null;
	}

}
