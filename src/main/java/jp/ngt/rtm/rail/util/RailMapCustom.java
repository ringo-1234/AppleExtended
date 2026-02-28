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

package jp.ngt.rtm.rail.util;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.modelpack.ModelPackManager;

import javax.script.ScriptEngine;

public final class RailMapCustom extends RailMap {
    private RailPosition startRP;
    private RailPosition endRP;

    private ScriptEngine script;

    public RailMapCustom(RailPosition rp, String scriptName, String args) {
        this.startRP = rp;
        this.init(scriptName, args);
    }

    private void init(String scriptName, String args) {
        this.script = ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(scriptName));
        int split = (int) (this.getLength() * 4.0D);
        double[] dzx = this.getRailPos(split, split);
        double dy = this.getRailHeight(split, split);
        float yaw = this.getRailYaw(split, split);
        int x = NGTMath.floor(dzx[1]);
        int y = NGTMath.floor(dy);
        int z = NGTMath.floor(dzx[0]);
        int dir = NGTMath.floor(((yaw + 360.0F) % 360.0F) / 45.0F);
        this.endRP = new RailPosition(x, y, z, dir, 0);
    }

    public static String getDefaultArgs(String scriptName) {
        return getDefaultArgs(ScriptUtil.doScript(ModelPackManager.INSTANCE.getScript(scriptName)));
    }

    public static String getDefaultArgs(ScriptEngine se) {
        return (String) ScriptUtil.doScriptFunction(se, "getDefaultArgs");
    }

    @Override
    public RailPosition getStartRP() {
        return this.startRP;
    }

    @Override
    public RailPosition getEndRP() {
        return this.endRP;
    }

    @Override
    public double getLength() {
        return (double) ScriptUtil.doScriptFunction(this.script, "getLength");
    }

    @Override
    public int getNearlestPoint(int split, double x, double z) {
        return (int) ScriptUtil.doScriptFunction(this.script, "getNearlestPoint", split, x, z);
    }

    @Override
    public double[] getRailPos(int split, int index) {
        return (double[]) ScriptUtil.doScriptFunction(this.script, "getPos", split, index);
    }

    @Override
    public double getRailHeight(int split, int index) {
        return (double) ScriptUtil.doScriptFunction(this.script, "getHeight", split, index);
    }

    @Override
    public float getRailYaw(int split, int index) {
        float yaw = (float) ScriptUtil.doScriptFunction(this.script, "getYaw", split, index);
        return yaw + this.startRP.anchorYaw;
    }

    @Override
    public float getRailPitch(int split, int index) {
        return (float) ScriptUtil.doScriptFunction(this.script, "getPitch", split, index);
    }

    @Override
    public float getRailRoll(int split, int index) {
        return (float) ScriptUtil.doScriptFunction(this.script, "getRoll", split, index);
    }

}
