package me.mrCookieSlime.MagicLoot;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class LostLibrarian {
	
	@SuppressWarnings("deprecation")
	public static void openMenu(Player p) throws Exception {
		ItemStack item = p.getItemInHand();
		LootTier tier = LootTier.get(item);
		if (tier.equals(LootTier.UNKNOWN)) {
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1F, 1F);
			ChestMenu menu = new ChestMenu("§5§lLost Librarian");
			
			menu.addItem(4, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzk3OTU1NDYyZTRlNTc2NjY0NDk5YWM0YTFjNTcyZjYxNDNmMTlhZDJkNjE5NDc3NjE5OGY4ZDEzNmZkYjIifX19"), "&7[&rRandom&7]", "", "§7Cost: §b" + main.cfg.getInt("costs.RANDOM") + " XP Level"));
			menu.addMenuClickHandler(4, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.getRandomApplicable());
					return false;
				}
			});
			
			menu.addItem(11, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) LootTier.COMMON.getPrimaryColor()), LootTier.COMMON.getTag(), "", "§7Cost: §b" + main.cfg.getInt("costs." + LootTier.COMMON.toString()) + " XP Level"));
			menu.addMenuClickHandler(11, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.COMMON);
					return false;
				}
			});
			
			menu.addItem(12, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) LootTier.UNCOMMON.getPrimaryColor()), LootTier.UNCOMMON.getTag(), "", "§7Cost: §b" + main.cfg.getInt("costs." + LootTier.UNCOMMON.toString()) + " XP Level"));
			menu.addMenuClickHandler(12, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.UNCOMMON);
					return false;
				}
			});
			
			menu.addItem(13, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) LootTier.RARE.getPrimaryColor()), LootTier.RARE.getTag(), "", "§7Cost: §b" + main.cfg.getInt("costs." + LootTier.RARE.toString()) + " XP Level"));
			menu.addMenuClickHandler(13, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.RARE);
					return false;
				}
			});
			
			menu.addItem(14, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) LootTier.EPIC.getPrimaryColor()), LootTier.EPIC.getTag(), "", "§7Cost: §b" + main.cfg.getInt("costs." + LootTier.EPIC.toString()) + " XP Level"));
			menu.addMenuClickHandler(14, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.EPIC);
					return false;
				}
			});
			
			menu.addItem(15, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) LootTier.LEGENDARY.getPrimaryColor()), LootTier.LEGENDARY.getTag(), "", "§7Cost: §b" + main.cfg.getInt("costs." + LootTier.LEGENDARY.toString()) + " XP Level"));
			menu.addMenuClickHandler(15, new MenuClickHandler() {
				
				@Override
				public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
					examineTier(p, LootTier.LEGENDARY);
					return false;
				}
			});
			
			for (int i: new int[] {0, 8, 9, 17}) {
				menu.addItem(i, new CustomItem(Material.STAINED_GLASS_PANE, " ", 7));
				menu.addMenuClickHandler(i, new MenuClickHandler() {
					
					@Override
					public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
						return false;
					}
				});
			}
			
			menu.build().open(p);
		}
		else p.sendMessage("§5§lLost Librarian §8> §7Sorry, but I cannot seem to be able to examine this Item");
	}

	@SuppressWarnings("deprecation")
	public static void examineTier(Player p, LootTier tier) {
		int cost = main.cfg.getInt("costs." + tier.toString());
		
		if (p.getLevel() >= cost) {
			ItemStack item = p.getItemInHand();
			p.setItemInHand(ItemManager.applyTier(item, tier));
			p.updateInventory();
			p.setLevel(p.getLevel() - cost);
			p.sendMessage("§5§lLost Librarian §8> §7There you go! Thanks for visiting me.");
		}
		else p.sendMessage("§5§lLost Librarian §8> §7Sorry, but it seems like you have insufficient Experience Levels.");
	}

}
