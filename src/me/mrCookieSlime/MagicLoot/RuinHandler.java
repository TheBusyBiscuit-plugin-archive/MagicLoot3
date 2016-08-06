package me.mrCookieSlime.MagicLoot;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface RuinHandler {

	public void generate(Block b1, Block b2);
	public void generate(Block b, Material type, byte data);

}
