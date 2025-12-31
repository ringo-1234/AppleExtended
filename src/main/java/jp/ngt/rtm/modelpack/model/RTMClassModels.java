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
