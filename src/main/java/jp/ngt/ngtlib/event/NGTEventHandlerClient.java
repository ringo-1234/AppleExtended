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

import jp.ngt.ngtlib.renderer.GLHelper;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NGTEventHandlerClient
{
	public static final NGTEventHandlerClient INSTANCE = new NGTEventHandlerClient();

	private NGTEventHandlerClient(){}

	@SubscribeEvent
	public void onChangeTexture(TextureStitchEvent.Post event)
	{
		GLHelper.initGLList();
	}

	/*@SubscribeEvent
	public void onFinishRenderWorld(RenderWorldLastEvent event)
	{
		GLHelper.clearGLList();
	}*/

	/*@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event)//Minecraft 830
	{
		GLHelper.clearGLList();
	}*/

	@SubscribeEvent
	public void onUnloadWorld(WorldEvent.Unload event)
	{
		if(event.getWorld().isRemote)
		{
			//GLHelper.clearGLList();
		}
	}
}