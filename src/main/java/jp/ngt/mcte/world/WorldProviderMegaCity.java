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

import jp.ngt.mcte.MCTE;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderMegaCity extends WorldProvider {
    @Override
    public void init() {
        this.biomeProvider = new BiomeProviderSingle(MCTEBiome.MEGA_CITY);
        this.nether = false;
        //this.hasSkyLight = false;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkProviderMegaCity(this.world, this.world.getSeed());
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }

    @Override
    public double getHorizon() {
        return 16.0D;
    }

    @Override
    public DimensionType getDimensionType() {
        return MCTE.MEGA_CITY;
    }
}