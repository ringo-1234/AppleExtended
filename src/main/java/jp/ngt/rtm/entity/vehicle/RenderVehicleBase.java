package jp.ngt.rtm.entity.vehicle;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector3f;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.GLObject;
import jp.ngt.ngtlib.renderer.NGTObjectRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.world.NGTWorld;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.TrainState;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.modelpack.cfg.TrainConfig;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig.Light;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig.Rollsign;
import jp.ngt.rtm.modelpack.modelset.ModelSetVehicleBase;
import jp.ngt.rtm.render.PartsRenderer;
import jp.ngt.rtm.render.RenderPass;
import jp.ngt.rtm.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderVehicleBase extends Render<EntityVehicleBase>
{
	protected static final int DAYLIGHT_LIMIT = 7;

	protected Vector3f lightVecF = new Vector3f();

	public RenderVehicleBase(RenderManager renderManager)
	{
		super(renderManager);
	}

	protected void renderVehicleBase(EntityVehicleBase vehicle, double par2, double par4, double par6, float par8, float par9)
    {
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //partialTick補完するとガクガク(1.9~)
        double x = par2;
        double y = par4;
        double z = par6;
        GL11.glTranslatef((float)x, (float)y + vehicle.getVehicleYOffset(), (float)z);
        //NGTLog.debug("(R) %d x%5.1f z%5.1f", vehicle.getEntityId(), vehicle.lastTickPosX, vehicle.lastTickPosZ);

        float yaw =  vehicle.prevRotationYaw + NGTMath.wrapAngle(vehicle.rotationYaw - vehicle.prevRotationYaw) * par9;
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        float pitch =  vehicle.prevRotationPitch + (vehicle.rotationPitch - vehicle.prevRotationPitch) * par9;
        GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
        float roll = vehicle.prevRotationRoll + (vehicle.rotationRoll - vehicle.prevRotationRoll) * par9;
        GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);

        if(Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
        	this.debugCollision(vehicle);
        }

        VehicleNGTO vngto = vehicle.getNGTO();
    	if(vngto != null)
    	{
    		this.renderVehicleNGTO((EntityVehicle)vehicle, vngto, par9);
    	}
    	else
        {
        	ModelSetVehicleBase modelSet = (ModelSetVehicleBase)vehicle.getResourceState().getResourceSet();
            if(modelSet != null)
            {
                float[] fa = modelSet.getConfig().offset;
            	GL11.glTranslated(fa[0], fa[1], fa[2]);
                this.renderVehicleMain(vehicle, modelSet, par9);
            }
            else
            {
            	RTMCore.proxy.renderMissingModel();
            }
        }

        GL11.glPopMatrix();
    }

	/**車体&方向幕&ライトの描画*/
    protected void renderVehicleMain(EntityVehicleBase vehicle, ModelSetVehicleBase modelSet, float par4)
    {
    	int pass = MinecraftForgeClient.getRenderPass();
    	boolean smoothing = modelSet.getConfig().smoothing;
    	boolean culling = modelSet.getConfig().doCulling;

    	if(smoothing){GL11.glShadeModel(GL11.GL_SMOOTH);}
    	if(!culling){GL11.glDisable(GL11.GL_CULL_FACE);}

    	if(pass == 0)
    	{
    		this.renderBodyNormal(vehicle, modelSet, par4);
    	}
    	else if(pass == 1)//EntityRenderer.1412->pass=1
    	{
    		if(modelSet.modelObj.light)
    		{
    			this.renderBodyLight(vehicle, modelSet, par4);
    		}

    		if(modelSet.rollsignTexture != null)
            {
            	this.renderRollsign(vehicle, modelSet);//方向幕が窓で隠れないように
            }

    		if(modelSet.modelObj.alphaBlend)
    		{
    			this.renderBodyTransparent(vehicle, modelSet, par4);
    		}
    	}

    	if(!culling){GL11.glEnable(GL11.GL_CULL_FACE);}
    	if(smoothing){GL11.glShadeModel(GL11.GL_FLAT);}

    	if(pass == 1)
    	{
    		if(!NGTUtilClient.usingShader())
    		{
    			GL11.glDisable(GL11.GL_CULL_FACE);
            	this.renderLightEffect(vehicle, modelSet);
            	GL11.glEnable(GL11.GL_CULL_FACE);
    		}
    	}
    }

    //車内灯前処理
    private void preRenderBody(ModelSetVehicleBase modelSet, byte lightState, boolean useInteriorLighting, boolean customLighting)
    {
		Light[] lights = modelSet.getConfig().interiorLights;
		if(lightState > 0 && useInteriorLighting)
		{
			GLHelper.setLightmapMaxBrightness();
			float r = (lightState == TrainState.InteriorLight_On.data) ? 1.0F : -1.0F;
			for(int k = 0; k < lights.length; ++k)
			{
				float[] pos = lights[k].pos;
				RenderUtil.enableCustomLighting(k, pos[0], pos[1], pos[2], r, 1.0F, 1.0F);
			}
		}
		else if(customLighting)
		{
			GLHelper.setLightmapMaxBrightness();
			RenderUtil.enableCustomLighting(3, 128.0F, 256.0F, 128.0F, 1.0F, 1.0F, 1.0F);
		}
    }

    //車内灯後処理
    private void postRenderBody(ModelSetVehicleBase modelSet, boolean useInteriorLighting, boolean customLighting)
    {
    	Light[] lights = modelSet.getConfig().interiorLights;
		if(useInteriorLighting)
		{
			for(int k = 0; k < lights.length; ++k)
			{
				RenderUtil.disableCustomLighting(k);
			}
			GLHelper.enableLighting();
		}
		else if(customLighting)
		{
			RenderUtil.disableCustomLighting(3);
			GLHelper.enableLighting();
		}
    }

    private void renderBody(EntityVehicleBase vehicle, ModelSetVehicleBase modelSet, RenderPass pass, float par4)
    {
    	//遊び(ランダムカラー)
    	//float hue = (float)(((int)((double)((System.currentTimeMillis() / 50) % 24000) * 15.0D)) % 360) / 360.0F;
		//float[] buf = RenderUtil.convertHueToRGB(hue);
		//GL11.glColor4f(buf[0], buf[1], buf[2], 1.0F);
    	modelSet.modelObj.preRender();
    	modelSet.modelObj.renderWithTexture(vehicle, pass, par4);
    	modelSet.modelObj.postRender();
    }

    /**半透明、ライト以外の描画*/
    private void renderBodyNormal(EntityVehicleBase vehicle, ModelSetVehicleBase modelSet, float par4)
    {
    	//半透明ピクセルは描画しない
		GL11.glAlphaFunc(GL11.GL_EQUAL, 1.0F);//a==1.0

		int value = this.getLightValue(vehicle);
		//boolean flag = !NGTUtilClient.usingShader() && lights != null && vehicle.shouldUseInteriorLight();
		byte state = vehicle.getVehicleState(TrainStateType.InteriorLight);
		boolean useInteriorLighting = modelSet.getConfig().interiorLights != null && value < DAYLIGHT_LIMIT;
		boolean customLighting = modelSet.modelObj.useShader;

		this.preRenderBody(modelSet, state, useInteriorLighting, customLighting);

		boolean canUseColor = (modelSet.getConfig().useCustomColor);
		if(canUseColor)
    	{
			GLHelper.setColor(vehicle.getResourceState().color, 0xFF);
    	}

		this.renderBody(vehicle, modelSet, RenderPass.NORMAL, par4);

		if(canUseColor)
    	{
			GLHelper.setColor(0xFFFFFF, 0xFF);
    	}

		this.postRenderBody(modelSet, useInteriorLighting, customLighting);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    }

    /**ライトの描画*/
    private void renderBodyLight(EntityVehicleBase vehicle, ModelSetVehicleBase modelSet, float par4)
    {
    	boolean isTrain = vehicle instanceof EntityTrainBase;
    	int dir = 0;
    	byte mode = 0;
    	boolean isFrontEmpty = false;
    	boolean isBackEmpty = false;

    	if(isTrain)
    	{
    		EntityTrainBase train = (EntityTrainBase)vehicle;
    		dir = train.getTrainDirection();
        	mode = train.getVehicleState(TrainStateType.Light);
        	isFrontEmpty = train.getConnectedTrain(dir) == null;//front空き
        	isBackEmpty = train.getConnectedTrain(1 - dir) == null;//back空き
    	}

    	for(int i = 0; i < 3; ++i)//0:消灯,1:前照灯,2:尾灯
    	{
    		boolean doRender = false;
    		if(isTrain && ((TrainConfig)modelSet.getConfig()).isSingleTrain && isFrontEmpty && isBackEmpty)//単行
    		{
    			switch(i)
    			{
    			case 0: doRender = (mode == 0 || mode == 1);break;
    			case 1: doRender = (mode == 1 && dir == 0) || mode == 2;break;
    			case 2: doRender = (mode == 1 && dir == 1) || mode == 2;break;
    			}
    		}
    		else
    		{
    			switch(i)
    			{
    			case 0: doRender = (mode == 0 || mode == 1);break;
    			case 1: doRender = (mode == 1 && isFrontEmpty) || mode == 2;break;
    			case 2: doRender = (mode == 1 && !isFrontEmpty && isBackEmpty) || mode == 2;break;
    			}
    		}

    		if(doRender)
    		{
    			boolean isLightON = (i > 0);

    			if(isLightON)
    	    	{
    				GLHelper.disableLighting();
    	    		GL11.glEnable(GL11.GL_BLEND);
    	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
    	            GLHelper.setLightmapMaxBrightness();
    	    	}

    			RenderPass pass;
    			switch(i)
    			{
    			case 1:
    				pass = RenderPass.LIGHT_FRONT;
    				break;
    			case 2:
    				pass = RenderPass.LIGHT_BACK;
    				break;
    			default:
    				pass = RenderPass.LIGHT;
    			}
				this.renderBody(vehicle, modelSet, pass, par4);

				if(isLightON)
    	    	{
    	            GL11.glDisable(GL11.GL_BLEND);
    	            GL11.glEnable(GL11.GL_ALPHA_TEST);
    	            GLHelper.enableLighting();
    	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	    	}
    		}
    	}

		if(modelSet.modelObj.renderer.shouldRenderOutline(vehicle))
		{
			GLHelper.disableLighting();
			GLHelper.setLightmapMaxBrightness();
			modelSet.modelObj.renderWithTexture(vehicle, RenderPass.OUTLINE, par4);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GLHelper.enableLighting();
		}
    }

    /**半透明部分の描画*/
    private void renderBodyTransparent(EntityVehicleBase vehicle, ModelSetVehicleBase modelSet, float par4)
    {
    	GL11.glAlphaFunc(GL11.GL_LESS, 1.0F);//a<1.0
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.renderBody(vehicle, modelSet, RenderPass.TRANSPARENT, par4);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);//a>0.1
    }

    /**方向幕の描画（2系統＋交互表示対応）*/
    protected void renderRollsign(EntityVehicleBase vehicle, ModelSetVehicleBase modelset)
    {
        GL11.glPushMatrix();

        // 2系統目が存在するか判定
        boolean useSecond = false;
        if (modelset.rollsignTexture2 != null &&
                modelset.getConfig().rollsigns2 != null &&
                modelset.getConfig().rollsigns2.length > 0)
        {
            int cycle = modelset.getConfig().rollsignAlternateCycle;
            if (cycle > 0)
            {
                long time = vehicle.world.getTotalWorldTime();
                useSecond = (time / (cycle / 50)) % 2 == 1;  // 50tick = 1秒相当
            }
        }

        ResourceLocation texture = useSecond ? modelset.rollsignTexture2 : modelset.rollsignTexture;
        Rollsign[] signs = useSecond ? modelset.getConfig().rollsigns2 : modelset.getConfig().rollsigns;
        String[] names = useSecond ? modelset.getConfig().rollsignNames2 : modelset.getConfig().rollsignNames;

        if (texture == null || signs == null || signs.length == 0 || names == null || names.length == 0)
        {
            GL11.glPopMatrix();
            return;
        }

        this.bindTexture(texture);

        for (int i = 0; i < signs.length; ++i)
        {
            Rollsign rollsign = signs[i];

            if (!rollsign.disableLighting)
            {
                GLHelper.disableLighting();
                GLHelper.setLightmapMaxBrightness();
            }

            float f0 = (rollsign.uv[3] - rollsign.uv[2]) / names.length;
            float uMin = rollsign.uv[0];
            float uMax = rollsign.uv[1];

            float f1 = 0.0F;
            if (vehicle instanceof EntityTrainBase)
            {
                EntityTrainBase train = (EntityTrainBase)vehicle;
                f1 = rollsign.doAnimation ? train.getRollsignAnimation() : train.getVehicleState(TrainStateType.Destination);
            }

            float vMin = rollsign.uv[2] + f0 * f1;
            float vMax = rollsign.uv[2] + f0 * (f1 + 1.0F);

            NGTTessellator tessellator = NGTTessellator.instance;
            tessellator.startDrawingQuads();
            for (int j = 0; j < rollsign.pos.length; ++j)
            {
                tessellator.addVertexWithUV(rollsign.pos[j][3][0], rollsign.pos[j][3][1], rollsign.pos[j][3][2], uMin, vMin);
                tessellator.addVertexWithUV(rollsign.pos[j][2][0], rollsign.pos[j][2][1], rollsign.pos[j][2][2], uMin, vMax);
                tessellator.addVertexWithUV(rollsign.pos[j][1][0], rollsign.pos[j][1][1], rollsign.pos[j][1][2], uMax, vMax);
                tessellator.addVertexWithUV(rollsign.pos[j][0][0], rollsign.pos[j][0][1], rollsign.pos[j][0][2], uMax, vMin);
            }
            tessellator.draw();

            if (!rollsign.disableLighting)
            {
                GLHelper.enableLighting();
            }
        }

        GL11.glPopMatrix();
    }

    private int getLightValue(EntityVehicleBase vehicle)
    {
    	World world = NGTUtil.getClientWorld();
    	int x = NGTMath.floor(vehicle.posX);
    	int y = NGTMath.floor(vehicle.posY + 0.5D);
    	int z = NGTMath.floor(vehicle.posZ);
    	//int light = MathHelper.floor_float(world.getLightBrightness(new BlockPos(x, y, z)) * 15.0F);
    	//int light = NGTUtil.getLightValue(world, x, y, z);
    	//return light;
    	BlockPos pos = new BlockPos(x, y, z);
    	int skyLight1 = NGTMath.floor(world.getSunBrightness(1.0F) * 15.0F);//Worldの日光レベル(時間から算出)
    	int skyLight2 = world.getLightFor(EnumSkyBlock.SKY, pos);//日光の減衰度(空に面していると15)
    	int skyLight = skyLight1 * skyLight2 / 15;//現在の日光レベルを隠蔽度合いで補正
    	int blockLight = world.getLightFor(EnumSkyBlock.BLOCK, pos);
    	return skyLight > blockLight ? skyLight : blockLight;
    }

    /**前照灯のボリュームライト効果*/
    private void renderLightEffect(EntityVehicleBase vehicle, ModelSetVehicleBase modelset)
    {
    	boolean isTrain = vehicle instanceof EntityTrainBase;
    	int dir = 0;
    	byte mode = vehicle.getVehicleState(TrainStateType.Light);
    	boolean b0 = false;
    	boolean b1 = false;

    	if(isTrain)
    	{
    		EntityTrainBase train = (EntityTrainBase)vehicle;
    		dir = train.getTrainDirection();
        	b0 = train.getConnectedTrain(dir) == null;//front空き
        	b1 = train.getConnectedTrain(1 - dir) == null;//back空き
    	}
    	else
    	{
    		b0 = b1 = true;
    	}

    	if(modelset.getConfig().headLights == null || modelset.getConfig().tailLights == null || mode <= 0){return;}

    	GL11.glPushMatrix();
    	GLHelper.disableLighting();
    	GLHelper.setLightmapMaxBrightness();
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glShadeModel(GL11.GL_SMOOTH);
    	GL11.glDisable(GL11.GL_ALPHA_TEST);

    	int value = this.getLightValue(vehicle);

    	GL11.glEnable(GL11.GL_BLEND);
    	//if(value > DAYLIGHT_LIMIT)
    	{
    		//GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE);//合成:スクリーン
    		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    	}
    	/*else
    	{
    		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);//明るさの段階的変更が不可能
    	}*/
        GL11.glDepthMask(false);

        int renderModeHead = -1;
        int renderModeTail = -1;
        boolean b2 = isTrain && ((TrainConfig)modelset.getConfig()).isSingleTrain && b0 && b1;//単行
        if(b2)
		{
        	renderModeHead = (mode == 1) ? dir : (mode == 2 ? 2 : -1);
        	renderModeTail = (mode == 1) ? 1 - dir : (mode == 2 ? 2 : -1);
		}
		else
		{
			renderModeHead = ((mode == 1 && b0) || mode == 2) ? 0 : -1;
        	renderModeTail = ((mode == 1 && !b0 && b1) || mode == 2) ? 0 : -1;
		}

        Vec3 vec = PooledVec3.create(0.0D, 0.0D, 1.0D);
        vec = vec.rotateAroundY(vehicle.rotationYaw);
        vec = vec.rotateAroundZ(vehicle.rotationPitch);
        this.lightVecF.set((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());

        if(renderModeHead >= 0)//前照灯
        {
        	for(Light light : modelset.getConfig().headLights)
            {
        		if(light.type == 1 && value > DAYLIGHT_LIMIT){continue;}
        		this.renderLightEffect(vehicle, this.lightVecF, light, renderModeHead);
            }
        }

        if(renderModeTail >= 0)//尾灯
        {
        	for(Light light : modelset.getConfig().tailLights)
            {
        		if(light.type == 1 && value > DAYLIGHT_LIMIT){continue;}
        		this.renderLightEffect(vehicle, this.lightVecF, light, renderModeTail);
            }
        }

    	GL11.glDepthMask(true);
    	GL11.glDisable(GL11.GL_BLEND);
    	GL11.glShadeModel(GL11.GL_FLAT);
    	GL11.glEnable(GL11.GL_ALPHA_TEST);
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GLHelper.enableLighting();
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    /**
     * @param normal 車両の向き
     * @param light
     * @param mode 車体の 0:前, 1:後, 2:両側
     */
    private void renderLightEffect(EntityVehicleBase vehicle, Vector3f normal, Light light, int mode)
    {
    	if(mode == 2)
    	{
    		this.renderLightEffect(vehicle, normal, light, 0);
    		this.renderLightEffect(vehicle, normal, light, 1);
    		return;
    	}

    	Vec3 vec = PooledVec3.create(light.pos[0], light.pos[1], light.pos[2]);
    	vec = vec.rotateAroundY(vehicle.rotationYaw);
    	vec = vec.rotateAroundZ(vehicle.rotationPitch);
    	vec = vec.add(vehicle.posX, vehicle.posY, vehicle.posZ);

    	GL11.glPushMatrix();
    	GL11.glTranslatef(light.pos[0], light.pos[1], mode == 0 ? light.pos[2] : -light.pos[2]);
    	if(mode == 1)
    	{
    		GL11.glScalef(1.0F, 1.0F, -1.0F);
    	}

    	PartsRenderer.renderLightEffectS(normal, vec.getX(), vec.getY(), vec.getZ(),
    			light.r, 0.0625F, 16.0F, light.color, light.type, (mode == 1));

    	if(mode == 1)
    	{
    		GL11.glScalef(1.0F, 1.0F, -1.0F);
    	}

    	GL11.glPopMatrix();
    }

    protected void renderVehicleNGTO(EntityVehicle vehicle, VehicleNGTO vngto, float par4)
    {
    	GL11.glScalef(vngto.scale, vngto.scale, vngto.scale);
    	GL11.glTranslatef(vngto.offsetX, vngto.offsetY, vngto.offsetZ);

    	int pass = MinecraftForgeClient.getRenderPass();
    	if(pass < 0 || pass > 1){return;}

    	if(vngto.glLists == null)
		{
    		vngto.glLists = new GLObject[2];
		}

    	GLHelper.disableLighting();
		GLHelper.setLightmapMaxBrightness();

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		boolean smoothing = NGTUtilClient.getMinecraft().gameSettings.ambientOcclusion != 0;
		if(smoothing){GL11.glShadeModel(GL11.GL_SMOOTH);}

		if(!GLHelper.isValid(vngto.glLists[pass]))
		{
			vngto.glLists[pass] = GLHelper.generateGLList(vngto.glLists[pass]);
			GLHelper.startCompile(vngto.glLists[pass]);
			NGTWorld world = new NGTWorld(vehicle.getEntityWorld(), vngto.ngto);
			NGTObjectRenderer.INSTANCE.renderNGTObject(world, vngto.ngto, false, 0, pass);
			GLHelper.endCompile();
		}
		else
		{
			GLHelper.callList(vngto.glLists[pass]);
		}

		if(smoothing){GL11.glShadeModel(GL11.GL_FLAT);}
		GLHelper.enableLighting();
		NGTUtilClient.getMinecraft().entityRenderer.enableLightmap();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);//明るさ戻す
    }

	@Override
    protected ResourceLocation getEntityTexture(EntityVehicleBase par1)
    {
        return null;
    }

	@Override
	protected boolean bindEntityTexture(EntityVehicleBase entiy){return false;}

	@Override
    public void doRender(EntityVehicleBase par1, double par2, double par4, double par6, float par8, float par9)
    {
        //this.renderVehicleBase(par1, par2, par4, par6, par8, par9);
    }

	@Override
	public boolean shouldRender(EntityVehicleBase entity, ICamera camera, double camX, double camY, double camZ)
    {
		return false;//WeatherEffectとしてのみ描画するので、Entity描画は除外
    }

	private void debugCollision(EntityVehicleBase vehicle)
	{
		if(MinecraftForgeClient.getRenderPass() == 1)
        {
        	ModelSetVehicleBase modelSet = (ModelSetVehicleBase)vehicle.getResourceState().getResourceSet();
            if(modelSet != null)
            {
            	if(modelSet.getCollisionObj() != null)
            	{
            		modelSet.getCollisionObj().checkAndRenderCollision(
            				Minecraft.getMinecraft().player, vehicle, vehicle.getResourceState().exclusionParts);
            	}
            }
        }
	}
}