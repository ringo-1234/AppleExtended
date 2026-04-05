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

package jp.ngt.ngtlib.event;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tick毎処理を登録する
 */
public final class TickProcessQueue {
    private static final TickProcessQueue INSTANCE_SERVER = new TickProcessQueue();
    private static final TickProcessQueue INSTANCE_CLIENT = new TickProcessQueue();

    private final List<TickProcessEntry> entries = Collections.synchronizedList(new ArrayList());
    private final List<TickProcessEntry> succeededEntries = new ArrayList();
    private final List<TickProcessEntry> newEntries = new ArrayList();

    private TickProcessQueue() {
    }

    public static TickProcessQueue getInstance(Side side) {
        //Worldインスタンスを使い分けるため
        return side == Side.SERVER ? INSTANCE_SERVER : INSTANCE_CLIENT;
    }

    public void add(TickProcessEntry entry) {
        this.newEntries.add(entry);
    }

    /**
     * 遅延付きTick処理を登録
     */
    public void add(TickProcessEntry entry, int delay) {
        this.newEntries.add(new DelayProcessEntry(entry, delay));
    }

    /**
     * 再試行可能Tick処理を登録
     */
    public void add(TickProcessEntry entry, int maxRetry, int interval) {
        this.newEntries.add(new RetryProcessEntry(entry, maxRetry, interval));
    }

    public void onTick(World world) {
        synchronized (this.entries) {
            this.entries.addAll(this.newEntries);
            this.newEntries.clear();

            if (!this.entries.isEmpty()) {
                for (TickProcessEntry entry : this.entries) {
                    if (entry != null && entry.process(world))//たまにnullになる...?
                    {
                        this.succeededEntries.add(entry);
                    }
                }

                if (!this.succeededEntries.isEmpty()) {
                    this.entries.removeAll(this.succeededEntries);
                    this.succeededEntries.clear();
                }
            }
        }
    }

    /**
     * 遅延付きTick処理
     */
    private class DelayProcessEntry implements TickProcessEntry {
        private final TickProcessEntry entry;
        private final int delay;
        private int count;

        public DelayProcessEntry(TickProcessEntry par1, int par2) {
            this.entry = par1;
            this.delay = par2;
        }

        @Override
        public boolean process(World world) {
            if (this.delay <= this.count) {
                return this.entry.process(world);
            }
            ++this.count;
            return false;
        }
    }

    /**
     * 再試行可能Tick処理
     */
    private class RetryProcessEntry implements TickProcessEntry {
        private final TickProcessEntry entry;
        private final int maxRetry;
        private final int interval;
        private int count;

        public RetryProcessEntry(TickProcessEntry par1, int par2, int par3) {
            this.entry = par1;
            this.maxRetry = par2;
            this.interval = par3;
        }

        @Override
        public boolean process(World world) {
            if (this.count % this.interval == 0) {
                if (this.entry.process(world)) {
                    return true;
                } else if (this.maxRetry <= this.count) {
                    //NGTLog.debug("Discarded entry (TPQ)");
                    return true;
                }
            }
            ++this.count;
            return false;
        }
    }
}