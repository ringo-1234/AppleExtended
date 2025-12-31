package jp.ngt.rtm.modelpack.modelset;

import javax.script.ScriptEngine;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.rtm.entity.util.ColFace;
import jp.ngt.rtm.entity.util.CollisionObj;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.ModelConfig;
import jp.ngt.rtm.render.ModelObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModelSetBase<T extends ModelConfig> extends ResourceSet<T>
{
	@SideOnly(Side.CLIENT)
	public ModelObject modelObj;
	@SideOnly(Side.CLIENT)
	public ResourceLocation buttonTexture;

	@SideOnly(Side.CLIENT)
	public ScriptEngine guiSE;
	@SideOnly(Side.CLIENT)
	public ResourceLocation guiTexture;

	private CollisionObj collisionObj;
	private boolean syncFinished;

	public ScriptEngine serverSE;

	public ModelSetBase()
	{
		super();
	}

	public ModelSetBase(T par1)
	{
		super(par1);
	}

	@Override
	public void constructOnServer()
	{
		if(this.cfg.serverScriptPath != null)
		{
			this.serverSE = ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(this.cfg.serverScriptPath));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void constructOnClient()
	{
		if(!this.isDummy())
		{
			//SSP用
			if(this.cfg.serverScriptPath != null)
			{
				this.serverSE = ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(this.cfg.serverScriptPath));
			}

			if(this.cfg.guiScriptPath != null)
			{
				this.guiSE = ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(this.cfg.guiScriptPath));
				this.guiTexture = ModelPackManager.INSTANCE.getResource(this.cfg.guiTexture);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void finishConstruct()
	{
		if(this.modelObj != null)
		{
			this.collisionObj = new CollisionObj(this.modelObj.model, this.getConfig());
		}
	}

	@Override
	public T getConfig()
	{
		return this.cfg;
	}

	@SideOnly(Side.CLIENT)
	public void renderModelInGui(Minecraft mc)
	{
        this.modelObj.render(null, this.getConfig(), 0, 0.0F);
	}

	//Side.SERVER
	public void addColFace(String partsName, ColFace face, byte status)
	{
		if(!this.syncFinished)
		{
			if(this.collisionObj == null)
			{
				this.collisionObj = new CollisionObj();
			}
			this.collisionObj.addColFace(partsName, face, status);
			this.syncFinished = (status == 2);
		}
	}

	public CollisionObj getCollisionObj()
	{
		return this.collisionObj;
	}
}