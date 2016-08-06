package me.mrCookieSlime.MagicLoot;

import java.util.Arrays;
import java.util.List;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class MLListener implements Listener {
	
	private final List<DamageCause> causes = Arrays.asList(
			DamageCause.BLOCK_EXPLOSION, 
			DamageCause.CONTACT, 
			DamageCause.ENTITY_ATTACK, 
			DamageCause.ENTITY_EXPLOSION,
			DamageCause.FALL,
			DamageCause.FALLING_BLOCK,
			DamageCause.FIRE,
			DamageCause.LAVA,
			DamageCause.MAGIC,
			DamageCause.THORNS
	);
	
	public MLListener(main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onRuinGenerate(ChunkPopulateEvent e) {
		if (!main.cfg.getStringList("world-blacklist").contains(e.getWorld().getName())) {
			if (CSCoreLib.randomizer().nextInt(100) < main.cfg.getInt("chances.ruin")) {
				int x, z, y;
				x = e.getChunk().getX() * 16 + CSCoreLib.randomizer().nextInt(16);
				z = e.getChunk().getZ() * 16 + CSCoreLib.randomizer().nextInt(16);
				boolean flat = false;
				for (y = e.getWorld().getMaxHeight(); y > 30; y--) {
					Block current = e.getWorld().getBlockAt(x, y, z);
					if (!current.getType().isSolid()) {
						flat = true;
						for (int i = 0; i < 6; i++) {
							for (int j = 0; j < 6; j++) {
								for (int k = 0; k < 8; k++) {
									if ((current.getRelative(i, k, j).getType().isSolid() || current.getRelative(i, k, j).getType().toString().contains("LEAVES")) || !current.getRelative(i, -1, j).getType().isSolid()) flat = false;
								}
							}
						}
						if (flat) {
							RuinBuilder.buildRuin(current.getLocation());
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Villager && ((LivingEntity) e.getRightClicked()).getCustomName() != null && ((LivingEntity) e.getRightClicked()).getCustomName().equals("§5§lLost Librarian")) {
			e.setCancelled(true);
			try {
				LostLibrarian.openMenu(e.getPlayer());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onDamage(EntityDamageEvent e) {
		
		if (!(e.getEntity() instanceof LivingEntity)) return;
		if (!causes.contains(e.getCause())) return;
		if (!e.getEntity().getWorld().getPVP()) return;
		
		if (e.getEntity() instanceof Villager && ((LivingEntity) e.getEntity()).getCustomName() != null && ((LivingEntity) e.getEntity()).getCustomName().equals("§5§lLost Librarian")) {
			e.setCancelled(true);
		}	
		else {
			try {
				if (e instanceof EntityDamageByEntityEvent) {
					if (((EntityDamageByEntityEvent) e).getDamager() instanceof LivingEntity) {
						ItemStack item = getItemInHand((LivingEntity) ((EntityDamageByEntityEvent) e).getDamager());
						if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
							List<String> lore = item.getItemMeta().getLore();
							for (String effect: lore) {
								effect = ChatColor.stripColor(effect);
								if (effect.startsWith("+ ")) {
									String attribute = effect.substring(2, effect.length() - 2);
									int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
									if (ItemManager.potion.containsKey(attribute)) {
										((LivingEntity) ((EntityDamageByEntityEvent) e).getDamager()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
									}
								}
								else if (effect.startsWith("- ")) {
									String attribute = effect.substring(2, effect.length() - 2);
									int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
									if (ItemManager.potion.containsKey(attribute)) {
										((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
									}
								}
							}
						}
					}
					else if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
						if (((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter() != null && ((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter() instanceof LivingEntity) {
							ItemStack item = getItemInHand((LivingEntity) ((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter());
							if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
								List<String> lore = item.getItemMeta().getLore();
								for (String effect: lore) {
									effect = ChatColor.stripColor(effect);
									if (effect.startsWith("+ ")) {
										String attribute = effect.substring(2, effect.length() - 2);
										int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
										if (ItemManager.potion.containsKey(attribute) && ((EntityDamageByEntityEvent) e).getDamager() instanceof LivingEntity) {
											((LivingEntity) ((EntityDamageByEntityEvent) e).getDamager()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
										}
									}
									else if (effect.startsWith("- ")) {
										String attribute = effect.substring(2, effect.length() - 2);
										int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
										if (ItemManager.potion.containsKey(attribute)) {
											((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
										}
									}
								}
							}
						}
					}
				}
				for (ItemStack item: getArmorContents((LivingEntity) e.getEntity())) {
					if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
						List<String> lore = item.getItemMeta().getLore();
						for (String effect: lore) {
							effect = ChatColor.stripColor(effect);
							if (effect.startsWith("+ ")) {
								String attribute = effect.substring(2, effect.length() - 2);
								int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
								if (ItemManager.potion.containsKey(attribute)) {
									((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
								}
							}
							else if (effect.startsWith("- ") && e instanceof EntityDamageByEntityEvent) {
								if (((EntityDamageByEntityEvent) e).getDamager() instanceof LivingEntity) {
									String attribute = effect.substring(2, effect.length() - 2);
									int level = Integer.parseInt(effect.split(" ")[effect.split(" ").length - 1]);
									if (ItemManager.potion.containsKey(attribute)) {
										((LivingEntity) ((EntityDamageByEntityEvent) e).getDamager()).addPotionEffect(new PotionEffect(ItemManager.potion.get(attribute), level * 3 * 20, level - 1));
									}
								}
							}
						}
					}
				}
			}
			catch(NumberFormatException x) {
			}
		}
	}
	
	private ItemStack getItemInHand(LivingEntity n) {
		if (n instanceof Player) return ((Player) n).getItemInHand();
		else return n.getEquipment().getItemInHand();
	}
	
	private ItemStack[] getArmorContents(LivingEntity n) {
		if (n instanceof Player) return ((Player) n).getInventory().getArmorContents();
		else return n.getEquipment().getArmorContents();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onSpawn(CreatureSpawnEvent e) {
		if (e.getEntityType() == EntityType.ZOMBIE || e.getEntityType() == EntityType.SKELETON || e.getEntityType() == EntityType.PIG_ZOMBIE) {
			if (CSCoreLib.randomizer().nextInt(100) < main.cfg.getInt("chances.mobs")) ItemManager.equipEntity(e.getEntity());
		}
	}
}
