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

import jp.ngt.rtm.modelpack.IResourceSelector;
import jp.ngt.rtm.modelpack.ScriptExecuter;
import net.minecraft.entity.Entity;

public class ScriptExecuterNPC extends ScriptExecuter {
    public boolean onAttackedFrom(IResourceSelector selector, Entity attacker) {
        // ret = this.callMethod(selector, "onAttackedFrom", selector, attacker, this);
        // (ret instanceof Boolean) ? (Boolean)ret : true;
        return true;
    }

    public boolean attackEntity(IResourceSelector selector, Entity target) {
        //Object ret = this.callMethod(selector, "attackEntity", selector, target, this);
        //return (ret instanceof Boolean) ? (Boolean)ret : true;
        return true;
    }
}