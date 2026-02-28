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

public abstract class VehicleBaseConfig extends ModelConfig {
    private float[] size;

    public String rollsignTexture;
    public String[] rollsignNames;
    public Rollsign[] rollsigns;

    public String[][] customButtons;
    public String[] customButtonTips;

    public VehicleParts[] door_left;
    public VehicleParts[] door_right;
    public VehicleParts[] pantograph_front;
    public VehicleParts[] pantograph_back;

    public String sound_Stop;
    public String sound_S_A;
    public String sound_Acceleration;
    public String sound_Deceleration;
    public String sound_D_S;
    public String sound_Horn;
    public String sound_DoorOpen;
    public String sound_DoorClose;

    public String sound_ATSChime;
    public String sound_ATSBell;

    public String[][] sound_Announcement;

    public String soundScriptPath;

    public Object[][] smoke;

    public Light[] headLights;
    public Light[] tailLights;
    public Light[] interiorLights;

    private float[][] seatPosF;
    @Deprecated
    private int[][] seatPos;
    protected float[][] playerPos;

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
        this.sound_ATSChime = this.fixSoundPath(this.sound_ATSChime, "rtm:sounds/train/ats.ogg");
        this.sound_ATSBell = this.fixSoundPath(this.sound_ATSBell, "rtm:sounds/train/ats_bell.ogg");

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
    }

    public abstract ModelSource getModel();

    public float[] getSize() {
        return this.size;
    }

    public float[][] getSlotPos() {
        return this.seatPosF;
    }

    public float[][] getPlayerPos() {
        return this.playerPos;
    }

    public class Rollsign {
        public float[] uv;

        public float[][][] pos;

        public boolean doAnimation;
        public boolean disableLighting;
    }

    public class Light {
        public byte type;
        public int color;
        public float[] pos;
        public float r;
        public boolean reverse;
    }

    public class VehicleParts extends Parts {
        public VehicleParts[] childParts;
        public float[][] transform;
    }
}