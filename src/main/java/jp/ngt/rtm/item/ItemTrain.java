package jp.ngt.rtm.item;

import java.util.ArrayList;
import java.util.List;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.util.PermissionManager;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMItem;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityFreightCar;
import jp.ngt.rtm.entity.train.EntityTanker;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.EntityTrainDieselCar;
import jp.ngt.rtm.entity.train.EntityTrainElectricCar;
import jp.ngt.rtm.entity.train.EntityTrainTest;
import jp.ngt.rtm.entity.train.parts.EntityVehiclePart;
import jp.ngt.rtm.entity.train.util.FormationEntry;
import jp.ngt.rtm.item.ItemTrain.TrainSet;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.util.RailMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemTrain extends ItemWithModel
{
	private static final int SPLIT = 128;

	public ItemTrain()
	{
		super();
	}

	@Override
	protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder)
    {
		if(holder.getWorld().isRemote)
		{
			List<TrainSet> trainSets = getFormationFromItem(holder.getItemStack());
			if(!trainSets.isEmpty())
			{
				return holder.success();//編成データある場合はGUI開かない
			}
			return super.onItemRightClick(holder);
		}
		return holder.success();
    }

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		BlockPos pos = holder.getBlockPos();
		EntityPlayer player = holder.getPlayer();

		if(world.isRemote || !PermissionManager.INSTANCE.hasPermission(player, RTMCore.EDIT_VEHICLE)){return holder.success();}

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		ResourceState<ModelSetTrain> state = this.getModelState(itemStack);

		RailMap rm0 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, player, x, y, z);
    	if(rm0 == null){return holder.success();}
    	if(!this.checkObstacle(state.getResourceSet().getConfig(), player, world, x, y, z, rm0)){return holder.success();}

		int spIndex = rm0.getNearlestPoint(SPLIT, (double)x + 0.5D, (double)z + 0.5D);
		float yw0 = NGTMath.wrapAngle(rm0.getRailYaw(SPLIT, spIndex));
    	float yaw = EntityBogie.fixBogieYaw(-player.rotationYaw, yw0);
    	float pitch = EntityBogie.fixBogiePitch(rm0.getRailPitch(SPLIT, spIndex), yw0, yaw);
    	double posX = rm0.getRailPos(SPLIT, spIndex)[1];
		double posY = rm0.getRailHeight(SPLIT, spIndex);// + EntityTrainBase.TRAIN_HEIGHT;
		double posZ = rm0.getRailPos(SPLIT, spIndex)[0];

		List<TrainSet> trainSets = getFormationFromItem(itemStack);
		if(trainSets.isEmpty())
		{
			EntityTrainBase train = this.getTrain(itemStack, world);
			train.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
			train.getResourceState().readFromNBT(state.writeToNBT());
			train.spawnTrain(world);
	    	train.updateResourceState();
		}
		else
		{
			for(TrainSet set : trainSets)
			{
				Vec3 vec = PooledVec3.create(set.posX, set.posY, set.posZ);
				vec = vec.rotateAroundY(yaw);

				EntityTrainBase train = this.getTrain(itemStack, world);
				train.setPositionAndRotation(posX + vec.getX(), posY + vec.getY(), posZ + vec.getZ(), yaw + set.yaw, pitch + set.pitch);
				//train.getResourceState().readFromNBT(this.getModelState(itemStack).writeToNBT());
				train.getResourceState().setResourceName(set.modelName);
				train.spawnTrain(world);
		    	train.updateResourceState();
		    	//以下連結処理
		    	train.getBogie(0).isActivated = true;
		    	train.getBogie(1).isActivated = true;
		    	train.setNotch(1);
			}
		}

		itemStack.shrink(1);
		return holder.success();
    }

	/**設置予定線の路上に障害物がないか*/
	private boolean checkObstacle(TrainConfig cfg, EntityPlayer player, World world, int x, int y, int z, RailMap rm0)
	{
		float range = cfg.trainDistance + 4.0F;
    	List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player,
    			new AxisAlignedBB(x - range, y - 4, z - range, x + range + 1, y + 8, z + range + 1));
		for(Entity entity : list)
		{
			if(entity instanceof EntityTrainBase || entity instanceof EntityBogie || entity instanceof EntityVehiclePart)
			{
				double distanceSq = entity.getDistanceSq(x, y, z);
				RailMap rm1 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, player, entity.posX, entity.posY, entity.posZ);
				if(distanceSq < range * range && rm0.equals(rm1))
				{
					NGTLog.sendChatMessage(player, "message.train.obstacle", new Object[]{entity.toString()});
					return false;
				}
			}
		}
		return true;
	}

	private EntityTrainBase getTrain(ItemStack itemStack, World world)
	{
		switch(itemStack.getItemDamage())
		{
		case 1: return new EntityTrainElectricCar(world, "");
		case 2: return new EntityFreightCar(world, "");
		case 3: return new EntityTanker(world, "");
		case 127: return new EntityTrainTest(world, "");
		default:return new EntityTrainDieselCar(world, "");
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack par1)
	{
		return this.getUnlocalizedName() + "." + par1.getItemDamage();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		if(!this.isInCreativeTab(tab)){return;}

		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
		list.add(new ItemStack(this, 1, 3));
		list.add(new ItemStack(this, 1, 127));
	}

	@Override
	protected ResourceType getModelType(ItemStack itemStack)
	{
		switch(itemStack.getItemDamage())
		{
		case 0: return RTMResource.TRAIN_DC;
		case 1: return RTMResource.TRAIN_EC;
		case 2: return RTMResource.TRAIN_CC;
		case 3: return RTMResource.TRAIN_TC;
		case 127: return RTMResource.TRAIN_TEST;
		default: return RTMResource.TRAIN_DC;
		}
	}

	@Override
	public int getGuiId(ItemStack stack)
	{
		return RTMCore.guiIdSelectItemModel;
	}

	@Override
	protected ResourceState getNewState(ItemStack itemStack, ResourceType type)
	{
		return new ResourceState(type, null);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void addInformation(ItemArgHolder holder, List list, ITooltipFlag flag)
    {
		List<TrainSet> trainSets = getFormationFromItem(holder.getItemStack());
		if(trainSets.isEmpty())
		{
			super.addInformation(holder, list, flag);
		}
		else
		{
			StringBuilder sb = new StringBuilder("{");
			for(TrainSet set : trainSets)
			{
				sb.append(set.modelName);
				sb.append(",");
			}
			sb.append("}");
			list.add(TextFormatting.GRAY + sb.toString());
		}
    }

	private static ItemStack getItem(ResourceType type)
	{
		if(type == RTMResource.TRAIN_DC)
		{
			return new ItemStack(RTMItem.itemtrain, 1, 0);
		}
		else if(type == RTMResource.TRAIN_EC)
		{
			return new ItemStack(RTMItem.itemtrain, 1, 1);
		}
		else if(type == RTMResource.TRAIN_CC)
		{
			return new ItemStack(RTMItem.itemtrain, 1, 2);
		}
		else if(type == RTMResource.TRAIN_TC)
		{
			return new ItemStack(RTMItem.itemtrain, 1, 3);
		}
		else if(type == RTMResource.TRAIN_TEST)
		{
			return new ItemStack(RTMItem.itemtrain, 1, 127);
		}
		return new ItemStack(RTMItem.itemtrain, 1, 0);
	}

	/**編成をアイテム化*/
	public static ItemStack convertFormationAsItem(EntityTrainBase train)
	{
		ItemStack stack = getItem(train.getResourceState().type);
		NBTTagList tagList = new NBTTagList();
		for(FormationEntry entry : train.getFormation().entries)
		{
			if(entry != null)
			{
				EntityTrainBase train2 = entry.train;
				Vec3 vec = PooledVec3.create(train2.posX - train.posX, train2.posY - train.posY, train2.posZ - train.posZ);
				vec = vec.rotateAroundY(NGTMath.wrapAngle(-train.rotationYaw));
				TrainSet set = new TrainSet(train2.getResourceState().getResourceName(), entry.entryId,
						(float)vec.getX(), (float)vec.getY(), (float)vec.getZ(), (float)(train2.rotationYaw - train.rotationYaw), (float)(train2.rotationPitch - train.rotationPitch));
				tagList.appendTag(set.writeToNBT());
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("formations", tagList);
		stack.setTagCompound(nbt);
		return stack;
	}

	public static List<TrainSet> getFormationFromItem(ItemStack stack)
	{
		List<TrainSet> list = new ArrayList<>();
		if(stack.hasTagCompound())
		{
			NBTTagList tagList = stack.getTagCompound().getTagList("formations", 10);
			for(int i = 0; i < tagList.tagCount(); ++i)
			{
				list.add(TrainSet.readFromNBT(tagList.getCompoundTagAt(i)));
			}
		}
		return list;
	}

	/**位置と向きは1両目との相対値*/
	public static class TrainSet
	{
		public final String modelName;
		public final int index;
		public final float posX, posY, posZ;
		public final float yaw, pitch;

		public TrainSet(String p1, int p2, float p3, float p4, float p5, float p6, float p7)
		{
			this.modelName = p1;
			this.index = p2;
			this.posX = p3;
			this.posY = p4;
			this.posZ = p5;
			this.yaw = p6;
			this.pitch = p7;
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("model", this.modelName);
			nbt.setInteger("index", this.index);
			nbt.setFloat("pos_x", this.posX);
			nbt.setFloat("pos_y", this.posY);
			nbt.setFloat("pos_z", this.posZ);
			nbt.setFloat("yaw", this.yaw);
			nbt.setFloat("pitch", this.pitch);
			return nbt;
		}

		public static TrainSet readFromNBT(NBTTagCompound nbt)
		{
			String s = nbt.getString("model");
			int i = nbt.getInteger("index");
			float f0 = nbt.getFloat("pos_x");
			float f1 = nbt.getFloat("pos_y");
			float f2 = nbt.getFloat("pos_z");
			float f3 = nbt.getFloat("yaw");
			float f4 = nbt.getFloat("pitch");
			return new TrainSet(s, i, f0, f1, f2, f3, f4);
		}
	}
}