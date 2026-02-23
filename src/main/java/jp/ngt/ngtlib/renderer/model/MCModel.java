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

package jp.ngt.ngtlib.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.io.FileType;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**Minecraftデフォルトのモデル形式を扱う*/
@SideOnly(Side.CLIENT)
public abstract class MCModel extends ModelBase implements IModelNGT
{
	private final float[] sizeBox = new float[6];

	@Override
	public void renderAll(boolean smoothing)
	{
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		//GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		this.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	protected final void setSizeBox(float nx, float ny, float nz, float px, float py, float pz)
	{
		this.sizeBox[0] = nx;
		this.sizeBox[1] = ny;
		this.sizeBox[2] = nz;
		this.sizeBox[3] = px;
		this.sizeBox[4] = py;
		this.sizeBox[5] = pz;
	}

	@Override
    public float[] getSize()
    {
    	return this.sizeBox;
    }

	@Override
	public void renderOnly(boolean smoothing, String... groupNames)
	{
		this.renderAll(smoothing);
	}

	@Override
	public void renderPart(boolean smoothing, String partName)
	{
		this.renderAll(smoothing);
	}

	@Override
	public int getDrawMode()
	{
		return 0;
	}

	@Override
	public ArrayList<GroupObject> getGroupObjects()
	{
		return new ArrayList<GroupObject>();
	}

	@Override
	public Map<String, Material> getMaterials()
	{
		return new HashMap<String, Material>();
	}

	@Override
	public FileType getType()
	{
		return FileType.CLASS;
	}
}