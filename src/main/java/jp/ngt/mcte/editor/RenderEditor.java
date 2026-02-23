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

package jp.ngt.mcte.editor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.renderer.NGTRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.world.NGTWorld;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEditor extends Render<EntityEditor>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("mcte", "textures/atc.png");
	/**{startX, startY, startZ, endX, endY, endZ}*/
	private final int[] hitBoxBuf = new int[6];
	/**{minX, minY, minZ, maxX, maxY, maxZ}*/
	private final int[] selectBoxBuf = new int[6];

	public RenderEditor(RenderManager renderManager)
	{
		super(renderManager);
	}

	public void renderEditor(EntityEditor editor, double par2, double par4, double par6, float par8, float par9)
    {
    	GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);

        this.initSelectBox(editor);
        this.renderText(editor);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        int startX = this.hitBoxBuf[0];
        int startY = this.hitBoxBuf[1];
        int startZ = this.hitBoxBuf[2];
        int endX = this.hitBoxBuf[3];
        int endY = this.hitBoxBuf[4];
        int endZ = this.hitBoxBuf[5];
        int minX = this.selectBoxBuf[0];
        int minY = this.selectBoxBuf[1];
        int minZ = this.selectBoxBuf[2];
        int maxX = this.selectBoxBuf[3];
        int maxY = this.selectBoxBuf[4];
        int maxZ = this.selectBoxBuf[5];

        double pX = editor.prevPosX + (editor.posX - editor.prevPosX) * par9;
        double pY = editor.prevPosY + (editor.posY - editor.prevPosY) * par9;
        double pZ = editor.prevPosZ + (editor.posZ - editor.prevPosZ) * par9;
        GL11.glTranslatef((float)((double)minX - pX), (float)((double)minY - pY), (float)((double)minZ - pZ));

        float difX = maxX - minX;
        float difY = maxY - minY;
        float difZ = maxZ - minZ;
        float minPos = -0.05F;
        float f1 = 0.06F;
        float size1 = 1.12F;

        this.renderBox(minPos, minPos, minPos, 1.1F + difX, 1.1F + difY, 1.1F + difZ, 0x204020, 32);
        NGTRenderer.renderFrame(minPos, minPos, minPos, 1.1F + difX, 1.1F + difY, 1.1F + difZ, 0x000000, 255);

        //始点
        this.renderBox((float)(startX - minX) - f1, (float)(startY - minY) - f1, (float)(startZ - minZ) - f1,
        		size1, size1, size1, 0xff0000, 128);
        NGTRenderer.renderFrame((float)(startX - minX) - f1, (float)(startY - minY) - f1, (float)(startZ - minZ) - f1,
        		size1, size1, size1, 0xff0000, 255);

        //終点
        this.renderBox((float)(endX - minX) - f1, (float)(endY - minY) - f1, (float)(endZ - minZ) - f1,
        		size1, size1, size1, 0x0000ff, 128);
        NGTRenderer.renderFrame((float)(endX - minX) - f1, (float)(endY - minY) - f1, (float)(endZ - minZ) - f1,
        		size1, size1, size1, 0x0000ff, 255);

        byte editMode = editor.getEditMode();
        boolean flag1 = editor.isSelectEnd() && (editMode == Editor.EditMode_VisibleBox_0 || editMode == Editor.EditMode_VisibleBox_1);
        RayTraceResult target = null;
        if(flag1)
        {
        	target = editor.getTarget(true);
        	if(target != null && target.getBlockPos() != null)
        	{
        		int tgX = target.getBlockPos().getX();
            	int tgY = target.getBlockPos().getY();
            	int tgZ = target.getBlockPos().getZ();

            	//ブロック描画
            	if(editor.blocksForRenderer != null && target != null)
                {
            		GL11.glEnable(GL11.GL_TEXTURE_2D);
                	GL11.glPushMatrix();
        			GL11.glTranslatef((float)(tgX - minX), (float)(tgY - minY), (float)(tgZ - minZ));
        			this.renderBlocks(editor, tgX, tgY, tgZ);
        			GL11.glPopMatrix();
        			GL11.glDisable(GL11.GL_TEXTURE_2D);
                }

            	//コピーボックス描画
            	int[] box = editor.getPos(EntityEditor.PASTE_BOX);
        		if(target != null && box[0] * box[1] * box[2] > 0)
            	{
        			GL11.glPushMatrix();
        			GL11.glTranslatef((float)(tgX - minX), (float)(tgY - minY), (float)(tgZ - minZ));
                	this.renderBox(minPos, minPos, minPos, 0.1F + (float)box[0], 0.1F + (float)box[1], 0.1F + (float)box[2], 0x808020, 64);
                	NGTRenderer.renderFrame(minPos, minPos, minPos, 0.1F + (float)box[0], 0.1F + (float)box[1], 0.1F + (float)box[2], 0x000000, 255);
                	GL11.glPopMatrix();
            	}
        	}
        }

        if(editor.hasCloneBox())
        {
        	GL11.glPushMatrix();
        	int[] box = editor.getCloneBox();
        	for(int i = 0; i < box[3]; ++i)
        	{
        		GL11.glTranslatef((float)box[0], (float)box[1], (float)box[2]);
        		this.renderBox(minPos, minPos, minPos, 1.1F + difX, 1.1F + difY, 1.1F + difZ, 0x303000, 64);
        		NGTRenderer.renderFrame(minPos, minPos, minPos, 1.1F + difX, 1.1F + difY, 1.1F + difZ, 0x000000, 255);
        	}
        	GL11.glPopMatrix();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

	private void initSelectBox(EntityEditor editor)
	{
		//選択範囲のクリア
		for(int i = 0; i < 6; ++i)
		{
			this.hitBoxBuf[i] = 0;
			this.selectBoxBuf[i] = 0;
		}

		int[] start = editor.getPos(EntityEditor.START_POS);
        int[] end = editor.getPos(EntityEditor.END_POS);

        if(!editor.isSelectEnd())
    	{
        	RayTraceResult target = editor.getTarget(false);
    		if(target != null)
        	{
    			end[0] = target.getBlockPos().getX();
            	end[1] = target.getBlockPos().getY();
            	end[2] = target.getBlockPos().getZ();
        	}
    		else//視線先にブロックがない場合
    		{
    			end[0] = start[0];
            	end[1] = start[1];
            	end[2] = start[2];
    		}
    	}

        for(int i = 0; i < 3; ++i)
		{
        	this.hitBoxBuf[i] = start[i];
        	this.hitBoxBuf[i + 3] = end[i];

        	if(start[i] < end[i])
        	{
        		this.selectBoxBuf[i] = start[i];
        		this.selectBoxBuf[i + 3] = end[i];
        	}
        	else
        	{
        		this.selectBoxBuf[i] = end[i];
        		this.selectBoxBuf[i + 3] = start[i];
        	}
		}
	}

	private void renderText(EntityEditor editor)
	{
		GLHelper.disableLighting();
		GL11.glPushMatrix();
		GL11.glRotatef(-NGTUtilClient.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(NGTUtilClient.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(4.0F, 4.0F, 3.0F);
		float scale = 0.03125F;
		GL11.glScalef(-scale, -scale, scale);
		FontRenderer fontRenderer = NGTUtilClient.getMinecraft().getRenderManager().getFontRenderer();
		fontRenderer.drawString(
				String.format("Start : %d, %d, %d", this.hitBoxBuf[0], this.hitBoxBuf[1], this.hitBoxBuf[2]),
				0, 0, 0xFF0000);
		fontRenderer.drawString(
				String.format("End : %d, %d, %d", this.hitBoxBuf[3], this.hitBoxBuf[4], this.hitBoxBuf[5]),
				0, 12, 0x0000FF);
		String size = String.format("Size : %d, %d, %d",
				Math.abs(this.hitBoxBuf[3] - this.hitBoxBuf[0]) + 1,
				Math.abs(this.hitBoxBuf[4] - this.hitBoxBuf[1]) + 1,
				Math.abs(this.hitBoxBuf[5] - this.hitBoxBuf[2]) + 1);
		fontRenderer.drawString(size, 0, 24, 0x00AA00);
		GL11.glPopMatrix();
		GLHelper.enableLighting();
	}

	private void renderBlocks(EntityEditor editor, int x, int y, int z)
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if(editor.shouldUpdate() || !GLHelper.isValid(editor.displayList))
		{
			if(!GLHelper.isValid(editor.displayList))
			{
				editor.displayList = GLHelper.generateGLList(editor.displayList);
			}

			GLHelper.startCompile(editor.displayList);
			NGTObjectRenderer.INSTANCE.renderNGTObject((NGTWorld)editor.dummyWorld, editor.blocksForRenderer, true, 0, 0);
	        GLHelper.endCompile();

	        editor.setUpdate(false);
		}
		else
		{
			GLHelper.callList(editor.displayList);
		}

		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	@Override
	public void doRender(EntityEditor entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderEditor(entity, par2, par4, par6, par8, par9);
    }

	private final void renderBox(float minX, float minY, float minZ, float width, float height, float depth, int color, int alpha)
	{
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);//加算
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);//アルファ
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GLHelper.setLightmapMaxBrightness();

        float maxX = minX + width;
        float maxY = minY + height;
        float maxZ = minZ + depth;

		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(color, alpha);

		tessellator.addVertex(maxX, minY, minZ);//maxX
		tessellator.addVertex(maxX, maxY, minZ);
		tessellator.addVertex(maxX, maxY, maxZ);
		tessellator.addVertex(maxX, minY, maxZ);

		tessellator.addVertex(minX, minY, maxZ);//minX
		tessellator.addVertex(minX, maxY, maxZ);
		tessellator.addVertex(minX, maxY, minZ);
		tessellator.addVertex(minX, minY, minZ);

		tessellator.addVertex(maxX, maxY, maxZ);//maxY
		tessellator.addVertex(maxX, maxY, minZ);
		tessellator.addVertex(minX, maxY, minZ);
		tessellator.addVertex(minX, maxY, maxZ);

		tessellator.addVertex(maxX, minY, minZ);//minY
		tessellator.addVertex(maxX, minY, maxZ);
		tessellator.addVertex(minX, minY, maxZ);
		tessellator.addVertex(minX, minY, minZ);

		tessellator.addVertex(maxX, minY, maxZ);//maxZ
		tessellator.addVertex(maxX, maxY, maxZ);
		tessellator.addVertex(minX, maxY, maxZ);
		tessellator.addVertex(minX, minY, maxZ);

		tessellator.addVertex(minX, minY, minZ);//minZ
		tessellator.addVertex(minX, maxY, minZ);
		tessellator.addVertex(maxX, maxY, minZ);
		tessellator.addVertex(maxX, minY, minZ);

		tessellator.draw();

		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEditor entity)
	{
		return TEXTURE;
	}
}