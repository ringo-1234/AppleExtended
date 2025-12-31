package jp.ngt.rtm.item;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.tileentity.TileEntityPaint;
import jp.ngt.rtm.item.PaintProperty.EnumPaintType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintTool extends ItemCustom
{
	public ItemPaintTool()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();

		if(player.isSneaking())
		{
			if(world.isRemote)
			{
				player.openGui(RTMCore.instance, RTMCore.guiIdPaintTool, world, player.getEntityId(), 0, 0);
			}
		}
		else
		{
			player.setActiveHand(holder.getHand());
			//player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));//1.8
		}
        return holder.success();
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		return holder.pass();
    }

	/**ServerOnly*/
    public void usePaintTool(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int dir, float fx, float fy, float fz)
    {
    	if(world.isRemote){return;}

    	PaintProperty prop = PaintProperty.getProperty(itemStack);
		int[] ia = BlockUtil.facing[dir];
		int bx = (int)(fx * 16.0F);
		int by = (int)(fy * 16.0F);
		int bz = (int)(fz * 16.0F);
		int maxX = (bx + prop.radius) * (1 - ia[0]);
		int maxY = (by + prop.radius) * (1 - ia[1]);
		int maxZ = (bz + prop.radius) * (1 - ia[2]);
		int minX = (bx - prop.radius) * (1 - ia[0]);
		int minY = (by - prop.radius) * (1 - ia[1]);
		int minZ = (bz - prop.radius) * (1 - ia[2]);

		int i = minX >= 0 ? (minX >> 4) : (-(Math.abs(minX - 1) >> 4) - 1);
		for(; i <= maxX >> 4; ++i)
		{
			int j = minY >= 0 ? (minY >> 4) : (-(Math.abs(minY - 1) >> 4) - 1);
			for(; j <= maxY >> 4; ++j)
			{
				int k = minZ >= 0 ? (minZ >> 4) : (-(Math.abs(minZ - 1) >> 4) - 1);
				for(; k <= maxZ >> 4; ++k)
				{
					this.paint(world, x + i, y + j, z + k, dir, bx - (i << 4), by - (j << 4), bz - (k << 4), prop);
				}
			}
		}

		/*if(!player.capabilities.isCreativeMode)
		{
			if(itemStack.isItemStackDamageable())
            {
				itemStack.setItemDamage(itemStack.getItemDamage() + 1);
            }
		}*/
    }

    @Override
	public void onUsingTick(ItemStack stack, EntityLivingBase living, int count)
    {
    	if(living instanceof EntityPlayer)
		{
    		EntityPlayer player = (EntityPlayer)living;
    		float f0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;//PlayerControllerMP.getBlockReachDistance()
    		RayTraceResult mop = this.rayTrace(player, f0, 0.0F);//EntityRenderer.getMouseOver()
    		if(mop != null)
    		{
    			int x = mop.getBlockPos().getX();
    			int y = mop.getBlockPos().getY();
    			int z = mop.getBlockPos().getZ();
    			float fx = (float)mop.hitVec.x - (float)x;
    	        float fy = (float)mop.hitVec.y - (float)y;
    	        float fz = (float)mop.hitVec.z - (float)z;
    	        this.usePaintTool(stack, player, player.world, x, y, z, mop.sideHit.getIndex(), fx, fy, fz);
    		}
		}
    }

    //EntityPlayer.rayTrace()はClientOnlyなので同様の内容を実装
    private RayTraceResult rayTrace(EntityPlayer player, double distance, float partialTicks)
    {
        Vec3d vec3d = player.getPositionEyes(partialTicks);
        Vec3d vec3d1 = player.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        return player.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
		;
    }

	private void paint(World world, int x, int y, int z, int dir, int paintX, int paintY, int paintZ, PaintProperty prop)
	{
		IBlockState state = BlockUtil.getBlockState(world, x, y, z);
		if(!state.isOpaqueCube()){return;}//isFullCube(),isOpaqueCube()はガラスでfalse

		int[] ia = BlockUtil.facing[dir];
		x += ia[0];
		y += ia[1];
		z += ia[2];
		BlockPos pos = new BlockPos(x, y, z);
		Block block = BlockUtil.getBlock(world, pos);
		if(block == Blocks.AIR)
		{
			BlockUtil.setBlock(world, pos, RTMBlock.paint, 0, 3);
		}
		else if(block != RTMBlock.paint)
		{
			return;
		}

		int p1 = -1;
		int p2 = -1;
		switch(dir)
		{
		case 0:
		case 1:
			p1 = paintX;
			p2 = paintZ;
			break;
		case 2:
		case 3:
			p1 = paintX;
			p2 = paintY;
			break;
		case 4:
		case 5:
			p1 = paintY;
			p2 = paintZ;
			break;
		}

		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityPaint)
		{
			TileEntityPaint paint = (TileEntityPaint)tile;
			for(int i = 0; i < 16; ++i)//前の位置記憶して間を補完
			{
				for(int j = 0; j < 16; ++j)
				{
					EnumPaintType type = EnumPaintType.values()[prop.type];
					boolean flag = false;
					int alpha = prop.alpha;
					int r = prop.radius - 1;
					if(type == EnumPaintType.pen_circle || type == EnumPaintType.brush || type == EnumPaintType.eraser_circle)
					{
						int disQ = (i - p1) * (i - p1) + (j - p2) * (j - p2);
						int rad2 = r * r;
						flag = disQ <= rad2;

						if(type == EnumPaintType.brush && rad2 > 0)
						{
							alpha -= (alpha * disQ / rad2);
						}
					}
					else if(type == EnumPaintType.pen_square || type == EnumPaintType.eraser_square)
					{
						flag = Math.abs(i - p1) <= r && Math.abs(j - p2) <= r;
					}

					if(flag)
					{
						if(type == EnumPaintType.eraser_circle || type == EnumPaintType.eraser_square)
						{
							paint.clearColor(i, j, dir);
						}
						else
						{
							paint.setColor(prop.color, alpha, i, j, dir);
						}
					}
				}
			}
			paint.markDirty();
			BlockUtil.markBlockForUpdate(world, paint.getPos());
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 72000;
    }

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }

	@Override
	public int getItemEnchantability()
    {
        return 1;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
}