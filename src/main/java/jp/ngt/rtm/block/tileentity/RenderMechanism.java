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

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.cfg.MechanismConfig;
import jp.ngt.rtm.modelpack.cfg.WireConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetMechanism;
import jp.ngt.rtm.modelpack.modelset.ModelSetWire;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMechanism extends TileEntitySpecialRenderer<TileEntityMechanism>
{
	private void renderMechanism(TileEntityMechanism tileEntity, double par2, double par4, double par6, float partialTick)
	{
		this.updateRotation(tileEntity);

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.5F, (float)par6 + 0.5F);

		int pass = MinecraftForgeClient.getRenderPass();
		ModelSetMechanism modelSet = tileEntity.getResourceState().getResourceSet();
		MechanismConfig cfg = modelSet.getConfig();

		if(cfg.type == MechanismType.PULLEY && tileEntity.getPulleySource() != null && pass == 0)
		{
			this.renderWire(tileEntity, partialTick);//発光は一旦未対応
		}

		if(NGTUtilClient.getMinecraft().gameSettings.showDebugInfo)
        {
			float prevLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			GL11.glLineWidth(5.0F);

			//this.renderAxis(tileEntity);//debug

			if(tileEntity.getType() == MechanismType.PULLEY)
			{
				this.renderWirePos(tileEntity);//debug
			}

			GL11.glLineWidth(prevLineWidth);
        }

		this.rotateBySide(tileEntity.side);
		GL11.glRotatef(NGTMath.wrapAngle(tileEntity.dir * 90.0F), 0.0F, 1.0F, 0.0F);

		GL11.glTranslatef(0.0F, -0.5F, 0.0F);
		modelSet.modelObj.render(tileEntity, cfg, pass, partialTick);

		GL11.glPopMatrix();
	}

	private void updateRotation(TileEntityMechanism tileEntity)
	{
		long time = System.currentTimeMillis();
		int timeDif = (tileEntity.prevTime <= 0) ? 0 : (int)(time - tileEntity.prevTime);
		tileEntity.prevTime = time;
		for(int i = 0; i < tileEntity.rotations.length; ++i)
		{
			float speed = tileEntity.getOutputRaw(i);
			double d0 = 360.0D * speed * ((double)timeDif / (1000.0D * 60.0D));
			float prevRotation = tileEntity.rotations[i];
			tileEntity.rotations[i] = NGTMath.wrapAngle(prevRotation + (float)d0);
		}

		MechanismConfig cfg = tileEntity.getResourceState().getResourceSet().getConfig();
		if(cfg.type == MechanismType.PULLEY)
		{
			double len = tileEntity.wirePosDst.sub(tileEntity.wirePosSrc).length();
			if(len > 0.0D)
			{
				//A rpm * 2PiR / 60 = B m/s
				float speedRPM = tileEntity.getOutputRaw(1);
				double speedMPS = speedRPM * cfg.radius * Math.PI / 30.0D;
				double move = tileEntity.wireMove - (speedMPS * timeDif / 1000.0D);//反転
				if(move < 0.0D)
				{
					move += len;//負値はとらないように
				}
				tileEntity.wireMove = (float)(move % len);
			}
		}
	}

	private void rotateBySide(EnumFacing side)
	{
		switch(side)
		{
		case DOWN:
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			break;
		case UP:
			break;
		case NORTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			break;
		case SOUTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			break;
		case WEST:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			break;
		case EAST:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			break;
		}
	}

	private void renderWire(TileEntityMechanism tileEntity, float partialTick)
	{
		ModelSetWire modelSet = tileEntity.getWireState().getResourceSet();
		if(modelSet.isDummy()){return;}

		Vec3 dstVec = Vec3.ZERO;
		Vec3 srcVec = Vec3.ZERO;
		if(tileEntity.isConnectedToPlayer())
		{
			Entity player = NGTUtilClient.getMinecraft().player;
			if(player.getEntityId() == tileEntity.getPulleySource().getX())
			{
				double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
				double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
				double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
				double difX = (px - (tileEntity.getX() + 0.5D));
				double difY = (py + 1.0D - (tileEntity.getY() + 0.5D));
				double difZ = (pz - (tileEntity.getZ() + 0.5D));
				Vec3 vp = new Vec3(difX, difY, difZ);
				Vec3 vn = tileEntity.getPulleyNormal();
				Vec3 v0 = new Vec3(difX - (difX * vn.getX()), difY - (difY * vn.getY()), difZ - (difZ * vn.getZ()));
				float r = tileEntity.getResourceState().getResourceSet().getConfig().radius;
				float angle = (float)NGTMath.toDegrees(Math.acos(r / v0.length()));
				float invert = tileEntity.invertWirePos ? 1.0F : -1.0F;
				Vec3 v1 = v0.normalize().multi(r).rotateAroundVec(vn, angle * invert);
				dstVec = v1;
				srcVec = vp;
			}
		}
		else
		{
			dstVec = tileEntity.wirePosDst;
			srcVec = tileEntity.wirePosSrc;
		}

		this.renderWire(tileEntity, modelSet, dstVec, srcVec, tileEntity.wireMove, 0);
	}

	private void renderWire(TileEntityMechanism tileEntity, ModelSetWire modelSet, Vec3 dstVec, Vec3 srcVec, float wireMove, int pass)
	{
		NGTUtilClient.bindTexture(modelSet.modelObj.textures[0].material.texture);
		WireConfig cfg = modelSet.getConfig();

		GL11.glPushMatrix();
		Vec3 v0 = dstVec.sub(srcVec);
		GL11.glTranslatef((float)srcVec.getX(), (float)srcVec.getY(), (float)srcVec.getZ());
		GL11.glRotatef(v0.getYaw() + 180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(v0.getPitch() - 90.0F, 1.0F, 0.0F, 0.0F);

		double length = v0.length();
		float secLen = cfg.sectionLength;
		double move2 = (tileEntity.wireMove % secLen);
		if(move2 > 0.0D)
		{
			length += secLen;
			GL11.glTranslatef(0.0F, (float)move2 - secLen, 0.0F);
		}
		int split = NGTMath.floor(length / secLen);
		float scaleY = (float)((length / (double)split) / secLen);
		GL11.glScalef(1.0F, scaleY, 1.0F);
		for(int i = 0; i < split; ++i)
		{
			modelSet.modelObj.model.renderAll(cfg.smoothing);
			GL11.glTranslatef(0.0F, secLen, 0.0F);
		}

		GL11.glPopMatrix();

		if(!tileEntity.wirePosDstOpposite.equals(Vec3.ZERO))
		{
			GL11.glPushMatrix();
			this.rotateBySide(tileEntity.side);

			float r = tileEntity.getResourceState().getResourceSet().getConfig().radius;
			float angle = tileEntity.wirePosDst.getAngle360(tileEntity.wirePosDstOpposite, tileEntity.getPulleyNormal()) + 180.0F;
			double circumference = 2.0D * Math.PI * r;
			double arcLen = circumference * (angle / 360.0D);
			move2 = secLen - move2;
			float firstPosAngle = tileEntity.wirePosAngle;
			if(move2 > 0.0D)
			{
				arcLen += secLen;
				float ro = (float)(360.0D * ((move2 - secLen) / circumference));//直線移動→回転
				firstPosAngle += ro;
			}
			int split2 = NGTMath.floor(arcLen / secLen);
			float secAngle = (float)(360.0D * (secLen / circumference));//直線移動→回転
			GL11.glRotatef(firstPosAngle + (secAngle * 0.5F), 0.0F, 1.0F, 0.0F);
			for(int i = 0; i < split2; ++i)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(r, 0.0F, 0.0F);
				float move3 = -(secLen * 0.5F);
				GL11.glTranslatef(0.0F, 0.0F, move3);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				modelSet.modelObj.model.renderAll(cfg.smoothing);
				GL11.glPopMatrix();
				GL11.glRotatef(secAngle, 0.0F, 1.0F, 0.0F);
			}
			GL11.glPopMatrix();
		}
	}

	private void renderAxis(TileEntityMechanism tileEntity)
	{
		GL11.glPushMatrix();
		EnumFacing[] ia = tileEntity.indexConverter[0];
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawing(GL11.GL_LINES);
		this.renderAxisParts(tessellator, ia[5], 0xFF0000);//px
		this.renderAxisParts(tessellator, ia[1], 0x00FF00);//py
		this.renderAxisParts(tessellator, ia[3], 0x0000FF);//pz
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderAxisParts(NGTTessellator tessellator, EnumFacing face, int color)
	{
		float scale = 2.0F;
		tessellator.setColorRGBA_I(color, 0xFF);
		tessellator.addVertex(0.0F, 0.0F, 0.0F);
		tessellator.addVertex(
				face.getDirectionVec().getX() * scale, face.getDirectionVec().getY() * scale, face.getDirectionVec().getZ() * scale);
	}

	private void renderWirePos(TileEntityMechanism tileEntity)
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		NGTTessellator tessellator = NGTTessellator.instance;
		tessellator.startDrawing(GL11.GL_LINES);
		this.renderWirePosVec(tessellator, tileEntity.wirePosSrc, 0xFF0000);
		this.renderWirePosVec(tessellator, tileEntity.wirePosDst, 0x00FF00);
		this.renderWirePosVec(tessellator, tileEntity.wirePosDstOpposite, 0x0000FF);
		tessellator.draw();

		this.rotateBySide(tileEntity.side);
		GL11.glRotatef(tileEntity.wirePosAngle, 0.0F, 1.0F, 0.0F);
		tessellator.startDrawing(GL11.GL_LINES);
		tessellator.setColorRGBA_I(0xFF00FF, 0xFF);
		tessellator.addVertex(0.0F, 0.0F, 0.0F);
		tessellator.addVertex(0.0F, 0.0F, 3.0F);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderWirePosVec(NGTTessellator tessellator, Vec3 vec, int color)
	{
		tessellator.setColorRGBA_I(color, 0xFF);
		tessellator.addVertex(0.0F, 0.0F, 0.0F);
		tessellator.addVertex((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
	}

	@Override
	public void render(TileEntityMechanism tileEntity, double par2, double par4, double par6, float par8, int par9, float alpha)
    {
        this.renderMechanism(tileEntity, par2, par4, par6, par8);
    }
}
