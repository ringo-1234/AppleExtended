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

package jp.apple.fix.model;

import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.renderer.model.PolygonModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class CachedModelUtil {
    private CachedModelUtil() {
    }

    public static PolygonModel loadModel(ResourceLocation resource, VecAccuracy accuracy, Object... args) {
        return CachedModelManager.getInstance().loadModel(resource, accuracy, args);
    }

    public static void compact(IModelNGT model) {
        CachedModelManager.getInstance().compactModel(model);
    }

    public static boolean prepare(IModelNGT model) {
        return CachedModelManager.getInstance().prepareModel(model);
    }

    public static boolean prepareSync(IModelNGT model) {
        return CachedModelManager.getInstance().prepareModelSync(model);
    }

    public static List<String> getDebugLines() {
        return CachedModelManager.getInstance().getDebugLines();
    }
}
