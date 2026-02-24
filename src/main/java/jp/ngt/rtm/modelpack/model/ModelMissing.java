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

package jp.ngt.rtm.modelpack.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public final class ModelMissing extends ModelBase
{
	ModelRenderer shape1;

	public ModelMissing()
	{
	    this.textureWidth = 64;
	    this.textureHeight = 32;

	    this.shape1 = new ModelRenderer(this, 0, 0);
	    this.shape1.addBox(-8F, -8F, -8F, 16, 16, 16);
	    this.shape1.setRotationPoint(0F, 0F, 0F);
	    this.shape1.setTextureSize(64, 32);
	    this.shape1.mirror = true;
	    this.setRotation(this.shape1, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
	    super.render((Entity)null, f, f1, f2, f3, f4, f5);
	    this.setRotationAngles(f, f1, f2, f3, f4, f5);
	    this.shape1.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
	    super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
	}
}