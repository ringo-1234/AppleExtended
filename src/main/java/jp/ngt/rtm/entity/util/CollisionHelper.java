package jp.ngt.rtm.entity.util;

import java.util.List;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;

public final class CollisionHelper
{
	public static final CollisionHelper INSTANCE = new CollisionHelper();

	private CollisionHelper(){}

	public void syncCollisionObj(ResourceType type, ModelSetBase modelSet)
	{
		if(modelSet.getCollisionObj() != null)
		{
			Thread thread = new Thread(){
				@Override
				public void run()
				{
					modelSet.getCollisionObj().syncCollisionObj(type, modelSet, this);
				}
			};
			thread.start();
		}
	}

	public void onCollison(GetCollisionBoxesEvent event)
	{
		if(event.getEntity() == null || !(event.getEntity() instanceof EntityPlayer)){return;}

		double range = 10.5D;//通常0.25
		AxisAlignedBB box = event.getAabb();
		List<AxisAlignedBB> aabbList = event.getCollisionBoxesList();
		List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getEntity(), box.grow(range));
		for(Entity entity : list)
		{
			if(entity.isDead || !(entity instanceof IResourceSelector)){continue;}

			((IResourceSelector)entity).getResourceState().applyCollison(event.getEntity(), entity, box, aabbList);
		}

		boolean debug = false;
		if(debug && !aabbList.isEmpty())
		{
			for(int i = 0; i < aabbList.size(); ++i)
			{
				AxisAlignedBB hit = aabbList.get(i);
				NGTLog.debug("[%d] %3.1f, %3.1f, %3.1f (%3.0f %3.0f %3.0f)",
						i, hit.maxX - hit.minX, hit.maxY - hit.minY, hit.maxZ - hit.minZ,
						(hit.maxX + hit.minX) * 0.5D, (hit.maxY + hit.minY) * 0.5D, (hit.maxZ + hit.minZ) * 0.5D);
			}
		}
	}
}