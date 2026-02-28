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

package jp.ngt.ngtlib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemArgHolderBase<T extends ItemArgHolderBase> {
    private World world;
    private BlockPos pos;
    private EntityPlayer player;
    private EnumFacing facing;

    public World getWorld() {
        return this.world;
    }

    public T setWorld(World par1) {
        this.world = par1;
        return (T) this;
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public T setBlockPos(BlockPos par1) {
        this.pos = par1;
        return (T) this;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public T setPlayer(EntityPlayer par1) {
        this.player = par1;
        return (T) this;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public T setFacing(EnumFacing par1) {
        this.facing = par1;
        return (T) this;
    }

    public static class ItemArgHolder extends ItemArgHolderBase<ItemArgHolder> {
        private ItemStack itemStack;
        private EnumHand hand;

        public ItemStack getItemStack() {
            if (this.itemStack == null) {
                this.itemStack = this.getPlayer().getHeldItem(this.getHand());
            }
            return this.itemStack;
        }

        public ItemArgHolder setItemStack(ItemStack par1) {
            this.itemStack = par1;
            return this;
        }

        public EnumHand getHand() {
            return this.hand;
        }

        public ItemArgHolder setHand(EnumHand par1) {
            this.hand = par1;
            return this;
        }

        public ActionResult<ItemStack> pass() {
            return new ActionResult(EnumActionResult.PASS, this.getItemStack());
        }

        public ActionResult<ItemStack> success() {
            return new ActionResult(EnumActionResult.SUCCESS, this.getItemStack());
        }

        public ActionResult<ItemStack> fail() {
            return new ActionResult(EnumActionResult.FAIL, this.getItemStack());
        }
    }
}