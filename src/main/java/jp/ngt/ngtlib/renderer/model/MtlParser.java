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

package jp.ngt.ngtlib.renderer.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class MtlParser {
    private Map<String, Material> materials = new HashMap<String, Material>();
    private Material currentMaterial;

    public MtlParser(InputStream is) {
        this.loadMaterial(is);
    }

    private void loadMaterial(InputStream inputStream) throws ModelFormatException {
        if (inputStream == null) {
            return;
        }

        BufferedReader reader = null;
        String currentLine = null;
        int lineCount = 0;
        this.materials.clear();

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((currentLine = reader.readLine()) != null) {
                lineCount++;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();

                if (currentLine.length() == 0 || currentLine.startsWith("#")) {
                    continue;
                } else if (currentLine.startsWith("newmtl ")) {
                    String[] sa = currentLine.split(" ");
                    this.currentMaterial = new Material((byte) this.materials.size(), null);
                    this.materials.put(sa[1], this.currentMaterial);
                }

                //Tr:透過
            }
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public Map<String, Material> getMaterials() {
        return this.materials;
    }
}