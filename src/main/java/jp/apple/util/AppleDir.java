package jp.apple.util;

import java.io.File;

public class AppleDir {

    public static File appleLibDir;

    public static void init(File mcRoot) {
        appleLibDir = new File(mcRoot, "AppleLib");
        if (!appleLibDir.exists()) {
            appleLibDir.mkdirs();
        }
    }
}
