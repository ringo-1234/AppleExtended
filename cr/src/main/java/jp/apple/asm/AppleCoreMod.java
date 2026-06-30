package jp.apple.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.anatawa12.fixRtm.asm.", "com.anatawa12.fixRtm.libs.", "jp.apple.asm."})
public class AppleCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"jp.apple.asm.WorldClientTransformer"};
    }
    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }
}
