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

package jp.ngt.rtm.electric;

import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.rtm.item.ItemInstalledObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockElectricalWiring extends BlockContainerCustomWithMeta implements IBlockConnective {
    protected BlockElectricalWiring(Material material) {
        super(material);
    }

    @Override
    public boolean onBlockActivated(BlockArgHolder holder, float hitX, float hitY, float hitZ) {
        if (holder.getPlayer().inventory.getCurrentItem().getItem() == jp.ngt.rtm.RTMItem.crowbar) {
            if (holder.getWorld().isRemote)
                com.anatawa12.fixRtm.UtilsKt.openGui(holder.getPlayer(), com.anatawa12.fixRtm.gui.GuiId.ChangeOffset, holder.getWorld(), holder.getBlockPos());
            return true;
        }
        if (holder.getWorld().isRemote) {
            return true;
        } else {
            TileEntityElectricalWiring tile = (TileEntityElectricalWiring) holder.getWorld().getTileEntity(holder.getBlockPos());
            tile.onRightClick(holder.getPlayer());
            return true;
        }
    }

    @Override
    public net.minecraft.item.ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        return com.anatawa12.fixRtm.rtm.block.BlockOrnamentMain.getPickBlock(world, pos, istlObjType(BlockUtil.getMetadata(world, pos)));
    }

    protected abstract ItemInstalledObject.IstlObjType istlObjType(int damage);

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityElectricalWiring tile = (TileEntityElectricalWiring) world.getTileEntity(pos);
        tile.onBlockBreaked();
        super.breakBlock(world, pos, state);
    }
}