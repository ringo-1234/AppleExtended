package jp.apple.log;

import jp.apple.config.AppleConfig;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockLogHandler {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (!AppleConfig.enableBlockChangeLog) return;
        if (!event.getWorld().isRemote) {
            AppleLogger.logBlockChange(
                    event.getPlayer(),
                    event.getPos(),
                    event.getPlacedBlock(),
                    "PLACE"
            );
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!AppleConfig.enableBlockChangeLog) return;
        if (!event.getWorld().isRemote) {
            AppleLogger.logBlockChange(
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    "BREAK"
            );
        }
    }
}