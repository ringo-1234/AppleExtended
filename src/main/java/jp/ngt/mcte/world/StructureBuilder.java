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

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

@Deprecated
public class StructureBuilder
{
	private Random rand;

	public StructureBuilder(Random par1)
	{
		this.rand = par1;
	}

	/**
     * 地形のベースと海の生成
     * @param par1 chunkX
     * @param par2 chunkZ
     * @param par3 16*16*256
     * */
    public void setBlocksInChunk(int par1, int par2, ChunkPrimer par3)
    {
    	int chunkX = par1 << 4;
    	int chunkZ = par2 << 4;
    	StructureType stType = this.getStructureType(par1, par2);

    	int height = 16;
		if(stType == StructureType.LOW)
		{
			height = 64 + ((rand.nextInt(5) - 2) << 4);
		}
		else if(stType == StructureType.MEDIUM)
		{
			height = 192 + ((rand.nextInt(7) - 2) << 4);
		}
		else if(stType == StructureType.HIGHT)
		{
			height = 256;
		}

		boolean hasPillorX = false;
		boolean hasPillorY = this.rand.nextInt(4) == 0;
		boolean hasPillorZ = false;
		int pillorExistY = 128 + ((rand.nextInt(7) - 2) << 4);

		for(int y = 0; y < 256; ++y)
    	{
			if((y & 15) == 0)
			{
				hasPillorX = y < pillorExistY && this.rand.nextInt(2) == 0;
				hasPillorZ = y < pillorExistY && this.rand.nextInt(2) == 0;
			}

			for(int x = 0; x < 16; ++x)
        	{
				for(int z = 0; z < 16; ++z)
    			{
    				if(y == 0)
    				{
    					par3.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
    				}
    				else if(y <= rand.nextInt(3))
    				{
    					par3.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
    				}
    				else
    				{
    					//建物生成
    					if(y < height)
    					{
    						if(stType == StructureType.WATER)
            				{
            					par3.setBlockState(x, y, z, Blocks.WATER.getDefaultState());
            				}
            				else
            				{
            					par3.setBlockState(x, y, z, Blocks.STONE.getDefaultState());

            					if((y & 3) == 1)
            					{
            						if(((x == 0 || x == 15) && ((z & 3) == 1)) || ((z == 0 || z == 15) && ((x & 3) == 1)))
            						{
            							par3.setBlockState(x, y, z, Blocks.SEA_LANTERN.getDefaultState());
            						}
            					}
            				}
    					}

    					//柱生成
    					int y2 = y & 15;
    					boolean flagX = x >= 6 && x < 10;
    					boolean flagY = y2 >= 6 && y2 < 10;
    					boolean flagZ = z >= 6 && z < 10;
    					if((flagX && flagY && hasPillorZ) || (flagY && flagZ && hasPillorX) || (flagX && flagZ && hasPillorY && y < pillorExistY))
    					{
    						par3.setBlockState(x, y, z, Blocks.STONE.getDefaultState());
    					}
    				}
    			}
        	}
    	}
    }

    protected StructureType getStructureType(int par1, int par2)
    {
    	switch(rand.nextInt(15))
    	{
    	case 0:
    	case 1:
    	case 2:
    	case 3:
    	case 4: return StructureType.WATER;
    	case 5:
    	case 6: return StructureType.GROUND;
    	case 7:
    	case 8:
    	case 9:
    	case 10: return StructureType.LOW;
    	case 11:
    	case 12:
    	case 13: return StructureType.MEDIUM;
    	case 14: return StructureType.HIGHT;
    	}
    	return StructureType.WATER;
    }

    public enum StructureType
    {
    	WATER,
    	GROUND,
    	LOW,
    	MEDIUM,
    	HIGHT;
    }
}