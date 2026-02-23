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

package jp.ngt.ngtlib.renderer;

import java.lang.reflect.Constructor;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**EnriryRendererの登録に使用*/
@SideOnly(Side.CLIENT)
public class BasicRenderFactory<T extends Entity> implements IRenderFactory
{
	private final Class<Render<? super T>> rendererClass;

	public BasicRenderFactory(Class<Render<? super T>> clazz)
	{
		this.rendererClass = clazz;
	}

	@Override
	public Render<? super T> createRenderFor(RenderManager manager)
	{
		try
		{
			Constructor<Render<? super T>> constructor = this.rendererClass.getConstructor(RenderManager.class);
			Render<? super T> renderer = constructor.newInstance(manager);
			return renderer;
		}
		catch (ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}