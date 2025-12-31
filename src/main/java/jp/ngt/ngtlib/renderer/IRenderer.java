package jp.ngt.ngtlib.renderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderer
{
	void startDrawing(int par1);

	int draw();

	void addVertexWithUV(float x, float y, float z, float u, float v);

	void setNormal(float x, float y, float z);

	void setBrightness(int par1);

	void setColor(int r, int g, int b, int a);
}