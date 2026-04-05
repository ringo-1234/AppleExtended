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

package jp.ngt.mcte.block;

import jp.ngt.mcte.block.RSPortSet.PortType;
import jp.ngt.mcte.world.MCTEWorld;
import jp.ngt.ngtlib.block.BlockArgHolder;
import jp.ngt.ngtlib.block.BlockCustomWithMeta;
import jp.ngt.ngtlib.block.BlockUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPort extends BlockCustomWithMeta {
    public final PortType type;

    public BlockPort(PortType par1) {
        super(Material.ROCK);
        this.type = par1;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    //隣からRS入力
    @Override
    protected void neighborChanged(BlockArgHolder holder) {
        World world = holder.getWorld();
        BlockPos pos = holder.getBlockPos();

        if (this.type == PortType.OUT && world instanceof MCTEWorld) {
            int power = world.getStrongPower(pos);
            if (BlockUtil.getMetadata(world, pos) != power) {
                BlockUtil.setBlock(world, pos, this, power, 3);
                ((MCTEWorld) world).onPortChanged(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Override
    protected int getWeakPower(BlockArgHolder holder) {
        return this.getStrongPower(holder);
    }

    @Override
    protected int getStrongPower(BlockArgHolder holder) {
        int meta = BlockUtil.getMetadata(holder.getBlockAccess(), holder.getBlockPos());
        return this.type == PortType.IN ? meta : 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    protected boolean shouldCheckWeakPower(BlockArgHolder holder) {
        return this.type == PortType.OUT;//隣接ブロックからのの信号を使うか
    }
}