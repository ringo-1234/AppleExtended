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

package jp.ngt.rtm.block.decoration;

import jp.ngt.ngtlib.io.NGTJson;

import java.io.IOException;

public class DecorationModel implements Cloneable {
    public static final DecorationModel DEFAULT_MODEL = getDefaultModel("default");

    public String name;
    public Element[] elements;

    @Override
    public DecorationModel clone() {
        DecorationModel model = new DecorationModel();
        model.elements = new Element[this.elements.length];
        for (int i = 0; i < model.elements.length; ++i) {
            model.elements[i] = this.elements[i].clone();
        }
        model.name = this.name + "_copy";
        return model;
    }

    public String toJson() {
        return NGTJson.getJsonFromObject(this);
    }

    public static DecorationModel fromJson(String json) throws IOException {
        return (DecorationModel) NGTJson.getObjectFromJson(json, DecorationModel.class);
    }

    /**
     * 通常形状のブロックを返す
     */
    public static DecorationModel getDefaultModel(String name) {
        DecorationModel model = new DecorationModel();
        model.name = name;
        model.elements = new Element[]{Element.getDefaultElement()};
        return model;
    }
}