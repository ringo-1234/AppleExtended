package jp.ngt.mcte;

import jp.ngt.mcte.block.BlockMinesweeper.MinesweeperType;
import jp.ngt.mcte.block.RenderMiniature;
import jp.ngt.mcte.block.TileEntityMiniature;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.mcte.editor.RenderEditor;
import jp.ngt.mcte.editor.filter.FilterManager;
import jp.ngt.mcte.item.RenderItemMiniature;
import jp.ngt.ngtlib.item.ItemRenderHandler;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.ngtlib.util.PackInfo;
import jp.ngt.ngtlib.util.VersionChecker;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
    public void preInit()
    {
		VersionChecker.addToCheckList(new PackInfo(MCTE.metadata.name, MCTE.metadata.url, MCTE.metadata.updateUrl, MCTE.metadata.version));

		NGTUtilClient.registerEntityRender(EntityEditor.class, RenderEditor.class);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniature.class, RenderMiniature.INSTANCE);

		String domain = "mcte";
		for(MinesweeperType type : MinesweeperType.values())
		{
			String typeNameLC = type.toString().toLowerCase();
			NGTUtilClient.registerItemModel(MCTE.minesweeper, type.id, domain, "minesweeper_" + typeNameLC);
		}
		this.registerModel(Item.getItemFromBlock(MCTE.portIn), 0, "port_in");
		this.registerModel(Item.getItemFromBlock(MCTE.portOut), 0, "port_out");
		this.registerModel(MCTE.editor,        0, "editor");
		this.registerModel(MCTE.generator,     0, "generator");
		this.registerModel(MCTE.painter,       0, "painter");
		this.registerModel(MCTE.itemMiniature, 0, "item_miniature");

		NGTUtilClient.registerBuildinModel(MCTE.miniature, true);

		//MinecraftForgeClient.registerItemRenderer(MCTE.itemMiniature, RenderItemMiniature.INSTANCE);

		MCTEKeyHandlerClient.init();
    }

	@Override
    public void init()
    {
    	MinecraftForge.EVENT_BUS.register(new MCTEKeyHandlerClient());
    	MinecraftForge.EVENT_BUS.register(RenderItemMiniature.INSTANCE);
    	ItemRenderHandler.INSTANCE.register(MCTE.itemMiniature, RenderItemMiniature.INSTANCE);
    	FilterManager.INSTANCE.loadFilters();//preInitではdevPathが設定できてないため
    }

	private void registerModel(Item item, int meta, String name)
	{
		//後でまとめてItemModelMesherに登録される
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(MCTE.MODID + ":" + name, "inventory"));
	}
}