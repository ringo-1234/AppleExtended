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

public class FirearmConfig extends ModelConfig
{
	/**名前(重複不可)*/
	private String firearmName;
	/**モデル*/
	public ModelSource model;
	@Deprecated
	public String firearmModel;
	@Deprecated
	public String firearmTexture;
	/**旧パーツ描画方式を使うか*/
	public boolean useOldSystem;

	/**パーツ,回転なし*/
	public Parts modelPartsN;
	/**パーツ,Y軸回転*/
	public Parts modelPartsY;
	/**パーツ,Y軸回転→X軸回転*/
	public Parts modelPartsX;
	/**パーツ,砲身*/
	public Parts modelPartsBarrel;

	/**砲口の位置*/
	public float[] muzzlePos;
	/**砲手の位置*/
	public float[] playerPos;
	/**旋回角度のMax, Min*/
	public float[] yaw;
	/**仰角のMax, Min*/
	public float[] pitch;
	/**旋回速度(0.0~1.0)*/
	public float rotationSpeedY;
	/**仰角速度(0.0~1.0)*/
	public float rotationSpeedX;
	/**反動*/
	public float recoil;

	public int rateOfFire;
	public int magazineSize;

	/**
	 * 砲弾のタイプ<br>
	 * 0:40cm, 4:12.7mm
	 * ※現状コマンドでのみ有効
	 * */
	public byte ammoType;

	/**一人称視点のときモデルを描画しない*/
	public boolean fpvMode;

	@Override
	public void init()
	{
		super.init();

		if(this.model == null)
		{
			this.model = new ModelSource();
			this.model.modelFile = this.firearmModel;
			this.model.textures = new String[][]{{"default", this.firearmTexture}};
			this.model.rendererPath = null;
			this.useOldSystem = true;
		}

		if(this.modelPartsN == null)
		{
			this.modelPartsN = new Parts();
		}

		if(this.modelPartsY == null)
		{
			this.modelPartsY = new Parts();
		}

		if(this.modelPartsX == null)
		{
			this.modelPartsX = new Parts();
		}

		if(this.modelPartsBarrel == null)
		{
			this.modelPartsBarrel = new Parts();
		}

		this.modelPartsN.initParts();
		this.modelPartsY.initParts();
		this.modelPartsX.initParts();
		this.modelPartsBarrel.initParts();

		if(this.muzzlePos == null || this.muzzlePos.length != 3)
		{
			this.muzzlePos = new float[]{0.0F, 0.0F, 5.0F};
		}

		if(this.playerPos == null || this.playerPos.length != 3)
		{
			this.playerPos = new float[]{0.0F, 2.0F, -1.0F};
		}

		if(this.yaw == null || this.yaw.length != 2)
		{
			this.yaw = new float[]{0.0F, 0.0F};
		}

		if(this.pitch == null || this.pitch.length != 2)
		{
			this.pitch = new float[]{0.0F, 0.0F};
		}

		if(this.rateOfFire < 0)
		{
			this.rateOfFire = 20;
		}

		if(this.magazineSize < 0)
		{
			this.magazineSize = 1;
		}
	}

	@Override
	public String getName()
	{
		return this.firearmName;
	}

	public static FirearmConfig getDummyConfig()
	{
		FirearmConfig config = new FirearmConfig();
		config.firearmName = "DummyFirearm";
		//config.ammoType = 0;
		config.init();
		return config;
	}
}