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

package jp.apple.proxy;

import jp.apple.highlight.BlockHighlightHandler;
import jp.apple.highlight.HighlightEntry;
import jp.apple.highlight.HighlightItemChecker;
import jp.apple.highlight.HighlightRegistry;
import jp.apple.reloader.ReloadGuiHandler;
import jp.apple.render.item.CustomIconModelHandler;
import jp.apple.train.PlayerCameraTrain;
import jp.apple.train.SoundBlocker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ReloadGuiHandler());
        MinecraftForge.EVENT_BUS.register(new SoundBlocker());
        MinecraftForge.EVENT_BUS.register(new BlockHighlightHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerCameraTrain());
        MinecraftForge.EVENT_BUS.register(new CustomIconModelHandler());

        HighlightRegistry.register( //試しにやってみたけど別に需要ないなと後から気づいたコネクター
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isConnectorItem(stack))
                        .block(block -> block instanceof jp.ngt.rtm.electric.BlockConnector)
                        .color(0.0F, 1.0F, 0.0F)
                        .build()
        );
        HighlightRegistry.register(
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isInsulatorItem(stack))
                        .block(block -> block instanceof jp.ngt.rtm.electric.BlockInsulator)
                        .color(0.0F, 1.0F, 0.0F)
                        .build()
        );
        HighlightRegistry.register(
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isCrossingItem(stack))
                        .block(block -> block instanceof jp.ngt.rtm.block.BlockCrossingGate)
                        .color(1.0F, 1.0F, 0.0F)  // 黄
                        .build()
        );
        HighlightRegistry.register(
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isLightItem(stack))
                        .block(block -> block instanceof jp.ngt.rtm.block.BlockLight)
                        .color(1.0F, 0.8F, 0.0F)  // オレンジ
                        .build()
        );
        HighlightRegistry.register(
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isMiniatureItem(stack))
                        .block(block -> block instanceof jp.ngt.mcte.block.BlockMiniature)
                        .color(0.5F, 0.0F, 1.0F)  // 紫
                        .build()
        );
        HighlightRegistry.register(
                HighlightEntry.builder()
                        .item(stack -> HighlightItemChecker.isItemBlockTrainModel(stack))
                        .block(block -> block instanceof jp.apple.block.BlockTrainModel)
                        .color(0.0F, 0.8F, 1.0F) //みずいろ
                        .build()
        );
    }

    @Override
    public void init(FMLInitializationEvent event) {
    }
}