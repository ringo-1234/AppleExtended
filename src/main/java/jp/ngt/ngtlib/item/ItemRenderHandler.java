package jp.ngt.ngtlib.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRenderHandler extends TileEntityItemStackRenderer
{
	public static final ItemRenderHandler INSTANCE = new ItemRenderHandler(TileEntityItemStackRenderer.instance);

	private final TileEntityItemStackRenderer parentRenderer;
	private final Map<Item, IItemRendererCustom> rendererMap = new HashMap<>();

	private ItemRenderHandler(TileEntityItemStackRenderer renderer)
	{
		this.parentRenderer = renderer;
		TileEntityItemStackRenderer.instance = this;
	}

	@Override
	public void renderByItem(ItemStack itemStack)
    {
		if(this.rendererMap.containsKey(itemStack.getItem()))
		{
			this.rendererMap.get(itemStack.getItem()).renderItem(itemStack);
		}
		else
		{
			this.parentRenderer.renderByItem(itemStack);
		}
    }

	public void register(Item item, IItemRendererCustom renderer)
	{
		this.rendererMap.put(item, renderer);
	}
}