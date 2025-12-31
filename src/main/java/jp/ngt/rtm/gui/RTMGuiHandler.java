package jp.ngt.rtm.gui;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.block.tileentity.TileEntityMovingMachine;
import jp.ngt.rtm.block.tileentity.TileEntityStation;
import jp.ngt.rtm.block.tileentity.TileEntityTrainWorkBench;
import jp.ngt.rtm.electric.TileEntitySignalConverter;
import jp.ngt.rtm.electric.TileEntitySpeaker;
import jp.ngt.rtm.electric.TileEntityTicketVendor;
import jp.ngt.rtm.entity.npc.EntityMotorman;
import jp.ngt.rtm.entity.npc.EntityNPC;
import jp.ngt.rtm.entity.npc.Role;
import jp.ngt.rtm.entity.train.EntityFreightCar;
import jp.ngt.rtm.entity.train.parts.EntityContainer;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.gui.vendor.ContainerTicketVendor;
import jp.ngt.rtm.gui.vendor.GuiTicketVendor;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.rail.TileEntityMarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class RTMGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == RTMCore.guiIdFreightCar)
		{
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityFreightCar)
			{
				return new ContainerFreightCar(player, (EntityFreightCar)entity);
			}
		}
		else if(ID == RTMCore.guiIdItemContainer)
		{
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityContainer)
			{
				return new ContainerItemContainer(player, (EntityContainer)entity);
			}
		}
		else if(ID == RTMCore.guiIdTrainControlPanel)
		{
			Entity entity0 = world.getEntityByID(x);
			if(entity0 instanceof EntityVehicleBase)
			{
				EntityVehicleBase vehicle = (EntityVehicleBase)entity0;
				if(vehicle.getFirstPassenger() instanceof EntityPlayer)
				{
					return new ContainerTrainControlPanel(vehicle, (EntityPlayer)vehicle.getFirstPassenger());
				}
			}
		}
		else if(ID == RTMCore.guiIdTrainWorkBench)
		{
			TileEntityTrainWorkBench tile = (TileEntityTrainWorkBench)BlockUtil.getTileEntity(world, x, y, z);
			return new ContainerRTMWorkBench(player.inventory, world, tile, player.capabilities.isCreativeMode);
		}
		else if(ID == RTMCore.guiIdTicketVendor)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			if(tile instanceof TileEntityTicketVendor)
			{
				return new ContainerTicketVendor(player.inventory, (TileEntityTicketVendor)tile);
			}
		}
		else if(ID == RTMCore.guiIdNPC)
		{
			return new ContainerNPC(player, (EntityNPC)world.getEntityByID(x));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == RTMCore.guiIdSelectEntityModel)
		{
			Entity entity = world.getEntityByID(x);
			return new GuiSelectModel(world, (IResourceSelector)entity);
		}
		else if(ID == RTMCore.guiIdSelectTileEntityModel)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			return new GuiSelectModel(world, (IResourceSelector)tile);
		}
		else if(ID == RTMCore.guiIdSelectItemModel)
		{
			Item item = player.inventory.getCurrentItem().getItem();
			return new GuiSelectModel(world, (IResourceSelector)item);
		}
		else if(ID == RTMCore.guiIdSelectTileEntityTexture)
		{
			return new GuiSelectTexture((IResourceSelector)BlockUtil.getTileEntity(world, x, y, z), null);
		}
		else if(ID == RTMCore.guiIdSelectItemTexture)
		{
			Item item = player.inventory.getCurrentItem().getItem();
			return new GuiSelectTexture((IResourceSelector)item, null);
		}
		else if(ID == RTMCore.guiIdFreightCar)
		{
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityFreightCar)
			{
				return new GuiFreightCar(player, (EntityFreightCar)entity);
			}
		}
		else if(ID == RTMCore.guiIdItemContainer)
		{
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityContainer)
			{
				return new GuiItemContainer(player, (EntityContainer)entity);
			}
		}
		else if(ID == RTMCore.guiIdTrainControlPanel)
		{
			Entity entity0 = world.getEntityByID(x);
			if(entity0 instanceof EntityVehicleBase)
			{
				EntityVehicleBase vehicle = (EntityVehicleBase)entity0;
				if(vehicle.getFirstPassenger() instanceof EntityPlayer)
				{
					return new GuiVehicleControlPanel(new ContainerTrainControlPanel(vehicle, (EntityPlayer)vehicle.getFirstPassenger()));
				}
			}
		}
		else if(ID == RTMCore.guiIdTrainWorkBench)
		{
			TileEntityTrainWorkBench tile = (TileEntityTrainWorkBench)BlockUtil.getTileEntity(world, x, y, z);
			return new GuiRTMWorkBench(player.inventory, world, tile, player.capabilities.isCreativeMode);
		}
		else if(ID == RTMCore.guiIdSignalConverter)
		{
			return new GuiSignalConverter((TileEntitySignalConverter)BlockUtil.getTileEntity(world, x, y, z));
		}
		else if(ID == RTMCore.guiIdTicketVendor)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			if(tile instanceof TileEntityTicketVendor)
			{
				return new GuiTicketVendor(player.inventory, (TileEntityTicketVendor)tile);
			}
		}
		else if(ID == RTMCore.guiIdStation)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			if(tile instanceof TileEntityStation)
			{
				return new GuiStation((TileEntityStation)tile);
			}
		}
		else if(ID == RTMCore.guiIdPaintTool)
		{
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityPlayer)
			{
				return new GuiPaintTool((EntityPlayer)entity);
			}
		}
		else if(ID == RTMCore.guiIdMovingMachine)
		{
			TileEntity tile = BlockUtil.getTileEntity(world, x, y, z);
			if(tile instanceof TileEntityMovingMachine)
			{
				return new GuiMovingMachine((TileEntityMovingMachine)tile);
			}
		}
		else if(ID == RTMCore.guiIdNPC)
		{
			EntityNPC npc = (EntityNPC)world.getEntityByID(x);
			if(npc.getRole() == Role.SALESPERSON || npc.getRole() == Role.BUYER)
			{
				return new GuiSalesperson(player, npc);
			}
			else
			{
				return new GuiNPC(player, npc);
			}
		}
		else if(ID == RTMCore.guiIdMotorman)
		{
			return GuiMotorman.getGui((EntityMotorman)world.getEntityByID(x));
		}
		else if(ID == RTMCore.guiIdRailMarker)
		{
			return new GuiRailMarker((TileEntityMarker)BlockUtil.getTileEntity(world, x, y, z));
		}
		else if(ID == RTMCore.guiIdCamera)
		{
			return new GuiCamera(player);
		}
		else if(ID == RTMCore.guiIdConvertModel)
		{
			return new GuiConvertModel(player);
		}
		else if(ID == RTMCore.guiIdDecoration)
		{
			return new GuiDecorationBlock(player);
		}
		else if(ID == RTMCore.guiIdSignboard)
		{
			IResourceSelector selector;
			if(y < 0)
			{
				selector = (IResourceSelector)player.inventory.getCurrentItem().getItem();
			}
			else
			{
				selector = (IResourceSelector)BlockUtil.getTileEntity(world, x, y, z);
			}
			return new GuiSignboard(selector);
		}
		else if(ID == RTMCore.guiIdSpeaker)
		{
			return new GuiSpeaker((TileEntitySpeaker)BlockUtil.getTileEntity(world, x, y, z));
		}
		return null;
	}
}