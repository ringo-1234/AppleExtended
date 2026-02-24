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

package jp.ngt.rtm;

import jp.ngt.rtm.entity.train.util.FormationManager;
import jp.ngt.rtm.entity.vehicle.EntityVehicleBase;
import jp.ngt.rtm.entity.vehicle.IUpdateVehicle;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.network.PacketPlaySound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
	private final FormationManager fm = new FormationManager(false);

	public void preInit()
	{
    	;
	}

	public void init()
	{
		ModelPackLoadThread thread = new ModelPackLoadThread(Side.SERVER);
		thread.start();
	}

	public void complete(){}

	public IUpdateVehicle getSoundUpdater(EntityVehicleBase par1)
	{
		return null;
	}

	/**@return 0:接続なし, 1:接続完了*/
	public byte getConnectionState()
	{
		return 1;
	}

	/**@param par1 : 0:接続なし, 1:接続完了*/
	public void setConnectionState(byte par1){}

	public void spawnModParticle(World world, double x, double y, double z, double mX, double mY, double mZ){}

	public void renderMissingModel(){}

	public float getFov(EntityPlayer player, float fov)
	{
		return 1.0F;
	}

	/**
	 * 音を鳴らす、リピートなし
	 * @param entity
	 * @param sound null可, SoundObj(RTM)またはSoundEvent(MC)のResourceLocation
	 */
	public void playSound(Entity entity, String sound, float vol, float pitch)
    {
		if(sound != null)
		{
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketPlaySound(entity, sound, vol, pitch));
		}
    }

	public void playSound(TileEntity entity, String sound, float vol, float pitch)
    {
		if(sound != null)
		{
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketPlaySound(entity, sound, vol, pitch));
		}
    }

	/**Sever/Clientでインスタンス分けて取得*/
	public FormationManager getFormationManager()
	{
		return this.fm;
	}
}