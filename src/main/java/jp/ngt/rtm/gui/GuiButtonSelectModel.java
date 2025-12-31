package jp.ngt.rtm.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Project;

import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonSelectModel extends GuiButton
{
	private static final int TEX_W = 160;
	private static final int TEX_H = 32;

	private final ModelSetBase modelSet;
	private final GuiSelectModel parentGui;
	public boolean notCheckPos;
	/**画面開く前に、このモデルが選択されていたか*/
	public boolean isSelected = false;

	public GuiButtonSelectModel(int par1, int par2, int par3, ModelSetBase par4, String name, GuiSelectModel par5)
	{
		super(par1, par2, par3, TEX_W, TEX_H, name);
		this.modelSet = par4;
		this.parentGui = par5;
	}

	public GuiButtonSelectModel(int par1, int par2, int par3, ModelSetBase par4, String name)
	{
		this(par1, par2, par3, par4, name, null);
		this.notCheckPos = true;
	}

	@Override
	public void drawButton(Minecraft mc, int posX, int posY, float ptick)
    {
        if(this.visible && (this.notCheckPos || (this.y >= -20 && this.y < this.parentGui.height + 20)))
        {
        	GL11.glPushMatrix();

    		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    		this.renderButtonNormal(mc);

            boolean inRange = posX >= this.x && posY >= this.y && posX < this.x + this.width && posY < this.y + this.height;
            int hoverState = this.getHoverState(inRange);

            if(hoverState == 2)
            {
            	this.renderButtonOverlay(mc);

                GL11.glTranslatef(this.x + 240, this.y + 16, 0.0F);//z効果なし
                this.preRenderModelInGui(mc);

                float[] box;
                if(this.modelSet.modelObj != null)
                {
                	box = this.modelSet.modelObj.model.getSize();
                }
                else
				{
					box = new float[]{-0.5F, 0.0F, -0.5F, 0.5F, 2.0F, 0.5F};//NPC用
				}
                GL11.glTranslatef(5.0F, 1.0F, -18.0F);//X:右が+, Z:手前が+
                float scale = 10.0F * this.getScale(box);
                GL11.glScalef(scale, scale, scale);
                float moveY = (box[4] - box[1]) * 0.5F;
                GL11.glTranslatef(0.0F, -moveY, 0.0F);
                float rotation = (float)(System.currentTimeMillis() % 12000) * 360.0F / 12000.0F;//12s/r
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                this.modelSet.renderModelInGui(mc);

                this.postRenderModelInGui(mc);

                /*this.drawString(mc.fontRenderer,
                		String.format("%6.4f / %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f", scale, box[0], box[1], box[2], box[3], box[4], box[5]),
                		0, 0, 0x000000);*/
            }
            else if(this.isSelected)
            {
            	this.renderButtonOverlay(mc);
            }

            GL11.glPopMatrix();
        }
    }

	private final float getScale(float[] box)
	{
		float x = box[3] - box[0];
		float y = box[4] - box[1];
		float z = box[5] - box[2];
		float maxSize = x > y ? (x > z ? x : z) : (y > z ? y : z);
		return 1.0F / maxSize;
	}

	public void renderButtonNormal(Minecraft mc)
	{
		mc.getTextureManager().bindTexture(this.modelSet.buttonTexture);
		this.drawTexturedModalRect(this.x, this.y, 0, 0, TEX_W, TEX_H);
	}

	protected void renderButtonOverlay(Minecraft mc)
	{
		mc.getTextureManager().bindTexture(GuiSelectModel.ButtonBlue);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA);
    	this.drawTexturedModalRect(this.x, this.y, 0, 32, TEX_W, TEX_H);
        GL11.glDisable(GL11.GL_BLEND);
	}

	private final void preRenderModelInGui(Minecraft mc)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        //ScaledResolution sr = new ScaledResolution(par2, par2.displayWidth, par2.displayHeight);
        //GL11.glViewport(0, 0, sr.getScaledWidth(), sr.getScaledHeight());//いらない
        Project.gluPerspective(80.0F, 1.0F, 5.0F, 500.0F);//視野角,アスペクト比,奥行きのmin, max
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        //GLHelper.enableLighting();
        RenderHelper.enableStandardItemLighting();//GLHelperの方は暗くなる
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	private final void postRenderModelInGui(Minecraft mc)
	{
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        //GLHelper.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        //GLHelper.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}