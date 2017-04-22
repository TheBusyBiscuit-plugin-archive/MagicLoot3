package me.mrCookieSlime.MagicLoot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Particles.FireworkShow;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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

    @EventHandler
    public void onItemUse(ItemUseEvent event) {
        Player p = event.getPlayer();
        if (event.getItem() != null && this.isItemSimiliar(event.getItem(), main.BOOK, true)) {
            ArrayList<Integer> convert = new ArrayList<Integer>();
            int i = 0;
            while (i < p.getInventory().getContents().length) {
                if (p.getInventory().getContents()[i] != null && p.getInventory().getContents()[i].hasItemMeta() && p.getInventory().getContents()[i].getItemMeta().hasLore() && p.getInventory().getContents()[i].getItemMeta().getLore().size() == 2 && ((String)p.getInventory().getContents()[i].getItemMeta().getLore().get(1)).equalsIgnoreCase(ChatColor.translateAlternateColorCodes((char)'&', (String)"&7&oUnanalized"))) {
                    convert.add(i);
                }
                ++i;
            }
            if (!convert.isEmpty()) {
                int min2 = main.cfg.getInt("enchantments.min");
                int max2 = main.cfg.getInt("enchantments.max");
                int min4 = main.cfg.getInt("effects.min");
                int max4 = main.cfg.getInt("effects.max");
                Iterator<Integer> iterator = convert.iterator();
                while (iterator.hasNext()) {
                    int c = (Integer)iterator.next();
                    ItemStack item = p.getInventory().getContents()[c];
                    String name = String.valueOf(ItemManager.COLOR.get(CSCoreLib.randomizer().nextInt(ItemManager.COLOR.size()))) + ItemManager.PREFIX.get(CSCoreLib.randomizer().nextInt(ItemManager.PREFIX.size())) + " " + ItemManager.SUFFIX.get(CSCoreLib.randomizer().nextInt(ItemManager.SUFFIX.size()));
                    ItemMeta im = item.getItemMeta();
                    im.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
                    ArrayList<String> lore = new ArrayList<String>();
                    int i2 = 0;
                    while (i2 < CSCoreLib.randomizer().nextInt(max4 - min4) + min4) {
                        String e = ItemManager.EFFECTS.get(CSCoreLib.randomizer().nextInt(ItemManager.EFFECTS.size()));
                        String apply = CSCoreLib.randomizer().nextInt(100) > 50 ? "+" : "-";
                        int level = MagicLoot.getMaxLevel(e) > 1 ? CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel(e) - 1) + 1 : 1;
                        lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)(String.valueOf(ItemManager.COLOR.get(CSCoreLib.randomizer().nextInt(ItemManager.COLOR.size()))) + apply + " " + e + " " + level)));
                        ++i2;
                    }
                    im.setLore(lore);
                    if (im instanceof LeatherArmorMeta) {
                        ((LeatherArmorMeta)im).setColor(Color.fromRGB((int)CSCoreLib.randomizer().nextInt(255), (int)CSCoreLib.randomizer().nextInt(255), (int)CSCoreLib.randomizer().nextInt(255)));
                    }
                    item.setItemMeta(im);
                    i2 = 0;
                    while (i2 < CSCoreLib.randomizer().nextInt(max2 - min2) + min2) {
                    	Enchantment en = ItemManager.ENCHANTMENTS.get(CSCoreLib.randomizer().nextInt(ItemManager.ENCHANTMENTS.size()));
                        item.addUnsafeEnchantment((Enchantment)en, CSCoreLib.randomizer().nextInt(MagicLoot.getMaxLevel((Enchantment)en) - 1) + 1);
                        ++i2;
                    }
                    if (item.getType().getMaxDurability() > 0) item.setDurability((short)(CSCoreLib.randomizer().nextInt(item.getType().getMaxDurability() / 4) * 3));
                    p.getInventory().setItem(c, item);
                }
                me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory.consumeItemInHand((Player)p);
                me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory.update((Player)p);
                p.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)("&7You have just analized &e&o" + convert.size() + " &7Items")));
                FireworkShow.launchRandom((Player)p, (int)2);
            }
        }
    }

    public boolean isItemSimiliar(ItemStack item, ItemStack SFitem, boolean lore) {
        boolean similiar = false;
        if (item != null && SFitem != null && item.getType() == SFitem.getType() && item.getAmount() >= SFitem.getAmount()) {
            if (item.hasItemMeta() && SFitem.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName() && SFitem.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(SFitem.getItemMeta().getDisplayName())) {
                        if (lore) {
                            if (item.getItemMeta().hasLore() && SFitem.getItemMeta().hasLore()) {
                                if (item.getItemMeta().getLore().toString().equalsIgnoreCase(SFitem.getItemMeta().getLore().toString())) {
                                    similiar = true;
                                }
                            } else if (!item.getItemMeta().hasLore() && !SFitem.getItemMeta().hasLore()) {
                                similiar = true;
                            }
                        } else {
                            similiar = true;
                        }
                    }
                } else if (!item.getItemMeta().hasDisplayName() && !SFitem.getItemMeta().hasDisplayName()) {
                    if (lore) {
                        if (item.getItemMeta().hasLore() && SFitem.getItemMeta().hasLore()) {
                            if (item.getItemMeta().getLore().toString().equalsIgnoreCase(SFitem.getItemMeta().getLore().toString())) {
                                similiar = true;
                            }
                        } else if (!item.getItemMeta().hasLore() && !SFitem.getItemMeta().hasLore()) {
                            similiar = true;
                        }
                    } else {
                        similiar = true;
                    }
                }
            } else if (!item.hasItemMeta() && !SFitem.hasItemMeta()) {
                similiar = true;
            }
        }
        if (item == null && SFitem == null) {
            similiar = true;
        }
        return similiar;
    }
}
