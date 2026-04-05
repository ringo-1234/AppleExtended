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

package jp.apple.arse.block;

import jp.apple.AppleLib;
import jp.apple.arse.tileentity.TileEntitySoundRemover;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoundRemover extends Block implements ITileEntityProvider {
    public SoundRemover() {
        super(Material.IRON);
        setUnlocalizedName("sound_remover");
        setRegistryName("sound_remover");
        setCreativeTab(AppleLib.tabAppleLib);
        setHardness(2.0F);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySoundRemover();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntitySoundRemover) {
                TileEntitySoundRemover tesr = (TileEntitySoundRemover) te;

                boolean nextMode = !tesr.isUseRS();
                tesr.setUseRS(nextMode);

                String modeStr = nextMode ? "RS信号が必要 (ON)" : "常に動作 (OFF)";
                playerIn.sendMessage(new TextComponentString("[ARSE] モード切替: " + modeStr));
            }
        }
        return true;
    }
}