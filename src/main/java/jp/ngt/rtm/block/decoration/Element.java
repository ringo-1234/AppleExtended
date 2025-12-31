package jp.ngt.rtm.block.decoration;

import jp.ngt.rtm.block.decoration.Face.FaceType;

public class Element implements Cloneable
{
	public String name;
	public Face[] faces;

	@Override
	public Element clone()
	{
		Element element = new Element();
		element.name = this.name;
		element.faces = new Face[this.faces.length];
		for(int i = 0; i < element.faces.length; ++i)
		{
			element.faces[i] = this.faces[i].clone();
		}
		return element;
	}

	public void addVec(float[] vec3, boolean lockUV)
	{
		for(Face face : this.faces)
		{
			face.addVec(vec3, lockUV);
		}
	}

	public static Element getDefaultElement()
	{
		Element element = new Element();
		element.name = "default";

		float minU = 0.0F;
		float maxU = 1.0F;
		float minV = 0.0F;
		float maxV = 1.0F;
		//z+1
		Face front = new Face();
		front.name = "front";
		front.texture = "minecraft:decoration/deco_platform_side";
		front.shadow = 0.8F;
		front.type = FaceType.FRONT;
		front.vertex = new float[][]{
			{0.0F, 1.0F, 1.0F, minU, minV},
			{0.0F, 0.0F, 1.0F, minU, maxV},
			{1.0F, 0.0F, 1.0F, maxU, maxV},
			{1.0F, 1.0F, 1.0F, maxU, minV}
		};
		//z-1
		Face back = new Face();
		back.name = "back";
		back.texture = "minecraft:blocks/log_birch";
		back.shadow = 0.8F;
		back.type = FaceType.BACK;
		back.vertex = new float[][]{
			{1.0F, 1.0F, 0.0F, minU, minV},
			{1.0F, 0.0F, 0.0F, minU, maxV},
			{0.0F, 0.0F, 0.0F, maxU, maxV},
			{0.0F, 1.0F, 0.0F, maxU, minV}
		};
		//x+1
		Face left = new Face();
		left.name = "left";
		left.texture = "rtm:blocks/fireBrick";
		left.shadow = 0.6F;
		left.type = FaceType.LEFT;
		left.vertex = new float[][]{
			{1.0F, 1.0F, 1.0F, minU, minV},
			{1.0F, 0.0F, 1.0F, minU, maxV},
			{1.0F, 0.0F, 0.0F, maxU, maxV},
			{1.0F, 1.0F, 0.0F, maxU, minV}
		};
		//x-1
		Face right = new Face();
		right.name = "right";
		right.texture = "minecraft:blocks/bookshelf";
		right.shadow = 0.6F;
		right.type = FaceType.RIGHT;
		right.vertex = new float[][]{
			{0.0F, 1.0F, 0.0F, minU, minV},
			{0.0F, 0.0F, 0.0F, minU, maxV},
			{0.0F, 0.0F, 1.0F, maxU, maxV},
			{0.0F, 1.0F, 1.0F, maxU, minV}
		};
		//y+1
		Face top = new Face();
		top.name = "top";
		top.texture = "minecraft:decoration/deco_platform_top";
		top.shadow = 1.0F;
		top.type = FaceType.TOP;
		top.vertex = new float[][]{
			{0.0F, 1.0F, 0.0F, minU, minV},
			{0.0F, 1.0F, 1.0F, minU, maxV},
			{1.0F, 1.0F, 1.0F, maxU, maxV},
			{1.0F, 1.0F, 0.0F, maxU, minV}
		};
		//y-1
		Face bottom = new Face();
		bottom.name = "bottom";
		bottom.texture = "sound_test:blocks/deco_test";
		bottom.shadow = 0.5F;
		bottom.type = FaceType.BOTTOM;
		bottom.vertex = new float[][]{
			{0.0F, 0.0F, 1.0F, minU, minV},
			{0.0F, 0.0F, 0.0F, minU, maxV},
			{1.0F, 0.0F, 0.0F, maxU, maxV},
			{1.0F, 0.0F, 1.0F, maxU, minV}
		};

		element.faces = new Face[]{front, back, left, right, top, bottom};

		return element;
	}
}