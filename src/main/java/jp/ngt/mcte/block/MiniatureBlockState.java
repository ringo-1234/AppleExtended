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

package jp.ngt.mcte.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.util.NGTUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;

/**明るさや硬さを管理*/
public class MiniatureBlockState
{
	private static final Map<Object, MiniatureBlockState> STATE_CACHE = new HashMap<>();

	public String name = "no_name";
	public float hardness = 2.0F;
	public byte redstonePower = 0;
	public byte lightValue = 0;
	private int boolStates = 0;
	public float explosionResistance = 10.0F;
	public MiniatureBB mbb = new MiniatureBB();
	public MiniatureMotion mm = new MiniatureMotion();

	private MiniatureBlockState(){}

	public static MiniatureBlockState create(TileEntityMiniature key)//ReadNBT時呼び出しだから座標とか使えない?はず
	{
		return getState(key, true);
	}

	public static MiniatureBlockState create(@Nullable ItemStack key)
	{
		return getState(key, (key != null));
	}

	private static MiniatureBlockState getState(Object key, boolean useCache)
	{
		if(NGTUtil.isServer())
		{
			return new MiniatureBlockState();
		}
		else
		{
			if(STATE_CACHE.containsKey(key))
			{
				return STATE_CACHE.get(key);
			}
			MiniatureBlockState state = new MiniatureBlockState();
			STATE_CACHE.put(key, state);
			return state;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public boolean isLadder()
	{
		return (this.boolStates & 1) > 0;
	}

	public void setLadder(boolean par1)
	{
		if(par1)
		{
			this.boolStates |= 1;
		}
		else
		{
			this.boolStates ^= 1;
		}
	}

	public boolean isBurning()
	{
		return (this.boolStates & 2) > 0;
	}

	public boolean isFireSource()
	{
		return (this.boolStates & 4) > 0;
	}

	public boolean isBed()
	{
		return (this.boolStates & 8) > 0;
	}

	public void setBed(boolean par1)
	{
		if(par1)
		{
			this.boolStates |= 8;
		}
		else
		{
			this.boolStates ^= 8;
		}
	}

	public boolean hasCustomAABB()
	{
		return (this.boolStates & 16) > 0;
	}

	public void setCustomAABB(boolean par1)
	{
		if(par1)
		{
			this.boolStates |= 16;
		}
		else
		{
			this.boolStates ^= 16;
		}
	}

	public AxisAlignedBB getSelectBox()
	{
		return this.getAABB(this.mbb.selectBox);
	}

	/**ブロックの座標を加えたAABBを取得*/
	public List<AxisAlignedBB> getCollisionBoxes()
	{
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(this.hasCustomAABB())
		{
			for(int i = 0; i < this.mbb.collisionBoxes.length; ++i)
			{
				list.add(this.getAABB(this.mbb.collisionBoxes[i]));
			}
		}
		return list;
	}

	private AxisAlignedBB getAABB(float[] fa)
	{
		AxisAlignedBB aabb = new AxisAlignedBB(
				(double)fa[0], (double)fa[1], (double)fa[2],
				(double)fa[3], (double)fa[4], (double)fa[5]);
		return aabb;
	}

	public byte getMotionType()
	{
		return this.mm.motionType;
	}

	private static final long DAY_MILS = 1000 * 60 * 60 * 24;
	private float prevMotion;

	public float getMotion()
	{
		int time = (int)(System.currentTimeMillis() % DAY_MILS);
		float rotation = ((float)time * 0.001F * this.mm.rotationSpeed) + this.mm.rotationOffset;
		return rotation;
	}

	public float getPrevMotion()
	{
		return this.prevMotion;
	}

	public void setPrevMotion(float par1)
	{
		this.prevMotion = par1;
	}

	public float getTranslationScale()
	{
		return this.mm.translationScale;
	}

	public float getTranslationOffset()
	{
		return this.mm.translationOffset;
	}

	public void setAABB(String par1)
	{
		try
		{
			this.mbb = NGTJson.getObjectFromJson(par1, MiniatureBB.class);
		}
		catch(IOException e)
		{
			this.mbb = new MiniatureBB();
		}
	}

	public void setMotion(String par1)
	{
		try
		{
			this.mm = NGTJson.getObjectFromJson(par1, MiniatureMotion.class);
		}
		catch(IOException e)
		{
			this.mm = new MiniatureMotion();
		}
	}

	public String getAabbAsJson()
	{
		String s = NGTJson.getJsonFromObject(this.mbb);
		s = s.replaceAll(" ", "");//スペース除去
		s = s.replaceAll("\n", "");//改行除去
		s = s.replaceAll(",", ", ");//1スペース入れ
		return s;
	}

	public String getMotionAsJson()
	{
		String s = NGTJson.getJsonFromObject(this.mm);
		s = s.replaceAll(" ", "");//スペース除去
		s = s.replaceAll("\n", "");//改行除去
		s = s.replaceAll(",", ", ");//1スペース入れ
		return s;
	}

	public static MiniatureBlockState readFromNBT(NBTTagCompound nbt, Object obj)
	{
		MiniatureBlockState state;
		if(obj instanceof ItemStack)
		{
			state = create((ItemStack)obj);
		}
		else
		{
			state = create((TileEntityMiniature)obj);
		}
		state.name = nbt.getString("Name");
		state.hardness = nbt.getFloat("Hardness");
		state.redstonePower = nbt.getByte("RSPower");
		state.lightValue = nbt.getByte("LightValue");
		state.boolStates = nbt.getInteger("BoolStates");
		state.explosionResistance = nbt.getFloat("Resistance");
		state.mbb = MiniatureBB.readFromNBT(nbt.getCompoundTag("MiniatureBB"));
		state.mm = MiniatureMotion.readFromNBT(nbt.getCompoundTag("MiniatureMotion"));
		return state;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Name", this.name);
		nbt.setFloat("Hardness", this.hardness);
		nbt.setByte("RSPower", this.redstonePower);
		nbt.setByte("LightValue", this.lightValue);
		nbt.setInteger("BoolStates", this.boolStates);
		nbt.setFloat("Resistance", this.explosionResistance);
		nbt.setTag("MiniatureBB", this.mbb.writeToNBT());
		nbt.setTag("MiniatureMotion", this.mm.writeToNBT());
		return nbt;
	}

	public static class MiniatureBB
	{
		public float[] selectBox;
		public float[][] collisionBoxes;

		public MiniatureBB()
		{
			this.selectBox = new float[]{-0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F};
			this.collisionBoxes = new float[][]{{-0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F}};
		}

		public static MiniatureBB readFromNBT(NBTTagCompound nbt)
		{
			MiniatureBB mbb = new MiniatureBB();
			mbb.selectBox = new float[6];
			int[] ia = nbt.getIntArray("SelectBox");
			for(int j = 0; j < 6; ++j)
			{
				mbb.selectBox[j] = Float.intBitsToFloat(ia[j]);
			}

			NBTTagList tagList = nbt.getTagList("CollisionBoxes", 11);
			mbb.collisionBoxes = new float[tagList.tagCount()][6];
			for(int i = 0; i < tagList.tagCount(); ++i)
	    	{
				int[] ia2 = tagList.getIntArrayAt(i);
				for(int j = 0; j < 6; ++j)
				{
					mbb.collisionBoxes[i][j] = Float.intBitsToFloat(ia2[j]);
				}
	    	}

			return mbb;
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			int[] ia = new int[6];
			for(int j = 0; j < 6; ++j)
			{
				ia[j] = Float.floatToIntBits(this.selectBox[j]);
			}
			nbt.setIntArray("SelectBox", ia);

			NBTTagList tagList = new NBTTagList();
			for(int i = 0; i < this.collisionBoxes.length; ++i)
			{
				int[] ia2 = new int[6];
				for(int j = 0; j < 6; ++j)
				{
					ia2[j] = Float.floatToIntBits(this.collisionBoxes[i][j]);
				}
				tagList.appendTag(new NBTTagIntArray(ia2));
			}
			nbt.setTag("CollisionBoxes", tagList);
			return nbt;
		}
	}

	public static class MiniatureMotion
	{
		/**0:なし, 1:回転, 2:往復, 3:振り子*/
		public byte motionType;
		/**度/s*/
		public float rotationSpeed;
		/**度*/
		public float rotationOffset;
		public float translationScale;
		public float translationOffset;

		public static MiniatureMotion readFromNBT(NBTTagCompound nbt)
		{
			MiniatureMotion mm = new MiniatureMotion();
			mm.motionType = nbt.getByte("MotionType");
			mm.rotationSpeed = nbt.getFloat("RotationSpeed");
			mm.rotationOffset = nbt.getFloat("RotationOffset");
			mm.translationScale = nbt.getFloat("TranslationScale");
			mm.translationOffset = nbt.getFloat("TranslationOffset");
			return mm;
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("MotionType", this.motionType);
			nbt.setFloat("RotationSpeed", this.rotationSpeed);
			nbt.setFloat("RotationOffset", this.rotationOffset);
			nbt.setFloat("TranslationScale", this.translationScale);
			nbt.setFloat("TranslationOffset", this.translationOffset);
			return nbt;
		}
	}
}