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

package jp.ngt.rtm.entity.vehicle;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderWeatherEffectDummy extends Render<WeatherEffectDummy> {
    private final RenderVehicleBase vehicleRenderer;

    public RenderWeatherEffectDummy(RenderManager renderManager) {
        super(renderManager);
        this.vehicleRenderer = new RenderVehicleBase(renderManager);
    }

    @Override
    public void doRender(WeatherEffectDummy dummy, double renderX, double renderY, double renderZ, float par8, float partialTick) {
        EntityVehicleBase vehicle = dummy.getParent();
        double dummyX = dummy.lastTickPosX + (dummy.posX - dummy.lastTickPosX) * (double) partialTick;
        double dummyY = dummy.lastTickPosY + (dummy.posY - dummy.lastTickPosY) * (double) partialTick;
        double dummyZ = dummy.lastTickPosZ + (dummy.posZ - dummy.lastTickPosZ) * (double) partialTick;
        double vehicleX = vehicle.lastTickPosX + (vehicle.posX - vehicle.lastTickPosX) * (double) partialTick;
        double vehicleY = vehicle.lastTickPosY + (vehicle.posY - vehicle.lastTickPosY) * (double) partialTick;
        double vehicleZ = vehicle.lastTickPosZ + (vehicle.posZ - vehicle.lastTickPosZ) * (double) partialTick;
        renderX += (vehicleX - dummyX);
        renderY += (vehicleY - dummyY);
        renderZ += (vehicleZ - dummyZ);
        this.vehicleRenderer.renderVehicleBase(vehicle, renderX, renderY, renderZ, par8, partialTick);
    }

    @Override
    protected ResourceLocation getEntityTexture(WeatherEffectDummy entity) {
        return null;
    }
}
