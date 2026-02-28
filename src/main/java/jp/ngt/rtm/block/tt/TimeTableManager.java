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

package jp.ngt.rtm.block.tt;

import jp.ngt.ngtlib.io.NGTFileLoader;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.io.NGTText;
import jp.ngt.ngtlib.renderer.model.ModelFormatException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeTableManager {
    public static final String SAMPLE = "tt_sample.csv";
    public static final TimeTableManager INSTANCE = new TimeTableManager();

    private final Map<String, TimeTable> ttEntries = new HashMap<>();
    private final Map<String, TimeTable> trainToTT = new HashMap<>();

    private TimeTableManager() {
        ;
    }

    public void load() {
        List<File> fileList = NGTFileLoader.findFile((file) -> {
            return file.getName().startsWith("tt_") && file.getName().endsWith(".csv");
        });
        for (File file : fileList) {
            try {
                this.loadTT(file);
            } catch (IOException e) {
                throw new ModelFormatException(String.format("[TTM] Can't load TT : %s", file.getAbsolutePath()), e);
            }
        }
    }

    private void loadTT(File file) throws IOException {
        String[][] csv = NGTText.readCSV(file, "UTF-8");
        String name = file.getName();
        this.ttEntries.put(name, new TimeTable(name, csv));
        NGTLog.debug("[TTM] Load TT : %s", name);
    }

    public TimeTable getTimeTable(String name) {
        if (this.ttEntries.containsKey(name)) {
            return this.ttEntries.get(name);
        }
        return this.ttEntries.get(SAMPLE);
    }

    public void addTTAndTrain(String train, TimeTable tt) {
        this.trainToTT.put(train, tt);
    }

    public TimeTable getTimeTableByTrainName(String name) {
        if (this.trainToTT.containsKey(name)) {
            return this.trainToTT.get(name);
        }
        return this.ttEntries.get(SAMPLE);
    }

    public String[] getNames() {
        Set<String> set = this.ttEntries.keySet();
        return set.toArray(new String[set.size()]);
    }
}
