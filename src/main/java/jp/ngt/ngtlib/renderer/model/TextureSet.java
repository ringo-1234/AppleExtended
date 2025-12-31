package jp.ngt.ngtlib.renderer.model;

import jp.ngt.rtm.modelpack.ModelPackManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**Material + ライト用テクスチャ*/
@SideOnly(Side.CLIENT)
public class TextureSet
{
	public final Material material;
	public final ResourceLocation[] subTextures;
	public final boolean doAlphaBlend;
	public final boolean doLighting;

	public TextureSet(Material mat, int subTexturesSize, boolean alpha, boolean light, String... args)
	{
		this.material = mat;
		this.doAlphaBlend = alpha;
		this.doLighting = light;

		if(subTexturesSize > 0)
		{
			this.subTextures = new ResourceLocation[subTexturesSize];
			String textureName = mat.texture.getResourcePath();
			int index = textureName.indexOf(".png");
			for(int i = 0; i < subTexturesSize; ++i)
			{
				String name;
				if(args.length > 0)
				{
					name = args[i];
				}
				else
				{
					name = new StringBuilder(textureName).insert(index, "_light" + i).toString();
				}
				this.subTextures[i] = ModelPackManager.INSTANCE.getResource(name);
			}
		}
		else
		{
			this.subTextures = null;
		}
	}
}