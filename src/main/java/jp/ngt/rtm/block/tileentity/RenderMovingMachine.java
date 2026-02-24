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

package jp.ngt.rtm.block.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMovingMachine extends TileEntitySpecialRenderer<TileEntityMovingMachine>
{
	private void renderMovingMachine(TileEntityMovingMachine tileEntity, double x, double y, double z, float p5)
	{
		if((!tileEntity.isCore && tileEntity.hasPair())){return;}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);

		GLHelper.disableLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		if(tileEntity.guideVisibility)
		{
			NGTTessellator tessellator = NGTTessellator.instance;
			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.setColorRGBA_I(0xFF0000, 0xFF);
			tessellator.addVertex(0.0F, 0.0F, 0.0F);
			tessellator.addVertex(tileEntity.pairBlockX, tileEntity.pairBlockY, tileEntity.pairBlockZ);
			tessellator.draw();
		}

		double dx = tileEntity.prevPosX + (tileEntity.posX - tileEntity.prevPosX) * (double)p5;
		double dy = tileEntity.prevPosY + (tileEntity.posY - tileEntity.prevPosY) * (double)p5;
		double dz = tileEntity.prevPosZ + (tileEntity.posZ - tileEntity.prevPosZ) * (double)p5;
		GL11.glTranslatef((float)(dx + 0.5D), (float)(dy + 0.5D), (float)(dz + 0.5D));

		if(tileEntity.guideVisibility)
		{
			NGTRenderer.renderFrame(tileEntity.offsetX, tileEntity.offsetY, tileEntity.offsetZ, tileEntity.width, tileEntity.height, tileEntity.depth, 0x00FF0F, 0xFF);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GLHelper.enableLighting();

		if(this.setupBrightness(tileEntity))
		{

		}
		this.renderBlocks(tileEntity, p5);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	/**現在位置にブロックが存在しないならtrue*/
	private boolean setupBrightness(TileEntityMovingMachine tileEntity)
	{
		int x = NGTMath.floor((double)tileEntity.getPos().getX() + tileEntity.posX);
		int y = NGTMath.floor((double)tileEntity.getPos().getY() + tileEntity.posY);
        int z = NGTMath.floor((double)tileEntity.getPos().getZ() + tileEntity.posZ);
        if(BlockUtil.isAir(this.getWorld(), x, y, z))
        {
        	int i = tileEntity.getWorld().getLightFromNeighbors(new BlockPos(x, y, z));
    		GLHelper.setBrightness(i);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return true;
        }
		return false;
	}

	private void renderBlocks(TileEntityMovingMachine tile, float p2)
	{
		if(tile.dummyWorld == null || tile.blocksObject == null){return;}

		if(tile.glLists == null)
		{
			tile.glLists = new GLObject[2];
		}

		int pass = MinecraftForgeClient.getRenderPass();
		if(pass == -1)
		{
			pass = 0;
		}
		NGTWorld world = (NGTWorld)tile.dummyWorld;
		NGTObjectRenderer.INSTANCE.renderTileEntities(world, p2, pass);

		GLHelper.disableLighting();
		//RenderHelper.disableStandardItemLighting();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if(!GLHelper.isValid(tile.glLists[pass]))
		{
			tile.glLists[pass] = GLHelper.generateGLList(tile.glLists[pass]);
			GLHelper.startCompile(tile.glLists[pass]);
			NGTObjectRenderer.INSTANCE.renderNGTObject(world, tile.blocksObject, false, 0, pass);
	        GLHelper.endCompile();
		}
		else
		{
			GLHelper.callList(tile.glLists[pass]);
		}
		GLHelper.enableLighting();
		//RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void render(TileEntityMovingMachine tileEntity, double p2, double p3, double p4, float p5, int p6, float alpha)
	{
		this.renderMovingMachine(tileEntity, p2, p3, p4, p5);
	}
}