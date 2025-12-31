package jp.ngt.rtm.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.io.ResourceLocationCustom;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.renderer.model.MCModel;
import jp.ngt.ngtlib.renderer.model.Material;
import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.TextureSet;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import jp.ngt.rtm.modelpack.cfg.ModelConfig.ModelSource;
import jp.ngt.rtm.modelpack.modelset.ModelSetBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**モデルデータとテクスチャを管理*/
@SideOnly(Side.CLIENT)
public class ModelObject
{
	private static ModelObject DUMMY;

	/*ポリゴンモデル*/
	public final IModelNGT model;
	/*材質ごとのテクスチャ*/
	public final TextureSet[] textures;
	/*専用レンダラ*/
	public final PartsRenderer renderer;

	public final boolean light;
	public final boolean alphaBlend;
	private final boolean useTexture;

	private String vshPath, fshPath;
	public boolean useShader;
	private int program;

	public ModelObject(ModelSource par1, ModelSetBase par2, PartsRenderer par3, Object... args)
	{
		String filePath = par1.modelFile;
		//NGTLog.startTimer();
		this.model = ModelPackManager.INSTANCE.loadModel(filePath, GL11.GL_TRIANGLES, true, par2.getConfig(), par2.md5);
		//NGTLog.stopTimer(par2.getConfig().getName() + ",mo");

		//NGTLog.startTimer();
		Material[] materials = this.getMaterials(this.getTextureMap(par1.textures));
		this.textures = new TextureSet[materials.length];
		boolean useLight = false;
		boolean useAlpha = false;
		int size = (materials.length != par1.textures.length) ? 1 : materials.length;
		for(int i = 0; i < size; ++i)
		{
			Material mat = materials[i];
			String[] sa = par1.textures[size == 1 ? 0 : mat.id];
			String texOption = (sa.length >= 3) ? sa[2] : "";
			boolean flagLight = texOption.contains("Light");
			boolean flagAlpha = texOption.contains("AlphaBlend");
			boolean disableSubTex = texOption.contains("OneTex");
			int texSize = (flagLight && !disableSubTex ? 3 : 0);

			//独自定義のライト用テクスチャ名を使ってる場合
			String[] lightTextureNames = new String[sa.length >= 4 ? sa.length - 3 : 0];
			if(lightTextureNames.length > 0)
			{
				for(int j = 0; j < lightTextureNames.length; ++j)
				{
					lightTextureNames[j] = sa[j + 3];
				}
				texSize = lightTextureNames.length;
			}

			this.textures[mat.id] = new TextureSet(mat, texSize, flagAlpha, flagLight, lightTextureNames);
			useLight |= flagLight;
			useAlpha |= flagAlpha;
		}
		this.light = useLight;
		this.alphaBlend = useAlpha;
		this.useTexture = !(this.model.getType() == FileType.NGTO || this.model.getType() == FileType.NGTZ);

		if(this.textures[0] == null)//もしものため([0]で参照した場合)
		{
			this.textures[0] = new TextureSet(new Material((byte)0, new ResourceLocation("hoge")), 0, false, false);
		}
		//NGTLog.stopTimer(par2.getConfig().getName() + ",tex");

		//NGTLog.startTimer();
		this.renderer = (par3 == null) ? this.getPartsRenderer(par1.rendererPath, this.model, args) : par3;
		this.renderer.init(par2, this);
		//NGTLog.stopTimer(par2.getConfig().getName() + ",init");

		this.vshPath = par1.vertexShaderPath;
		this.fshPath = par1.fragmentShaderPath;
	}

	/**MissingModel用*/
	public ModelObject(IModelNGT par1, TextureSet[] par2)
	{
		this.model = par1;
		this.textures = par2;
		this.light = false;
		this.alphaBlend = false;
		this.useTexture = true;

		this.renderer = this.getPartsRenderer(null, par1);
		//this.renderer.init(par3, this);
	}

	public static ModelObject getDummy()
	{
		if(DUMMY == null)
		{
			DUMMY = new ModelObject(
					ModelLoader.loadModel(new ResourceLocationCustom("models/ModelContainer_19g.obj"), VecAccuracy.LOW),
					new TextureSet[]{new TextureSet(new Material((byte)0,
							ModelPackManager.INSTANCE.getResource("textures/container/19g_JRF_0.png")), 0, false, false)}
					);
		}
		return DUMMY;
	}

	private PartsRenderer getPartsRenderer(String path, IModelNGT par2, Object... args)
	{
		boolean b0 = !(args.length >= 1 && ("isBogie".equals(args[0])));

		if(path != null)
		{
			return RTMRenderers.getRendererWithScript(ModelPackManager.INSTANCE.getResource(path), String.valueOf(b0));
		}
		else if(par2 instanceof MCModel)
		{
			if(args.length >= 1 && "vehicle".equals(args[0]))
			{
				return new MCVehicleRenderer(String.valueOf(b0));
			}
			return new MCModelRenderer(String.valueOf(b0));
		}
		else
		{
			return new BasicPartsRenderer();
		}
	}

	private void initShader()
	{
		if(this.vshPath != null && this.fshPath != null)
		{
			try
			{
				String vsh = NGTText.getText(new ResourceLocation(this.vshPath), true);//コメント対策でインデント有り
				String fsh = NGTText.getText(new ResourceLocation(this.fshPath), true);
				this.program = GLHelper.getShaderProgram(vsh, fsh);
				this.useShader = (this.program > 0);
				return;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		this.program = -1;
	}

	/**
	 * モデル描画(通常はこれを使用)
	 * @param pass 0:通常, 1:透過、発光
	 */
	public void render(Object entity, ModelConfig cfg, int pass, float partialTick)
	{
		this.preRender();

		GL11.glPushMatrix();

		if(!(entity instanceof EntityBogie))
		{
			float[] fa = cfg.offset;
	    	GL11.glTranslated(fa[0], fa[1], fa[2]);
		}

		boolean isSelector = (entity instanceof IResourceSelector);
		boolean canUseColor = (cfg.useCustomColor && isSelector);

		this.renderer.preRender(entity, cfg.smoothing, cfg.doCulling, partialTick);

		if(!cfg.doCulling)
        {
    		GL11.glDisable(GL11.GL_CULL_FACE);
        }

		if(cfg.smoothing)
    	{
    		GL11.glShadeModel(GL11.GL_SMOOTH);
    	}

		if(canUseColor)
    	{
			GLHelper.setColor(((IResourceSelector)entity).getResourceState().color, 0xFF);
    	}

		if(pass == 0)
		{
			this.renderWithTexture(entity, RenderPass.NORMAL, partialTick);
		}
		else if(pass == 1)
		{
			//半透明
			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        this.renderWithTexture(entity, RenderPass.TRANSPARENT, partialTick);
	        GL11.glDisable(GL11.GL_BLEND);

	        //発光
	        GLHelper.disableLighting();
			GLHelper.setLightmapMaxBrightness();
			this.renderWithTexture(entity, RenderPass.LIGHT, partialTick);
			if(this.renderer.shouldRenderOutline(entity))
			{
				this.renderWithTexture(entity, RenderPass.OUTLINE, partialTick);
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GLHelper.enableLighting();
		}

		if(canUseColor)
    	{
			GLHelper.setColor(0xFFFFFF, 0xFF);
    	}

		if(cfg.smoothing)
    	{
    		GL11.glShadeModel(GL11.GL_FLAT);
    	}

		GL11.glEnable(GL11.GL_CULL_FACE);

		this.renderer.postRender(entity, cfg.smoothing, cfg.doCulling, partialTick);

		GL11.glPopMatrix();

		this.postRender();
	}

	public void preRender()
	{
		if(!this.useShader && this.program == 0)
		{
			this.initShader();
		}

		if(this.useShader)
		{
			ARBShaderObjects.glUseProgramObjectARB(this.program);
		}
	}

	public void postRender()
	{
		if(this.useShader)
		{
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
	}

	/**スムージング、アルファブレンド等行わず*/
	public void renderWithTexture(Object entity, RenderPass pass, float partialTick)
	{
		for(int i = 0; i < this.textures.length; ++i)
		{
			if(this.useTexture)
			{
				ResourceLocation texture = null;
				if(pass ==  RenderPass.NORMAL)
	    		{
					texture = this.textures[i].material.texture;
	    		}
				else if(pass == RenderPass.TRANSPARENT)
				{
					if(!this.textures[i].doAlphaBlend){continue;}

					texture = this.textures[i].material.texture;
				}
	    		else if(pass == RenderPass.LIGHT || pass == RenderPass.LIGHT_FRONT || pass == RenderPass.LIGHT_BACK)
	    		{
	    			if(!this.textures[i].doLighting){continue;}

	    			if(this.textures[i].subTextures != null)
	    			{
	    				texture = this.textures[i].subTextures[pass.id - 2];
	    			}
	    			else
	    			{
	    				texture = this.textures[i].material.texture;//ライト有、サブTexなし
	    			}
	    		}
	    		else
	    		{
	    			texture = this.textures[i].material.texture;
	    		}

				NGTUtilClient.bindTexture(texture);

				if(this.useShader)
				{
					//アクティブなテクスチャ番号≠TextureId
					ARBShaderObjects.glUniform1iARB(
							ARBShaderObjects.glGetUniformLocationARB(this.program, "texture"), 0);

					int time = (int)(System.currentTimeMillis() % (24000 * 50));
					ARBShaderObjects.glUniform1iARB(
							ARBShaderObjects.glGetUniformLocationARB(this.program, "time"), time);
				}
			}

			this.renderer.currentMatId = this.textures[i].material.id;
			this.renderer.render(entity, pass, partialTick);
		}
	}

	public Material[] getMaterials(Map<String, String> map)
	{
		Map<String, Material> matMap = this.model.getMaterials();
		Material[] materials;
		if(matMap.isEmpty())
		{
			materials = new Material[]{new Material((byte)0, ModelPackManager.INSTANCE.getResource(map.get("default")))};
		}
		else
		{
			materials = new Material[matMap.size()];
			Iterator<Entry<String, Material>> iterator = matMap.entrySet().iterator();
			for(int i = 0; iterator.hasNext(); ++i)
			{
				Entry<String, Material> entry = iterator.next();
				String matName = map.get(entry.getKey());
				if(matName == null){matName = map.get("default");}
				materials[i] = new Material(entry.getValue().id, ModelPackManager.INSTANCE.getResource(matName));
			}
		}
		return materials;
	}

	protected Map<String, String> getTextureMap(String[][] par1)
	{
		Map<String, String> map = new HashMap<String, String>();
		for(String[] sa : par1)
		{
			map.put(sa[0], sa[1]);
		}
		return map;
	}
}