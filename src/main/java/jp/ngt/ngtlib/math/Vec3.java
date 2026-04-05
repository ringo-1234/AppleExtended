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

package jp.ngt.ngtlib.math;

import net.minecraft.util.math.MathHelper;

/**
 * 完全独立のVecクラス
 * S/C問わず使えるように
 * Scriptからの仕様も想定
 */
public class Vec3 {
    public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);

    private double x;
    private double y;
    private double z;

    public Vec3(double x, double y, double z) {
        this.set(x, y, z);
    }

    protected void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double length() {
        //ポリゴンスムージングには精度が低い
        //return NGTMath.firstSqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
        if (!(this.getX() == 0.0D && this.getY() == 0.0D && this.getZ() == 0.0D)) {
            return Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
        }
        return 0.0D;
    }

    public double lengthSq(double px, double py, double pz) {
        double dx = px - this.getX();
        double dy = py - this.getY();
        double dz = pz - this.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * @param par1 度
     */
    public Vec3 rotateAroundX(float par1) {
        float rad = NGTMath.toRadians(par1);
        float cos = MathHelper.cos(rad);
        float sin = MathHelper.sin(rad);
        double d0 = this.x;
        double d1 = this.y * (double) cos + this.z * (double) sin;
        double d2 = this.z * (double) cos - this.y * (double) sin;
        return new Vec3(d0, d1, d2);
    }

    /**
     * @param par1 度
     */
    public Vec3 rotateAroundY(float par1) {
        float rad = NGTMath.toRadians(par1);
        float cos = MathHelper.cos(rad);
        float sin = MathHelper.sin(rad);
        double d0 = this.x * (double) cos + this.z * (double) sin;
        double d1 = this.y;
        double d2 = this.z * (double) cos - this.x * (double) sin;
        return new Vec3(d0, d1, d2);
    }

    /**
     * @param par1 度
     */
    public Vec3 rotateAroundZ(float par1) {
        float rad = NGTMath.toRadians(par1);
        float cos = MathHelper.cos(rad);
        float sin = MathHelper.sin(rad);
        double d0 = this.x * (double) cos + this.y * (double) sin;
        double d1 = this.y * (double) cos - this.x * (double) sin;
        double d2 = this.z;
        return new Vec3(d0, d1, d2);
    }

    //ロドリゲスの回転公式
    public Vec3 rotateAroundVec(Vec3 axis, float rotation) {
        float rad = NGTMath.toRadians(rotation);
        float sin = MathHelper.sin(rad);
        float cos = MathHelper.cos(rad);
        float ncos = 1.0F - cos;
        double x0 = this.x * (cos + (axis.x * axis.x) * ncos) + this.y * ((axis.x * axis.y) * ncos - axis.z * sin) + this.z * ((axis.z * axis.x) * ncos + axis.y * sin);
        double y0 = this.x * ((axis.x * axis.y) * ncos + axis.z * sin) + this.y * (cos + (axis.y * axis.y) * ncos) + this.z * ((axis.y * axis.z) * ncos - axis.x * sin);
        double z0 = this.x * ((axis.z * axis.x) * ncos - axis.y * sin) + this.y * ((axis.y * axis.z) * ncos + axis.x * sin) + this.z * (cos + (axis.z * axis.z) * ncos);
        return new Vec3(x0, y0, z0);
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public Vec3 add(Vec3 vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vec3 sub(Vec3 vec) {
        return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
    }

    public Vec3 multi(double num) {
        return new Vec3(this.getX() * num, this.getY() * num, this.getZ() * num);
    }

    /**
     * 外積
     */
    public Vec3 crossProduct(Vec3 par1) {
        return new Vec3(
                this.y * par1.z - this.z * par1.y,
                this.z * par1.x - this.x * par1.z,
                this.x * par1.y - this.y * par1.x
        );
    }

    /**
     * 内積
     */
    public double dotProduct(Vec3 vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public Vec3 normalize() {
        double len = this.length();
        if (len > 0.0D) {
            double d1 = 1.0D / len;
            return new Vec3(this.x * d1, this.y * d1, this.z * d1);
        }
        return ZERO;
    }

    /**
     * 度
     */
    public float getYaw() {
        return (float) NGTMath.toDegrees(Math.atan2(this.x, this.z));
    }

    /**
     * 度
     */
    public float getPitch() {
        double xz = Math.sqrt(this.x * this.x + this.z * this.z);
        return (float) NGTMath.toDegrees(Math.atan2(this.y, xz));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3) {
            Vec3 vec = (Vec3) obj;
            return vec.getX() == this.getX() && vec.getY() == this.getY() && vec.getZ() == this.getZ();
        }
        return false;
    }

    /**
     * @return ラジアン(0.0~PI)
     *
     */
    public double getAngle(Vec3 vec) {
        return Math.acos(this.getAngleCos(vec));
    }

    public double getAngleCos(Vec3 vec) {
        double len2 = this.length() * vec.length();
        if (len2 > 0.0D) {
            double d0 = this.dotProduct(vec) / len2;
            return d0 > 1.0D ? 1.0D : d0;
        }
        return 1.0D;
    }

    /**
     * @return 度(-180~180)、vecはnormalに対し反時計回り
     *
     */
    public float getAngle360(Vec3 vec, Vec3 normal) {
        double angle = NGTMath.toDegrees(getAngle(vec));
        Vec3 cross = this.crossProduct(vec);
        double dot = cross.dotProduct(normal);
        return NGTMath.wrapAngle((float) angle) * (dot >= 0.0D ? 1.0F : -1.0F);
    }
}