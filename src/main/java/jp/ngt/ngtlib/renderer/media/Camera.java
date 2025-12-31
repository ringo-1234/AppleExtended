package jp.ngt.ngtlib.renderer.media;

import java.awt.image.BufferedImage;

import javax.annotation.Nullable;

public class Camera extends ImageBase
{
    private int texId = -1;

    private Camera(){}

    public static Camera create(int cameraId)
    {
        return new Camera();
    }

    @Override
    public @Nullable BufferedImage getImage()
    {
        return null;
    }

    @Override
    public int getTextureId()
    {
        return texId;
    }

    @Override
    public void exit() {}
}