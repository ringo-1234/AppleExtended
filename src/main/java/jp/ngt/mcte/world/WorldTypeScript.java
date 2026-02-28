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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldTypeScript extends WorldType {
    public static WorldType INSTANCE;

    public static void init() {
        //WorldTypeのコンストラクタで登録は行われる
        //WorldType.parseWorldType()でサーバーのコンフィグからWorldTypeを取得(大小文字区別なし)
        INSTANCE = new WorldTypeScript("script");
    }

    public WorldTypeScript(String name) {
        super(name);
    }

	/*@Override
	public BiomeProvider getBiomeProvider(World world)
	{
		WorldData worldData = WorldData.getWorldData(world, world.getWorldInfo().getGeneratorOptions());

		if(worldData == null || !worldData.getWorldGenerator().hasBiomesData())
		{
			NGTLog.debug("set default ChunkManager");
			return new BiomeProvider(world.getWorldInfo());
		}
		else
		{
			return new ChunkManagerPictorialCustom(world, worldData);
		}
	}*/

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
		/*WorldData worldData = WorldData.getWorldData(world, generatorOptions);
		if(worldData == null || !worldData.getWorldGenerator().hasTerrainData())
		{
			NGTLog.debug("set default ChunkGenerator");
			return new ChunkProviderOverworld(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
		}
		else
		{
			return new ChunkProviderScript(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
		}*/
        return new ChunkProviderScript(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
    }

    @Override
    public int getMinimumSpawnHeight(World world) {
        //return (int)this.getHorizon(world) + 1;
        return 150;
    }

    @Override
    public double getHorizon(World world) {
        return 63.0D;
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onCustomizeButton(Minecraft instance, GuiCreateWorld guiCreateWorld) {
        //instance.displayGuiScreen(new GuiCreatePictorialWorld(guiCreateWorld));
    }

	/*@Override
	public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer, String chunkProviderSettingsJson)
    {
		return new GenLayerPictorialCustom(worldSeed);
    }*/
}