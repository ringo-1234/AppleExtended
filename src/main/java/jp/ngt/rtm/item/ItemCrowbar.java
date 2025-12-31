package jp.ngt.rtm.item;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.BlockConverter;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCrowbar extends ItemSword
{
	public ItemCrowbar()
	{
		super(ToolMaterial.IRON);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		return new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		int bx = pos.getX();
		int by = pos.getY();
		int bz = pos.getZ();

		if(world.isRemote)
		{
			if(BlockUtil.getBlock(world, pos) == Blocks.IRON_BLOCK)
			{
				player.openGui(RTMCore.instance, RTMCore.guiIdConvertModel, world, player.getEntityId(), 0, 0);
			}
		}
		else
		{
			if(BlockUtil.getBlock(world, pos) == Blocks.COBBLESTONE)
			{
				byte b0 = BlockConverter.shouldCreateConverter(world, bx, by, bz);
				if(b0 >= 0)
				{
					BlockConverter.createConverter(world, bx, by, bz, b0, false);
					//player.addStat(RTMAchievement.buildConverter, 1);
					return EnumActionResult.SUCCESS;
				}
			}
			else
			{
				for(int i = 0; i < 64; ++i)
				{
					for(int j = 0; j < 64; ++j)
					{
						int x = bx + i - 32;
						int y = by;
						int z = bz + j - 32;
						BlockPos pos2 = new BlockPos(x, y, z);
						TileEntity tile0 = world.getTileEntity(pos2);
						if(tile0 instanceof TileEntityLargeRailBase && ((TileEntityLargeRailBase)tile0).getRailCore() == null)
						{
							world.setBlockToAir(pos2);
						}
					}
				}
			}
		}
		return EnumActionResult.SUCCESS;
    }
}