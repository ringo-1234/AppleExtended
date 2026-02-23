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

package jp.ngt.mcte.editor.filter;

import java.util.Iterator;
import java.util.List;

import jp.ngt.mcte.editor.Editor;
import jp.ngt.mcte.editor.EntityEditor;
import jp.ngt.mcte.editor.WorldSnapshot;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.AABBInt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EditFilterDeleteEntity extends EditFilterBase
{
	@Override
	public void init(Config par)
	{
		super.init(par);
	}

	@Override
	public String getFilterName()
	{
		return "DeleteEntity";
	}

	@Override
	public boolean edit(Editor editor)
	{
		AABBInt box = editor.getSelectBox();
		if(box != null)
		{
			//editor.record(box);
			WorldSnapshot snapshot = editor.copy(box, "");
			List<Entity> list2 = snapshot.getEntities();
    		Iterator iterator = list2.iterator();
    		while(iterator.hasNext())
    		{
    			Object obj = iterator.next();
    			if(!(obj instanceof EntityEditor) && !(obj instanceof EntityPlayer))
    			{
    				((Entity)obj).setDead();
    			}
    		}
    		NGTLog.sendChatMessage(editor.getEntity().getPlayer(), "Delete Entities : " + list2.size());
			return true;
		}
		return false;
	}
}