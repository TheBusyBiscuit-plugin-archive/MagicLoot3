package me.mrCookieSlime.MagicLoot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.MagicLoot.Schematic;
import me.mrCookieSlime.MagicLoot.main;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class RuinBuilder {

	public static List<Schematic> schematics = new ArrayList<Schematic>();
	public static Map<String, Config> configs = new HashMap<String, Config>();
	public static List<Schematic> buildings = new ArrayList<Schematic>();

	public static void loadRuins() throws IOException {
		for (File file: new File("plugins/MagicLoot/schematics").listFiles()) {
			if (file.getName().endsWith(".schematic")) {
				schematics.add(Schematic.loadSchematic(file));
				Config cfg = new Config("plugins/MagicLoot/ruin_settings/" + file.getName().replace(".schematic", ".yml"));
				cfg.setDefaultValue("y-offset", 0);
				cfg.setDefaultValue("underwater", false);
				cfg.save();
				configs.put(file.getName().replace(".schematic", ""), cfg);
			}
		}
		for (File file: new File("plugins/MagicLoot/buildings").listFiles()) {
			if (file.getName().endsWith(".schematic")) buildings.add(Schematic.loadSchematic(file));
		}
	}

	public static void buildRuin(Location l) {
		Schematic s = schematics.get(CSCoreLib.randomizer().nextInt(schematics.size()));
		if (CSCoreLib.randomizer().nextInt(100) < 4) {
			s = buildings.get(CSCoreLib.randomizer().nextInt(buildings.size()));
			if (main.cfg.getBoolean("options.log")) System.out.println("Generated \"" + s.getName() + "\"");
			if (!isSafe(l, s)) return;
			Schematic.pasteSchematic(l, s, false);
		}
		else {
			if (main.cfg.getBoolean("options.log")) System.out.println("Generated \"" + s.getName() + "\"");
			Config cfg = configs.get(s.getName());
			if (!isSafe(l, s) && !cfg.getBoolean("underwater")) return;
			l.setY(l.getY() + cfg.getInt("y-offset"));
			Schematic.pasteSchematic(l, s, true);
		}
	}

	public static boolean isSafe(Location l, Schematic s) {
		int length = s.getLenght();
		int width = s.getWidth();
		int x = 0;
		boolean safe = true;
		Block destination = l.getBlock();
		while (x < width) {
			int y = -2;
			while (y < 2) {
				int z = 0;
				while (z < length) {
					if (destination.getRelative(x, y, z).isLiquid()) {
						safe = false;
						x = width;
						y = 3;
						z = length;
					}
					z++;
				}
				y++;
			}
			x++;
		}
		return safe;
	}
	//	public static void buildRuin(Location l) {
	//		Schematic s = schematics.get(CSCoreLib.randomizer().nextInt(schematics.size()));
	//		int length = s.getLenght();
	//		int width = s.getWidth();
	//		int x = 0;
	//		boolean inwater = main.cfg.getBoolean("options.log");//avoid water and lava before paste
	//		boolean safe = true;
	//		Block destination = l.getBlock();
	//		if (!inwater) {
	//			while (x < width) {
	//				int y = -1;
	//				while (y < 2) {
	//					int z = 0;
	//					while (z < length) {
	//						if (destination.getRelative(x, y, z).isLiquid()) {
	//							safe = false;
	//							x = width;
	//							y = 3;
	//							z = length;
	//						}
	//						z++;
	//					}
	//					y++;
	//				}
	//				x++;
	//			}
	//		}
	//		if (safe) {
	//			Schematic.pasteSchematic(l, s, true);
	//		}
	//		if (main.cfg.getBoolean("options.log")) {
	//			if (!safe) {
	//				System.out.println("[ML] Building a ruin was NOT allowed in water/lava");
	//			} else {
	//				System.out.println("[ML] Building a ruin generated \"" + s.getName() + "\"");
	//			}
	//		}
	//	}
}

