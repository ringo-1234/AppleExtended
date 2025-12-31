package jp.ngt.rtm.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InternalGUI
{
	private float startX, startY, width, height;
	private int color;
	private List<InternalButton> buttons = new ArrayList<>();
	private boolean mouseClicked;

	public InternalGUI(float startX, float startY, float width, float height)
	{
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	public InternalGUI setColor(int par1)
	{
		this.color = par1;
		return this;
	}

	public InternalButton addButton(InternalButton button)
	{
		this.buttons.add(button);
		return button;
	}

	public void render()
	{
		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawing(GL11.GL_QUADS);
		tessellator.setColorRGBA_I(this.color, 0xB0);
		NGTRenderHelper.addQuadGuiFaceWithSize(this.startX, this.startY, this.width, this.height, 0.0F);
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);

		for(int i = 0; i < 2; ++i)
		{
			boolean pickMode = (i == 0);

			if(pickMode)
			{
				if(!Mouse.isButtonDown(1))
				{
					this.mouseClicked = false;
				}
				GLHelper.startMousePicking(2.0F);
			}

			int id = 1;
			for(InternalButton button : this.buttons)
			{
				if(pickMode)
				{
					GL11.glLoadName(id++);
					button.hovered = false;
				}
				button.render(pickMode);
			}

			if(pickMode)
			{
				int hits = GLHelper.finishMousePicking();
				if(hits > 0)
				{
					int pickedId = GLHelper.getPickedObjId(0);
					InternalButton button = this.buttons.get(pickedId - 1);
					button.hovered = true;
					if(!this.mouseClicked && Mouse.isButtonDown(1))
					{
						this.clickButton(button);
						this.mouseClicked = true;
					}
				}
			}
		}

		GL11.glPopMatrix();
	}

	private void clickButton(InternalButton button)
	{
		button.getListner().onClick(button);
		NGTUtilClient.getMinecraft().player.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1.0F, 1.0F);
	}
}
