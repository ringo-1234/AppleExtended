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

package jp.ngt.rtm.rail;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.renderer.GLHelper;
import jp.ngt.ngtlib.renderer.IRenderer;
import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.renderer.VertexArray2;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.state.ResourceStateRail;
import jp.ngt.rtm.rail.util.RailMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**ISimpleBlockRenderingHandlerが使えなくなったのでTileEntityとして描画する*/
@SideOnly(Side.CLIENT)
public final class RenderRailBlock
{
	private static final float SNOW_THICKNESS = 0.125F;

	private final List<int[]> blockPosList = new ArrayList<>();

	//VA版
	public void renderRailBlocksVA(TileEntityLargeRailCore tileEntity, double par2, double par4, double par6, float par8)
	{
		boolean hasGLList = GLHelper.isValid(tileEntity.railBlocks);
		if(!hasGLList)
		{
			tileEntity.railBlocks = GLHelper.generateVA();
		}
		else if(tileEntity.shouldRerenderBlock)//再描画
		{
			hasGLList = false;
		}

		if(!hasGLList)//ディスプレイリスト生成
		{
			if(this.renderBlocks(tileEntity, (IRenderer)tileEntity.railBlocks))
			{
				tileEntity.shouldRerenderBlock = false;
				hasGLList = true;
			}
			else
			{
				tileEntity.shouldRerenderBlock = true;
				hasGLList = false;
			}
		}

		if(hasGLList)//ディスプレイリスト描画
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(par2 - tileEntity.getX()), (float)(par4 - tileEntity.getY()), (float)(par6 - tileEntity.getZ()));
			NGTUtilClient.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);//ディスプレイリストに入れると生成重い
			((VertexArray2)tileEntity.railBlocks).render();
			GL11.glPopMatrix();
		}
	}

	//GLList版
	public void renderRailBlocks(TileEntityLargeRailCore tileEntity, double par2, double par4, double par6, float par8)
	{
		boolean hasGLList = GLHelper.isValid(tileEntity.railBlocks);
		if(!hasGLList)
		{
			tileEntity.railBlocks = GLHelper.generateGLList(tileEntity.railBlocks);
		}
		else if(tileEntity.shouldRerenderBlock)//再描画
		{
			hasGLList = false;
		}

		if(!hasGLList)//ディスプレイリスト生成
		{
			NGTTessellator tessellator = NGTTessellator.instance;
			GLHelper.startCompile(tileEntity.railBlocks);
			if(this.renderBlocks(tileEntity, tessellator))
			{
				tileEntity.shouldRerenderBlock = false;
				hasGLList = true;
			}
			else
			{
				tileEntity.shouldRerenderBlock = true;
				hasGLList = false;
			}
			GLHelper.endCompile();
		}

		if(hasGLList)//ディスプレイリスト描画
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(par2 - tileEntity.getX()), (float)(par4 - tileEntity.getY()), (float)(par6 - tileEntity.getZ()));
			NGTUtilClient.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);//ディスプレイリストに入れると生成重い
			GLHelper.callList(tileEntity.railBlocks);
			GL11.glPopMatrix();
		}
	}

	/**@return 全ブロックの描画に成功*/
	public boolean renderBlocks(TileEntityLargeRailCore rail, IRenderer renderer)
	{
		RailMap[] rms = rail.getAllRailMaps();
		if(rms == null){return false;}

		boolean flag = true;

		for(RailMap rm : rms)
		{
			List<int[]> list = rm.getRailBlockList(rail.getResourceState(), false);
			/*if(!this.checkRailBlock(tileEntity.getWorld(), list))
			{
				break;//ブロック取得失敗時は描画破棄→MissingBlockを描画するよう変更
			}*/
			this.blockPosList.addAll(list);
		}

		if(this.blockPosList.size() < 2)
		{
			this.blockPosList.clear();
			return false;
		}

		renderer.startDrawing(GL11.GL_QUADS);
		for(int[] ia : this.blockPosList)
		{
			World world = rail.getWorld();
			int x = ia[0];
			int y = ia[1];
			int z = ia[2];
			Block block = BlockUtil.getBlock(world, x, y, z);
			try
			{
				if(!this.renderWorldBlock(rail, renderer, world, x, y, z, block))
				{
					rail.rerenderCount = 1;
					this.renderMissingBlock(renderer, world, x, y, z);
				}
			}
			catch(ClassCastException e)//マーカー置き換え未完了時
			{
				flag = false;
			}
		}

		if(renderer instanceof VertexArray2)
		{
			if(flag)
			{
				renderer.draw();
			}
			else
			{
				((VertexArray2)renderer).cancel();//描画失敗時の無駄なDirectBuf生成を防止
			}
		}
		else
		{
			renderer.draw();
		}

		this.blockPosList.clear();
		return flag;
	}

	private boolean renderWorldBlock(TileEntityLargeRailCore rail, IRenderer renderer, IBlockAccess world, int x, int y, int z, Block block)
	{
		TileEntityLargeRailBase tile = (TileEntityLargeRailBase)BlockUtil.getTileEntity(world, x, y, z);
		if(tile == null){return false;}
		ResourceStateRail prop = rail.getResourceState();
		if(prop.block == Blocks.AIR){return true;}

        float[] fa = tile.getBlockHeights(x, y, z, prop.blockHeight, false);
        float y0 = fa[0];
        float y1 = fa[1];
        float y2 = fa[2];
        float y3 = fa[3];

        //renderer.setBrightness(block.getMixedBrightnessForBlock(world, tile.getPos()));
        BlockPos origPos = tile.getPos();
        int brightness = block.getPackedLightmapCoords(world.getBlockState(origPos), world, origPos);
        renderer.setBrightness(brightness);;

      //雪の厚み
        if(prop.block.getMaterial(prop.block.getDefaultState()) == Material.CRAFTED_SNOW)
        {
        	y0 += SNOW_THICKNESS;
        	y1 += SNOW_THICKNESS;
        	y2 += SNOW_THICKNESS;
        	y3 += SNOW_THICKNESS;
        }

        TextureAtlasSprite icon;
        float minU;
        float maxU;
        float maxV;
        float v;

        IBlockState sideBlock = BlockUtil.getBlockState(world, x, y, z + 1);
        if(!sideBlock.isOpaqueCube() && !(sideBlock.getBlock() instanceof BlockLargeRailBase))
        {
        	icon = this.getIcon(prop.block, prop.meta, 3);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(204, 204, 204, 255);
        	//renderer.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        	renderer.addVertexWithUV(x + 0.0F, y + y0, z + 1.0F, minU, maxV - (v * this.getV(y0)));
        	renderer.addVertexWithUV(x + 0.0F, y + 0.0F, z + 1.0F, minU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y + 0.0F, z + 1.0F, maxU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y + y1, z + 1.0F, maxU, maxV - (v * this.getV(y1)));
        }

        sideBlock = BlockUtil.getBlockState(world, x + 1, y, z);
        if(!sideBlock.isOpaqueCube() && !(sideBlock.getBlock() instanceof BlockLargeRailBase))
        {
        	icon = this.getIcon(prop.block, prop.meta, 5);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(153, 153, 153, 255);
        	//renderer.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        	renderer.addVertexWithUV(x + 1.0F, y + y1, z + 1.0F, minU, maxV - (v * this.getV(y1)));
        	renderer.addVertexWithUV(x + 1.0F, y + 0.0F, z + 1.0F, minU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y + 0.0F, z + 0.0F, maxU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y + y2, z + 0.0F, maxU, maxV - (v * this.getV(y2)));
        }

        sideBlock = BlockUtil.getBlockState(world, x, y, z - 1);
        if(!sideBlock.isOpaqueCube() && !(sideBlock.getBlock() instanceof BlockLargeRailBase))
        {
        	icon = this.getIcon(prop.block, prop.meta, 2);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(204, 204, 204, 255);
        	//renderer.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        	renderer.addVertexWithUV(x + 1.0F, y + y2, z + 0.0F, minU, maxV - (v * this.getV(y2)));
        	renderer.addVertexWithUV(x + 1.0F, y + 0.0F, z + 0.0F, minU, maxV);
	        renderer.addVertexWithUV(x + 0.0F, y + 0.0F, z + 0.0F, maxU, maxV);
	        renderer.addVertexWithUV(x + 0.0F, y + y3, z + 0.0F, maxU, maxV - (v * this.getV(y3)));
        }

        sideBlock = BlockUtil.getBlockState(world, x - 1, y, z);
        if(!sideBlock.isOpaqueCube() && !(sideBlock.getBlock() instanceof BlockLargeRailBase))
        {
        	icon = this.getIcon(prop.block, prop.meta, 4);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(153, 153, 153, 255);
        	//renderer.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        	renderer.addVertexWithUV(x + 0.0F, y + y3, z + 0.0F, minU, maxV - (v * this.getV(y3)));
        	renderer.addVertexWithUV(x + 0.0F, y + 0.0F, z + 0.0F, minU, maxV);
        	renderer.addVertexWithUV(x + 0.0F, y + 0.0F, z + 1.0F, maxU, maxV);
        	renderer.addVertexWithUV(x + 0.0F, y + y0, z + 1.0F, maxU, maxV - (v * this.getV(y0)));
        }

        //上面
        {
        	icon = this.getIcon(prop.block, prop.meta, 1);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(255, 255, 255, 255);
        	//renderer.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        	renderer.addVertexWithUV(x + 0.0F, y + y0, z + 1.0F, minU, maxV - v);
        	renderer.addVertexWithUV(x + 1.0F, y + y1, z + 1.0F, minU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y + y2, z + 0.0F, maxU, maxV);
        	renderer.addVertexWithUV(x + 0.0F, y + y3, z + 0.0F, maxU, maxV - v);
        }

        //下面
        sideBlock = BlockUtil.getBlockState(world, x, y - 1, z);
        if(!sideBlock.isOpaqueCube())
        {
        	icon = this.getIcon(prop.block, prop.meta, 0);
        	minU = icon.getMinU();
        	maxU = icon.getMaxU();
        	maxV = icon.getMaxV();
        	v = maxV - icon.getMinV();

        	renderer.setColor(127, 127, 127, 255);
        	//renderer.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        	renderer.addVertexWithUV(x + 0.0F, y, z + 0.0F, minU, maxV - v);
        	renderer.addVertexWithUV(x + 1.0F, y, z + 0.0F, minU, maxV);
        	renderer.addVertexWithUV(x + 1.0F, y, z + 1.0F, maxU, maxV);
        	renderer.addVertexWithUV(x + 0.0F, y, z + 1.0F, maxU, maxV - v);
        }

        return true;
	}

	private void renderMissingBlock(IRenderer renderer, IBlockAccess world, int x, int y, int z)
	{
		TextureAtlasSprite icon = this.getIcon(Blocks.BEDROCK, 0, 1);
    	float minU = icon.getMinU();
    	float maxU = icon.getMaxU();
    	float maxV = icon.getMaxV();
    	float minV = icon.getMinV();
    	float y0 = 0.0625F;

    	renderer.setColor(255, 255, 255, 255);
    	//renderer.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    	renderer.addVertexWithUV(x + 0.0F, y + y0, z + 1.0F, minU, minV);
    	renderer.addVertexWithUV(x + 1.0F, y + y0, z + 1.0F, minU, maxV);
    	renderer.addVertexWithUV(x + 1.0F, y + y0, z + 0.0F, maxU, maxV);
    	renderer.addVertexWithUV(x + 0.0F, y + y0, z + 0.0F, maxU, minV);
	}

	private TextureAtlasSprite getIcon(Block block, int meta, int side)
    {
		IBlockState state = block.getStateFromMeta(meta);
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

	private float getV(float par1)
	{
		return par1 > 1.0F ? 1.0F : (par1 < 0.0F ? 0.0F : par1);
	}

	@Deprecated
	/**道床位置のTileEntityが取得可能か*/
	private boolean checkRailBlock(World world, List<int[]> list)
	{
		for(int[] pos : list)
		{
			TileEntityLargeRailBase tile = (TileEntityLargeRailBase)BlockUtil.getTileEntity(world, pos[0], pos[1], pos[2]);
			if(tile == null){return false;}
			TileEntityLargeRailCore core = tile.getRailCore();
			if(core == null){return false;}
		}
		return true;
	}
}