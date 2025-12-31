package jp.ngt.rtm.entity.ai;

import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ScriptExecuter;
import net.minecraft.entity.Entity;

public class ScriptExecuterNPC extends ScriptExecuter
{
	public boolean onAttackedFrom(IResourceSelector selector, Entity attacker)
	{
		// ret = this.callMethod(selector, "onAttackedFrom", selector, attacker, this);
		// (ret instanceof Boolean) ? (Boolean)ret : true;
		return true;
	}

	public boolean attackEntity(IResourceSelector selector, Entity target)
	{
		//Object ret = this.callMethod(selector, "attackEntity", selector, target, this);
		//return (ret instanceof Boolean) ? (Boolean)ret : true;
		return true;
	}
}