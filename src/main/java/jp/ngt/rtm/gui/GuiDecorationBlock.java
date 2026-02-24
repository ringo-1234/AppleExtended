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

package jp.ngt.rtm.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Project;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import jp.ngt.ngtlib.gui.GuiTextFieldCustom;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.network.PacketNBT;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.NGTRenderHelper;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.block.decoration.DecorationModel;
import jp.ngt.rtm.block.decoration.DecorationStore;
import jp.ngt.rtm.block.decoration.Element;
import jp.ngt.rtm.block.decoration.Face;
import jp.ngt.rtm.block.decoration.Face.FaceType;
import jp.ngt.rtm.block.tileentity.RenderDecoration;
import jp.ngt.rtm.gui.GuiButtonIcon.IconElement;
import jp.ngt.rtm.item.ItemDecoration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDecorationBlock extends GuiScreenCustom
{
	private static final int W_ELEMENT = 60;
	private static final int W_FACE = 60;
	private static final int W_VERTEX = 60;
	private static final int W_UV = 40;
	private static final int H_FIELD = 20;
	private static final int THICKNESS = 3;
	private static final int Y_START = 35;

	private RenderDecoration renderer;
	private EntityPlayer player;
	private GuiButton buttonDone;

	private DecorationModel model;
	private GuiTextFieldCustom modelNameField;

	private final float[] moveVec = new float[3];
	private GuiTextFieldCustom moveVecField;
	private boolean lockUV = true;

	private List<GuiTextFieldCustom> elementList = new ArrayList<>();
	private GuiTextFieldCustom selectedElemntField;
	private Element selectedElemnt;

	private List<GuiTextFieldCustom> faceList = new ArrayList<>();
	private GuiTextFieldCustom selectedFaceField;
	private Face selectedFace;

	private GuiTextFieldCustom faceTextureField;
	private GuiTextFieldCustom faceShadowField;
	private int faceTypeIndex;
	private List<GuiTextFieldCustom> vertexList = new ArrayList<>();
	private List<GuiTextFieldCustom> uvList = new ArrayList<>();

	private float[] selectedVertex;

	private GuiTextFieldCustom selectedObjectField;
	private Object selectedObject;

	private float rotationYaw;
	private float rotationPitch;

	public GuiDecorationBlock(EntityPlayer player)
	{
		super();
		this.player = player;
		this.renderer = new RenderDecoration();

		this.setModel(DecorationStore.INSTANCE.getModel(ItemDecoration.getModelName(player.getHeldItemMainhand())));
	}

	public void setModel(DecorationModel par1)
	{
		this.model = par1;
		this.selectedElemnt = this.model.elements[0];
		this.selectedFace = this.selectedElemnt.faces[0];
	}

	@Override
	public void initGui()
    {
		super.initGui();

		int hw = this.width / 2;
		this.buttonList.clear();
		this.selectedObjectField = null;

		this.buttonDone = new GuiButton(0, hw - 155, this.height - 28, 150, 20, I18n.format("gui.done"));
		//this.buttonDone.enabled = false;
		this.buttonList.add(this.buttonDone);
        this.buttonList.add(new GuiButton(1, hw + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));

        int startX = this.width - (10 + W_ELEMENT + W_FACE + W_VERTEX + W_UV + THICKNESS * 5);
        int buttonY = Y_START - 20 - THICKNESS * 2;
        int hInc = H_FIELD + THICKNESS * 2;

        this.buttonList.add(new GuiButton(200, 30,      Y_START - 22, 50, 20, "load model"));
        this.buttonList.add(new GuiButton(201, 30 + 50, Y_START - 22, 50, 20, "save model"));
        this.modelNameField = this.setTextField(30, Y_START, 100, 20, this.model.name);

        this.buttonList.add(new GuiButton(210, 30, this.height - 82, 30, 20, "move"));
        this.buttonList.add(new GuiButton(211, 60, this.height - 82, 70, 20, "lock UV : " + this.lockUV));
        this.moveVecField = this.setTextField(30, this.height - 60, 100, 20, this.vtxToStr(this.moveVec, 0, 3));

        //Element列

        this.buttonList.add(new GuiButton(100, startX,      buttonY, 20, 20, "+"));
        this.buttonList.add(new GuiButton(101, startX + 20, buttonY, 20, 20, "C"));
        this.buttonList.add(new GuiButton(102, startX + 40, buttonY, 20, 20, "-"));

        this.elementList.clear();
        for(int i = 0; i < this.model.elements.length; ++i)
        {
        	Element element = this.model.elements[i];
        	GuiTextFieldCustom field = this.setTextField(startX, Y_START + i * hInc, W_ELEMENT, H_FIELD, element.name);
        	this.elementList.add(field);
			field.setListener((f)->{this.setElement(element, field);});
        	if(element == this.selectedElemnt)
        	{
        		this.selectedElemntField = field;
        	}

        	if(element == this.selectedObject)
        	{
        		this.selectedObjectField = field;
        	}
        }

        //Face列

        startX += W_ELEMENT + THICKNESS * 2;
        this.buttonList.add(new GuiButton(110, startX,      buttonY, 20, 20, "+"));
        this.buttonList.add(new GuiButton(111, startX + 20, buttonY, 20, 20, "C"));
        this.buttonList.add(new GuiButton(112, startX + 40, buttonY, 20, 20, "-"));

		this.faceList.clear();
        for(int i = 0; i < this.selectedElemnt.faces.length; ++i)
        {
        	Face face = this.selectedElemnt.faces[i];
        	GuiTextFieldCustom field = this.setTextField(startX, Y_START + i * hInc, W_FACE, H_FIELD, face.name);
        	this.faceList.add(field);
			field.setListener((f)->{this.setFace(face, field);});
        	if(face == this.selectedFace)
        	{
        		this.selectedFaceField = field;
        	}

        	if(face == this.selectedObject)
        	{
        		this.selectedObjectField = field;
        	}
        }

        //Vertex列

        startX += W_FACE + THICKNESS * 2;
        this.buttonList.add(new GuiButtonIcon(120, startX, Y_START - 12, 32, 32, new IconElementTexture(this.selectedFace.texture)));
        this.buttonList.add(new GuiButton(121, startX + 35, Y_START, 40, 20, this.selectedFace.type.toString()));
        this.faceTypeIndex = this.selectedFace.type.ordinal();
        this.faceShadowField = this.setTextField(startX, Y_START + hInc, 60, H_FIELD, String.valueOf(this.selectedFace.shadow));
        this.faceShadowField.setListener((f)->{this.saveFieldToModel();});

        this.vertexList.clear();
        for(int i = 0; i < this.selectedFace.vertex.length; ++i)
        {
        	float[] vtx = this.selectedFace.vertex[i];
        	String s = this.vtxToStr(vtx, 0, 3);
        	GuiTextFieldCustom field = this.setTextField(startX, Y_START + (2 + i) * hInc, W_VERTEX, H_FIELD, s);
        	this.vertexList.add(field);
        	field.setListener((f)->{this.setVertex(vtx, field);});
        	if(vtx == this.selectedObject)
        	{
        		this.selectedObjectField = field;
        	}
        }

        startX += W_VERTEX + THICKNESS * 2;
        this.uvList.clear();
        for(int i = 0; i < this.selectedFace.vertex.length; ++i)
        {
        	String s = this.vtxToStr(this.selectedFace.vertex[i], 3, 2);
        	GuiTextFieldCustom field = this.setTextField(startX, Y_START + (2 + i) * hInc, W_UV, H_FIELD, s);
			field.setListener((f)->{this.selectedObject = null;this.saveFieldToModel();});
        	this.uvList.add(field);
        }
    }

	protected void setElement(Element par1, GuiTextFieldCustom field)
	{
		if(par1 != this.selectedElemnt || par1 != this.selectedObject)
		{
			this.saveFieldToModel();
			this.selectedElemnt = par1;
			//this.selectedElemntField = field;//ここでセットすると初期時ぬるぽ
			this.selectedObject = par1;

			this.selectedFace = this.selectedElemnt.faces[0];
			this.selectedVertex = this.selectedFace.vertex[0];

			this.initGui();
		}
	}

	protected void setFace(Face par1, GuiTextFieldCustom field)
	{
		if(par1 != this.selectedFace || par1 != this.selectedObject)
		{
			this.saveFieldToModel();
			this.selectedFace = par1;
			this.selectedObject = par1;

			this.selectedVertex = this.selectedFace.vertex[0];

			this.initGui();
		}
	}

	protected void setVertex(float[] par1, GuiTextFieldCustom field)
	{
		if(par1 != this.selectedVertex || par1 != this.selectedObject)
		{
			this.saveFieldToModel();
			this.selectedVertex = par1;
			this.selectedObject = par1;
			this.initGui();
		}
	}

	protected void saveFieldToModel()
	{
		this.selectedFace.shadow = this.getFloat(this.faceShadowField.getText(), this.selectedFace.shadow);

		for(int i = 0; i < this.selectedFace.vertex.length; ++i)
		{
			this.strToVtx(this.vertexList.get(i).getText(), this.selectedFace.vertex[i], 0);
		}

		for(int i = 0; i < this.selectedFace.vertex.length; ++i)
		{
			this.strToVtx(this.uvList.get(i).getText(), this.selectedFace.vertex[i], 3);
		}

		for(int i = 0; i < this.selectedElemnt.faces.length; ++i)
		{
			this.selectedElemnt.faces[i].name = this.faceList.get(i).getText();
		}

		for(int i = 0; i < this.model.elements.length; ++i)
		{
			this.model.elements[i].name = this.elementList.get(i).getText();
		}

		this.model.name = this.modelNameField.getText();
	}

	private String vtxToStr(float[] vtx, int start, int len)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = start; i < start + len; ++i)
		{
			sb.append(String.valueOf(vtx[i]));
			if(i < start + len - 1)
			{
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	private void strToVtx(String s, float[] fa, int start)
	{
		String[] sa = s.split(",");
		for(int i = 0; i < sa.length; ++i)
		{
			fa[i + start] = this.getFloat(sa[i], fa[i + start]);
		}
	}

	private float getFloat(String s, float def)
	{
		try
		{
			float f = Float.valueOf(s);
			return f;
		}
		catch(NumberFormatException e)
		{
			return def;
		}
	}

	private void editElement(int par1)
	{
		this.saveFieldToModel();

		if(par1 == 0 || par1 == 1)
		{
			List<Element> list = new ArrayList<>();
			NGTUtil.addArray(list, this.model.elements);
			Element newElement;
			if(par1 == 0)
			{
				newElement = Element.getDefaultElement();
			}
			else
			{
				newElement = this.selectedElemnt.clone();
			}
			newElement.name = "element" + list.size() + 1;
			list.add(newElement);
			this.model.elements = list.toArray(new Element[list.size()]);
		}
		else//2
		{
			if(this.model.elements.length >= 2)
			{
				List<Element> list = new ArrayList<>();
				NGTUtil.addArray(list, this.model.elements);
				list.remove(this.selectedElemnt);
				this.model.elements = list.toArray(new Element[list.size()]);
			}
		}

		this.initGui();
	}

	private void editFace(int par1)
	{
		this.saveFieldToModel();

		if(par1 == 0 || par1 == 1)
		{
			List<Face> list = new ArrayList<>();
			NGTUtil.addArray(list, this.selectedElemnt.faces);
			Face newFace;
			if(par1 == 0)
			{
				newFace = Face.getDefaultFace();
			}
			else
			{
				newFace = this.selectedFace.clone();
			}
			newFace.name = "face" + list.size() + 1;
			list.add(newFace);
			this.selectedElemnt.faces = list.toArray(new Face[list.size()]);
		}
		else//2
		{
			if(this.selectedElemnt.faces.length >= 2)
			{
				List<Face> list = new ArrayList<>();
				NGTUtil.addArray(list, this.selectedElemnt.faces);
				list.remove(this.selectedFace);
				this.selectedElemnt.faces = list.toArray(new Face[list.size()]);
			}
		}

		this.initGui();
	}

	public void setFaceTexture(String name)
	{
		this.selectedFace.texture = name;
	}

	private void openLoadModelGUI()
	{
		List<DecorationModel> list1 = DecorationStore.INSTANCE.getModels();
    	List<IconElement> list2 = new ArrayList<>();
    	for(DecorationModel res : list1)
    	{
    		list2.add(new IconElementModel(res));
    	}
    	this.mc.displayGuiScreen(new GuiSelectIcon(this, list2, 64));
	}

	private void openIconSelectGUI()
	{
		List<ResourceLocation> list1 = DecorationStore.INSTANCE.getAllIcon();
    	List<IconElement> list2 = new ArrayList<>();
    	for(ResourceLocation res : list1)
    	{
    		list2.add(new IconElementTexture(res.toString()));
    	}
    	this.mc.displayGuiScreen(new GuiSelectIcon(this, list2, 32));
	}

	private void saveModel()
	{
		this.saveFieldToModel();
		DecorationStore.INSTANCE.registerModelOnClient(this.model);

		ItemStack stack = this.player.getHeldItemMainhand();
		ItemDecoration.setModel(stack, this.model.name);
		PacketNBT.sendToServer(this.player, stack);
	}

	@Override
	protected void actionPerformed(GuiButton button)
    {
		if(button.id == 0)//done
        {
			this.saveModel();
            this.mc.displayGuiScreen(null);
        }
		else if(button.id == 1)//cancel
        {
            this.mc.displayGuiScreen(null);
        }
		else if(button.id >= 100 && button.id <= 102)
        {
			this.editElement(button.id - 100);
        }
		else if(button.id >= 110 && button.id <= 112)
        {
			this.editFace(button.id - 110);
        }
        else if(button.id == 120)
        {
        	this.openIconSelectGUI();
        }
        else if(button.id == 121)
        {
        	this.faceTypeIndex = (++this.faceTypeIndex) % FaceType.values().length;
        	this.selectedFace.type = FaceType.values()[this.faceTypeIndex];
        	button.displayString = this.selectedFace.type.toString();
        }
        else if(button.id == 200)
        {
        	this.openLoadModelGUI();
        }
        else if(button.id == 201)
        {
        	this.saveModel();
        }
        else if(button.id == 210)
        {
        	this.strToVtx(this.moveVecField.getText(), this.moveVec, 0);
        	this.moveObject();
        	this.initGui();
        }
        else if(button.id == 211)
        {
        	this.lockUV ^= true;
        	button.displayString = "lock UV : "  + this.lockUV;
        }
    }

	private void moveObject()
	{
		if(this.selectedObject instanceof Element)
		{
			((Element)this.selectedObject).addVec(this.moveVec, this.lockUV);
		}
		else if(this.selectedObject instanceof Face)
		{
			((Face)this.selectedObject).addVec(this.moveVec, this.lockUV);
		}
		else if(this.selectedObject instanceof float[])
		{
			Face.addVecToVertex((float[])this.selectedObject, this.selectedFace.type, this.moveVec, this.lockUV);
		}
	}

	@Override
	protected void onTextFieldClicked(GuiTextField field)
	{
		this.saveFieldToModel();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		this.drawDefaultBackground();

        //this.drawString(this.fontRendererObj, this.modelFile.getAbsolutePath(), 140, 45, 0xFFFFFF);

        int color1 = 0x707070;
        int color2 = 0xA0A0A0;
        int color3 = 0xD0D0D0;
        int t2 = THICKNESS * 2;
        int hInc = H_FIELD + THICKNESS * 2;
        int startX = this.width - (10 + W_ELEMENT + W_FACE + W_VERTEX + W_UV + THICKNESS * 5);
        int y2 = 20 + t2;
        int startY = Y_START - THICKNESS - y2;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawingQuads();

        //Element列
		startX -= THICKNESS;
        tessellator.setColorOpaque_I(color1);
        NGTRenderHelper.addQuadGuiFaceWithSize(startX, startY, W_ELEMENT + t2, this.elementList.size() * hInc + y2, this.zLevel);
        tessellator.setColorOpaque_I(color2);
        NGTRenderHelper.addQuadGuiFaceWithSize(startX, this.selectedElemntField.y - THICKNESS, W_ELEMENT + t2, hInc, this.zLevel);
        //Face列
        startX += W_ELEMENT + t2;
        //tessellator.setColorOpaque_I(color2);
        NGTRenderHelper.addQuadGuiFaceWithSize(startX, startY, W_FACE + t2, this.faceList.size() * hInc + y2, this.zLevel);
        tessellator.setColorOpaque_I(color3);
        NGTRenderHelper.addQuadGuiFaceWithSize(startX, this.selectedFaceField.y - THICKNESS, W_ELEMENT + t2, hInc, this.zLevel);
        //Vertex列
        startX += W_FACE + t2;
        //tessellator.setColorOpaque_I(color3);
        NGTRenderHelper.addQuadGuiFaceWithSize(startX, startY, W_VERTEX + t2 + W_UV + t2, (this.vertexList.size() + 2) * hInc + y2, this.zLevel);

		tessellator.draw();

		//選択箇所の枠
		if(this.selectedObjectField != null)
		{
			tessellator.startDrawing(GL11.GL_LINES);
			tessellator.setColorOpaque_I(0xFF0000);
			int sx = this.selectedObjectField.x - THICKNESS;
			int sy = this.selectedObjectField.y - THICKNESS;
			int w = this.selectedObjectField.width + t2;
			int h = this.selectedObjectField.height + t2;
			NGTRenderHelper.addQuadGuiFrameWithSize(sx, sy, w, h, this.zLevel);
			tessellator.draw();
		}
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        super.drawScreen(mouseX, mouseY, partialTicks);

		//モデルプレビュー
        float speed = 1.0F;
        if(Mouse.isButtonDown(1))//2:ホイール
        {
        	//ポインタが画面中央時を角度0とする
            this.rotationYaw = (this.width / 2 - mouseX) * speed;
            this.rotationPitch = (this.height / 2 - mouseY) * speed;
        }

        this.preRenderModel();
        GL11.glPushMatrix();
        GL11.glTranslatef(-7.5F, 0.0F, -8.0F);//X:右が+, Z:手前が+
        GL11.glRotatef(this.rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(this.rotationYaw, 0.0F, 1.0F, 0.0F);
        float scale = 2.0F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        this.renderer.renderModel(this.model, false);
        GL11.glPopMatrix();
        this.postRenderModel();
    }

	public void preRenderModel()
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        //ScaledResolution sr = new ScaledResolution(par2, par2.displayWidth, par2.displayHeight);
        //GL11.glViewport(0, 0, sr.getScaledWidth(), sr.getScaledHeight());//いらない
        Project.gluPerspective(80.0F, ((float)this.width / (float)this.height), 5.0F, 1000.0F);//視野角,アスペクト比,奥行きのmin, max
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GLHelper.enableLighting();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	public void postRenderModel()
	{
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);////
        GLHelper.disableLighting();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GLHelper.disableLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	public class IconElementTexture implements IconElement
	{
		public final String iconName;
		private final TextureAtlasSprite icon;

		public IconElementTexture(String par1)
		{
			this.iconName = par1;
			this.icon = NGTUtilClient.getIcon(par1);
		}

		@Override
		public void draw(GuiButtonIcon button, Minecraft mc, int par2, int par3)
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			NGTUtilClient.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			NGTTessellator tessellator = NGTTessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(button.x             , button.y              , button.getZLevel(), icon.getMinU(), icon.getMinV());
			tessellator.addVertexWithUV(button.x             , button.y + button.height, button.getZLevel(), icon.getMinU(), icon.getMaxV());
			tessellator.addVertexWithUV(button.x + button.width, button.y + button.height, button.getZLevel(), icon.getMaxU(), icon.getMaxV());
			tessellator.addVertexWithUV(button.x + button.width, button.y              , button.getZLevel(), icon.getMaxU(), icon.getMinV());
			tessellator.draw();

	    	/*int k = this.getHoverState(this.field_146123_n);
	    	if(k == 2)
	        {
	            GL11.glColor4f(0.5F, 0.5F, 1.0F, 0.5F);
	        	GL11.glEnable(GL11.GL_BLEND);
	        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA);
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
	            GL11.glDisable(GL11.GL_BLEND);
	        }*/
		}

		@Override
		public void onClick()
		{
			GuiDecorationBlock.this.setFaceTexture(this.iconName);
		}
	}

	public class IconElementModel implements IconElement
	{
		private static final float ROTATION_PER_SEC = 90.0F;

		private final DecorationModel model;
		private float rotation;
		private long prevTime;

		private float roX = 20.0F;
		private float roY = 45.0F;
		private Vec3 roVec = new Vec3(0.0D, 1.0D, 0.0D);//モデル回転用ベクトル(Y軸回転)

		public IconElementModel(DecorationModel par1)
		{
			this.model = par1;

			float sinX = NGTMath.sin(-roX);
			this.roVec = new Vec3(sinX * NGTMath.sin(-roY), NGTMath.cos(-roX), sinX * NGTMath.cos(-roY));
		}

		@Override
		public void draw(GuiButtonIcon button, Minecraft mc, int par2, int par3)
		{
			long time = System.currentTimeMillis();
			float roInc = ROTATION_PER_SEC * (time - this.prevTime) * 0.001F;//2.5
			this.prevTime = time;
			this.rotation = (this.rotation + roInc) % 360.0F;

			float scale = 32.0F;

			//GuiDecorationBlock.this.preRenderModel();
	        GL11.glPushMatrix();
	        GL11.glTranslatef(button.x, button.y, button.getZLevel());
	        GL11.glScalef(1.0F, -1.0F, -1.0F);
	        GL11.glTranslatef(scale, -scale, -32.0F);
	        GL11.glRotatef(roX, 1.0F, 0.0F, 0.0F);
	        GL11.glRotatef(roY, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(this.rotation, (float)this.roVec.getX(), (float)this.roVec.getY(), (float)this.roVec.getZ());
	        GL11.glScalef(scale, scale, scale);
	        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	        GuiDecorationBlock.this.renderer.renderModel(this.model, false);
	        GL11.glPopMatrix();
	        //GuiDecorationBlock.this.postRenderModel();
		}

		@Override
		public void onClick()
		{
			DecorationModel newModel = this.model.clone();
			GuiDecorationBlock.this.setModel(newModel);
		}
	}
}