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

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.OrnamentConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetOrnament extends ModelSetBase<OrnamentConfig>
{
	public ModelSetOrnament()
	{
		super();
	}

	public ModelSetOrnament(OrnamentConfig par1)
	{
		super(par1);
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
			OrnamentConfig cfg = this.getConfig();
			this.modelObj = new ModelObject(cfg.model, this, null);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public OrnamentConfig getDummyConfig()
	{
		return OrnamentConfig.getDummy();
	}
}