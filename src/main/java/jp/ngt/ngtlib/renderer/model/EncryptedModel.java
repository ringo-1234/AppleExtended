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

import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.io.NGTJson;
import jp.ngt.ngtlib.io.NGTText;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 暗号化モデルフォーマット
 */
//@SideOnly(Side.CLIENT)//getMD5()などServer側でも使うため
public class EncryptedModel {
    private static final int VERSION = 1;
    private static boolean DEBUG = false;

    private EncryptedData encData;
    private byte[][] md5;
    private byte[][] data;

    /**
     * 暗号化時にGUIから呼び出し
     *
     * @param modelFile MQO, OBJ
     * @param jsonFiles
     */
    public EncryptedModel(File modelFile, File... jsonFiles) throws IOException {
        this.encData = new EncryptedData();
        this.md5 = this.getMD5s(jsonFiles);
        this.data = this.convertModelData(modelFile);

        this.encData.version = VERSION;
        this.encData.originalFileName = modelFile.getName();
        this.encData.md5 = this.byteToStr(this.md5);
        this.encData.data = this.byteToStr(this.data);
    }

    public EncryptedModel(EncryptedData par1) throws IOException {
        this.encData = par1;
        this.md5 = this.strToByte(par1.md5);
        this.data = this.strToByte(par1.data);
    }

    /**
     * モデルロード時に呼び出し
     */
    public static EncryptedModel getInstance(InputStream is, byte[] jsonMD5) {
        EncryptedModel model;
        try {
            model = new EncryptedModel(importData(is));
        } catch (Exception e) {
            throw new ModelFormatException("Failed to load NPM", e);
        }

        if (!model.match(jsonMD5)) {
            throw new ModelFormatException("Illegal ModelPack");
        }
        return model;
    }

    private byte[][] convertModelData(File file) throws IOException {
        byte[][] ba;
        File[] files;

        if (FileType.OBJ.match(file.getName())) {
            String mtlFileName = file.getName().replaceAll(".obj", ".mtl");
            File mtlFile = new File(file.getParentFile(), mtlFileName);
            if (mtlFile.exists()) {
                ba = new byte[2][];
                files = new File[]{file, mtlFile};
            } else {
                ba = new byte[1][];
                files = new File[]{file};
            }
        } else {
            ba = new byte[1][];
            files = new File[]{file};
        }

        for (int i = 0; i < ba.length; ++i) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(files[i]);
            byte[] b = new byte[1];
            while (fis.read(b) > 0) {
                baos.write(b);
            }
            fis.close();
            baos.close();
            ba[i] = baos.toByteArray();
        }

        return ba;
    }

    /**
     * 暗号化ファイルを出力
     */
    public void exportData(File file) throws IOException {
        NGTJson.writeToJson(NGTJson.getJsonFromObject(this.encData), file);
    }

    public static EncryptedData importData(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String string;
        while ((string = br.readLine()) != null) {
            sb.append(string);
        }
        br.close();

        String json = sb.toString();
        EncryptedData res = NGTJson.getObjectFromJson(json, EncryptedData.class);
        return res;
    }

    /**
     * モデルロード時に使用
     */
    public PolygonModel getModel(String name, VecAccuracy par1, Object... args) {
        InputStream[] isa = new InputStream[this.data.length];
        for (int i = 0; i < isa.length; ++i) {
            isa[i] = new ByteArrayInputStream(this.data[i]);
        }

        PolygonModel model = ModelLoader.loadModel(isa, this.encData.originalFileName, par1, args);

        for (int i = 0; i < isa.length; ++i) {
            try {
                isa[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    //MD5/////////////////////////////////////////////////////////////////////////////////////////////////////////

    private byte[][] getMD5s(File... files) {
        byte[][] md5 = new byte[files.length][];
        for (int i = 0; i < md5.length; ++i) {
            try {
                String s = formatJson(NGTText.readText(files[i], false, ""));
                md5[i] = getMD5(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    public static String formatJson(String s) {
        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r", "");
        s = s.replaceAll(" ", "");
        s = s.replaceAll("\t", "");
        return s;
    }

    public static byte[] getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean match(byte[] ba) {
        for (int i = 0; i < this.md5.length; ++i) {
            if (MessageDigest.isEqual(ba, this.md5[i])) {
                return true;
            }
        }
        return DEBUG;
    }

    //データ変換/////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * byte[]->GZIP->Base64->Str
     */
    private String[] byteToStr(byte[][] arrays) throws IOException {
        String[] sa = new String[arrays.length];
        for (int i = 0; i < arrays.length; ++i) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(arrays[i]);
            gos.close();
            baos.close();
            byte[] ret = baos.toByteArray();

            sa[i] = Base64.getEncoder().encodeToString(ret);
        }
        return sa;
    }

    /**
     * Str->Base64->GZIP->byte[]
     */
    private byte[][] strToByte(String[] sa) throws IOException {
        byte[][] ba = new byte[sa.length][];
        for (int i = 0; i < sa.length; ++i) {
            byte[] decData = Base64.getDecoder().decode(sa[i]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(decData);
            GZIPInputStream gis = new GZIPInputStream(bais);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = gis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            gis.close();
            bais.close();
            baos.close();
            byte[] ret = baos.toByteArray();

            ba[i] = ret;
        }
        return ba;
    }
}