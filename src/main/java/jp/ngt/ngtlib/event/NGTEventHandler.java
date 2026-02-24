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

package jp.ngt.ngtlib.event;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.command.CommandUsage;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.protection.ProtectionManager;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.VersionChecker;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class NGTEventHandler
{
	@SubscribeEvent
	public void onTick(WorldTickEvent event)//Side.SERVERのみ
	{
		if(event.phase == Phase.END)
		{
			TickProcessQueue.getInstance(Side.SERVER).onTick(event.world);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if(event.phase == Phase.END && NGTUtil.getClientWorld() != null)
		{
			TickProcessQueue.getInstance(Side.CLIENT).onTick(NGTUtil.getClientWorld());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(final ClientConnectedToServerEvent event)
	{
		//イベント内でパケ送信しないように
		TickProcessQueue.getInstance(Side.CLIENT).add(new TickProcessEntry(){
			@Override
			public boolean process(World world)
			{
				VersionChecker.showUpdateMessage(event.getHandler());
				CommandUsage.showUsage();
				return true;
			}
		});
	}

	//このタイミングではS->Cのパケット届かない
	/*@SubscribeEvent
	public void connectedFromClient(ServerConnectionFromClientEvent event)
	{
		;
	}*/

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		ProtectionManager.INSTANCE.sendDataToClient();
	}

	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load event)
	{
		ProtectionManager.INSTANCE.loadData(event.getWorld());
	}

	/*@SubscribeEvent
	public void onSaveWorld(WorldEvent.Save event)
	{
		ProtectionManager.INSTANCE.saveData(event.world);
	}*/

	@SubscribeEvent
	public void onRightClickBlock(RightClickBlock event)
	{
		//1クリックで2回呼ばれる
		//うち1回はstack=nullなので弾く
		if(event.getItemStack().isEmpty()){return;}

		try
		{
			//座標取得はActionがBlockの時にしないとぬるぽ
			int x = NGTMath.floor(event.getHitVec().x);
			int y = NGTMath.floor(event.getHitVec().y);
			int z = NGTMath.floor(event.getHitVec().z);
			if(ProtectionManager.INSTANCE.rightClickBlock(event.getEntityPlayer(), x, y, z))
			{
				if(!event.getWorld().isRemote)//Clientでキャンセルするとパケ送られない
				{
					event.setCanceled(true);
				}
			}
		}
		catch(NullPointerException e)
		{
			//Sponge鯖でhitVecがnullになる場合がある
			//vec==nullで弾いても解決しないっぽいのでとりあえず握り潰す
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onLeftClickBlock(LeftClickBlock event)
	{
		try
		{
			int x = NGTMath.floor(event.getHitVec().x);
			int y = NGTMath.floor(event.getHitVec().y);
			int z = NGTMath.floor(event.getHitVec().z);
			if(ProtectionManager.INSTANCE.leftClickBlock(event.getEntityPlayer(), x, y, z))
			{
				//クリエイティブでは呼ばれない?
				event.setCanceled(true);
				//Client側で破壊されたブロックを再描画
				BlockUtil.markBlockForUpdate(event.getEntityPlayer().world, x, y, z);
			}
		}
		catch(NullPointerException e)
		{
			//上に同じ
			//e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPlayerBreakBlock(BreakEvent event)
	{
		int x = event.getPos().getX();
		int y = event.getPos().getY();
		int z = event.getPos().getZ();
		//鯖側のみ
		if(ProtectionManager.INSTANCE.leftClickBlock(event.getPlayer(), x, y, z))
		{
			event.setCanceled(true);
			//Client側で破壊されたブロックを再描画
			BlockUtil.markBlockForUpdate(event.getPlayer().world, x, y, z);
		}
	}

	@SubscribeEvent
	public void onPlayerInteractEntity(EntityInteract event)
	{
		if(ProtectionManager.INSTANCE.rightClickEntity(event.getEntityPlayer(), event.getTarget()))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerAttackEntity(AttackEntityEvent event)
	{
		if(ProtectionManager.INSTANCE.leftClickEntity(event.getEntityPlayer(), event.getTarget()))
		{
			event.setCanceled(true);
		}
	}
}