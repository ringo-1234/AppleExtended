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

package jp.ngt.mcte.world;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class MCTETeleporter extends Teleporter {
    protected final WorldServer worldServer;
    protected final Random random;

    public MCTETeleporter(WorldServer worldIn) {
        super(worldIn);
        this.worldServer = worldIn;
        this.random = new Random(worldIn.getSeed());
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        int i = NGTMath.floor(entityIn.posX);
        int j = NGTMath.floor(entityIn.posY) - 1;
        int k = NGTMath.floor(entityIn.posZ);
        int l = 1;
        int i1 = 0;

        for (int j1 = -2; j1 <= 2; ++j1) {
            for (int k1 = -2; k1 <= 2; ++k1) {
                for (int l1 = -1; l1 < 3; ++l1) {
                    int i2 = i + k1 * l + j1 * i1;
                    int j2 = j + l1;
                    int k2 = k + k1 * i1 - j1 * l;
                    boolean flag = l1 < 0;
                    this.worldServer.setBlockState(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        entityIn.setLocationAndAngles((double) i, (double) j, (double) k, entityIn.rotationYaw, 0.0F);
        entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0D;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {
        ;
    }
}