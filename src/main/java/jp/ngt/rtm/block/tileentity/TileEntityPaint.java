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

package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.block.TileEntityCustom;
import jp.ngt.ngtlib.util.ColorUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPaint extends TileEntityCustom
{
	private int[][] colors = new int[6][256];
	private int[][] alphas = new int[6][256];
	private boolean[] hasColor = new boolean[6];

	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        int[] ia = nbt.getIntArray("Colors");
        if(ia.length == 6 * 256)
        {
        	for(int i = 0; i < 6; ++i)
            {
            	for(int j = 0; j < 256; ++j)
                {
            		this.colors[i][j] = ia[i * 256 + j];
                }
            }
        }

        byte[] ba = nbt.getByteArray("Alphas");
        if(ba.length == 6 * 256)
        {
        	for(int i = 0; i < 6; ++i)
            {
            	for(int j = 0; j < 256; ++j)
                {
            		this.alphas[i][j] = (int)ba[i * 256 + j] + 128;
                }
            }
        }

        byte[] ba2 = nbt.getByteArray("HasColor");
        if(ba2.length == 6)
        {
        	for(int i = 0; i < 6; ++i)
            {
        		this.hasColor[i] = (ba2[i] == 1);
            }
        }
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        int[] ia = new int[6 * 256];
        for(int i = 0; i < 6; ++i)
        {
        	for(int j = 0; j < 256; ++j)
            {
            	ia[i * 256 + j] = this.colors[i][j];
            }
        }
        nbt.setIntArray("Colors", ia);

        byte[] ba = new byte[6 * 256];
        for(int i = 0; i < 6; ++i)
        {
        	for(int j = 0; j < 256; ++j)
            {
            	ba[i * 256 + j] = (byte)(this.alphas[i][j] - 128);
            }
        }
        nbt.setByteArray("Alphas", ba);

        byte[] ba2 = new byte[6];
        for(int i = 0; i < 6; ++i)
        {
        	ba2[i] = (byte)(this.hasColor[i] ? 1 : 0);
        }
        nbt.setByteArray("HasColor", ba2);

        return nbt;
    }

	/**{p1, p2} = {x, y} or {y, z} or {x, z}*/
	public void setColor(int color, int alpha, int p1, int p2, int dir)
	{
		if(p1 < 0 || p1 >= 16 || p2 < 0 || p2 >= 16){return;}
		int index = p1 * 16 + p2;
		int c0 = this.colors[dir][index];
		int a0 = this.alphas[dir][index];
		int[] ca = ColorUtil.alphaBlending(color, alpha, c0, a0);
		this.colors[dir][index] = ca[0];
		this.alphas[dir][index] = ca[1];
		this.hasColor[dir] = true;
	}

	public void clearColor(int p1, int p2, int dir)
	{
		if(p1 < 0 || p1 >= 16 || p2 < 0 || p2 >= 16){return;}
		int index = p1 * 16 + p2;
		this.colors[dir][index] = 0;
		this.alphas[dir][index] = 0;
	}

	public boolean hasColor(int dir)
	{
		return this.hasColor[dir];
	}

	public int getColor(int p1, int p2, int dir)
	{
		return this.colors[dir][p1 * 16 + p2];
	}

	public int getAlpha(int p1, int p2, int dir)
	{
		return this.alphas[dir][p1 * 16 + p2];
	}

	@Override
	public void markDirty()
	{
		super.markDirty();

		if(!this.world.isRemote)
		{
			boolean flag = false;
			for(int i = 0; i < 6; ++i)
	        {
				if(this.hasColor(i))
				{
					this.hasColor[i] = false;
					for(int j = 0; j < 256; ++j)
		            {
						if(this.alphas[i][j] != 0)
						{
							this.hasColor[i] = true;
							flag = true;
							break;
						}
		            }
				}
	        }

			if(flag)
			{
				this.sendPacket();
			}
			else
			{
				this.world.setBlockToAir(this.getPos());
			}
		}
	}

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
    	return new AxisAlignedBB(this.getPos(), this.getPos().add(1, 1, 1));
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;//ミニチュア化した際に後から描画されるブロックで隠れないように
    }
}