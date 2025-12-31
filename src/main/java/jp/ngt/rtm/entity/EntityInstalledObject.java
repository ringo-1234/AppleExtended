package jp.ngt.rtm.entity;

import jp.ngt.ngtlib.entity.EntityCustom;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.ScriptExecuter;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import jp.ngt.rtm.modelpack.state.ResourceState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityInstalledObject extends EntityCustom implements IResourceSelector
{
	private ResourceState<ModelSetMachine> state = new ResourceState<>(this.getSubType(), this);
	private ScriptExecuter executer = new ScriptExecuter();

	public float rotationRoll;

	public EntityInstalledObject(World world)
	{
		super(world);
	}

	@Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass >= 0;
    }

	@Override
	protected void entityInit()
	{
		;
	}

	@Override
	public void syncData()
	{
		this.updateResourceState();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
    {
		this.state.readFromNBT(nbt.getCompoundTag("State"));
		this.rotationRoll = nbt.getFloat("RotationRoll");
    }

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
    {
		nbt.setTag("State", this.state.writeToNBT());
		nbt.setFloat("RotationRoll", this.rotationRoll);
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

	@Override
	public boolean canBePushed()
    {
        return false;
    }

	@Override
	public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
    public void onUpdate()
    {
		super.onUpdate();

		if(!this.getEntityWorld().isRemote)
		{
			this.executer.execScript(this);
		}
    }

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
		if(player.isSneaking())
    	{
			if(this.world.isRemote)
	        {
				player.openGui(RTMCore.instance, RTMCore.guiIdSelectEntityModel, player.world, this.getEntityId(), 0, 0);
	        }
			return true;
		}
		return true;
    }

	@Override
    public boolean attackEntityFrom(DamageSource par1, float par2)
    {
		if(this.isEntityInvulnerable(par1) || this.isDead)
        {
            return false;
        }
        else
        {
        	if(par1.getTrueSource() instanceof EntityPlayer)
        	{
        		if(!this.world.isRemote)
                {
        			this.markVelocityChanged();
            		EntityPlayer entityplayer = (EntityPlayer)par1.getTrueSource();
            		if(!entityplayer.capabilities.isCreativeMode)
                    {
                		this.dropItems();
                    }
            		this.getEntityWorld().playSound(this.posX, this.posY, this.posZ, SoundEvents.BLOCK_STONE_BREAK,
            				SoundCategory.BLOCKS, 1.0F, 1.0F, true);
        			this.setDead();
                }
        		return true;
        	}
        	return false;
        }
    }

	protected abstract void dropItems();

	@Override
	public void move(MoverType type, double par1, double par3, double par5){}

	@Override
	public void addVelocity(double par1, double par3, double par5){}

	@Override
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int par9, boolean par10)
    {
		this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

	/*=====================================================================================*/

	@Override
    public ResourceState<ModelSetMachine> getResourceState()
    {
    	return this.state;
    }

	@Override
	public void updateResourceState()
	{
		if(this.world == null || !this.world.isRemote)
		{
        	PacketNBT.sendToClient(this);
		}
	}

	@Override
	public int[] getSelectorPos()
	{
		return new int[]{this.getEntityId(), -1, 0};
	}

	@Override
	public boolean closeGui(ResourceState par1)
	{
		this.updateResourceState();
		return true;
	}

	protected abstract ResourceType getSubType();
}