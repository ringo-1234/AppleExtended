package jp.ngt.rtm.entity.npc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.ai.EntityAIDriveWithMacro;
import jp.ngt.rtm.entity.ai.EntityAIDrivingWithDiagram;
import jp.ngt.rtm.entity.ai.EntityAIDrivingWithSignal;
import jp.ngt.rtm.entity.npc.macro.TrainCommand;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMotorman extends EntityNPC
{
	private static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.<ItemStack>createKey(EntityMotorman.class, DataSerializers.ITEM_STACK);

	private EntityAIDriveWithMacro aiMacro;

	public EntityMotorman(World world)
	{
		super(world);
		this.myRole = Role.MOTORMAN;
		this.aiMacro = new EntityAIDriveWithMacro(this);
		this.tasks.taskEntries.clear();//EntityNPCのタスク削除
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiMacro);
        this.tasks.addTask(3, new EntityAIDrivingWithDiagram(this));
        this.tasks.addTask(4, new EntityAIDrivingWithSignal(this));
        this.tasks.addTask(5, new EntityAIWander(this, SPEED));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public EntityMotorman(World world, EntityPlayer player)
	{
		this(world);//AI登録のため
		this.setOwnerId(player.getUniqueID());
	}

	@Override
	public void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(HELD_ITEM, new ItemStack(Items.APPLE));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if(this.getDiagram() != null)
        {
        	NBTTagCompound diagramNBT = new NBTTagCompound();
        	ItemStack itemstack = this.getDiagram();
        	itemstack.writeToNBT(diagramNBT);
        	NBTTagList nbttaglist = new NBTTagList();
        	nbttaglist.appendTag(diagramNBT);
        	nbt.setTag("DiagramRTM", nbttaglist);
        }
    }

	@Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if(nbt.hasKey("DiagramRTM"))
        {
        	NBTTagList nbttaglist = nbt.getTagList("DiagramRTM", 10);
    		NBTTagCompound diagramNBT = (NBTTagCompound)nbttaglist.getCompoundTagAt(0);
    		ItemStack itemstack = new ItemStack(diagramNBT);
    		this.setDiagram(itemstack);
        }
    }

	@Override
	public void onDeath(DamageSource source)
    {
		super.onDeath(source);
		if(!this.world.isRemote && this.hasDiagram())
		{
			this.entityDropItem(this.getDiagram(), 1.0F);
		}
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
    	if(player.world.isRemote)
		{
			player.openGui(RTMCore.instance, RTMCore.guiIdMotorman, player.world, this.getEntityId(), 0, 0);
		}

    	ItemStack itemstack = player.inventory.getCurrentItem();
    	if(itemstack.getItem() instanceof ItemWritableBook)
		{
			if(!this.world.isRemote)
			{
				this.setDiagram(itemstack.copy());
			}
			itemstack.shrink(1);
		}
    	return false;
    }

    @SideOnly(Side.CLIENT)
    public void setMacro(File file) throws IOException
    {
    	List<String> list = NGTText.readText(file, "");
    	StringBuilder sb = new StringBuilder("TMacro");
    	for(String s : list)
    	{
    		sb.append(TrainCommand.SEPARATOR);
    		sb.append(s);
    	}
    	RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, sb.toString(), this));
    	NGTLog.sendChatMessage(NGTUtil.getClientPlayer(), "Set macro : " + file.getName());
    }

    public void setMacro(String[] args)
    {
    	this.aiMacro.setMacro(args);
    }

    public boolean hasDiagram()
    {
    	ItemStack itemstack = this.getDiagram();
    	return itemstack != null && itemstack.getItem() instanceof ItemWritableBook;
    }

    public ItemStack getDiagram()
    {
    	return this.getDataManager().get(HELD_ITEM);
    }

    public void setDiagram(ItemStack par1)
    {
    	this.getDataManager().set(HELD_ITEM, par1);
    }

    @Override
    public boolean isMotorman()
	{
		return true;
	}
}