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

package jp.ngt.mcte.item;

import jp.apple.log.AppleLogger;
import jp.ngt.mcte.MCTE;
import jp.ngt.mcte.block.MiniatureBlockState;
import jp.ngt.mcte.block.TileEntityMiniature;
import jp.ngt.ngtlib.block.BlockSet;
import jp.ngt.ngtlib.block.BlockUtil;
import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.item.ItemArgHolderBase.ItemArgHolder;
import jp.ngt.ngtlib.item.ItemCustom;
import jp.ngt.ngtlib.item.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemMiniature extends ItemCustom {
    private static float[] offset = new float[3];

    public ItemMiniature() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return armorType == EntityEquipmentSlot.HEAD || armorType == EntityEquipmentSlot.CHEST;
    }

    @Override
    protected ActionResult<ItemStack> onItemRightClick(ItemArgHolder holder) {
        if (holder.getWorld().isRemote) {
            holder.getPlayer().openGui(MCTE.instance, MCTE.guiIdItemMiniature, holder.getWorld(), 0, 0, 0);
        }
        return holder.success();
    }

    @Override
    protected ActionResult<ItemStack> onItemUse(ItemArgHolder holder, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = holder.getItemStack();
        World world = holder.getWorld();
        EntityPlayer player = holder.getPlayer();
        BlockPos pos = holder.getBlockPos();

        if (itemStack.hasTagCompound()) {
            if (!world.isRemote) {
                BlockPos newPos = ItemUtil.getPlacePos(pos, holder.getFacing());

                if (!player.canPlayerEdit(newPos, holder.getFacing(), itemStack)) {
                    return holder.success();
                }
                Block block = BlockUtil.getBlock(world, newPos);
                if (!block.isReplaceable(world, newPos)) {
                    return holder.success();
                }

                NBTTagCompound nbt = itemStack.getTagCompound();

                NGTObject object = getNGTObject(nbt);
                if (object != null) {
                    float scale = getScale(nbt);
                    float[] fa = getOffset(nbt);
                    MiniatureMode mode = getMode(nbt);
                    MiniatureBlockState state = getMiniatureBlockState(nbt, itemStack);

                    if (mode == MiniatureMode.original) {
                        this.setOriginalBlocks(player, world, newPos, object);
                    } else {
                        BlockUtil.setBlock(world, newPos, MCTE.miniature, 0, 3);
                        TileEntityMiniature tile = (TileEntityMiniature) world.getTileEntity(newPos);
                        tile.setRotation(player, MCTE.rotationInterval, false);
                        tile.attachSide = (byte) holder.getFacing().getIndex();
                        tile.setMBState(state);
                        tile.setBlockState(object, scale, fa[0], fa[1], fa[2], mode);

                    }

                    world.playSound((double) newPos.getX() + 0.5D, (double) newPos.getY() + 0.5D, (double) newPos.getZ() + 0.5D, SoundEvents.BLOCK_STONE_PLACE,
                            SoundCategory.BLOCKS, 1.0F, 1.0F, true);
                    itemStack.shrink(1);
                }
            }
        } else {
            if (world.isRemote) {
                player.openGui(MCTE.instance, MCTE.guiIdItemMiniature, player.world, 0, 0, 0);
            }
        }

        return holder.success();
    }

    private void setOriginalBlocks(EntityPlayer player, World world, BlockPos pos, NGTObject ngto) {
        for (int i = 0; i < ngto.xSize; ++i) {
            for (int j = 0; j < ngto.ySize; ++j) {
                for (int k = 0; k < ngto.zSize; ++k) {
                    BlockSet set = ngto.getBlockSet(i, j, k);
                    BlockPos targetPos = pos.add(i, j, k);
                    //ここからああ
                    AppleLogger.logBlockChange(
                            player,
                            targetPos,
                            set.toBlockState(),
                            "MCTE_DEPLOY"
                    );
                    //終わりいい
                    BlockUtil.setBlock(world, pos.add(i, j, k), set.block, set.metadata, 3);
                }
            }
        }
    }

    public static NGTObject getNGTObject(NBTTagCompound nbt) {
        if (nbt.hasKey("BlocksData")) {
            NBTTagCompound data = nbt.getCompoundTag("BlocksData");
            return NGTObject.readFromNBT(data);
        }
        return null;
    }

    public static void setNGTObject(NGTObject obj, NBTTagCompound nbt) {
        NBTTagCompound data = obj.writeToNBT();
        nbt.setTag("BlocksData", data);
    }

    public static float getScale(NBTTagCompound nbt) {
        if (nbt.hasKey("Scale")) {
            return nbt.getFloat("Scale");
        } else if (nbt.hasKey("MinimizeRate"))//互換性
        {
            int i = nbt.getInteger("MinimizeRate");
            if (i <= 0) {
                i = 1;
            }
            return 1.0F / (float) i;
        }
        return 1.0F;
    }

    public static void setScale(float scale, NBTTagCompound nbt) {
        nbt.setFloat("Scale", scale);
    }

    public static float[] getOffset(NBTTagCompound nbt) {
        offset[0] = nbt.getFloat("OffsetX");
        offset[1] = nbt.getFloat("OffsetY");
        offset[2] = nbt.getFloat("OffsetZ");
        return offset;
    }

    public static void setOffset(NBTTagCompound nbt, float x, float y, float z) {
        nbt.setFloat("OffsetX", x);
        nbt.setFloat("OffsetY", y);
        nbt.setFloat("OffsetZ", z);
    }

    public static MiniatureMode getMode(NBTTagCompound nbt) {
        int i = nbt.getByte("Mode");
        return MiniatureMode.values()[i];
    }

    public static void setMode(NBTTagCompound nbt, MiniatureMode mode) {
        nbt.setByte("Mode", (byte) mode.id);
    }

    public static MiniatureBlockState getMiniatureBlockState(NBTTagCompound nbt, ItemStack stack) {
        MiniatureBlockState state;
        if (nbt.hasKey("MBState")) {
            state = MiniatureBlockState.readFromNBT(nbt.getCompoundTag("MBState"), stack);
        } else {
            state = MiniatureBlockState.create(stack);
            state.lightValue = nbt.getByte("LightValue");
        }
        return state;
    }

    public static void setMiniatureBlockState(NBTTagCompound nbt, MiniatureBlockState state) {
        nbt.setTag("MBState", state.writeToNBT());
    }

    @Override
    protected void addInformation(ItemArgHolder holder, List<String> list, ITooltipFlag flag) {
        if (holder.getItemStack().hasTagCompound()) {
            NBTTagCompound nbt = holder.getItemStack().getTagCompound();
            NBTTagCompound data = nbt.getCompoundTag("BlocksData");
            float scale = getScale(nbt);
            NGTObject.addInformation(list, data, scale);
        }
    }

    /**
     * @param par1
     * @param par2 スケール
     */
    public static ItemStack createMiniatureItem(NGTObject par1, float par2, float x, float y, float z, MiniatureMode mode, MiniatureBlockState state) {
        ItemStack stack = new ItemStack(MCTE.itemMiniature, 1, 0);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("BlocksData", par1.writeToNBT());
        setScale(par2, nbt);
        setOffset(nbt, x, y, z);
        setMode(nbt, mode);
        setMiniatureBlockState(nbt, state);
        stack.setTagCompound(nbt);
        return stack;
    }

    /**
     * 武器として使用
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NGTObject ngto = this.getNGTObject(nbt);
            float scale = getScale(nbt);
            float power = (ngto.blockList.size() / 16) * scale;
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), power);
            //NGTLog.debug("at:%f", power);
        }
        return false;
    }

    private static int nextId;

    public enum MiniatureMode {
        miniature(),
        sculpture(),
        original();

        public final int id;

        private MiniatureMode() {
            this.id = nextId++;
        }
    }
}