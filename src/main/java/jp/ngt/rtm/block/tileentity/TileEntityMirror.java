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

import jp.ngt.ngtlib.block.EnumFace;
import jp.ngt.rtm.block.BlockMirror;
import jp.ngt.rtm.block.BlockMirror.MirrorType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMirror extends TileEntity implements ITickable
{
	public MirrorType mirrorType;
	@SideOnly(Side.CLIENT)
	public MirrorComponent[] mirrors;

	@Override
	public void update()
	{
		if(this.world.isRemote && this.mirrors == null)
		{
			this.mirrorType = ((BlockMirror)this.getBlockType()).mirrorType;
			this.setupMirror();
		}
	}

	private void setupMirror()
	{
		boolean b = this.mirrorType == MirrorType.Mono_Panel;
		this.mirrors = new MirrorComponent[b ? 1 : 6];
		for(int i = 0; i < this.mirrors.length; ++i)
		{
			EnumFace face = b ? EnumFace.get(this.getBlockMetadata()) : EnumFace.get(i);
			this.mirrors[i] = new MirrorComponent(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.mirrorType, face);
			MirrorObject.add(this.world, this.mirrors[i], face, this.mirrorType);
		}
	}

	@Override
	public void onChunkUnload()
    {
		this.removeMirror();
    }

	@Override
	public void invalidate()//ブロック破壊時
	{
		super.invalidate();
		this.removeMirror();
	}

	private void removeMirror()
	{
		if(this.world.isRemote && this.mirrors != null)
		{
			for(int i = 0; i < this.mirrors.length; ++i)
			{
				MirrorObject.remove(this.mirrors[i]);
			}
			this.mirrors = null;
		}
	}

	public int getAlpha()
	{
		if(this.mirrorType == MirrorType.Hexa_Cube)
		{
			int meta = this.getBlockMetadata();
			return (meta << 4) + meta;//*16
		}
		else
		{
			return 0xFF;
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass)
    {
        return this.mirrorType == MirrorType.Hexa_Cube ? pass == 1 : pass == 0;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 4096.0D;
    }

	@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
    	AxisAlignedBB bb = new AxisAlignedBB(this.getPos(), this.getPos().add(1, 1, 1));
    	return bb;
    }
}