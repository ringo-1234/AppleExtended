package jp.ngt.rtm.entity.npc;

import java.util.HashMap;
import java.util.Map;

import jp.ngt.rtm.entity.ai.EntityAIRangedAttackWithItem;
import jp.ngt.rtm.entity.ai.EntityAITravelByTrain;
import jp.ngt.rtm.item.ItemGun;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Role
{
	private static Map<String, Role> nameMap = new HashMap<String, Role>();

	public static final Role PASSENGER = new RolePassenger("passenger");
	public static final Role ATTENDANT = new RoleAttendant("attendant");
	public static final Role MANNEQUIN = new Role("mannequin");
	public static final Role GUARD = new RoleGuard("guard");
	public static final Role MOTORMAN = new Role("motorman");
	public static final Role SALESPERSON = new RoleSalesperson("salesperson");
	public static final Role BUYER = new RoleSalesperson("buyer");

	public Role(String name)
	{
		nameMap.put(name, this);
	}

	public static Role getRole(String name)
	{
		return nameMap.containsKey(name) ? nameMap.get(name) : MANNEQUIN;
	}

	public void init(EntityNPC entity)
	{
		entity.getNavigator().clearPath();
		entity.setAttackTarget(null);
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}

	public void onInventoryChanged(EntityNPC entity){}

	//子クラス//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static class RolePassenger extends Role
	{
		public RolePassenger(String name)
		{
			super(name);
		}

		@Override
		public void init(EntityNPC entity)
		{
			super.init(entity);
			entity.tasks.addTask(1, new EntityAISwimming(entity));
			entity.tasks.addTask(2, new EntityAITravelByTrain(entity, EntityNPC.SPEED));
			entity.tasks.addTask(3, new EntityAIWander(entity, EntityNPC.SPEED));
			//entity.tasks.addTask(3, new EntityAIWanderAvoidWater(entity, EntityNPC.SPEED));
			entity.tasks.addTask(4, new EntityAIWatchClosest(entity, EntityPlayer.class, 4.0F));
			entity.tasks.addTask(5, new EntityAILookIdle(entity));
		}
	}

	public static class RoleAttendant extends Role
	{
		public RoleAttendant(String name)
		{
			super(name);
		}

		@Override
		public void init(EntityNPC entity)
		{
			super.init(entity);
			entity.tasks.addTask(1, new EntityAISwimming(entity));
			entity.tasks.addTask(2, new EntityAIWander(entity, EntityNPC.SPEED));//デバッグ向けに入れた
			entity.tasks.addTask(3, new EntityAIWatchClosest(entity, EntityPlayer.class, 4.0F));
			entity.tasks.addTask(4, new EntityAILookIdle(entity));
		}
	}

	public static class RoleGuard extends Role
	{
		private EntityAIBase aiRangedAttack;
		private EntityAIBase aiLeapAtTarget;
		private EntityAIBase aiCollideAttack;

		public RoleGuard(String name)
		{
			super(name);
		}

		@Override
		public void init(EntityNPC entity)
		{
			this.aiRangedAttack = new EntityAIRangedAttackWithItem(entity, EntityNPC.SPEED * 1.5F, 20, 30, 20.0F);
			this.aiLeapAtTarget = new EntityAILeapAtTarget(entity, EntityNPC.SPEED);
			this.aiCollideAttack = new EntityAIAttackMelee(entity, EntityNPC.SPEED * 1.5F, true);

			super.init(entity);
			entity.tasks.addTask(1, new EntityAISwimming(entity));
			//
			entity.tasks.addTask(4, new EntityAIWander(entity, EntityNPC.SPEED));
			entity.tasks.addTask(5, new EntityAIWatchClosest(entity, EntityPlayer.class, 4.0F));
			entity.tasks.addTask(6, new EntityAILookIdle(entity));
			entity.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(entity));
			entity.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(entity));
			entity.targetTasks.addTask(3, new EntityAIHurtByTarget(entity, true));
			entity.targetTasks.addTask(4, new EntityAINearestAttackableTarget(entity, EntityLiving.class, 0, false, false, IMob.MOB_SELECTOR));
		}

		@Override
		public void onInventoryChanged(EntityNPC entity)
		{
			entity.tasks.removeTask(this.aiRangedAttack);
			entity.tasks.removeTask(this.aiLeapAtTarget);
			entity.tasks.removeTask(this.aiCollideAttack);

			ItemStack item = entity.getHeldItem();
			if(item != null && item.getItem() instanceof ItemGun)
			{
				entity.tasks.addTask(3, this.aiRangedAttack);
			}
			else
			{
				entity.tasks.addTask(2, this.aiLeapAtTarget);
				entity.tasks.addTask(3, this.aiCollideAttack);
			}
		}
	}

	public static class RoleSalesperson extends Role
	{
		public RoleSalesperson(String name)
		{
			super(name);
		}

		@Override
		public void init(EntityNPC entity)
		{
			super.init(entity);
			//entity.tasks.addTask(1, new EntityAISwimming(entity));
			//entity.tasks.addTask(2, new EntityAIWander(entity, EntityNPC.SPEED));
			entity.tasks.addTask(1, new EntityAIWatchClosest(entity, EntityPlayer.class, 4.0F));
			entity.tasks.addTask(3, new EntityAILookIdle(entity));
		}
	}
}