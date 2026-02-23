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

import jp.ngt.ngtlib.io.ResourceLocationCustom;
import jp.ngt.rtm.modelpack.cfg.TextureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TextureSetBase<T extends TextureConfig> extends ResourceSet<T>
{
	@SideOnly(Side.CLIENT)
	public ResourceLocation texture;

	public TextureSetBase()
	{
		super();
	}

	public TextureSetBase(T par1)
	{
		super(par1);
	}

	@Override
	public T getConfig()
	{
		return this.cfg;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		super.constructOnClient();

		this.texture = new ResourceLocationCustom("minecraft", this.getConfig().texture);
	}
}