package jp.ngt.rtm.entity.npc;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**NPCで使用->Itemの引数に*/
public class EntityDummyPlayer extends EntityPlayer
{
	public final EntityNPC npc;
	private double yOffset;

	public EntityDummyPlayer(World world, EntityNPC par2)
	{
		super(world, new GameProfile(UUID.randomUUID(), "HogeHoge"));
		this.npc = par2;
		this.capabilities.isCreativeMode = true;
		this.yOffset = par2.getYOffset();
	}

	@Override
    public double getYOffset()
    {
		return this.yOffset;
    }

	@Override
	public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar)
    {
		;
	}

	/*@Override
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
	{
		return false;
	}*/

	@Override
	public float getEyeHeight()
    {
        return this.height * 0.85F;
    }

	@Override
	public boolean isSpectator()
	{
		return true;
	}

	@Override
	public boolean isCreative()
	{
		return true;
	}
}