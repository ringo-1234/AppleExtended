/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.rtm.modelpack;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.rtm.entity.EntityBullet;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ScriptExecuter implements ICommandSender
{
	private IResourceSelector caller;
	public long count;

	protected Object callMethod(IResourceSelector selector, String name, Object... args)
	{
		ModelSetBase set = (ModelSetBase)selector.getResourceState().getResourceSet();
		if(set.serverSE != null)
		{
			return ScriptUtil.doScriptIgnoreError(set.serverSE, name, args);
		}
		return null;
	}

	public void execScript(IResourceSelector selector)
	{
		this.caller = selector;
		this.callMethod(selector, "onUpdate", selector, this);
		++this.count;
	}

	public void execCommand(String command)
	{
		MinecraftServer server = NGTUtil.getServer();

        if(server != null && server.isAnvilFileSet() && server.isCommandBlockEnabled())
        {
            ICommandManager icommandmanager = server.getCommandManager();
            try
            {
                icommandmanager.executeCommand(this, command);
            }
            catch (Throwable throwable)
            {
            	throwable.printStackTrace();
            }
        }
	}

	public void fireBullet(World world, Entity shooter, String type, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		EntityBullet bullet = new EntityBullet(world, shooter, BulletType.getBulletType(type), posX, posY, posZ, speedX, speedY, speedZ);
		world.spawnEntity(bullet);
	}

	@Override
	public String getName()
	{
		return "RTM Script Executer";
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean canUseCommand(int permLevel, String commandName)
	{
		return permLevel <= 2;
	}

	@Override
	public BlockPos getPosition()
	{
		return new BlockPos(this.getPositionVector());
	}

	@Override
	public Vec3d getPositionVector()
	{
		if(this.caller instanceof Entity)
		{
			return ((Entity)this.caller).getPositionVector();
		}
		else if(this.caller instanceof TileEntity)
		{
			BlockPos pos = ((TileEntity)this.caller).getPos();
			return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		}
		return null;
	}

	@Override
	public World getEntityWorld()
	{
		if(this.caller instanceof Entity)
		{
			return ((Entity)this.caller).getEntityWorld();
		}
		else if(this.caller instanceof TileEntity)
		{
			return ((TileEntity)this.caller).getWorld();
		}
		return null;
	}

	@Override
	public Entity getCommandSenderEntity()
	{
		if(this.caller instanceof Entity)
		{
			return ((Entity)this.caller);
		}
		return null;
	}

	@Override
	public boolean sendCommandFeedback()
	{
		return false;
	}

	@Override
	public void setCommandStat(Type type, int amount) {}

	@Override
	public MinecraftServer getServer()
	{
		return NGTUtil.getServer();
	}
}