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

package jp.ngt.ngtlib.renderer;

public class GLObject
{
	public int value;
	public boolean delFlag;

	protected GLObject(int par1)
	{
		this.value = par1;
	}

	public void setDelFlag(boolean par1)
	{
		this.delFlag = par1;
	}
}