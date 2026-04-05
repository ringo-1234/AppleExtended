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

package jp.apple.artpe.block;

import jp.apple.AppleLib;
import jp.apple.artpe.ARTPECore;
import jp.apple.artpe.ARTPEGuiHandler;
import jp.apple.artpe.tileentity.TileEntityTrainPlacer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTrainPlacer extends Block implements ITileEntityProvider {
    public BlockTrainPlacer() {
        super(Material.IRON);
        this.setUnlocalizedName("trainplacerblock");
        this.setRegistryName("trainplacerblock");
        this.setCreativeTab(AppleLib.tabAppleLib);
        this.setHardness(2.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityTrainPlacer)) {
            System.out.println("Error: TileEntity not found at " + pos);
            return false;
        }

        player.openGui(ARTPECore.instance, ARTPEGuiHandler.GUI_ID_TRAIN_PLACER, world, pos.getX(), pos.getY(),
                pos.getZ());
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTrainPlacer();
    }
}