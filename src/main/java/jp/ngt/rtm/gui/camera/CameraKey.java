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

package jp.ngt.rtm.gui.camera;

import org.lwjgl.input.Keyboard;

public enum CameraKey
{
	ZOOM_IN(Keyboard.KEY_X, 'X'),
	ZOOM_OUT(Keyboard.KEY_Z, 'Z'),
	SENSIT_UP(Keyboard.KEY_V, 'V'),
	SENSIT_DOWN(Keyboard.KEY_C, 'C'),
	FOCUS_IN(Keyboard.KEY_N, 'N'),
	FOCUS_OUT(Keyboard.KEY_B, 'B'),
	FOCUS_MODE(Keyboard.KEY_M, 'M'),
	DEBUG(Keyboard.KEY_K, 'K');

	public final int key;
	public final char chara;

	private CameraKey(int p1, char p2)
	{
		this.key = p1;
		this.chara = p2;
	}

	public boolean isDown()
	{
		return Keyboard.isKeyDown(this.key);
	}

	/**キー押下中の最初の呼び出しのみtrue*/
	public boolean isPressed()
	{
		if(CameraKeySet.PREV_KEY != this.key)
		{
			if(this.isDown())
			{
				CameraKeySet.PREV_KEY = this.key;
				return true;
			}
		}
		else
		{
			if(!this.isDown())
			{
				CameraKeySet.PREV_KEY = 0;
			}
		}
		return false;
	}
}