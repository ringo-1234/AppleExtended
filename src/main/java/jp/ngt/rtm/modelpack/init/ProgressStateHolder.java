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

package jp.ngt.rtm.modelpack.init;

public final class ProgressStateHolder
{
	public static final int BAR_MAIN = 0;
	public static final int BAR_SUB = 1;

	public enum ProgressState
	{
		//DOWNLOADING_FROM_SERVER(""),
		SEARCHING_MODEL("Searching model from zip"),
		LOADING_MODEL("Loading model from json"),
		SEARCHING_RRS("Searching rail load sign from zip"),
		LOADING_RRS("Loading rail load sign from json"),
		SEARCHING_SCRIPT("Searching script from zip"),
		LOADING_SCRIPT("Loading script"),
		CONSTRUCTING_MODEL("Constructing ModelPack");

		public final String message;

		private ProgressState(String par1)
		{
			this.message = par1;
		}
	}
}