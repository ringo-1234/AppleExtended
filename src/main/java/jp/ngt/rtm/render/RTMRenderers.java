package jp.ngt.rtm.render;

import javax.script.ScriptEngine;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.rtm.modelpack.ModelPackException;
import jp.ngt.rtm.modelpack.ModelPackManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RTMRenderers
{
	public static <R extends PartsRenderer> R getRendererWithScript(ResourceLocation par1, String... args)
	{
		String text = ModelPackManager.INSTANCE.getScript(par1.getResourcePath());
		ScriptEngine se = ScriptUtil.doScript(text);

		String s = (String)ScriptUtil.getScriptField(se, "renderClass");
		R renderer = (R)RTMRenderers.getRenderer(s, args);
		renderer.setScript(se, par1);
		se.put("renderer", renderer);

		return renderer;
	}

	public static PartsRenderer getRenderer(String name, String... args)
	{
		name = name.replace("jp.ngt.rtm.render.", "");

		if(name.equals("FirearmPartsRenderer"))
		{
			return new FirearmPartsRenderer(args);
		}
		else if(name.equals("MachinePartsRenderer"))
		{
			return new MachinePartsRenderer(args);
		}
		else if(name.equals("NPCPartsRenderer"))
		{
			return new NPCPartsRenderer(args);
		}
		else if(name.equals("OrnamentPartsRenderer"))
		{
			return new OrnamentPartsRenderer(args);
		}
		else if(name.equals("RailPartsRenderer"))
		{
			return new RailPartsRenderer(args);
		}
		else if(name.equals("SignalPartsRenderer"))
		{
			return new SignalPartsRenderer(args);
		}
		else if(name.equals("VehiclePartsRenderer"))
		{
			return new VehiclePartsRenderer(args);
		}
		else if(name.equals("WirePartsRenderer"))
		{
			return new WirePartsRenderer(args);
		}
		else if(name.equals("MechanismPartsRenderer"))
		{
			return new MechanismPartsRenderer(args);
		}
		else
		{
			throw new ModelPackException("PartsRenderer not found.", name);
		}
	}
}
