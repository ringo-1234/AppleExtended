package jp.ngt.rtm.item;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.SerializableItemType;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.block.tileentity.TileEntityCrossingGate;
import jp.ngt.rtm.block.tileentity.TileEntityFlag;
import jp.ngt.rtm.block.tileentity.TileEntityFluorescent;
import jp.ngt.rtm.block.tileentity.TileEntityLight;
import jp.ngt.rtm.block.tileentity.TileEntityMechanism;
import jp.ngt.rtm.block.tileentity.TileEntityPipe;
import jp.ngt.rtm.block.tileentity.TileEntityPlantOrnament;
import jp.ngt.rtm.block.tileentity.TileEntityPoint;
import jp.ngt.rtm.block.tileentity.TileEntityPole;
import jp.ngt.rtm.block.tileentity.TileEntityRailroadSign;
import jp.ngt.rtm.block.tileentity.TileEntityScaffold;
import jp.ngt.rtm.block.tileentity.TileEntityScaffoldStairs;
import jp.ngt.rtm.block.tileentity.TileEntitySignBoard;
import jp.ngt.rtm.block.tileentity.TileEntityTurnstile;
import jp.ngt.rtm.electric.Connection.ConnectionType;
import jp.ngt.rtm.electric.IBlockConnective;
import jp.ngt.rtm.electric.TileEntityConnector;
import jp.ngt.rtm.electric.TileEntityInsulator;
import jp.ngt.rtm.electric.TileEntitySignal;
import jp.ngt.rtm.electric.TileEntitySpeaker;
import jp.ngt.rtm.electric.TileEntityTicketVendor;
import jp.ngt.rtm.entity.EntityATC;
import jp.ngt.rtm.entity.EntityBumpingPost;
import jp.ngt.rtm.entity.EntityInstalledObject;
import jp.ngt.rtm.entity.EntityTrainDetector;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import jp.ngt.rtm.modelpack.state.ResourceState;
import jp.ngt.rtm.modelpack.state.ResourceStateSignboard;
import jp.ngt.rtm.rail.TileEntityLargeRailBase;
import jp.ngt.rtm.rail.util.RailMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemInstalledObject extends ItemWithModel
{
	public ItemInstalledObject()
	{
		super();
		this.setHasSubtypes(true);
	}

	@Override
	protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ)
    {
		ItemStack itemStack = holder.getItemStack();
		World world = holder.getWorld();
		EntityPlayer player = holder.getPlayer();
		BlockPos pos = holder.getBlockPos();
		EnumFacing side = holder.getFacing();

		if(!world.isRemote)
		{
			BlockPos newPos = pos;
			if(!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
			{
				//置換可能でないブロックなら、その側面に設置
				newPos = pos.offset(side);
				if(!world.isAirBlock(newPos)){return holder.success();}
			}
			//BlockPos newPos = ItemUtil.getPlacePos(pos, side);
			int x = newPos.getX();
			int y = newPos.getY();
			int z = newPos.getZ();
			int meta = itemStack.getItemDamage();
			int sideIndex = side.getIndex();
			Block block = null;
			IstlObjType type = IstlObjType.getType(meta);

			if(type == IstlObjType.FLUORESCENT)
			{
	            int i1 = NGTMath.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	            if(player.canPlayerEdit(newPos, side, itemStack))
	            {
	            	block = RTMBlock.fluorescent;
	            	BlockUtil.setBlock(world, x, y, z, block, meta, 3);
                	byte dir = 0;
                	switch(sideIndex)
                	{
                	case 0: if(i1 == 0 || i1 == 2)
                			{
                				dir = 0;
                			}
                			else if(i1 == 1 || i1 == 3)
                			{
                				dir = 4;
                			}
                			break;
                	case 1: if(i1 == 0 || i1 == 2)
                			{
                				dir = 2;
                			}
                			else if(i1 == 1 || i1 == 3)
                			{
                				dir = 6;
                			}
                			break;
                	case 2: dir = 1;break;
                	case 3: dir = 3;break;
                	case 4: dir = 5;break;
                	case 5: dir = 7;break;
                	}
                	TileEntityFluorescent tile = (TileEntityFluorescent)BlockUtil.getTileEntity(world, x, y, z);
                	tile.setDir(dir);
                	this.updateResource(tile, itemStack);
	            }
			}
			else if(type == IstlObjType.CROSSING)
			{
				if(side == EnumFacing.UP)
				{
					block = RTMBlock.crossingGate;
					BlockUtil.setBlock(world, x, y, z, block, 0, 3);
					TileEntityCrossingGate tile = (TileEntityCrossingGate)BlockUtil.getTileEntity(world, x, y, z);
					tile.setRotation(player, 15.0F, true);
					this.updateResource(tile, itemStack);
				}
			}
			else if(type == IstlObjType.RAILLOAD_SIGN)
			{
				if(side == EnumFacing.UP || side == EnumFacing.DOWN)
				{
					block = RTMBlock.railroadSign;
					BlockUtil.setBlock(world, x, y, z, block, 0, 3);
					TileEntityRailroadSign tile = (TileEntityRailroadSign)BlockUtil.getTileEntity(world, x, y, z);
					tile.setRotation(player, 15.0F, true);
					this.updateResource(tile, itemStack);
				}
			}
			else if(type == IstlObjType.SIGNAL)
			{
				if(side != EnumFacing.UP && side != EnumFacing.DOWN)
				{
					//信号機の場合は、右クリブロックを置換
					x = pos.getX();
					y = pos.getY();
					z = pos.getZ();
					IBlockState state = world.getBlockState(pos);
					Block target = state.getBlock();
					if(target != RTMBlock.signal)
					{
						TileEntity origTile = null;
						if(target.hasTileEntity(state))
						{
							origTile = world.getTileEntity(pos);
						}

						int meta2 = BlockUtil.getMetadata(world, pos);
						BlockUtil.setBlock(world, x, y, z, RTMBlock.signal, meta2, 3);

						TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
						if(tile instanceof TileEntitySignal)
			        	{
							TileEntitySignal teSignal = ((TileEntitySignal)tile);
							int dir = sideIndex == 2 ? 2 : (sideIndex == 4 ? 3 : (sideIndex == 3 ? 0 : 1));
							teSignal.setSignalProperty(this.getModelState(itemStack).getResourceName(), target, dir, player, origTile);
							block = RTMBlock.signal;
			        	}
					}
				}
			}
			else if(type == IstlObjType.TURNSTILE)
			{
				block = RTMBlock.turnstile;
				//当たり判定のため
				int dir = (NGTMath.floor((NGTMath.normalizeAngle(player.rotationYaw + 180.0D) / 90.0D) + 0.5D) & 3);
				BlockUtil.setBlock(world, x, y, z, block, dir, 3);
				TileEntityTurnstile tile = (TileEntityTurnstile)BlockUtil.getTileEntity(world, x, y, z);
				tile.setRotation(player, 90.0F, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.BUMPING_POST)
			{
				if(side == EnumFacing.UP && this.setEntityOnRail(world, new EntityBumpingPost(world), x, y - 1, z, player, itemStack))
				{
					block = Blocks.STONE;
				}
			}
			else if(type == IstlObjType.LINEPOLE)
			{
				block = RTMBlock.linePole;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityPole tile = (TileEntityPole)BlockUtil.getTileEntity(world, x, y, z);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.STAIR)
			{
				block = RTMBlock.scaffoldStairs;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				block.onBlockPlacedBy(world, newPos, null, player, null);//向き保存用
				TileEntityScaffoldStairs tile = (TileEntityScaffoldStairs)BlockUtil.getTileEntity(world, x, y, z);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.SCAFFOLD)
			{
				block = RTMBlock.scaffold;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				block.onBlockPlacedBy(world, newPos, null, player, null);//向き保存用
				TileEntityScaffold tile = (TileEntityScaffold)BlockUtil.getTileEntity(world, x, y, z);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.POINT)
			{
				if(side == EnumFacing.UP)
				{
					block = RTMBlock.point;
					BlockUtil.setBlock(world, x, y, z, block, 0, 3);
					TileEntityPoint tile = (TileEntityPoint)BlockUtil.getTileEntity(world, x, y, z);
					tile.setRotation(player, 15.0F, false);
					this.updateResource(tile, itemStack);
				}
			}
			else if(type == IstlObjType.SIGNBOARD)
			{
				block = RTMBlock.signboard;
				BlockUtil.setBlock(world, x, y, z, block, sideIndex, 3);
				TileEntitySignBoard tile = (TileEntitySignBoard)BlockUtil.getTileEntity(world, x, y, z);
				int playerFacing = (NGTMath.floor((NGTMath.normalizeAngle(player.rotationYaw + 180.0D) / 90D) + 0.5D) & 3);
				tile.setDirection((byte)playerFacing);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.TICKET_VENDOR)
			{
				block = RTMBlock.ticketVendor;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityTicketVendor tile = (TileEntityTicketVendor)BlockUtil.getTileEntity(world, x, y, z);
				tile.setRotation(player, 15.0F, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.LIGHT)
			{
				block = RTMBlock.light;
				BlockUtil.setBlock(world, x, y, z, block, sideIndex, 3);
				TileEntityLight tile = (TileEntityLight)BlockUtil.getTileEntity(world, x, y, z);
				tile.setRotation(player, 15.0F, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.FLAG)
			{
				block = RTMBlock.flag;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityFlag tile = (TileEntityFlag)BlockUtil.getTileEntity(world, x, y, z);
				tile.setRotation(player, 15.0F, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.ATC)
			{
				if(side == EnumFacing.UP && this.setEntityOnRail(world, new EntityATC(world), x, y - 1, z, player, itemStack))
				{
					block = Blocks.STONE;
				}
			}
			else if(type == IstlObjType.TRAIN_DETECTOR)
			{
				if(side == EnumFacing.UP && this.setEntityOnRail(world, new EntityTrainDetector(world), x, y - 1, z, player, itemStack))
				{
					block = Blocks.STONE;
				}
			}
			else if(type == IstlObjType.INSULATOR)
			{
				block = RTMBlock.insulator;
				BlockUtil.setBlock(world, x, y, z, block, sideIndex, 3);
				TileEntityInsulator tile = (TileEntityInsulator)BlockUtil.getTileEntity(world, x, y, z);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.CONNECTOR_IN || type == IstlObjType.CONNECTOR_OUT)
			{
				Block target = BlockUtil.getBlock(world, pos);
				if(target instanceof IBlockConnective && ((IBlockConnective)target).canConnect(world, x, y, z))
				{
					if(type == IstlObjType.CONNECTOR_OUT)
					{
						sideIndex += 6;
					}
					block = RTMBlock.connector;
					BlockUtil.setBlock(world, x, y, z, block, sideIndex, 3);
					TileEntityConnector tile = (TileEntityConnector)BlockUtil.getTileEntity(world, x, y, z);
					this.updateResource(tile, itemStack);
					tile.setConnectionTo(pos.getX(), pos.getY(), pos.getZ(), ConnectionType.DIRECT, null);
				}
			}
			else if(type == IstlObjType.PIPE)
			{
				block = RTMBlock.pipe;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityPipe tile = (TileEntityPipe)BlockUtil.getTileEntity(world, x, y, z);
				tile.setAttachedSide((byte)side.getIndex());
				tile.refresh();
				//world.notifyBlockOfStateChange(new BlockPos(x, y, z), block);
				world.notifyNeighborsOfStateChange(new BlockPos(x, y, z), block, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.PLANT)
			{
				block = RTMBlock.plant_ornament;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityPlantOrnament tile = (TileEntityPlantOrnament)BlockUtil.getTileEntity(world, x, y, z);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.SPEAKER)
			{
				block = RTMBlock.speaker;
				BlockUtil.setBlock(world, x, y, z, block, sideIndex, 3);
				TileEntitySpeaker tile = (TileEntitySpeaker)BlockUtil.getTileEntity(world, x, y, z);
				tile.setRotation(player, 15.0F, true);
				this.updateResource(tile, itemStack);
			}
			else if(type == IstlObjType.MECHANISM)
			{
				block = RTMBlock.mechanism;
				BlockUtil.setBlock(world, x, y, z, block, 0, 3);
				TileEntityMechanism tile = (TileEntityMechanism)BlockUtil.getTileEntity(world, x, y, z);
				tile.setSide(sideIndex);
				this.updateResource(tile, itemStack);
			}

			if(block != null)
			{
				//clientで呼び出す必要あり
				SoundType soundtype = block.getSoundType(world.getBlockState(pos), world, pos, player);
                //world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				RTMCore.proxy.playSound(player, "block.stone.place", (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemStack.shrink(1);
			}
		}
		return holder.success();
    }

	private void updateResource(final IResourceSelector selector, ItemStack stack)
	{
		selector.getResourceState().readFromNBT(this.getModelState(stack).writeToNBT());
		selector.updateResourceState();
	}

	private boolean setEntityOnRail(World world, EntityInstalledObject entity, int x, int y, int z, EntityPlayer player, ItemStack stack)
	{
		RailMap rm0 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, null, x, y, z);
    	if(rm0 == null){return false;}

    	int split = 128;
    	int i0 = rm0.getNearlestPoint(split, (double)x + 0.5D, (double)z + 0.5D);
    	double posX = rm0.getRailPos(split, i0)[1];
		double posY = rm0.getRailHeight(split, i0) + 0.0625D;
		double posZ = rm0.getRailPos(split, i0)[0];
		float yaw = rm0.getRailRotation(split, i0);
		float yaw2 = -player.rotationYaw + 180.0F;
		float dif = NGTMath.wrapAngle(yaw - yaw2);
		boolean invert = false;
		if(Math.abs(dif) > 90.0F)
		{
			yaw += 180.0F;
			invert = true;
		}

		ResourceState<ModelSetMachine> itemState = this.getModelState(stack);
		entity.setPosition(posX, posY, posZ);
		entity.rotationYaw = yaw;
		entity.rotationPitch = -rm0.getRailPitch(split, i0) * (invert ? -1.0F : 1.0F);
		entity.rotationRoll = rm0.getCant(split, i0) * (invert ? -1.0F : 1.0F);
		world.spawnEntity(entity);
		entity.getResourceState().readFromNBT(itemState.writeToNBT());
		//entity.getResourceState().setResourceName(this.getModelState(stack).getResourceName());
		entity.updateResourceState();
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
    {
        return super.getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
		if(!this.isInCreativeTab(tab)){return;}

        for(IstlObjType type : IstlObjType.values())
        {
        	if(type == IstlObjType.NONE){continue;}
        	list.add(new ItemStack(this, 1, type.id));
        }
    }

    @Override
	protected ResourceType getModelType(ItemStack itemStack)
    {
    	return IstlObjType.getType(itemStack.getItemDamage()).type;
	}

    public enum IstlObjType implements SerializableItemType
    {
    	FLUORESCENT(        0, RTMResource.ORNAMENT_LAMP),
    	PLANT(              1, RTMResource.ORNAMENT_PLANT),
    	//FLUORESCENT_BROKEN( 2, RTMResource.ORNAMENT_LAMP),
    	INSULATOR(          3, RTMResource.CONNECTOR_RELAY),
    	PIPE(               4, RTMResource.ORNAMENT_PIPE),
    	CROSSING(           5, RTMResource.MACHINE_GATE),
    	RAILLOAD_SIGN(      6, RTMResource.RRS),
    	SIGNAL(             7, RTMResource.SIGNAL),
    	CONNECTOR_IN(       8, RTMResource.CONNECTOR_INPUT),
    	CONNECTOR_OUT(      9, RTMResource.CONNECTOR_OUTPUT),
    	ATC(               10, RTMResource.MACHINE_ANTENNA_SEND),
    	TRAIN_DETECTOR(    11, RTMResource.MACHINE_ANTENNA_RECEIVE),
    	TURNSTILE(         12, RTMResource.MACHINE_TURNSTILE),
    	BUMPING_POST(      13, RTMResource.MACHINE_BUMPINGPOST),
    	LINEPOLE(          14, RTMResource.ORNAMENT_POLE),
    	POINT(             16, RTMResource.MACHINE_POINT),
    	SIGNBOARD(         17, RTMResource.SIGNBOARD),
    	TICKET_VENDOR(     18, RTMResource.MACHINE_VENDOR),
    	LIGHT(             19, RTMResource.MACHINE_LIGHT),
    	FLAG(              20, RTMResource.FLAG),
    	STAIR(             21, RTMResource.ORNAMENT_STAIR),
    	SCAFFOLD(          22, RTMResource.ORNAMENT_SCAFFOLD),
    	SPEAKER(          23, RTMResource.MACHINE_SPEAKER),
    	MECHANISM(          24, RTMResource.MECHANISM),
    	NONE(              -1, null);

    	public final byte id;
    	public final ResourceType type;

    	private IstlObjType(int par1, ResourceType par2)
    	{
    		this.id = (byte)par1;
    		this.type = par2;
    	}

    	public static IstlObjType getType(int id)
    	{
    		for(IstlObjType type : IstlObjType.values())
    		{
    			if(type.id == id)
    			{
    				return type;
    			}
    		}
    		return NONE;
    	}

		@Override
		public int getId()
		{
			return this.id;
		}
    }

	@Override
	public int getGuiId(ItemStack stack)
	{
		int meta = stack.getItemDamage();
		IstlObjType type = IstlObjType.getType(meta);
		if(type == IstlObjType.RAILLOAD_SIGN || type == IstlObjType.FLAG)
		{
			return RTMCore.guiIdSelectItemTexture;
		}
		else if(type == IstlObjType.SIGNBOARD)
		{
			return RTMCore.guiIdSignboard;
		}
		return RTMCore.guiIdSelectItemModel;
	}

	@Override
	protected ResourceState getNewState(ItemStack itemStack, ResourceType type)
	{
		IstlObjType type2 = IstlObjType.getType(itemStack.getItemDamage());
		if(type2 == IstlObjType.SIGNBOARD)
		{
			return new ResourceStateSignboard(type, null);
		}
		else
		{
			return new ResourceState<>(type, null);
		}
	}
}