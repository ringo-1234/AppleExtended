/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.ngtlib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class NGTUtil
{
	public static MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}

	public static boolean isSMP()
	{
		if(isServer())
		{
			return getServer() == null ? false : !getServer().isSinglePlayer();
		}
		else
		{
			return !NGTUtilClient.getMinecraft().isSingleplayer();
		}
	}

	public static boolean openedLANWorld()
	{
		return isServer() && NGTUtilClient.getMinecraft().isSingleplayer() ? NGTUtilClient.getMinecraft().getIntegratedServer().getPublic() : false;
	}

	@SideOnly(Side.CLIENT)
	public static World getClientWorld()
	{
		return NGTCore.proxy.getWorld();
	}

	@SideOnly(Side.CLIENT)
	public static EntityPlayer getClientPlayer()
	{
		return NGTCore.proxy.getPlayer();
	}

	public static int getChunkLoadDistance()
	{
		return NGTCore.proxy.getChunkLoadDistance();
	}

	public static double getChunkLoadDistanceSq()
	{
		int i = getChunkLoadDistance();
		return (double)(i * i);
	}

	public static int getLightValue(World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		int skyLight = getSkyLight(world, x, y, z);
		int blockLight = world.getLightFromNeighbors(pos);
		return skyLight > blockLight ? skyLight : blockLight;
	}

	public static int getSkyLight(World world, int x, int y, int z)
	{
		return getSkyLight(world, new BlockPos(x, y, z));
	}

	public static int getSkyLight(World world, BlockPos pos)
	{
		int skyLight = world.getLightFor(EnumSkyBlock.SKY, pos);
		int slsub = world.getSkylightSubtracted();
		int i = skyLight - slsub;
		float f = world.getCelestialAngleRadians(1.0F);
		float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
		f = f + (f1 - f) * 0.2F;
		i = Math.round((float)i * NGTMath.getCos(f));
		i = NGTMath.clamp(i, 0, 15);
		return i;
	}

	public static int byteArrayToInteger(byte[] par1)
	{
		return ByteBuffer.wrap(par1).asIntBuffer().get();
	}

	public static byte[] integerToByteArray(int par1)
	{
		byte[] bs = new byte[4];
		bs[3] = (byte)(0x000000FF & (par1));
		bs[2] = (byte)(0x000000FF & (par1 >>> 8));
		bs[1] = (byte)(0x000000FF & (par1 >>> 16));
		bs[0] = (byte)(0x000000FF & (par1 >>> 24));
		return bs;
	}

	public static long getUniqueId()
	{
		return System.currentTimeMillis();
	}

	public static void setValueToField(Class<?> clazz, Object instance, Object value, String... names)
	{
		Field field = ReflectionHelper.findField(clazz, names);
		try
		{
			field.set(instance, value);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public static Object getField(Class<?> clazz, Object instance, String... names)
	{
		Field field = ReflectionHelper.findField(clazz, names);
		try
		{
			return field.get(instance);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static <E> Object getMethod(Class<? super E> clazz, E instance, String name, String obfName, Class<?>[] types, Object... args)
	{
		Method method = ReflectionHelper.findMethod(clazz, name, obfName, types);
		try
		{
			return method.invoke(instance, args);
		}
		catch(ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static <E> void addArray(List<E> list, E[] array)
	{
		for(int i = 0; i < array.length; ++i)
		{
			list.add(array[i]);
		}
	}

	public static <E> void reverse(E[] array)
	{
		int half = array.length / 2;
		for(int i = 0; i < half; ++i)
		{
			E element = array[i];
			int i2 = array.length - i - 1;
			array[i] = array[i2];
			array[i2] = element;
		}
	}

	public static <E> boolean contains(E[] array, E obj)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == obj)
			{
				return true;
			}
			else if(obj instanceof Object)
			{
				if(((Object)array[i]).equals(obj))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isEquippedItem(EntityPlayer player, Item item)
	{
		ItemStack stack = player.inventory.getCurrentItem();
		return stack != null && stack.getItem() == item;
	}

	public static String translate(String par1)
	{
		return I18n.translateToLocal(par1);
	}
}