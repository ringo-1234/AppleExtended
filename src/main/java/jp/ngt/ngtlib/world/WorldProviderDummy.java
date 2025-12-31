package jp.ngt.ngtlib.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

/**NGTWorld用*/
public class WorldProviderDummy extends WorldProvider
{
	@Override
	public DimensionType getDimensionType()
	{
		return DimensionType.OVERWORLD;
	}
}