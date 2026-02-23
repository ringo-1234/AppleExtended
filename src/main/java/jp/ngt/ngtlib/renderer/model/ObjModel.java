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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import jp.ngt.ngtlib.io.FileType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ObjModel extends PolygonModel
{
    public static final float SMOOTHING = 60.0F;

    private static Pattern vertexPattern = 				Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern vertexNormalPattern = 		Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern textureCoordinatePattern = 	Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
    private static Pattern face_V_VT_VN_Pattern = 		Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VT_Pattern = 			Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VN_Pattern = 			Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
    private static Pattern face_V_Pattern = 			Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
    private static Pattern groupObjectPattern = 		Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");

    private ArrayList<Vertex> vertexNormals;
    private ArrayList<TextureCoordinate> textureCoordinates;
    private Map<String, Material> materials;

    private byte currentMaterial;

    protected ObjModel(InputStream[] is, String name, VecAccuracy par2) throws ModelFormatException
    {
        super(is, name, GL11.GL_TRIANGLES, par2);
    }

    @Override
    protected void init(InputStream[] is) throws ModelFormatException
    {
        this.vertexNormals = new ArrayList<Vertex>();
        this.textureCoordinates = new ArrayList<TextureCoordinate>();

        InputStream is2 = null;
        if(is.length >= 2)
        {
            is2 = is[1];
        }
        this.materials = (new MtlParser(is2)).getMaterials();

        super.init(is);
    }

    @Override
    protected void parseLine(String currentLine, int lineCount)
    {
        if(currentLine.isEmpty()){return;}

        if (currentLine.startsWith("f "))
        {
            if(this.currentGroupObject == null)
            {
                this.currentGroupObject = new GroupObject("Default", GL11.GL_TRIANGLES);
                this.currentGroupObject.smoothingAngle = SMOOTHING;
            }

            Face face = this.parseFace(currentLine, lineCount);

            if(face != null)
            {
                this.currentGroupObject.faces.add(face);
            }
        }
        else if(currentLine.startsWith("vt "))
        {
            TextureCoordinate textureCoordinate = this.parseTextureCoordinate(currentLine, lineCount);
            if (textureCoordinate != null)
            {
                this.textureCoordinates.add(textureCoordinate);
            }
        }
        else if (currentLine.startsWith("v "))
        {
            Vertex vertex = this.parseVertex(currentLine, lineCount);
            if (vertex != null)
            {
                this.vertices.add(vertex);
                this.calcSizeBox(vertex);
            }
        }

        else if (currentLine.startsWith("usemtl "))
        {
            String[] sa = this.split(currentLine, ' ');
            Material mat = this.materials.get(sa[1]);
            if(mat != null)
            {
                this.currentMaterial = mat.id;
            }
        }
        else if (currentLine.startsWith("vn "))
        {
            Vertex vertex = this.parseVertexNormal(currentLine, lineCount);
            if (vertex != null)
            {
                this.vertexNormals.add(vertex);
            }
        }
        else if (currentLine.startsWith("g ") | currentLine.startsWith("o "))
        {
            GroupObject group = this.parseGroupObject(currentLine, lineCount);

            if(group != null)
            {
                if(this.currentGroupObject != null)
                {
                    this.groupObjects.add(this.currentGroupObject);
                }
            }

            this.currentGroupObject = group;
            this.currentGroupObject.smoothingAngle = SMOOTHING;
        }
    }

    @Override
    protected void postInit()
    {
        this.groupObjects.add(this.currentGroupObject);
        this.vertexNormals.clear();
        this.textureCoordinates.clear();
    }

    private Vertex parseVertex(String line, int lineCount) throws ModelFormatException
    {
        if(this.isValidVertexLine(line))
        {
            line = line.substring(line.indexOf(' ') + 1);
            String[] tokens = this.split(line, ' ');

            try
            {
                if(tokens.length == 2)
                {
                    return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), 0.0F, this.accuracy);
                }
                else if(tokens.length == 3)
                {
                    return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), this.accuracy);
                }
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return null;
    }

    private Vertex parseVertexNormal(String line, int lineCount) throws ModelFormatException
    {
        if(this.isValidVertexNormalLine(line))
        {
            line = line.substring(line.indexOf(' ') + 1);
            String[] tokens = this.split(line, ' ');

            try
            {
                if(tokens.length == 3)
                {
                    return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), this.accuracy);
                }
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return null;
    }

    private TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException
    {
        if(this.isValidTextureCoordinateLine(line))
        {
            line = line.substring(line.indexOf(' ') + 1);
            String[] tokens = this.split(line, ' ');

            try
            {
                return TextureCoordinate.create(Float.parseFloat(tokens[0]), 1.0F - Float.parseFloat(tokens[1]), this.accuracy);
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }
    }

    private Face parseFace(String line, int lineCount) throws ModelFormatException
    {
        if(this.isValidFaceLine(line))
        {
            String trimmedLine = line.substring(line.indexOf(' ') + 1);
            String[] tokens = this.split(trimmedLine, ' ');

            if(tokens.length > 2)
            {
                return this.parsePolygon(line, tokens, lineCount);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }
        return null;
    }

    private Face parsePolygon(String line, String[] tokens, int lineCount)
    {
        byte type = this.getValidType(line);
        if(type >= 0)
        {
            int size = (tokens.length - 2) * 3;
            Face face = new Face(size, this.currentMaterial);

            if(type == 0 || type == 2)
            {
            }

            for(int i = 0; i < size; ++i)
            {
                int index = (i % 3 == 0) ? 0 : (i / 3) + (i % 3);
                Vertex vertex = null;
                TextureCoordinate tex = null;

                if(type < 3)
                {
                    String[] subTokens = null;

                    if(type == 0 || type == 1)
                    {
                        subTokens = this.split(tokens[index], '/');
                    }
                    else if(type == 2)
                    {
                        subTokens = this.split(tokens[index], "//");
                    }

                    vertex = this.vertices.get(Integer.parseInt(subTokens[0]) - 1);

                    if(type == 0 || type == 1)
                    {
                        tex = this.textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    }

                    if(type == 0 || type == 2)
                    {
                    }
                }
                else
                {
                    vertex = this.vertices.get(Integer.parseInt(tokens[index]) - 1);
                }

                face.addVertex(i, vertex, tex);
            }

            face.calculateFaceNormal(this.accuracy);
            return face;
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }
    }

    private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException
    {
        if(this.isValidGroupObjectLine(line))
        {
            String trimmedLine = line.substring(line.indexOf(' ') + 1);
            if(trimmedLine.length() > 0)
            {
                return new GroupObject(trimmedLine, GL11.GL_TRIANGLES);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }
        return null;
    }

    private static boolean isValidVertexLine(String line)
    {
        return vertexPattern.matcher(line).matches();
    }

    private static boolean isValidVertexNormalLine(String line)
    {
        return vertexNormalPattern.matcher(line).matches();
    }

    private static boolean isValidTextureCoordinateLine(String line)
    {
        return textureCoordinatePattern.matcher(line).matches();
    }

    private static boolean isValidFace_V_VT_VN_Line(String line)
    {
        return face_V_VT_VN_Pattern.matcher(line).matches();
    }

    private static boolean isValidFace_V_VT_Line(String line)
    {
        return face_V_VT_Pattern.matcher(line).matches();
    }

    private static boolean isValidFace_V_VN_Line(String line)
    {
        return face_V_VN_Pattern.matcher(line).matches();
    }

    private static boolean isValidFace_V_Line(String line)
    {
        return face_V_Pattern.matcher(line).matches();
    }

    private static boolean isValidFaceLine(String line)
    {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }

    private static byte getValidType(String line)
    {
        if(isValidFace_V_VT_VN_Line(line))
        {
            return 0;
        }
        else if(isValidFace_V_VT_Line(line))
        {
            return 1;
        }
        else if(isValidFace_V_VN_Line(line))
        {
            return 2;
        }
        else if(isValidFace_V_Line(line))
        {
            return 3;
        }
        return -1;
    }

    private static boolean isValidGroupObjectLine(String line)
    {
        return groupObjectPattern.matcher(line).matches();
    }

    @Override
    public FileType getType()
    {
        return FileType.OBJ;
    }

    @Override
    public Map<String, Material> getMaterials()
    {
        return this.materials;
    }
}