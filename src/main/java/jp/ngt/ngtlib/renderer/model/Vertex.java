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

package jp.ngt.ngtlib.renderer.model;

import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.math.PooledVec3;
import jp.ngt.ngtlib.math.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Vertex implements Comparable<Vertex> {
    public static Vertex create(Vec3 vec, VecAccuracy accuracy) {
        return create((float) vec.getX(), (float) vec.getY(), (float) vec.getZ(), accuracy);
    }

    public static Vertex create(float x, float y, float z, VecAccuracy accuracy) {
        switch (accuracy) {
            case LOW:
                return new VertexShort(x, y, z);
            case MEDIUM:
                return new VertexFloat(x, y, z);
            default:
                return new VertexFloat(x, y, z);
        }
    }

    public Vec3 toVec() {
        return PooledVec3.create(this.getX(), this.getY(), this.getZ());
    }

    public abstract float getX();

    public abstract float getY();

    public abstract float getZ();

    public abstract void setVec(float x, float y, float z);

    public Vertex add(Vertex vertex) {
        this.setVec(this.getX() + vertex.getX(), this.getY() + vertex.getY(), this.getZ() + vertex.getZ());
        return this;
    }

    public Vertex expand(float par1) {
        this.setVec(this.getX() * par1, this.getY() * par1, this.getZ() * par1);
        return this;
    }

    public Vertex copy(VecAccuracy par1) {
        return create(this.getX(), this.getY(), this.getZ(), par1);
    }

    private static final float ACCURACY = 0.0001F;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof Vertex) {
            Vertex v = (Vertex) object;
            //return this.getX() == v.getX() && this.getY() == v.getY() && this.getZ() == v.getZ();
            return Math.abs(this.getX() - v.getX()) < ACCURACY && Math.abs(this.getY() - v.getY()) < ACCURACY && Math.abs(this.getZ() - v.getZ()) < ACCURACY;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int ix = Float.floatToRawIntBits(this.getX());
        int iy = Float.floatToRawIntBits(this.getY());
        int iz = Float.floatToRawIntBits(this.getZ());
        return ((ix & 0xFF) << 16) | ((iy & 0xFF) << 8) | ((iz & 0xFF));
    }

    @Override
    public int compareTo(Vertex arg) {
        if (this == arg) {
            return 0;
        }

        float myF = 0.0F;
        float argF = 0.0F;

        if (this.getX() != arg.getX()) {
            myF = this.getX();
            argF = arg.getX();
        } else if (this.getY() != arg.getY()) {
            myF = this.getY();
            argF = arg.getY();
        } else if (this.getZ() != arg.getZ()) {
            myF = this.getZ();
            argF = arg.getZ();
        } else {
            return 0;//x,y,zすべて同じ
        }

        return (int) (myF * 10.0F - argF * 10.0F);
    }

    private static final class VertexFloat extends Vertex {
        private float x, y, z;

        public VertexFloat(float x, float y, float z) {
            this.setVec(x, y, z);
        }

        @Override
        public float getX() {
            return this.x;
        }

        @Override
        public float getY() {
            return this.y;
        }

        @Override
        public float getZ() {
            return this.z;
        }

        @Override
        public void setVec(float p1, float p2, float p3) {
            this.x = p1;
            this.y = p2;
            this.z = p3;
        }
    }

    /**
     * +-16.000の範囲まで
     */
    private static final class VertexShort extends Vertex {
        private short x, y, z;

        public VertexShort(float x, float y, float z) {
            this.setVec(x, y, z);
        }

        @Override
        public float getX() {
            return this.decode(this.x);
        }

        @Override
        public float getY() {
            return this.decode(this.y);
        }

        @Override
        public float getZ() {
            return this.decode(this.z);
        }

        @Override
        public void setVec(float p1, float p2, float p3) {
            this.x = this.encode(p1);
            this.y = this.encode(p2);
            this.z = this.encode(p3);
        }

        private short encode(float par1) {
            return (short) NGTMath.floor(par1 * 2000.0F);
        }

        private float decode(short par1) {
            return (float) par1 * 0.0005F;
        }
    }
}