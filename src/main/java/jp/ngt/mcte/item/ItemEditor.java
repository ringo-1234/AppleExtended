package jp.ngt.mcte.item;

import jp.ngt.mcte.editor.Editor;
import jp.ngt.mcte.editor.EditorManager;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemEditor extends ItemCustom
{
	public ItemEditor()
	{
		super();
		this.setMaxStackSize(1);
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();

		if(world.isRemote)
		{
			return holder.success();
		}
		else
		{
			if(EditorManager.INSTANCE.canPlayerUseEditor(player))
			{
				Editor editor = EditorManager.INSTANCE.getEditor(player);
				if(editor != null)
				{
					RayTraceResult target = editor.getEntity().getTarget(false);
		    		if(target != null)
		        	{
		    			BlockPos pos = target.getBlockPos();
		    			DataParameter<BlockPos> dp = editor.getEntity().isSelectEnd() ? EntityEditor.START_POS : EntityEditor.END_POS;
		    			editor.getEntity().setPos(dp, pos.getX(), pos.getY(), pos.getZ());
		        	}
		    		return holder.success();
				}
			}
			return holder.success();
		}
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();

		if(!world.isRemote)
		{
			if(EditorManager.INSTANCE.canPlayerUseEditor(player))
			{
				Editor editor = EditorManager.INSTANCE.getEditor(player);
				if(editor != null)
				{
					DataParameter<BlockPos> dp = editor.getEntity().isSelectEnd() ? EntityEditor.START_POS : EntityEditor.END_POS;
					editor.getEntity().setPos(dp, pos.getX(), pos.getY(), pos.getZ());
					return holder.success();
				}

				EntityEditor entityEditor = Editor.getNewEditor(world, player, pos.getX(), pos.getY(), pos.getZ());
				entityEditor.setPositionAndRotation(player.posX, player.posY, player.posZ, 0.0F, 0.0F);
				world.spawnEntity(entityEditor);
			}
			else
			{
				NGTLog.sendChatMessage(player, "You don't have permission to use Editor.");
			}
		}
		return holder.success();
    }

	public static RayTraceResult getTarget(EntityPlayer player, boolean par2, boolean selectSide)
	{
		if(par2)
		{
			RayTraceResult target = BlockUtil.getMOPFromPlayer(player, 128.0D, true);
			if(target != null && target.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos pos = target.getBlockPos();
				if(selectSide)
				{
					switch(target.sideHit)
					{
					case DOWN: pos = pos.down();break;
					case UP: pos = pos.up();break;
					case NORTH: pos = pos.north();break;
					case SOUTH: pos = pos.south();break;
					case EAST: pos = pos.east();break;
					case WEST: pos = pos.west();break;
					}
				}
				return new RayTraceResult(target.hitVec, target.sideHit, pos);
			}
		}
		else
		{
			float f = 1.0F;
	        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
	        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
	        double dx = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
	        double dy = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.getYOffset();
	        double dz = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
	        Vec3d vec3 = new Vec3d(dx, dy, dz);
	        float f3 = NGTMath.cos(-yaw - 180.0F);//0.017453292=PI/180
	        float f4 = NGTMath.sin(-yaw - 180.0F);
	        float f5 = -NGTMath.cos(-pitch);
	        float f6 = NGTMath.sin(-pitch);
	        float x2 = f4 * f5;
	        float z2 = f3 * f5;
	        double distance = 8.0D;
	        Vec3d vec31 = vec3.addVector((double)x2 * distance, (double)f6 * distance, (double)z2 * distance);

	        int x = NGTMath.floor(vec31.x);
	        int y = NGTMath.floor(vec31.y);
	        int z = NGTMath.floor(vec31.z);
	        if(y >= 0)
	        {
	        	if(y > 255)
	        	{
	        		y = 255;
	        	}
	        	return new RayTraceResult(vec31, EnumFacing.UP, new BlockPos(x, y, z));
	        }
		}
		return null;
	}
}