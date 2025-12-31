package jp.ngt.rtm.modelpack.cfg;

public abstract class VehicleBaseConfig extends ModelConfig
{
	/**{width, height}*/
	private float[] size;

	/**方向幕のテクスチャのパス*/
	public String rollsignTexture;
	/**方向幕の名前*/
	public String[] rollsignNames;
	public Rollsign[] rollsigns;
    /**2系統目方向幕のテクスチャのパス*/
    public String rollsignTexture2;
    /**2系統目方向幕の名前*/
    public String[] rollsignNames2;
    public Rollsign[] rollsigns2;
    /**2系統目と交互に切り替える間隔（ミリ秒、デフォルト3000）*/
    public int rollsignAlternateCycle = 3000;

	/**[ボタン][ボタンの状態名]*/
	public String[][] customButtons;
	/**[各ボタンの説明]*/
	public String[] customButtonTips;

	public VehicleParts[] door_left;
	public VehicleParts[] door_right;
	public VehicleParts[] pantograph_front;
	public VehicleParts[] pantograph_back;

	/**停車時のサウンド*/
	public String sound_Stop;
	/**停車-走行時のサウンド*/
	public String sound_S_A;
	/**走行時のサウンド*/
	public String sound_Acceleration;
	/**走行時のサウンド*/
	public String sound_Deceleration;
	/**走行-停車時のサウンド*/
	public String sound_D_S;
	/**警笛のサウンド*/
	public String sound_Horn;
	public String sound_DoorOpen;
	public String sound_DoorClose;

	/**車内放送 {name, sound}*/
	public String[][] sound_Announcement;

	public String soundScriptPath;

	/**
	 * 煙を出す<br>
	 * {float x, float y, float z, String name, int min, int max, float speed}<br>
	 */
	public Object[][] smoke;

	/**前照灯*/
	public Light[] headLights;
	/**尾灯*/
	public Light[] tailLights;
	/**車内灯*/
	public Light[] interiorLights;

	/**
	 * 座席の位置 {x, y, z, type}<br>
	 * <br>
	 * typeには座席の種類を指定<br>
	 * 0:なし<br>
	 * 1:クロスシート（モデル表示あり）<br>
	 * 2:クロスシート・ロングシート（モデル表示なし、車両モデルに座席を用意しているのであればこちらを指定する）)<br>
	 * 3:寝台(未実装)
	 */
	//private float[][] slotPos;//こちらに移行予定
	private float[][] seatPosF;
	@Deprecated
	private int[][] seatPos;
	/**プレーヤーの座る位置{x, y, z}*/
	protected float[][] playerPos;

	/**運転台を画面に表示しない*/
	public boolean notDisplayCab;

	public float wheelRotationSpeed;


	@Override
	public void init() {
        super.init();

        if (this.size == null) {
            this.size = new float[]{2.75F, 1.25F};
        }

        this.sound_Stop = this.fixSoundPath(this.sound_Stop);
        this.sound_S_A = this.fixSoundPath(this.sound_S_A);
        this.sound_Acceleration = this.fixSoundPath(this.sound_Acceleration);
        this.sound_Deceleration = this.fixSoundPath(this.sound_Deceleration);
        this.sound_D_S = this.fixSoundPath(this.sound_D_S);
        this.sound_Horn = this.fixSoundPath(this.sound_Horn);
        this.sound_DoorOpen = this.fixSoundPath(this.sound_DoorOpen);
        this.sound_DoorClose = this.fixSoundPath(this.sound_DoorClose);

        if (this.sound_Announcement != null) {
            for (int i = 0; i < this.sound_Announcement.length; ++i) {
                this.sound_Announcement[i][1] = this.fixSoundPath(this.sound_Announcement[i][1]);
            }
        }

        if (this.seatPosF == null) {
            if (this.seatPos != null) {
                this.seatPosF = new float[this.seatPos.length][];
                for (int i = 0; i < this.seatPosF.length; ++i) {
                    float x = (float) this.seatPos[i][0] * 0.0625F;
                    float y = (float) this.seatPos[i][1] * 0.0625F;
                    float z = (float) this.seatPos[i][2] * 0.0625F;
                    float type = (float) this.seatPos[i][3];
                    this.seatPosF[i] = new float[]{x, y, z, type};
                }
            } else {
                this.seatPosF = new float[][]{};
            }
        }

        if (this.playerPos == null) {
            this.playerPos = new float[][]{{0.8F, 0.0F, 9.187F}, {-0.8F, 0.0F, -9.187F}};
        }

        if (this.wheelRotationSpeed <= 0.0F) {
            this.wheelRotationSpeed = 1.0F;
        }

        if (this.customButtons == null) {
            this.customButtons = new String[][]{};
        }

        if (this.customButtonTips == null) {
            this.customButtonTips = new String[this.customButtons.length];
        }
        if (this.rollsignAlternateCycle <= 0) {
            this.rollsignAlternateCycle = 3000;
        }
    }

	public abstract ModelSource getModel();

	public float[] getSize()
	{
		return this.size;
	}

	public float[][] getSlotPos()
	{
		return this.seatPosF;
	}

	public float[][] getPlayerPos()
	{
		return this.playerPos;
	}


	/**方向幕の位置とマッピングを定義するクラス*/
	public class Rollsign
	{
		/**
		 * 方向幕のマッピング<br>
		 * テクスチャ上で方向幕に使いたい部位(１つだけではなく、全て)を指定<br>
		 * {uMin, uMax, vMin, vMax}<br>
		 * uvは0.0~1.0の値<br>
		 */
		public float[] uv;

		/**
		 * 方向幕の位置、複数可<br>
		 * テクスチャを貼る面をその頂点4つで指定<br>
		 * {{{点1(x,y,z)},{点2},{点3},{点4}}, ...}
		 */
		public float[][][] pos;

		/**方向幕を動かすかどうか*/
		public boolean doAnimation;
		/**光らせない*/
		public boolean disableLighting;
	}

	public class Light
	{
		public byte type;
		public int color;
		public float[] pos;
		public float r;
		/**向き反転*/
		public boolean reverse;
	}

	public class VehicleParts extends Parts
	{
		public VehicleParts[] childParts;
		/**
		 * 移動:{x, y, z}<br>
		 * 回転:{angle, vecX, vecY, vecZ}
		 */
		public float[][] transform;
	}
}