package jp.ngt.ngtlib.util;

import java.util.HashMap;
import java.util.List;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.renderer.BasicRenderFactory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NGTUtilClient
{
	/**-1:未初期化, 0:シェーダーなし, 1:シェーダーあり*/
	private static byte HAS_SHADER = -1;

	public static Minecraft getMinecraft()
	{
		//return FMLClientHandler.instance().getClient();
		return Minecraft.getMinecraft();
	}

	public static void bindTexture(ResourceLocation par1)
	{
		getMinecraft().getTextureManager().bindTexture(par1);
	}

	public static int getLightValue(World world, int x, int y, int z)
	{
		if(world.isBlockLoaded(new BlockPos(x, 0, z), false))
        {
			BlockPos pos = new BlockPos(x, y, z);
			int sky = world.getLightFor(EnumSkyBlock.SKY, pos);
	        int block = world.getLightFor(EnumSkyBlock.BLOCK, pos);
	        return sky > block ? sky : block;
        }
		return 0;
	}

	/**
	 * @return シェーダーModを使用しているか
	 * */
	public static boolean usingShader()
	{
		if(HAS_SHADER < 0)
		{
			/*hasShader = 0;
			List<ModContainer> list = Loader.instance().getActiveModList();
			for(ModContainer container : list)
			{
				if(container.getModId().equals("shadersmod"))
				{
					hasShader = 1;break;
				}
			}*/

			HAS_SHADER = 0;
			List<String> list = CoreModManager.getReparseableCoremods();
			for(String name : list)
			{
				if(name.contains(NGTCore.shaderModName))
				{
					HAS_SHADER = 1;
					break;
				}
			}
		}

		return HAS_SHADER == 1;
	}

	public static void registerItemModel(Block block, int meta, String domain, String path)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
				meta, new ModelResourceLocation(domain + ":" + path, "inventory"));
	}

	public static <T extends Entity> void registerEntityRender(Class<T> entityClass, Class<? extends Render<? super T>> renderer)
    {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, new BasicRenderFactory(renderer));
    }

	/**例:"minecraft:blocks/lava_still"*/
	public static TextureAtlasSprite getIcon(String name)
	{
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        return texturemap.getAtlasSprite(name);
	}

	/**
	 * モデルなしブロック登録<br>
	 * preInit()で呼ぶこと
	 */
	public static void registerBuildinModel(Block block, boolean noItem)
	{
		ModelLoader.setCustomStateMapper(block, (par1)->{return new HashMap<>();});

		if(noItem)
		{
			registerNoModelBlockAsItem(block);
		}

		//以下がタイミング的に使えないので、空のStateMapを返す方式で対応
		//BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		//dispatcher.getBlockModelShapes().registerBuiltInBlocks(blocks);
	}

	/**
	 * モデルなしアイテム(ブロック)登録<br>
	 * preInit()で呼ぶこと
	 */
	public static void registerNoModelBlockAsItem(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		ModelBakery.registerItemVariants(item, new ResourceLocation[]{});
		//ModelLoader.setCustomMeshDefinition(item, (stack)->{return null;});
	}
}