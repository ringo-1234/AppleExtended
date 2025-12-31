package jp.ngt.ngtlib.renderer.media;

import jp.ngt.ngtlib.renderer.NGTTessellator;
import jp.ngt.ngtlib.util.NGTUtilClient;

public class TweetState extends MediaBase
{
    private final String keyword;

    public TweetState(String par1)
    {
        this.keyword = par1;
    }

    public void update() {}
    public boolean isValid() { return false; }
    public String getUserName() { return ""; }
    public String getText() { return ""; }

    @Override
    public void render(float width, float height, boolean fitAspectRatio)
    {
        // 空実装
    }

    @Override
    public void exit() {}
}