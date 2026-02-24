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
		ScriptEngine scriptengine = com.anatawa12.fixRtm.scripting.FIXScriptUtil.getScriptAndDoScript(par1.toString());
		String s1 = (String)ScriptUtil.getScriptField(scriptengine, "renderClass");
		R r = (R)getRenderer(s1, args);
		r.setScript(scriptengine, par1);
		scriptengine.put("renderer", r);
		return r;
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