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

package jp.ngt.rtm.modelpack.modelset;

import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.WireConfig;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import jp.ngt.rtm.render.WirePartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetWire extends ModelSetBase<WireConfig>
{
	public ModelSetWire()
	{
		super();
	}

	public ModelSetWire(WireConfig cfg)
	{
		super(cfg);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		if(this.isDummy())
		{
			this.modelObj = ModelObject.getDummy();
			this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/container/button_19g_JRF_0.png");
		}
		else
		{
			WireConfig cfg = this.getConfig();
			PartsRenderer renderer = null;
			if(cfg.model.rendererPath == null || cfg.model.rendererPath.isEmpty())
			{
				renderer = new WirePartsRenderer(false);
			}
			this.modelObj = new ModelObject(cfg.model, this, renderer);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public WireConfig getDummyConfig()
	{
		return WireConfig.getDummy();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderModelInGui(Minecraft par1)
	{
        ModelObject mo = this.modelObj;
        NGTUtilClient.bindTexture(mo.textures[0].material.texture);
        mo.model.renderAll(false);
	}
}