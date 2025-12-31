package jp.ngt.rtm.block.tileentity;

import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.render.MachinePartsRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLight extends TileEntityMachineBase
{
	@Override
	public void update()
    {
		super.update();

    	boolean powered = this.world.isBlockIndirectlyGettingPowered(this.getPos()) > 0;
    	if(this.isGettingPower ^ powered)
    	{
    		this.isGettingPower = powered;
    		this.world.checkLight(this.getPos());//明るさ更新
    	}
    }

	@Override
	public Vec3 getNormal(float x, float y, float z, float pitch, float yaw)
	{
		if(this.normal == null)
		{
			this.normal = new Vec3(x, y, z);
			MachinePartsRenderer.rotateVec(this.normal,
					this.getBlockMetadata(), pitch, yaw);
		}
		return this.normal;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
    	return INFINITE_EXTENT_AABB;
    }

	@Override
	protected ResourceType getSubType()
	{
		return RTMResource.MACHINE_LIGHT;
	}
}