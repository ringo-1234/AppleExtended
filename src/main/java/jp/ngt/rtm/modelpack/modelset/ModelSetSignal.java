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

package jp.ngt.rtm.modelpack.modelset;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.SignalConfig;
import jp.ngt.rtm.render.BasicSignalPartsRenderer;
import jp.ngt.rtm.render.ModelObject;
import jp.ngt.rtm.render.PartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelSetSignal extends ModelSetBase<SignalConfig>
{
	private static Pattern SIGNAL_PATTERN = Pattern.compile("S\\((.+?)\\)");
	private static Pattern INTERVAL_PATTERN = Pattern.compile("I\\((.+?)\\)");
	private static Pattern PARTS_PATTERN = Pattern.compile("P\\((.+?)\\)");

	public ModelSetSignal()
	{
		super();
	}

	public ModelSetSignal(SignalConfig par1)
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
			this.buttonTexture = ModelPackManager.INSTANCE.getResource("textures/signal/button_4cB.png");
		}
		else
		{
			SignalConfig cfg = this.getConfig();
			PartsRenderer renderer = (!PartsRenderer.validPath(cfg.model.rendererPath)) ? new BasicSignalPartsRenderer(cfg) : null;
			this.modelObj = new ModelObject(cfg.model, this, renderer);
			this.buttonTexture = ModelPackManager.INSTANCE.getResource(cfg.buttonTexture);
		}
	}

	@Override
	public SignalConfig getDummyConfig()
	{
		return SignalConfig.getDummyConfig();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderModelInGui(Minecraft par1)
	{
		SignalConfig cfg = this.getConfig();
		this.modelObj.render(null, cfg, 0, 0.0F);
		this.modelObj.render(null, cfg, 1, 0.0F);
	}

	public static LightParts[] parseLightParts(String[] par1)
	{
		List<LightParts> list = new LinkedList<LightParts>();
		for(int i = 0; i < par1.length; ++i)
		{
			String s0 = getMatchedString(par1[i], SIGNAL_PATTERN);
			int i0 = Integer.parseInt(s0);
			String s1 = getMatchedString(par1[i], INTERVAL_PATTERN);
			int i1 = Integer.parseInt(s1);
			String s2 = getMatchedString(par1[i], PARTS_PATTERN);
			String[] sa = s2.split(" ");
			list.add(new LightParts(i0, i1, sa));
		}
		Collections.sort(list);
		return list.toArray(new LightParts[list.size()]);
	}

	private static String getMatchedString(String par1, Pattern par2)
	{
		Matcher matcher = par2.matcher(par1);
		if(matcher.find())
		{
			return matcher.group(1);
		}
		return "";
	}

	public static class LightParts implements Comparable<LightParts>
	{
		public final int signalLevel;
		public final int interval;
		public final String[] parts;

		public LightParts(int par1, int par2, String[] par3)
		{
			this.signalLevel = par1;
			this.interval = par2;
			this.parts = par3;
		}

		@Override
		public int compareTo(LightParts obj)
		{
			return this.signalLevel - obj.signalLevel;
		}
	}
}