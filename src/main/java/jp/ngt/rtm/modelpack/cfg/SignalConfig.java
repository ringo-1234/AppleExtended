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

import jp.ngt.rtm.modelpack.modelset.ModelSetSignal;
import jp.ngt.rtm.modelpack.modelset.ModelSetSignal.LightParts;

public class SignalConfig extends ModelConfig
{
	/**名前(重複不可)*/
	private String signalName;
	/**モデル*/
	public ModelSource model;
	/**本体の回転*/
	public boolean rotateBody;
	/**受信信号の上限*/
	public int maxSignalLevel;

	@Deprecated
	public String signalModel;
	@Deprecated
	public String signalTexture;
	@Deprecated
	public String lightTexture;
	@Deprecated
	public Parts modelPartsFixture;//パーツ,固定具
	@Deprecated
	public Parts modelPartsBody;//パーツ,本体
	/**
	 * ライト<br>
	 * "S(<どの信号で点灯するか:1~1024>) I(<点滅間隔:0~1200>) P(partsA partsB ...)"<br>
	 * パーツ名はスペースで分けること
	 */
	@Deprecated
	public String[] lights;

	@Override
	public void init()
	{
		super.init();

		if(this.model == null)
		{
			this.model = new ModelSource();
			this.model.modelFile = this.signalModel;
			this.model.textures = new String[][]{
					{"default", this.signalTexture, "Light", this.lightTexture}};
		}

		if(this.modelPartsFixture == null)
		{
			this.modelPartsFixture = new Parts();
		}

		if(this.modelPartsBody == null)
		{
			this.modelPartsBody = new Parts();
		}

		this.modelPartsFixture.initParts();
		this.modelPartsBody.initParts();

		if(this.maxSignalLevel == 0 && this.lights != null)
		{
			int i = 0;
			LightParts[] parts = ModelSetSignal.parseLightParts(this.lights);
			for(LightParts light : parts)
			{
				if(light.signalLevel > i)
				{
					i = light.signalLevel;
				}
			}
			this.maxSignalLevel = i;
		}
	}

	@Override
	public String getName()
	{
		return this.signalName;
	}

	public static SignalConfig getDummyConfig()
	{
		SignalConfig config = new SignalConfig();
		config.signalName = "DummySignal";
		config.init();
		return config;
	}
}