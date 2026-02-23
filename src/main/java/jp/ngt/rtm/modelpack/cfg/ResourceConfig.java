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

package jp.ngt.rtm.modelpack.cfg;

import jp.ngt.rtm.modelpack.state.DataMap;

import java.io.File;

public abstract class ResourceConfig
{
	public short version;

	public boolean useCustomColor;

	public String tags;

	public DMInitValue[] defaultValues;

	public File file;

	@Deprecated
	public String defaultData;

	public abstract String getName();

	public void init()
	{
		if(this.defaultValues == null && this.defaultData != null)
		{
			String[][] array = DataMap.convertArg(this.defaultData);
			this.defaultValues = new DMInitValue[array.length];
			for(int i = 0; i < array.length; ++i)
			{
				DMInitValue dmiv = new DMInitValue();
				dmiv.key = array[i][0];
				dmiv.type = array[i][1];
				dmiv.value = array[i][2];
				this.defaultValues[i] = dmiv;
			}
		}
	}

	protected String fixSoundPath(String path) {
		return fixSoundPath(path, null);
	}

	protected String fixSoundPath(String path, String defaults) {
		if (path != null && path.length() != 0) {
			return !path.contains(":") ? "rtm:" + path : path;
		} else {
			return defaults;
		}
	}

	public class DMInitValue
	{
		public String type;
		public String key;
		public String value;
		public String[] suggestions;
		public double[] minmax;
		public String[] pattern;
	}
}