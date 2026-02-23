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

import org.lwjgl.opengl.GL11;

import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetTrain extends ModelSetVehicleBase<TrainConfig>
{
	@SideOnly(Side.CLIENT)
	public ModelObject[] bogieModels;

	public ModelSetTrain()
	{
		super();
	}

	public ModelSetTrain(TrainConfig par1)
	{
		super(par1);
	}

	@Override
	public void constructOnServer()
	{
		super.constructOnServer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		if(this.isDummy())
		{
			this.bogieModels = new ModelObject[2];
			this.bogieModels[0] = this.bogieModels[1] = ModelObject.getDummy();
		}
		else
		{
			this.bogieModels = this.registerBogieModel();
		}
	}

	@SideOnly(Side.CLIENT)
	private ModelObject[] registerBogieModel()
	{
		ModelObject[] modelBogies = new ModelObject[2];
		for(int i = 0; i < 2; ++i)
		{
			modelBogies[i] = new ModelObject(((TrainConfig)this.getConfig()).getBogieModel(i), this, null, "isBogie");
		}
		return modelBogies;
	}

	@Override
	public TrainConfig getDummyConfig()
	{
		return TrainConfig.getDummyConfig();
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void renderPartsInGui(Minecraft par1)
	{
		TrainConfig cfg = (TrainConfig)this.cfg;
		for(int i = 0; i < 2; ++i)
		{
			GL11.glPushMatrix();
			float[] fa = cfg.getBogiePos()[i];
			GL11.glTranslatef(fa[0], fa[1], fa[2]);
			GL11.glRotatef(180.0F * (float)i, 0.0F, 1.0F, 0.0F);
			this.bogieModels[i].render(null, cfg, 0, 0.0F);
			GL11.glPopMatrix();
		}
	}
}