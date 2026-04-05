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

package jp.ngt.ngtlib.io;

import jp.ngt.ngtlib.block.NGTObject;
import jp.ngt.ngtlib.renderer.model.ModelFormatException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NGTZ {
    private Map<String, NGTObject> objects = new HashMap<String, NGTObject>();

    public NGTZ(ResourceLocation par1) {
        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(par1);
            this.load(res.getInputStream());
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model", e);
        }
    }

    public void load(InputStream is) throws IOException {
        ZipInputStream zip = new ZipInputStream(is);
        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
            if (!ze.isDirectory()) {
                String partsName = ze.getName().replace(".ngto", "");
                this.registerNGTO(partsName, zip);//getNextEntry()でZISをエントリのISとして扱える

            }
        }
        zip.close();
    }

    private void registerNGTO(String name, InputStream is) {
        NGTObject ngto = NGTObject.load(is);
        this.objects.put(name, ngto);
    }

    public Map<String, NGTObject> getObjects() {
        return this.objects;
    }
}