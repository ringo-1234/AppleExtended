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

package jp.ngt.rtm.entity;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.RTMBlock;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.item.ItemAmmunition.BulletType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * 参考 : 初速 拳銃:340m/s, 徹甲弾:1800m/s ･･･ここでの処理とは関係ない
 */
public class EntityBullet extends EntityArrow {
    private static final DataParameter<Byte> BREAKABLE = EntityDataManager.<Byte>createKey(EntityBullet.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> AMMO_TYPE = EntityDataManager.<Byte>createKey(EntityBullet.class, DataSerializers.BYTE);

    private static final int LIFE_ON_GROUND = 400;
    private static final int LIFE_IN_AIR = 1200;

    private BulletType type;

    private int tileX = -1;
    private int tileY = -1;
    private int tileZ = -1;
    private Block landingBlock;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;

    public EntityBullet(World world) {
        super(world);
        this.setBulletSize();
        if (world.isRemote) {
            this.setRenderDistanceWeight(10.0D);
        }
    }

    //EntityArtillery
    public EntityBullet(World world, Entity shooter, BulletType type, double x, double y, double z, double mX, double mY, double mZ) {
        this(world);
        this.shootingEntity = shooter;
        this.setBulletType(type);
        this.setPosition(x, y, z);

        float speed = (type == BulletType.rocket) ? 1.25F : 10.0F;
        this.motionX = mX;
        this.motionY = mY;
        this.motionZ = mZ;
        this.shoot(this.motionX, this.motionY, this.motionZ, speed, 0.0F);
    }

    //ItemGun
    public EntityBullet(World world, EntityLivingBase shooter, float speed, BulletType type) {
        this(world);
        this.shootingEntity = shooter;
        this.setBulletType(type);

        this.setLocationAndAngles(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        float yawRad = NGTMath.toRadians(this.rotationYaw);
        float pitchRad = NGTMath.toRadians(this.rotationPitch);
        this.motionX = (double) (-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad));
        this.motionZ = (double) (MathHelper.cos(yawRad) * MathHelper.cos(pitchRad));
        this.motionY = (double) (-MathHelper.sin(pitchRad));
        this.shoot(this.motionX, this.motionY, this.motionZ, speed, 1.0F);
    }

    //ItemGun(NPC)
    public EntityBullet(World world, EntityLivingBase shooter, EntityLivingBase target, float speed, BulletType type) {
        this(world);
        this.shootingEntity = shooter;
        this.setBulletType(type);

        this.posY = shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D;
        double d0 = target.posX - shooter.posX;
        double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - this.posY;
        double d2 = target.posZ - shooter.posZ;
        double distance = NGTMath.firstSqrt(d0 * d0 + d2 * d2);

        if (distance >= 1.0E-7D) {
            float f2 = (float) (NGTMath.toDegrees(Math.atan2(d2, d0))) - 90.0F;
            float f3 = (float) (-(NGTMath.toDegrees(Math.atan2(d1, distance))));
            double d4 = d0 / distance;
            double d5 = d2 / distance;
            this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f2, f3);
            this.shoot(d0, d1, d2, speed, 1.0F);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(BREAKABLE, Byte.valueOf((byte) 0));
        this.getDataManager().register(AMMO_TYPE, Byte.valueOf((byte) -1));
    }

    //保存しないようにしようとしたが、無理だった
    /*@Override
    public boolean writeToNBTOptional(NBTTagCompound nbt)
    {
    	return false;
    }*/

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("xTile", (short) this.tileX);
        nbt.setShort("yTile", (short) this.tileY);
        nbt.setShort("zTile", (short) this.tileZ);
        nbt.setShort("life", (short) this.ticksInGround);
        nbt.setByte("inTile", (byte) Block.getIdFromBlock(this.landingBlock));
        nbt.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        nbt.setByte("canBreak", (byte) (this.getCanBreakBlock() ? 1 : 0));
        nbt.setByte("bulletType", this.getBulletType().id);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.tileX = nbt.getShort("xTile");
        this.tileY = nbt.getShort("yTile");
        this.tileZ = nbt.getShort("zTile");
        this.ticksInGround = nbt.getShort("life");
        this.landingBlock = Block.getBlockById(nbt.getByte("inTile") & 255);
        this.inGround = nbt.getByte("inGround") == 1;
        this.setCanBreakBlock(nbt.getByte("canBreak") == 1);
        this.setBulletType(BulletType.getBulletType(nbt.getByte("bulletType")));
    }

    private void setBulletSize() {
        if (this.getBulletType() == BulletType.cannon_40cm) {
            this.setSize(0.5F, 0.5F);
        } else {
            this.setSize(0.05F, 0.05F);
        }
    }

    @Override
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
        double f2 = Math.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        double d0 = (double) par7 / f2;
        par1 *= d0;
        par3 *= d0;
        par5 *= d0;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        float f3 = (float) NGTMath.firstSqrt(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float) (NGTMath.toDegrees(Math.atan2(par1, par5)));
        this.prevRotationPitch = this.rotationPitch = (float) (NGTMath.toDegrees(Math.atan2(par3, f3)));
        this.ticksInGround = 0;

        if (this.shootingEntity != null) {
            double recoilCoe = 0.01D;
            this.shootingEntity.motionX -= this.motionX * recoilCoe;
            this.shootingEntity.motionY -= this.motionY * recoilCoe;
            this.shootingEntity.motionZ -= this.motionZ * recoilCoe;
            this.shootingEntity.velocityChanged = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double par1, double par3, double par5, float par7, float par8, int par9, boolean par10) {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double par1, double par3, double par5) {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = (float) NGTMath.firstSqrt(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float) (NGTMath.toDegrees(Math.atan2(par1, par5)));
            this.prevRotationPitch = this.rotationPitch = (float) (NGTMath.toDegrees(Math.atan2(par3, (double) f)));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        this.onEntityUpdate();

        if (this.world.isRemote) {
            this.updateClient();
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = (float) NGTMath.firstSqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (NGTMath.toDegrees(Math.atan2(this.motionX, this.motionZ)));
            this.prevRotationPitch = this.rotationPitch = (float) (NGTMath.toDegrees(Math.atan2(this.motionY, (double) f)));
        }

        BlockPos pos = new BlockPos(this.tileX, this.tileY, this.tileZ);
        IBlockState state = this.world.getBlockState(pos);
        if (state.getMaterial() != Material.AIR) {
            //state.getBlock().setBlockBoundsBasedOnState(this.worldObj, pos);
            AxisAlignedBB aabb = state.getCollisionBoundingBox(this.world, pos);
            if (aabb != null && aabb.contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.inGround) {
            if (state.getBlock() == this.landingBlock) {
                ++this.ticksInGround;
                if (!this.world.isRemote && this.ticksInGround >= LIFE_ON_GROUND) {
                    this.setDead();
                }
                this.onLanding(this.tileX, this.tileY, this.tileZ);
            } else {
                this.inGround = false;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;
            if (!this.world.isRemote && this.ticksInAir >= LIFE_IN_AIR) {
                this.setDead();
            }

            Vec3d vecPos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult mop = this.world.rayTraceBlocks(vecPos, vec3, false, true, false);
            vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (mop != null)//ブロックをすり抜けないように
            {
                vec3 = new Vec3d(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z);
            }

            Entity hitEntity = null;
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this,
                    this.getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            if (!list.isEmpty()) {
                Vec3d vecPos2 = new Vec3d(this.posX, this.posY, this.posZ);
                double d0 = 0.0D;
                for (Entity entity : list) {
                    boolean flag = true;
                    if (entity.equals(this.shootingEntity))//撃った人には当たらないように
                    {
                        flag = false;
                    }

                    if (entity.canBeCollidedWith() && flag) {
                        //double dis0 = vecPos2.distanceTo(vec3);
                        //double dis1 = this.getDistanceToEntity(entity);
                        //NGTLog.debug("vec:%S, entity:%S", new Object[]{dis0, dis1});
                        AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(0.5D, 0.5D, 0.5D);
                        RayTraceResult mop1 = aabb.calculateIntercept(vecPos2, vec3);
                        if (mop1 != null) {
                            double d1 = vecPos2.distanceTo(mop1.hitVec);
                            if (d1 < d0 || d0 == 0.0D) {
                                hitEntity = entity;
                                d0 = d1;
                            }
                        }
                    }
                }
            }

            if (hitEntity != null) {
                mop = new RayTraceResult(hitEntity);
            	/*if(hitEntity instanceof EntityPlayer)
            	{
            		EntityPlayer player = (EntityPlayer)hitEntity;
            		if(!player.capabilities.disableDamage && player != this.shootingEntity)
            		{
            			mop = new MovingObjectPosition(player);
            		}
            	}
            	else
            	{
            		mop = new MovingObjectPosition(hitEntity);
            	}*/
            }

            boolean hitBlock = false;
            if (mop != null) {
                if (mop.entityHit != null) {
                    this.onHitEntity(mop.entityHit);
                } else {
                    hitBlock = true;
                    this.tileX = mop.getBlockPos().getX();
                    this.tileY = mop.getBlockPos().getY();
                    this.tileZ = mop.getBlockPos().getZ();
                    this.landingBlock = state.getBlock();
                    /*this.motionX = (double)((float)(mop.hitVec.xCoord - this.posX));
                    this.motionY = (double)((float)(mop.hitVec.yCoord - this.posY));
                    this.motionZ = (double)((float)(mop.hitVec.zCoord - this.posZ));*/
                    /*float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    double d0 = 0.05000000074505806D;
                    this.posX -= this.motionX / (double)f2 * d0;
                    this.posY -= this.motionY / (double)f2 * d0;
                    this.posZ -= this.motionZ / (double)f2 * d0;*/
                    //this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;

                    if (this.landingBlock != Blocks.AIR) {
                        this.landingBlock.onEntityCollidedWithBlock(this.world, pos, state, this);
                    }

                    //this.onLanding(this.tileX, this.tileY, this.tileZ);
                }
            }

            if (hitBlock) {
                this.posX = mop.hitVec.x;
                this.posY = mop.hitVec.y;
                this.posZ = mop.hitVec.z;
            } else {
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            }

            float f2 = (float) NGTMath.firstSqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (NGTMath.toDegrees(Math.atan2(this.motionX, this.motionZ)));
            this.rotationPitch = (float) NGTMath.toDegrees(Math.atan2(this.motionY, (double) f2));

            this.prevRotationYaw = MathHelper.wrapDegrees(this.prevRotationYaw);
            this.prevRotationPitch = MathHelper.wrapDegrees(this.prevRotationPitch);

            this.rotationPitch = this.prevRotationPitch + MathHelper.wrapDegrees(this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw) * 0.2F;

            double d3 = 0.999D;
            if (this.isInWater()) {
                for (int l = 0; l < 4; ++l) {
                    double d4 = 0.25F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * d4, this.posY - this.motionY * d4, this.posZ - this.motionZ * d4, this.motionX, this.motionY, this.motionZ);
                }
                d3 = 0.9D;
            }

            BulletType type = this.getBulletType();
            if (type == BulletType.cannon_40cm || type == BulletType.cannon_Atomic) {
                this.motionX *= d3;
                this.motionZ *= d3;
                this.motionY -= 0.025D;
            } else if (type == BulletType.rocket)//X,Zは加速,Yはゆるく落下
            {
                double len = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                double accXZ = 0.125D * (1.0D - (len / 10.0D));
                double accY = -0.01D;
                //加速:速度10に達するまで x 向き x XZ成分抽出
                this.motionX += accXZ * (this.motionX >= 0.0D ? 1.0D : -1.0D) * (Math.abs(this.motionX) / len);
                this.motionZ += accXZ * (this.motionZ >= 0.0D ? 1.0D : -1.0D) * (Math.abs(this.motionZ) / len);
                this.motionY += accY;
            } else {
                this.motionX *= d3;
                this.motionY *= d3;
                this.motionZ *= d3;
                this.motionY -= 0.001D;
            }
            this.setPosition(this.posX, this.posY, this.posZ);

            this.doBlockCollisions();
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateClient() {
        if (this.getBulletType() == BulletType.rocket) {
            //離れてるときパーティクル描画しないように
            Entity viewer = NGTUtilClient.getMinecraft().getRenderViewEntity();
            double range = NGTUtil.getChunkLoadDistance() - 32.0D;
            if (viewer != null && viewer.getDistanceSq(this) > range * range) {
                return;
            }

            int count = 15;
            Random random = this.getEntityWorld().rand;
            for (int j = 0; j < count; ++j) {
                double d0 = this.posX + (random.nextDouble() * -this.motionX);
                double d1 = this.posY + (random.nextDouble() * -this.motionY);
                double d2 = this.posZ + (random.nextDouble() * -this.motionZ);
                double dece = 0.5D;
                double vari = 0.01D;
                double vx = -this.motionX * ((random.nextDouble() * 0.5D + 0.5D) * dece)
                        + random.nextGaussian() * vari;
                double vy = -this.motionY * ((random.nextDouble() * 0.5D + 0.5D) * dece)
                        + random.nextGaussian() * vari;
                double vz = -this.motionZ * ((random.nextDouble() * 0.5D + 0.5D) * dece)
                        + random.nextGaussian() * vari;
                this.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, d0, d1, d2, vx, vy, vz);
            }
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    public BulletType getBulletType() {
        if (this.type == null) {
            byte type = this.getDataManager().get(AMMO_TYPE);
            if (type < 0) {
                return BulletType.handgun_9mm;
            }
            this.type = BulletType.getBulletType(type);
        }
        return this.type;
    }

    public void setBulletType(BulletType par1) {
        this.getDataManager().set(AMMO_TYPE, Byte.valueOf(par1.id));
    }

    public void setCanBreakBlock(boolean par1) {
        byte b0 = this.getDataManager().get(BREAKABLE);
        byte b1 = (byte) (par1 ? (b0 & -2) : (b0 | 1));
        this.getDataManager().set(BREAKABLE, Byte.valueOf(b1));
    }

    public boolean getCanBreakBlock() {
        byte b0 = this.getDataManager().get(BREAKABLE);
        return (b0 & 1) == 0;
    }

    protected void onHitEntity(Entity entity) {
        if (entity.equals(this.shootingEntity)) {
            return;
        }

        //entity.setFire(5);
        float damage = this.getBulletType().damage;
        if (entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), damage)) {
            //this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        }

        if (!this.world.isRemote) {
            if (type == BulletType.cannon_40cm || type == BulletType.rocket) {
                boolean doMobGriefing = this.world.getGameRules().getBoolean("mobGriefing");
                this.world.newExplosion(this, entity.posX, entity.posY, entity.posZ, 12.0F, false, doMobGriefing);
            }
            this.setDead();
        }
    }

    /**
     * 着弾時のアクション
     */
    protected void onLanding(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = this.getEntityWorld().getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        boolean doMobGriefing = this.world.getGameRules().getBoolean("mobGriefing");
        BulletType type = this.getBulletType();

        if (type == BulletType.cannon_40cm || type == BulletType.rocket) {
            if (!this.world.isRemote) {
                float hardness = state.getBlockHardness(this.world, pos);
                if (block != Blocks.AIR && doMobGriefing) {
                    if (hardness >= 0.0F && hardness < 500.0F) {
                        this.world.setBlockToAir(pos);
                    }
                    this.world.newExplosion(this, this.posX, this.posY, this.posZ, 12.0F, false, doMobGriefing);
                }
                this.setDead();
            }
        } else if (type == BulletType.cannon_Atomic) {
            if (!this.world.isRemote) {
                float hardness = state.getBlockHardness(this.world, pos);
                if (block != Blocks.AIR && hardness > 0.0F && hardness < 500.0F) ;
                {
                    BlockUtil.setBlock(this.getEntityWorld(), x, y, z, RTMBlock.effect, RTMCore.ATOMIC_BOM_META, 3);
                    this.getEntityWorld().playSound(this.posX, this.posY, this.posZ,
                            SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 10.0F, 1.0F, false);
                }
                this.setDead();
            }
        } else {
            if (this.landingBlock != null && this.getCanBreakBlock()) {
                if (!RTMCore.gunBreakBlock) {
                    BlockUtil.playBlockBreakSound(this.getEntityWorld(), pos, this.landingBlock, meta);
                } else if (this.isBreakableBlock(this.getBulletType(), state, x, y, z, doMobGriefing)) {
                    NGTCore.proxy.breakBlock(this.world, x, y, z, meta);
                    if (!this.world.isRemote) {
                        this.world.setBlockToAir(pos);//world.func_147480_a()
                        this.setDead();
                    }
                } else if (this.landingBlock == Blocks.TNT) {
                    if (!this.world.isRemote) {
                        ((BlockTNT) this.landingBlock).explode(this.world, pos, block.getStateFromMeta(1), null);
                        this.world.setBlockToAir(pos);
                        this.setDead();
                    }
                } else {
                    BlockUtil.playBlockBreakSound(this.getEntityWorld(), pos, this.landingBlock, meta);
                }
            }
        }

        this.setCanBreakBlock(false);
    }

    protected boolean isBreakableBlock(BulletType bullet, IBlockState block, int x, int y, int z, boolean doMobGriefing) {
        Material mat = block.getMaterial();
        if (bullet == BulletType.handgun_9mm) {
            if (mat == Material.LEAVES || mat == Material.PLANTS
                    || mat == Material.VINE || mat == Material.CIRCUITS || mat == Material.CARPET
                    || mat == Material.GLASS || mat == Material.REDSTONE_LIGHT || mat == Material.CORAL
                    || mat == Material.SNOW
                    || mat == Material.CRAFTED_SNOW || mat == Material.CACTUS || mat == Material.CAKE) {
                return doMobGriefing;
            }
        } else if (bullet == BulletType.rifle_12_7mm) {
            if (block.getBlockHardness(this.world, new BlockPos(x, y, z)) < 5.0F && block.getBlock().getExplosionResistance(this) <= 6.0F) {
                return doMobGriefing;
            }
        } else {
            if (mat == Material.WOOD || mat == Material.LEAVES || mat == Material.PLANTS
                    || mat == Material.VINE || mat == Material.CIRCUITS || mat == Material.CARPET
                    || mat == Material.GLASS || mat == Material.REDSTONE_LIGHT || mat == Material.CORAL
                    || mat == Material.ICE || mat == Material.PACKED_ICE || mat == Material.SNOW
                    || mat == Material.CRAFTED_SNOW || mat == Material.CACTUS || mat == Material.CAKE) {
                return doMobGriefing;
            }
        }
        return false;
    }

    @Override
    protected ItemStack getArrowStack() {
        return null;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}