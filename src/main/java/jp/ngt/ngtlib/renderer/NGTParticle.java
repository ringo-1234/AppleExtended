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

package jp.ngt.ngtlib.renderer;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class NGTParticle {
    public static final NGTParticle INSTANCE = new NGTParticle();

    //private int nextId;

    private final Map<String, EnumParticleTypes> nameMap = new HashMap<>();

    private NGTParticle() {
        //this.nextId = EnumParticleTypes.values().length;
    }

    public static EnumParticleTypes getParticle(String name) {
        if (INSTANCE.nameMap.isEmpty()) {
            INSTANCE.init();
        }

        if (INSTANCE.nameMap.containsKey(name)) {
            return INSTANCE.nameMap.get(name);
        }

        return EnumParticleTypes.SMOKE_NORMAL;
    }

    private void init() {
        for (EnumParticleTypes type : EnumParticleTypes.values()) {
            this.nameMap.put(type.getParticleName(), type);
        }
    }

    public void register(int id, IParticleFactory factory) {
        NGTUtilClient.getMinecraft().effectRenderer.registerParticle(id, factory);
    }

    public void spawnParticle(World world, int particleID, boolean ignoreRange, double xCood, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        NGTUtil.getMethod(World.class, world, "spawnParticle", null,
                new Class<?>[]{int.class, boolean.class, double.class, double.class, double.class, double.class, double.class, double.class, int[].class},
                particleID, ignoreRange, xCood, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
}