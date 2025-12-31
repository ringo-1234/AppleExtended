package jp.ngt.rtm.entity;

import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.item.ItemInstalledObject.IstlObjType;
import jp.ngt.rtm.modelpack.ResourceType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBumpingPost extends EntityInstalledObject
{
	public EntityBumpingPost(World par1)
	{
		super(par1);
		this.setSize(1.5F, 1.5F);
	}

	@Override
	protected void dropItems()
	{
		this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.BUMPING_POST.id), 0.0F);
	}

	@Override
	public ResourceType getSubType()
	{
		return RTMResource.MACHINE_BUMPINGPOST;
	}
}