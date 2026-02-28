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

package jp.ngt.rtm.modelpack;

import jp.ngt.rtm.modelpack.cfg.ResourceConfig;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;

public class ResourceType<C extends ResourceConfig, S extends ResourceSet> implements Cloneable, Comparable<ResourceType> {
    public final String name;
    public final Class<C> cfgClass;
    public final Class<S> setClass;
    public final boolean hasSubType;

    public ResourceType parent;
    public String subType;
    public String defaultName;
    public boolean useCustomLoading;

    public ResourceType(String par1, Class<C> par2, Class<S> par3, boolean par4) {
        this.name = par1;
        this.cfgClass = par2;
        this.setClass = par3;
        this.hasSubType = par4;
        ModelPackManager.INSTANCE.registerType(this);
    }

    /**
     * デフォルト(設置時)のモデル名を設定
     */
    public ResourceType<C, S> setDefault(String s) {
        this.defaultName = s;
        return this;
    }

    /**
     * jsonを使わずに読み込むかどうか
     */
    public ResourceType<C, S> setCustomLoading(boolean par1) {
        this.useCustomLoading = par1;
        return this;
    }

    /**
     * SubTypeを取得
     */
    public ResourceType<C, S> getSubType(String s) {
        ResourceType newObj = this.clone();
        newObj.parent = this;
        newObj.subType = s;
        ModelPackManager.INSTANCE.registerType(newObj);
        return newObj;
    }

    @Override
    public ResourceType<C, S> clone() {
        ResourceType<C, S> copy = null;
        try {
            copy = (ResourceType<C, S>) super.clone();//メンバの参照先は同一のまま
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copy;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;//判定を高速にするため
        } else if (obj instanceof ResourceType) {
            ((ResourceType) obj).name.equals(this.name);//SubTypeも親が同じなら同一と判定
        }
        return false;
    }

    @Override
    public int compareTo(ResourceType arg0) {
        return this.name.compareTo(arg0.name);
    }
}