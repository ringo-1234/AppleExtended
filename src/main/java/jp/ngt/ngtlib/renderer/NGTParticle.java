package jp.ngt.ngtlib.renderer;

import java.util.HashMap;
import java.util.Map;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public final class NGTParticle
{
	public static final NGTParticle INSTANCE = new NGTParticle();

	//private int nextId;

	private final Map<String, EnumParticleTypes> nameMap = new HashMap<>();

	private NGTParticle()
	{
		//this.nextId = EnumParticleTypes.values().length;
	}

	public static EnumParticleTypes getParticle(String name)
	{
		if(INSTANCE.nameMap.isEmpty())
		{
			INSTANCE.init();
		}

		if(INSTANCE.nameMap.containsKey(name))
		{
			return INSTANCE.nameMap.get(name);
		}

		return EnumParticleTypes.SMOKE_NORMAL;
	}

	private void init()
	{
		for(EnumParticleTypes type : EnumParticleTypes.values())
		{
			this.nameMap.put(type.getParticleName(), type);
		}
	}

	public void register(int id, IParticleFactory factory)
	{
		NGTUtilClient.getMinecraft().effectRenderer.registerParticle(id, factory);
	}

	public void spawnParticle(World world, int particleID, boolean ignoreRange, double xCood, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
		NGTUtil.getMethod(World.class, world, "spawnParticle", null,
				new Class<?>[]{int.class, boolean.class, double.class, double.class, double.class, double.class, double.class, double.class, int[].class},
				particleID, ignoreRange, xCood, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
}