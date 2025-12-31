package jp.ngt.ngtlib.renderer;

import java.util.Comparator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class QuadComparator implements Comparator
{
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private int[] buffer;

    public QuadComparator(int[] buf, float x, float y, float z)
    {
        this.buffer = buf;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public int compare(Integer par1, Integer par2)
    {
        float f = Float.intBitsToFloat(this.buffer[par1.intValue()]) - this.xCoord;
        float f1 = Float.intBitsToFloat(this.buffer[par1.intValue() + 1]) - this.yCoord;
        float f2 = Float.intBitsToFloat(this.buffer[par1.intValue() + 2]) - this.zCoord;
        float f3 = Float.intBitsToFloat(this.buffer[par1.intValue() + 8]) - this.xCoord;
        float f4 = Float.intBitsToFloat(this.buffer[par1.intValue() + 9]) - this.yCoord;
        float f5 = Float.intBitsToFloat(this.buffer[par1.intValue() + 10]) - this.zCoord;
        float f6 = Float.intBitsToFloat(this.buffer[par1.intValue() + 16]) - this.xCoord;
        float f7 = Float.intBitsToFloat(this.buffer[par1.intValue() + 17]) - this.yCoord;
        float f8 = Float.intBitsToFloat(this.buffer[par1.intValue() + 18]) - this.zCoord;
        float f9 = Float.intBitsToFloat(this.buffer[par1.intValue() + 24]) - this.xCoord;
        float f10 = Float.intBitsToFloat(this.buffer[par1.intValue() + 25]) - this.yCoord;
        float f11 = Float.intBitsToFloat(this.buffer[par1.intValue() + 26]) - this.zCoord;
        float f12 = Float.intBitsToFloat(this.buffer[par2.intValue()]) - this.xCoord;
        float f13 = Float.intBitsToFloat(this.buffer[par2.intValue() + 1]) - this.yCoord;
        float f14 = Float.intBitsToFloat(this.buffer[par2.intValue() + 2]) - this.zCoord;
        float f15 = Float.intBitsToFloat(this.buffer[par2.intValue() + 8]) - this.xCoord;
        float f16 = Float.intBitsToFloat(this.buffer[par2.intValue() + 9]) - this.yCoord;
        float f17 = Float.intBitsToFloat(this.buffer[par2.intValue() + 10]) - this.zCoord;
        float f18 = Float.intBitsToFloat(this.buffer[par2.intValue() + 16]) - this.xCoord;
        float f19 = Float.intBitsToFloat(this.buffer[par2.intValue() + 17]) - this.yCoord;
        float f20 = Float.intBitsToFloat(this.buffer[par2.intValue() + 18]) - this.zCoord;
        float f21 = Float.intBitsToFloat(this.buffer[par2.intValue() + 24]) - this.xCoord;
        float f22 = Float.intBitsToFloat(this.buffer[par2.intValue() + 25]) - this.yCoord;
        float f23 = Float.intBitsToFloat(this.buffer[par2.intValue() + 26]) - this.zCoord;
        float f24 = (f + f3 + f6 + f9) * 0.25F;
        float f25 = (f1 + f4 + f7 + f10) * 0.25F;
        float f26 = (f2 + f5 + f8 + f11) * 0.25F;
        float f27 = (f12 + f15 + f18 + f21) * 0.25F;
        float f28 = (f13 + f16 + f19 + f22) * 0.25F;
        float f29 = (f14 + f17 + f20 + f23) * 0.25F;
        float f30 = f24 * f24 + f25 * f25 + f26 * f26;
        float f31 = f27 * f27 + f28 * f28 + f29 * f29;
        return Float.compare(f31, f30);
    }

    @Override
    public int compare(Object par1, Object par2)
    {
        return this.compare((Integer)par1, (Integer)par2);
    }
}