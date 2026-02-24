package jp.apple.log;

import jp.apple.util.AppleDir;
import jp.apple.config.AppleConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppleLogger {
    private static File currentLogFile;
    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void init() {
        if (!AppleConfig.enableBlockChangeLog) return;
        if (AppleDir.appleLibDir == null) return;

        // AppleLib/log フォルダを作成
        File logDir = new File(AppleDir.appleLibDir, "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        String fileName = "blockchange-" + fileDateFormat.format(new Date()) + ".log";
        currentLogFile = new File(logDir, fileName);
    }

    public static synchronized void logBlockChange(EntityPlayer player, BlockPos pos, IBlockState state, String action) {
        if (currentLogFile == null) return;

        String time = logDateFormat.format(new Date());
        String username = player.getName();
        String blockName = state.getBlock().getRegistryName() != null ? state.getBlock().getRegistryName().toString() : "unknown";
        String coords = String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ());
        String line = String.format("[%s] %s %s %s %s", time, username, action, blockName, coords);

        try (PrintWriter out = new PrintWriter(new FileWriter(currentLogFile, true))) {
            out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}