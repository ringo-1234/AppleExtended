package jp.ngt.ngtlib.event;

import net.minecraft.world.World;

public interface TickProcessEntry
{
	boolean process(World world);
}