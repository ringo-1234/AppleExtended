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

package jp.ngt.rtm.modelpack.state;

import jp.ngt.rtm.block.tt.SignboardText;
import jp.ngt.rtm.block.tt.StationTimeTable;
import jp.ngt.rtm.block.tt.TimeTableManager;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.modelset.TextureSetSignboard;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ResourceStateSignboard extends ResourceState<TextureSetSignboard> {
    public final List<SignboardText> texts = new ArrayList<>();
    private StationTimeTable timeTable;

    public ResourceStateSignboard(ResourceType type, Object entity) {
        super(type, entity);
        this.timeTable = new StationTimeTable(TimeTableManager.SAMPLE, "西京", -1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        String setting;
        if (nbt.hasKey("TimeTableSetting")) {
            this.setTTSetting(nbt.getString("TimeTableSetting"));
        }

        this.texts.clear();
        NBTTagList list = nbt.getTagList("Texts", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbt2 = list.getCompoundTagAt(i);
            this.texts.add(SignboardText.readFromNBT(nbt2, this.timeTable));
        }
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = super.writeToNBT();

        NBTTagList list = new NBTTagList();
        for (SignboardText text : this.texts) {
            list.appendTag(text.writeToNBT());
        }
        nbt.setTag("Texts", list);

        nbt.setString("TimeTableSetting", this.getTTSetting());

        return nbt;
    }

    public void setTTSetting(String setting) {
        String[] sa = setting.split(",");
        String ttName = this.timeTable.timeTable.fileName;
        String stationName = this.timeTable.station;
        int track = this.timeTable.track;
        for (String s : sa) {
            if (s.startsWith("tt")) {
                ttName = s.split("=")[1];
            } else if (s.startsWith("station")) {
                stationName = s.split("=")[1];
            } else if (s.startsWith("track")) {
                track = Byte.valueOf(s.split("=")[1]);
            }
        }
        this.timeTable = new StationTimeTable(ttName, stationName, track);
    }

    public String getTTSetting() {
        return String.format("tt=%s,station=%s,track=%d", this.timeTable.timeTable.fileName, this.timeTable.station, this.timeTable.track);
    }

    public StationTimeTable geTimeTable() {
        return this.timeTable;
    }
}