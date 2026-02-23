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

package jp.ngt.rtm.modelpack.model;

import java.util.HashMap;
import java.util.Map;

import jp.ngt.ngtlib.renderer.model.MCModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RTMClassModels
{
	private static final Map<String, MCModel> MODELS = new HashMap<String, MCModel>();

	private static void init()
	{
		MODELS.put("ModelBogie.class", new ModelBogie());
		MODELS.put("ModelContainer_BigChest.class", new ModelContainer_BigChest());
		MODELS.put("ModelTrain_kiha600.class", new ModelTrain_kiha600());
		MODELS.put("ModelTrain_Minecart.class", new ModelTrain_Minecart());
	}

	public static MCModel getModel(String key)
	{
		if(MODELS.isEmpty())
		{
			init();
		}
		return MODELS.get(key);
	}
}
