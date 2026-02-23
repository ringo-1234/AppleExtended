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

public class ContainerConfig extends ModelConfig
{
	/**名前(重複不可)*/
	private String containerName;
	/**モデル*/
	public ModelSource model;
	@Deprecated
	public String containerModel;
	@Deprecated
	public String containerTexture;

	/**当たり判定に使用*/
	public float containerWidth;
	/**当たり判定に使用*/
	public float containerHeight;
	/**貨車上での位置調整に使用*/
	public float containerLength;

	@Override
	public void init()
	{
		super.init();

		if(this.model == null)
		{
			this.model = new ModelSource();
			this.model.modelFile = this.containerModel;
			this.model.textures = new String[][]{{"default", this.containerTexture}};
			this.model.rendererPath = null;
		}

		if(this.containerWidth <= 0.0F)
		{
			this.containerWidth = 1.0F;
		}

		if(this.containerHeight <= 0.0F)
		{
			this.containerHeight = 1.0F;
		}

		if(this.containerLength <= 0.0F)
		{
			this.containerLength = 1.0F;
		}
	}

	@Override
	public String getName()
	{
		return this.containerName;
	}

	public static ContainerConfig getDummy()
	{
		ContainerConfig cfg = new ContainerConfig();
		cfg.containerName = "dummy";
		return cfg;
	}
}