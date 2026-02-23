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

package jp.ngt.mcte.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.ngt.mcte.MCTE;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.PermissionManager;
import net.minecraft.entity.player.EntityPlayer;

public class EditorManager
{
	public static final EditorManager INSTANCE = new EditorManager();

	/**[PlayerName, Editor]*/
	private Map<String, Editor> editorMap = new HashMap<String, Editor>();

	private EditorManager(){}

	public void add(String playerName, Editor editor)
	{
		this.editorMap.put(playerName, editor);
	}

	public void remove(EntityEditor entity)
	{
		String key = "";
		for(Entry<String, Editor> entry : this.editorMap.entrySet())
		{
			if(entry.getValue().getEntity().equals(entity))
			{
				key = entry.getKey();
				break;
			}
		}

		if(!key.isEmpty())
		{
			this.editorMap.remove(key);
		}
	}

	public void removeAll()
	{
		NGTLog.sendChatMessageToAll("Clear %s Editors", this.editorMap.size());
		for(Entry<String, Editor> entry : this.editorMap.entrySet())
		{
			entry.getValue().getEntity().setDead();
		}
		this.editorMap.clear();
	}

	public boolean canPlayerUseEditor(EntityPlayer par1)
	{
		//return playerMP.canCommandSenderUseCommand(MCTE.editorPermissionLevel, "");
		return PermissionManager.INSTANCE.hasPermission(par1, MCTE.USE_EDITOR);
	}

	public Editor getEditor(EntityPlayer par1)
	{
		return this.getEditor(par1.getName());
	}

	public Editor getEditor(String par1)
	{
		for(Entry<String, Editor> entry : this.editorMap.entrySet())
		{
			if(entry.getKey().equals(par1))
			{
				return entry.getValue();
			}
		}
		return null;
	}
}