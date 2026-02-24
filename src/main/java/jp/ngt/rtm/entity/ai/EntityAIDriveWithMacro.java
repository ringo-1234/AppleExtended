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

package jp.ngt.rtm.entity.ai;

import jp.ngt.rtm.entity.npc.EntityMotorman;
import jp.ngt.rtm.entity.npc.macro.MacroExecutor;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIDriveWithMacro extends EntityAIBase
{
	private final EntityMotorman motorman;
	private MacroExecutor executor;

	public EntityAIDriveWithMacro(EntityMotorman par1)
	{
		this.motorman = par1;
		this.setMutexBits(1);
	}

	public void setMacro(String[] args)
	{
		this.executor = new MacroExecutor(args);
	}

	@Override
	public boolean shouldExecute()
	{
		return this.motorman.getRidingEntity() instanceof EntityTrainBase && this.executor != null && !this.executor.finished();
	}

	@Override
	public void startExecuting()
    {
		this.executor.start(this.motorman.world);
    }

	@Override
	public boolean shouldContinueExecuting()
    {
		if(!this.shouldExecute())
		{
			this.executor.stop(this.motorman.world);
			return false;
		}
		return true;
    }

	@Override
	public void updateTask()
    {
		this.executor.tick(this.motorman.world, (EntityTrainBase)this.motorman.getRidingEntity());
    }
}