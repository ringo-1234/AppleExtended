package jp.ngt.ngtlib.renderer.model;

import java.util.List;
import java.util.Map;

import jp.ngt.ngtlib.io.FileType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**モデルファイルのデータを格納*/
@SideOnly(Side.CLIENT)
public interface IModelNGT
{
	void renderAll(boolean smoothing);

    void renderOnly(boolean smoothing, String... groupNames);

    void renderPart(boolean smoothing, String partName);

    /**GL_QUADS or GL_TRIANGLES*/
	int getDrawMode();

	List<GroupObject> getGroupObjects();

	Map<String, Material> getMaterials();

	FileType getType();

	/**[minX, minY, minZ, maxX, maxY, maxZ]*/
	float[] getSize();
}